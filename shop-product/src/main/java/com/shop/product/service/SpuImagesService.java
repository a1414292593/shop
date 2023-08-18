package com.shop.product.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.shop.common.utils.PageUtils;
import com.shop.product.entity.SpuImagesEntity;

import java.util.Map;

/**
 * spu图片
 *
 * @author ziyang
 * @email czy200205@qq.com
 * @date 2023-08-18 16:03:49
 */
public interface SpuImagesService extends IService<SpuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

