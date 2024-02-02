package com.atguigu.gulimail.ware.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.Data;

/**
 * 库存工作单
 * 
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-08 09:59:40
 */
@Data
@TableName("wms_ware_order_task_detail")
public class WareOrderTaskDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * sku_id
	 */
	private Long skuId;
	/**
	 * sku_name
	 */
	private String skuName;
	/**
	 * 购买个数
	 */
	private Integer skuNum;
	/**
	 * 工作单id
	 */
	private Long taskId;
	/**
	 * 库存id
	 */
	private Long wareId;
	/**
	 * 锁定状态
	 */
	private Integer lockStatus;

	public WareOrderTaskDetailEntity(Long id, Long skuId, String skuName, Integer skuNum, Long taskId, Long wareId, Integer lockStatus) {
	}

	public WareOrderTaskDetailEntity() {

	}
}
