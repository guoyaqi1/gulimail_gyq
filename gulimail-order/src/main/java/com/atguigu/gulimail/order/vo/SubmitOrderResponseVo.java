package com.atguigu.gulimail.order.vo;

import com.atguigu.gulimail.order.entity.OrderEntity;
import lombok.Data;

/**
 * @Author:guoyaqi
 * @Date: 2023/11/13 0:47
 */
@Data
public class SubmitOrderResponseVo {

        private OrderEntity order;
        private Integer code;


}
