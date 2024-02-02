package com.atguigu.gulimail.ware.service;

import com.atguigu.gulimail.common.to.mq.OrderTo;
import com.atguigu.gulimail.common.to.mq.StockLockedTo;
import com.atguigu.gulimail.ware.vo.LockStockResult;
import com.atguigu.gulimail.ware.vo.SkuHasStockVo;
import com.atguigu.gulimail.ware.vo.WareSkuLockVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimail.common.utils.PageUtils;
import com.atguigu.gulimail.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-08 09:59:40
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);


    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);

    Boolean orderLockStock(WareSkuLockVo vo);

    void unlockStock(StockLockedTo to);



    void unlockStock(OrderTo orderTo);
}

