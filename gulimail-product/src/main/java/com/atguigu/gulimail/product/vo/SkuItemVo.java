package com.atguigu.gulimail.product.vo;

import com.atguigu.gulimail.product.entity.SkuImagesEntity;
import com.atguigu.gulimail.product.entity.SkuInfoEntity;
import com.atguigu.gulimail.product.entity.SpuInfoDescEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Author:guoyaqi
 * @Date: 2023/8/26 21:05
 */
@Data
public class SkuItemVo {
    //sku基本信息获取  pms_sku_info
    SkuInfoEntity info;

    boolean hasStock =true;

    //2.sku图片信息  pms_sku_images

    List<SkuImagesEntity> images;

    //3.sku的销售属性组合

    private List<SkuItemSaleAttrVo> saleAttr;

    //4.获取spu的介绍

    SpuInfoDescEntity desp;

    //5.获取spu的规格参数信息
    List<SpuItemAttrGroupVo> groupAttrs;

    //当前商品的秒杀优惠信息
    SeckillSkuVo seckillSkuVo;


}
