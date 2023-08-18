package com.shop.product.dao;

import com.shop.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author ziyang
 * @email czy200205@qq.com
 * @date 2023-08-18 16:03:49
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
