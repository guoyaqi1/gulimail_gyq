package com.atguigu.gulimail.seckill.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author:guoyaqi
 * @Date: 2023/12/19 23:25
 */
@Data
public class SeckillSessionsWithSkus {

    private Long id;
    /**
     * 场次名称
     */
    private String name;
    /**
     * 每日开始时间
     */
    private Date startTime;

    private Integer status;

    private Date endTime;

    private Date createTime;

    private List<SeckillSkuVo> relationSkus;
}
