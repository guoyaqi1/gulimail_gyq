package com.atguigu.gulimail.seckill.service.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.gulimail.common.to.mq.SeckillOrderTo;
import com.atguigu.gulimail.common.utils.R;
import com.atguigu.gulimail.common.vo.MemberRespVo;
import com.atguigu.gulimail.seckill.feign.CouponFeignService;
import com.atguigu.gulimail.seckill.feign.ProductFeignService;
import com.atguigu.gulimail.seckill.interceptor.LoginUserInterceptor;
import com.atguigu.gulimail.seckill.service.SeckillService;
import com.atguigu.gulimail.seckill.to.SeckillSkuRedisTo;
import com.atguigu.gulimail.seckill.vo.SeckillSessionsWithSkus;
import com.atguigu.gulimail.seckill.vo.SkuInfoVo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.sun.xml.internal.ws.resources.UtilMessages;
import javafx.scene.control.TableColumnBase;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author:guoyaqi
 * @Date: 2023/12/18 23:48
 */
@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {

    @Resource
    LoginUserInterceptor loginUserInterceptor;

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    ProductFeignService productFeignService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Resource
    RabbitTemplate rabbitTemplate;

    private final String SESSIONS_CACHE_PREFIX="seckill:sessions";

    private final String SKUKILL_CACHE_PREFIX="seckill:skus";

    private final String SKU_STOCK_SEMAPHERE="seckill:stock:";//加上随机码




    @Override
    public void uploadSeckillSkulatest3Days() {
        //1.扫描最近三天的商品
        R session = couponFeignService.getLasts3DaySession();
        if(session.getCode()==0){
            //上架商品
            List<SeckillSessionsWithSkus> sessionData = session.getData(new TypeReference<List<SeckillSessionsWithSkus>>() {
            });

            //缓存到redis
            //1.缓存活动信息
            saveSessionInfos(sessionData);
            //2.缓存活动的关联商品信息
            saveSessionSkuInfos(sessionData);
            
        }

    }
    public List<SeckillSkuRedisTo> blockHandler(BlockException e){
        log.error("getCurrentSeckillSkusResource被限流了..");
        return null;
    }

    /**
     * 返回当前时间可以参与的秒杀商品信息
     * @return
     */
    @SentinelResource(value = "getCurrentSeckillSkusResource",blockHandler = "blockHandler")
    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
        //1.确定当时时间是哪个秒杀场次
        long time = System.currentTimeMillis();

        try (Entry entry = SphU.entry("seckillSkus")){
            Set<String> keys = stringRedisTemplate.keys(SESSIONS_CACHE_PREFIX + "*");
            for (String key : keys) {
                //seckill:session:1582250400000_137485940
                String replace = key.replace(SESSIONS_CACHE_PREFIX, "");
                String[] s = replace.split("_");
                Long start = Long.parseLong(s[0]);
                Long end = Long.parseLong(s[1]);
                if (time>=start&&time<=end){
                    //2.获取这个秒杀场次需要的所有商品信息
                    List<String> range = stringRedisTemplate.opsForList().range(key, -100, 100);
                    BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                    List<String> list = hashOps.multiGet(range);
                    if (list!=null){
                        List<SeckillSkuRedisTo> collect = list.stream().map(item -> {
                            SeckillSkuRedisTo redis = JSON.parseObject((String) item, SeckillSkuRedisTo.class);
                            //当秒杀开始就需要随机码
                            redis.setRandomCode(null);
                            return redis;
                        }).collect(Collectors.toList());
                    }
                    break;
                }
            }

        }catch (BlockException e){
            log.error("该资源被限流：{}",e.getMessage());
        }

        return null;
    }

    @Override
    public SeckillSkuRedisTo getSkuSeckillInfo(Long skuId) {
        //1.找到所有需要参与秒杀的商品key
        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);

        Set<String> keys = hashOps.keys();
        if (keys!=null&&keys.size()>0){
            String regx="\\d_"+skuId;
            for (String key : keys) {
                if (Pattern.matches(regx,key)){
                    String json = hashOps.get(key);
                    SeckillSkuRedisTo skuRedisTo = JSON.parseObject(json, SeckillSkuRedisTo.class);

                    //随机码
                    long current = System.currentTimeMillis();
                    if (current>=skuRedisTo.getStartTime()&&current<=skuRedisTo.getEndTime()){

                    }else {
                        skuRedisTo.setRandomCode(null);
                    }
                    return skuRedisTo;
                }
            }
        }
        return null;
    }

    @Override
    public String kill(String killId, String key, Integer num) throws InterruptedException {
        MemberRespVo respVo = LoginUserInterceptor.loginUser.get();
        //1.获取当前秒杀商品的信息
        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        String json = hashOps.get(killId);
        if (StringUtils.isEmpty(json)){
            return null;
        }else{
            SeckillSkuRedisTo redis =JSON.parseObject(json,SeckillSkuRedisTo.class);
            //2.校验合法性
            Long startTime = redis.getStartTime();
            Long endTime = redis.getEndTime();
            long time = System.currentTimeMillis();

            long ttl = endTime - time;
            //3.校验时间的合法性
            if (time>=startTime && time<=endTime){
                //4.校验商品的id和随机码
                String randomCode = redis.getRandomCode();
                String skuId = redis.getPromotionId() + "_" + redis.getSkuId();
                if (randomCode.equals(key)&&killId.equals(skuId)){
                    //5.验证码购物数量是否合理
                    Integer decimal = redis.getSeckillLimit();
                    if (num<= decimal){
                        //6.验证是否被购买过  幂等性 只要被秒杀成功，就去占位
                        //SETNX
                        String redisKey = respVo.getId() + "_" + skuId;
                        //自动过期
                        Boolean aBoolean = stringRedisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(),ttl,TimeUnit.MILLISECONDS);

                        if (aBoolean){
                            //占位成功 说明从来没有买过
                            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHERE + randomCode);
                            try {
                                boolean b = semaphore.tryAcquire(num, 100, TimeUnit.MILLISECONDS);
                               //秒杀成功
                                //快速下单 发送MQ消息
                                String timeId = IdWorker.getTimeId();
                                SeckillOrderTo orderTo = new SeckillOrderTo();
                                orderTo.setOrderSn(timeId);
                                orderTo.setMemberId(respVo.getId());
                                orderTo.setNum(num);
                                orderTo.setPromotionSessionId(redis.getPromotionSessionId());
                                orderTo.setSkuId(redis.getSkuId());
                                orderTo.setSeckillPrice(redis.getSeckillPrice());

                                rabbitTemplate.convertAndSend("order-event-exchange","order.seckill.order");

                                return timeId;
                            } catch (InterruptedException e) {
                                //秒杀失败
                                //说明已经下过单

                            }

                        }else {
                            //占位失败

                        }


                    }
                }else{
                    return null;
                }
            }

        }


        return null;
    }

    private void saveSessionInfos(List<SeckillSessionsWithSkus> sessions){

        sessions.stream().forEach(session ->{
            Long startTime = session.getStartTime().getTime();

            Long endTime = session.getEndTime().getTime();

            String key = SESSIONS_CACHE_PREFIX+startTime+"_"+endTime;
            Boolean hasKey = stringRedisTemplate.hasKey(key);
            if (hasKey!=null){
                List<String> collect = session.getRelationSkus().stream().map(item -> item.getPromotionId()+"_"+item.getSkuId().toString()).collect(Collectors.toList());
                //缓存活动信息
                stringRedisTemplate.opsForList().leftPushAll(key,collect);
            }


        });

    }

    /**
     * 订单服务的幂等性
     * @param sessions
     */
    private void saveSessionSkuInfos(List<SeckillSessionsWithSkus> sessions){
        sessions.stream().forEach(session ->{
            //4.随机码
            String token = UUID.randomUUID().toString().replace("-", "");
            //准备的hash操作
            BoundHashOperations<String, Object, Object> ops = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
            session.getRelationSkus().stream().forEach(seckillSkuVo -> {
                //防止幂等性
               if (!ops.hasKey(seckillSkuVo.getPromotionSessionId().toString()+"_"+seckillSkuVo.getSkuId().toString())){
                   //缓存商品
                   SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();
                   //1.sku的基本数据
                   R skuInfo = productFeignService.getSkuInfo(seckillSkuVo.getSkuId());
                   if (skuInfo.getCode()==0){
                       SkuInfoVo info = skuInfo.getData("skuInfo",new TypeReference<SkuInfoVo>(){
                       });
                       redisTo.setSkuInfo(info);
                   }

                   //2.sku的秒杀信息
                   BeanUtils.copyProperties(seckillSkuVo,redisTo);

                   //3.设置上当前商品的秒杀时间
                   redisTo.setStartTime(session.getStartTime().getTime());
                   redisTo.setEndTime(session.getEndTime().getTime());

/*                //4.随机码
                String token = UUID.randomUUID().toString().replace("-", "");*/
                   redisTo.setRandomCode(token);

                   String jsonString = JSON.toJSONString(redisTo);
                   ops.put(seckillSkuVo.getSkuId().toString(),jsonString);

                   //5.使用库存作为分布式的信号量，限流
                   RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHERE+token);

                   //商品可以秒杀的数量作为信号量
                   semaphore.trySetPermits(seckillSkuVo.getSeckillCount());

               }
            });
        });

    }
}
