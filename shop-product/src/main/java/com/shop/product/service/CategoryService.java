package com.shop.product.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.shop.common.utils.PageUtils;
import com.shop.product.entity.CategoryEntity;

import java.util.Map;

/**
 * 商品三级分类
 *
 * @author ziyang
 * @email czy200205@qq.com
 * @date 2023-08-18 16:03:49
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

