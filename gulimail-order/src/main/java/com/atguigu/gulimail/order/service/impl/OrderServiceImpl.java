package com.atguigu.gulimail.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.gulimail.common.exception.NoStockException;
import com.atguigu.gulimail.common.to.mq.OrderTo;
import com.atguigu.gulimail.common.to.mq.SeckillOrderTo;
import com.atguigu.gulimail.common.utils.R;
import com.atguigu.gulimail.common.vo.MemberRespVo;
import com.atguigu.gulimail.order.constant.OrderConstant;
import com.atguigu.gulimail.order.dao.OrderItemDao;
import com.atguigu.gulimail.order.entity.OrderItemEntity;
import com.atguigu.gulimail.order.entity.PaymentInfoEntity;
import com.atguigu.gulimail.order.enume.OrderStatusEnum;
import com.atguigu.gulimail.order.feign.*;
import com.atguigu.gulimail.order.feign.MemberFeignService;
import com.atguigu.gulimail.order.interceptor.LoginUserInterceptor;
import com.atguigu.gulimail.order.service.OrderItemService;
import com.atguigu.gulimail.order.service.PaymentInfoService;
import com.atguigu.gulimail.order.vo.*;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimail.common.utils.PageUtils;
import com.atguigu.gulimail.common.utils.Query;

import com.atguigu.gulimail.order.dao.OrderDao;
import com.atguigu.gulimail.order.entity.OrderEntity;
import com.atguigu.gulimail.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private ThreadLocal<OrderSubmitVo> confirmVoThreadLocal = new ThreadLocal<>();

    @Resource
    ThreadPoolExecutor execcutors;

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    OrderItemService orderItemService;

    @Resource
    OrderDao orderDao;

    @Resource
    OrderItemDao orderItemDao;

    @Autowired
    WmsFeignService wmsFeignService;

    @Autowired
    CartFeignService cartFeignService;

    @Resource
    StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Resource
    RabbitTemplate rabbitTemplate;

    @Autowired
    PaymentInfoService paymentInfoService;

    @Override
    public PayVo getOrderPay(String orderSn) {
        PayVo payVo = new PayVo();
        OrderEntity order = this.getOrderByOrderSn(orderSn);

        BigDecimal bigDecimal = order.getPayAmount().setScale(2, BigDecimal.ROUND_UP);
        payVo.setTotal_amount(bigDecimal.toString());
        payVo.setOut_trade_no(order.getOrderSn());

        List<OrderItemEntity> order_sn = orderItemService.list(new QueryWrapper<OrderItemEntity>());

        OrderItemEntity entity = order_sn.get(0);

        payVo.setSubject(entity.getSkuName());
        payVo.setBody(entity.getSkuAttrsVals());

        return null;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id",memberRespVo.getId()).orderByDesc("id")
        );

        List<OrderEntity> order_sn= page.getRecords().stream().map(order -> {
            List<OrderItemEntity> itemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>());
            order.setItemEntities(itemEntities);
            return order;
        }).collect(Collectors.toList());

        return new PageUtils(page);
    }

    /**
     * 订单确认
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();


        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //1.远程查询所有的收货地址列表
            List<MemberAddressVO> address = memberFeignService.getAddress(memberRespVo.getId());
            confirmVo.setAddress(address);
        }, execcutors);

        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //2.远程查询选中的购物车的数据
            List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems();
            confirmVo.setItems(items);
        }, execcutors).thenRunAsync(()->{
            List<OrderItemVo> items=confirmVo.getItems();
            List<Long> collect =items.stream().map(item->item.getSkuId()).collect(Collectors.toList());
            R hasStock = wmsFeignService.getSkusHasStock(collect);
            List<SkuStockVo> data = hasStock.getData(new TypeReference<List<SkuStockVo>>() {
            });
            if(data!=null){
                Map<Long, Boolean> map = data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                confirmVo.setStocks(map);
            }

        }, execcutors);

        //3.查询用户积分  不用远程
        Integer integration = memberRespVo.getIntegration();
        confirmVo.setIntegration(integration);

        //4.其他数据

        //5.防重令牌

        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX+memberRespVo.getId(),token,30, TimeUnit.MINUTES);
        confirmVo.setOrderToken(token);

        CompletableFuture.allOf(getAddressFuture,cartFuture).get();

        return confirmVo;
    }

    /**
     * 下单 主要业务
     * @param vo
     * @return
     */
    @GlobalTransactional //加上分布式事务注解
    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {


        SubmitOrderResponseVo response = new SubmitOrderResponseVo();

        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        confirmVoThreadLocal.set(vo);
        response.setCode(0);
        //下单：去创建订单 验令牌 验价格 锁库存
        //1.验证令牌 【令牌的对比和删除必须保证原子性】
        //0 令牌失效 1 删除成功
        String script = "if redis.call('get', KEYS[1]) ==  ARGV[1] then return redis.call('del', KEYS[1])";
        String orderToken = vo.getOrderToken();
        //原子验证和删除令牌
        Long result = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId()), orderToken);
        if(result==0L){
            //令牌验证失败
            response.setCode(1);
            return response;

        }else {
            //令牌验证成功
            //下单：去创建订单 验令牌 验价格 锁库存
            OrderCreateTo order = createOrder();

            //验价
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = vo.getPayPrice();
            if(Math.abs(payAmount.subtract(payPrice).doubleValue())<0.01){
                //金额对比

                //3.保存订单
                saveOrder(order);

                //4.库存锁定 只要有异常回滚订单数据
                //订单号 所有订单项(skuId skuName num)
                WareSkuLockVo lockVo = new WareSkuLockVo();
                lockVo.setOrderSn(order.getOrder().getOrderSn());
                List<OrderItemVo> locks = order.getOrderItems().stream().map(item->{
                    OrderItemVo itemVo = new OrderItemVo();
                    itemVo.setSkuId(item.getId());
                    itemVo.setCount(item.getSkuQuantity());
                    itemVo.setTitle(item.getSkuName());
                    return itemVo;
                }).collect(Collectors.toList());
                lockVo.setLocks(locks);
                //TODO: 4. 远程锁库存
                //库存锁定成功，但是网络原因超时了，订单回滚，库存不滚
                R r = wmsFeignService.orderLockStock(lockVo);
                if (r.getCode()==0){
                    //锁定成功
                    response.setOrder(order.getOrder());
                    //TODO 5. 远程扣减积分 出异常
                    //int i =10/0; //订单回滚 库存不滚
                    //TODO 订单创建成功发送消息给MQ
                    rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",order);


                    return response;
                }else{
                    //锁定失败
                    Long msg=(Long) r.get("msg");
                    throw new NoStockException(msg);

                }
            }else{
                response.setCode(2);
                return response;
            }
        }

    }

    /**
     * 查询订单状态
     * @param orderSn
     * @return
     */
    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        OrderEntity order_sn = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        return order_sn;
    }

    @Override
    public void closeOrder(OrderEntity entity) {
        //查询当前这个订单的最新状态
        OrderEntity orderEntity = this.getById(entity.getId());
        if(orderEntity.getStatus() == OrderStatusEnum.CREATE_NEW.getCode()){
            //关单
            OrderEntity update = new OrderEntity();
            update.setId(entity.getId());
            update.setStatus(OrderStatusEnum.CANCLED.getCode());
            this.updateById(update);
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(orderEntity,orderTo);
            //发给MQ一个

            try {
                //TODO 保证消息一定会发送出去，每一个消息都可以做好日志记录（给数据库保存每一个消息的详细消息）
                //TODO 定期扫描数据库将失败的消息再发送一遍
                rabbitTemplate.convertAndSend("order-event-exchange","order.release.other");
            } catch (AmqpException e) {
                //TODO 将没法发送的消息进行重试发送
                e.printStackTrace();
            }
        }

    }

    /**
     * 处理支付宝的结果
     * @param vo
     * @return
     */
    @Override
    public String handlePayResult(PayAsyncVo vo) {
        PaymentInfoEntity infoEntity = new PaymentInfoEntity();
        infoEntity.setAlipayTradeNo(vo.getTrade_no());
        infoEntity.setOrderSn(vo.getOut_trade_no());
        infoEntity.setPaymentStatus(vo.getTrade_status());
        infoEntity.setCallbackTime(vo.getNotify_time());

        //1.保存流水
        paymentInfoService.save(infoEntity);

        //2.修改订单状态
        if(vo.getTrade_status().equals("TRADE_SUCCESS")||vo.getTrade_status().equals("TRADE_FAILED")){
            //支付成功状态
            String outTradeNo = vo.getOut_trade_no();
            this.baseMapper.updateOrderStatus(outTradeNo,OrderStatusEnum.PAYED.getCode());
        }
        return null;
    }

    @Override
    public String asyncNotify(String notifyData) {
        return null;
    }

    @Override
    public void createSeckillOrder(SeckillOrderTo seckillOrderTo) {
        //TODO 保存订单信息
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(seckillOrderTo.getOrderSn());
        orderEntity.setMemberId(seckillOrderTo.getMemberId());
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());

        BigDecimal multiply = seckillOrderTo.getSeckillPrice().multiply(new BigDecimal("" + seckillOrderTo.getNum()));
        orderEntity.setPayAmount(multiply);

        //TODO 保存订单项信息
        OrderItemEntity itemEntity = new OrderItemEntity();
        itemEntity.setOrderSn(seckillOrderTo.getOrderSn());
        itemEntity.setRealAmount(multiply);
        //TODO 获取当前SKU的详细信息进行设置 productFeignService.getSpuInfoBySkuId()

        itemEntity.setSkuQuantity(seckillOrderTo.getNum());

        orderItemService.save(itemEntity);
    }

    /**
     * 保存订单数据
     * @param order
     */
    private void saveOrder(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());
        orderDao.insert(orderEntity);
        this.save(orderEntity);

        List<OrderItemEntity> orderItems = order.getOrderItems();

        orderItemService.saveBatch(orderItems);

    }

    private OrderCreateTo createOrder(){
        OrderCreateTo createTo = new OrderCreateTo();
        //1.生成订单号
        String orderSn = IdWorker.getTimeId();
        //创建订单号
        OrderEntity orderEntity = buildOrder(orderSn);
       //2.获取所有的订单项
        List<OrderItemEntity> itemEntities = buildOrderItems(orderSn);

        //3.验价 计算价格相关
        computePrice(orderEntity,itemEntities);

        return createTo;

    }

    /**
     * 计算价格
     * @param orderEntity
     * @param itemEntities
     */
    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> itemEntities){
        BigDecimal total = new BigDecimal("0.0");
        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal integration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");

        BigDecimal gift = new BigDecimal("0.0");
        BigDecimal growth = new BigDecimal("0.0");

        //订单的总额 叠加每一个订单项的总额信息
        for (OrderItemEntity entity : itemEntities) {
            coupon.add(entity.getCouponAmount());
            integration.add(entity.getIntegrationAmount());
            promotion.add(entity.getPromotionAmount());
            total=total.add(entity.getRealAmount());

            gift=gift.add(new BigDecimal(entity.getGiftIntegration().toString()));
            growth=growth.add(new BigDecimal(entity.getGiftGrowth().toString()));

        }

        //1.订单价格相关
        //应付总额
        orderEntity.setTotalAmount(total);
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));   //总额+运费
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(integration);
        orderEntity.setCouponAmount(coupon);

        //积分信息
        orderEntity.setIntegrationAmount(gift);
        orderEntity.setGrowth(growth.intValue());
        orderEntity.setDeleteStatus(0); //未删除


    }

    /**
     * 创建订单号
     * @param orderSn
     * @return
     */
    private OrderEntity buildOrder(String orderSn){
        //创建订单号
        MemberRespVo respVo = LoginUserInterceptor.loginUser.get();

        OrderEntity entity = new OrderEntity();
        entity.setOrderSn(orderSn);
        entity.setMemberId(respVo.getId());

        OrderSubmitVo submitVo = new OrderSubmitVo();

        R fare = wmsFeignService.getFare(submitVo.getAddrId());
        FareVo fareResp = fare.getData(new TypeReference<FareVo>(){
        });

        //设置运费信息
        entity.setFreightAmount(fareResp.getFare());
        entity.setReceiverCity(fareResp.getAddress().getCity());
        entity.setReceiverDetailAddress(fareResp.getAddress().getDetailAddress());
        entity.setReceiverName(fareResp.getAddress().getName());
        entity.setReceiverPhone(fareResp.getAddress().getPhone());
        entity.setReceiverPostCode(fareResp.getAddress().getPostCode());
        entity.setReceiverProvince(fareResp.getAddress().getProvince());
        entity.setReceiverRegion(fareResp.getAddress().getRegion());

        //设置订单相关信息
        entity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        entity.setAutoConfirmDay(7);


        return entity;
    }

    /**
     * 获取所有订单项
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        //最后订单的总价
        //2.获取所有的订单项
        List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
        if(currentUserCartItems!=null&& currentUserCartItems.size()>0){
            List<OrderItemEntity> itemEntities = currentUserCartItems.stream().map(cartItem->{
                OrderItemEntity itemEntity = buildOrderItem(cartItem);
                itemEntity.setOrderSn(orderSn);

                return itemEntity;
            }).collect(Collectors.toList());
        }

        return null;

    }

    /**
     * 获取单个订单项
     * @param cartItem
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemVo cartItem) {

        OrderItemEntity itemEntity = new OrderItemEntity();

        //1.订单信息 订单号

        //2.商品的SPU信息
        Long skuId = cartItem.getSkuId();
        R r = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoVo data = r.getData(new TypeReference<SpuInfoVo>() {
        });
        itemEntity.setSpuId(data.getId());
        itemEntity.setSpuBrand(data.getBrandId().toString());
        itemEntity.setSpuName(data.getSpuName());
        itemEntity.setCategoryId(data.getCatalogId());


        //3.商品的sku信息
        itemEntity.setSkuId(cartItem.getSkuId());
        itemEntity.setSkuName(cartItem.getTitle());
        itemEntity.setSkuPic(cartItem.getImage());
        itemEntity.setSkuPrice(cartItem.getPrice());
        String skuAttr = StringUtils.collectionToDelimitedString(cartItem.getSkuAttrValues(),"1");
        itemEntity.setSkuAttrsVals(skuAttr);
        itemEntity.setSkuQuantity(cartItem.getCount());
        //4.积分信息

        itemEntity.setGiftGrowth(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());
        itemEntity.setGiftIntegration(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());

        //6.订单项的价格信息
        itemEntity.setPromotionAmount(new BigDecimal("0.0"));
        itemEntity.setCouponAmount(new BigDecimal("0.0"));
        itemEntity.setIntegrationAmount(new BigDecimal("0.0"));
        //当前订单项的实际金额  减去优惠金额的最后金额
        BigDecimal orgin = itemEntity.getSkuPrice().multiply(new BigDecimal(itemEntity.getSkuQuantity().toString()));
        BigDecimal subtract = orgin.subtract(itemEntity.getCouponAmount())
                .subtract(itemEntity.getPromotionAmount())
                .subtract(itemEntity.getIntegrationAmount());

        itemEntity.setRealAmount(subtract);


        return itemEntity;

    }

}