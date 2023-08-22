package com.shop.order.dao;

import com.shop.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author ziyang
 * @email czy200205@qq.com
 * @date 2023-08-22 16:09:43
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
