package com.shop.coupon.dao;

import com.shop.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author ziyang
 * @email czy200205@qq.com
 * @date 2023-08-22 16:00:11
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
