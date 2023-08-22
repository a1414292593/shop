package com.shop.ware.dao;

import com.shop.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author ziyang
 * @email czy200205@qq.com
 * @date 2023-08-22 16:11:53
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
