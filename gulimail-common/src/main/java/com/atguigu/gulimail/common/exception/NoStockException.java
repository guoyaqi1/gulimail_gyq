package com.atguigu.gulimail.common.exception;

/**
 * @Author:guoyaqi
 * @Date: 2023/11/16 23:47
 */
public class NoStockException extends RuntimeException{
    private Long skuId;
    public NoStockException(Long skuId){
        super("商品id:"+skuId+";没有足够的库存了");
    }

    public Long getSkuId(){
        return skuId;
    }

    public void setSkuId(Long skuId){
        this.skuId=skuId;
    }
}
