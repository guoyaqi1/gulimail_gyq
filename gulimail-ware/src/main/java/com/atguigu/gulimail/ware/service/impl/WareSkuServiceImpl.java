package com.atguigu.gulimail.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.gulimail.common.exception.NoStockException;
import com.atguigu.gulimail.common.to.mq.OrderTo;
import com.atguigu.gulimail.common.to.mq.StockDetailTo;
import com.atguigu.gulimail.common.to.mq.StockLockedTo;
import com.atguigu.gulimail.common.utils.R;
import com.atguigu.gulimail.ware.entity.WareOrderTaskDetailEntity;
import com.atguigu.gulimail.ware.entity.WareOrderTaskEntity;
import com.atguigu.gulimail.ware.feign.OrderFeignService;
import com.atguigu.gulimail.ware.service.WareOrderTaskDetailService;
import com.atguigu.gulimail.ware.service.WareOrderTaskService;
import com.atguigu.gulimail.ware.service.WareSkuService;
import com.atguigu.gulimail.ware.feign.ProductFeignService;
import com.atguigu.gulimail.ware.vo.OrderItemVo;
import com.atguigu.gulimail.ware.vo.OrderVo;
import com.atguigu.gulimail.ware.vo.SkuHasStockVo;
import com.atguigu.gulimail.ware.vo.WareSkuLockVo;
import com.google.common.xml.XmlEscapers;
import com.rabbitmq.client.Channel;
import lombok.Data;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimail.common.utils.PageUtils;
import com.atguigu.gulimail.common.utils.Query;

import com.atguigu.gulimail.ware.dao.WareSkuDao;
import com.atguigu.gulimail.ware.entity.WareSkuEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import sun.net.www.MessageHeader;

import javax.annotation.Resource;

@RabbitListener(queues = "stock.release.stock.queue")
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Resource
    WareSkuDao wareSkuDao;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    WareOrderTaskService orderTaskService;

    @Autowired
    WareOrderTaskDetailService orderTaskDetailService;

    @Resource
    RabbitTemplate rabbitTemplate;

    @Autowired
    OrderFeignService orderFeignService;

    /**
     * 1.库存自动解锁。
     *   下订单成功，库存锁定成功，接下来的业务调用失败，导致订单回滚。之前锁的库存就要自动回滚
     *  2.订单失败
     *   锁库存失败
     *
     *   只要解锁库存的信息失败。一定要告诉服务解锁失败
     * @param to
     * @param msg
     */
    /*@RabbitHandler
    public void handleStockLockedRelease(StockLockedTo to , Message msg, Channel channel) throws IOException {
        System.out.println("收到解锁库存的消息");
        Long id = to.getId();
        StockDetailTo detail = to.getDetail();
        Long skuId = detail.getSkuId();
        Long detailId = detail.getId();
        //解锁
        //1.查询数据库关于这个订单的锁定库存信息
        // 有:证明库存解锁成功
        //    解锁：订单情况
        //        1.没有这个订单
        //
        //没有： 库存锁定失败 库存回滚了 这种情况无需解锁
        WareOrderTaskDetailEntity byId = orderTaskDetailService.getById(detailId);
        if(byId!=null){
            //解锁
            Long id1 =to.getId();
            WareOrderTaskEntity taskEntity = orderTaskService.getById(id);
            String orderSn = taskEntity.getOrderSn();
            R r = orderFeignService.getOrderStatus(orderSn);//根据订单号查询订单
            if(r.getCode()==0){
                //订单数据返回成功
                OrderVo data = r.getData(new TypeReference<OrderVo>() {
                });
                if(data==null || r.getCode()==4){
                    //订单不存在
                    //订单已经被取消。才能解锁库存
                    unlockStock(detail.getSkuId(),detail.getWareId(),detail.getSkuNum(),detailId);
                    channel.basicAck(msg.getMessageProperties().getDeliveryTag(),false);

                }else{
                    //消息拒绝以后重新放到队列里面，让别人继续消费解锁
                    channel.basicAck(msg.getMessageProperties().getDeliveryTag(),true);
                }
            }
        }else{
            //无需解锁
            channel.basicAck(msg.getMessageProperties().getDeliveryTag(),false);
        }
    }*/

    /**
     * 解锁库存
     * @param skuId
     * @param wareId
     * @param num
     * @param taskDetailId
     */

    private void unlockStock(Long skuId,Long wareId,Integer num , Long taskDetailId){
        //库存解锁
        wareSkuDao.unLockStock(skuId,wareId,num);

        //更新库存工作单的状态

        WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity();

        entity.setId(taskDetailId);
        //变为已解锁
        entity.setLockStatus(2);

        orderTaskDetailService.updateById(entity);

    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * skuId: 1
         * wareId: 2
         */
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if(!StringUtils.isEmpty(skuId)){
            queryWrapper.eq("sku_id",skuId);
        }

        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(wareId)){
            queryWrapper.eq("ware_id",wareId);
        }


        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1、判断如果还没有这个库存记录新增
        List<WareSkuEntity> entities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if(entities == null || entities.size() == 0){
            WareSkuEntity skuEntity = new WareSkuEntity();
            skuEntity.setSkuId(skuId);
            skuEntity.setStock(skuNum);
            skuEntity.setWareId(wareId);
            skuEntity.setStockLocked(0);
            //TODO 远程查询sku的名字，如果失败，整个事务无需回滚
            //1、自己catch异常
            //TODO 还可以用什么办法让异常出现以后不回滚？高级
            try {
                R info = productFeignService.info(skuId);
                Map<String,Object> data = (Map<String, Object>) info.get("skuInfo");

                if(info.getCode() == 0){
                    skuEntity.setSkuName((String) data.get("skuName"));
                }
            }catch (Exception e){

            }


            wareSkuDao.insert(skuEntity);
        }else{
            wareSkuDao.addStock(skuId,wareId,skuNum);
        }

    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {

        List<SkuHasStockVo> skuHasStockVos = skuIds.stream().map(item -> {
            Long count = this.baseMapper.getSkuStock(item);
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            skuHasStockVo.setSkuId(item);
            skuHasStockVo.setHasStock(count == null?false:count > 0);
            return skuHasStockVo;
        }).collect(Collectors.toList());
        return skuHasStockVos;
    }

    /**
     * 为某个订单锁定库存
     * (rollbackFor=NoStockException.class)
     * 默认只要是运行时异常都会回滚
     * @param vo
     * @return
     */
    @Transactional
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {

        /**
         * 保存库存工作单的详情，
         * 追随。
         */
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(vo.getOrderSn());
        orderTaskService.save(taskEntity);

        //1.找到每个商品在哪个仓库都有库存
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> collect = locks.stream().map(item->{
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());

            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(skuId);
            return stock;

        }).collect(Collectors.toList());

        //2.锁定库存
        for (SkuWareHasStock hasStock : collect) {
            Boolean skuStocked = false;
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareId();
            if(wareIds==null||wareIds.size()==0){
                //没有任何仓库有这个商品的库存
                throw new NoStockException(skuId);
            }
            for (Long wareId : wareIds) {
                //成功返回1 否则返回0
                Long count = wareSkuDao.lockSkuStock(skuId, wareId, hasStock.getNum());
                if(count==1){
                    skuStocked = true;
                    //TODO 告诉MQ库存锁定成功
                    //null, skuId, " ", hasStock.getNum(), taskEntity.getId(), wareId, 1
                    WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity(null,skuId," ",hasStock.getNum(),taskEntity.getId(),wareId,1);
                    orderTaskDetailService.save(entity);
                    StockLockedTo lockedTo = new StockLockedTo();
                    lockedTo.setId(taskEntity.getId());
                    StockDetailTo stockDetailTo = new StockDetailTo();
                    BeanUtils.copyProperties(entity,stockDetailTo);
                    //只发id 不行  防止回滚以后找不到数据 因此需要把wareorderdetail全部发送
                    lockedTo.setDetail(stockDetailTo);
                    rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",lockedTo);
                    break;

                }else{
                    //当前仓库锁失败 重试下一个仓库
                }
            }
            if(skuStocked==false){
                //当前商品所有仓库都没锁住
                throw new NoStockException(skuId);
            }
        }

        return null;
    }

    @Override
    public void unlockStock(StockLockedTo to) {
        System.out.println("收到解锁库存的消息");
        Long id = to.getId();
        StockDetailTo detail = to.getDetail();
        Long skuId = detail.getSkuId();
        Long detailId = detail.getId();
        //解锁
        //1.查询数据库关于这个订单的锁定库存信息
        // 有:证明库存解锁成功
        //    解锁：订单情况
        //        1.没有这个订单
        //
        //没有： 库存锁定失败 库存回滚了 这种情况无需解锁
        WareOrderTaskDetailEntity byId = orderTaskDetailService.getById(detailId);
        if(byId!=null){
            //解锁
            Long id1 =to.getId();
            WareOrderTaskEntity taskEntity = orderTaskService.getById(id);
            String orderSn = taskEntity.getOrderSn();
            R r = orderFeignService.getOrderStatus(orderSn);//根据订单号查询订单
            if(r.getCode()==0){
                //订单数据返回成功
                OrderVo data = r.getData(new TypeReference<OrderVo>() {
                });
                if(data==null || r.getCode()==4){
                    //订单不存在
                    //订单已经被取消。才能解锁库存
                    //detailId
                    if(byId.getLockStatus()==1){
                        //当前库存工作单详情，状态1 已解锁但是未解锁才可以解锁
                        unlockStock(detail.getSkuId(),detail.getWareId(),detail.getSkuNum(),detailId);
                    }


                }

            }else{
                //消息拒绝以后重新放到队列里面，让别人继续消费解锁

            }
        }else{
            //无需解锁
        }
    }

    /**
     * 防止订单服务一直卡顿，导致订单状态消息一直改不了，库存消息优先到期。查订单状态新建状态，什么都不做就走了
     * 导致卡顿的订单，永远不能解锁库存
     * @param orderTo
     */
    @Override
    public void unlockStock(OrderTo orderTo) {
        String orderSn = orderTo.getOrderSn();
        //查一下最新库存状态，防止重复解锁库存
        WareOrderTaskEntity task = orderTaskService.getOrderTaskByOrderSn(orderSn);
        Long id = task.getId();
        //按照工作单找到工作没有，没有解锁的库存，进行解锁
        List<WareOrderTaskDetailEntity> entities = orderTaskDetailService.list(
                new QueryWrapper<WareOrderTaskDetailEntity>()
                        .eq("task_id",id)
                        .eq("lock_status",1));
        for (WareOrderTaskDetailEntity entity : entities) {
            unlockStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum(),entity.getId());
        }


    }

    @Data
    class SkuWareHasStock{
        private Long skuId;
        private Integer num;
        private List<Long> wareId;
    }

}