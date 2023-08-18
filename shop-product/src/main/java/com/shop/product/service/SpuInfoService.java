package com.shop.product.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.shop.common.utils.PageUtils;
import com.shop.product.entity.SpuInfoEntity;

import java.util.Map;

/**
 * spu信息
 *
 * @author ziyang
 * @email czy200205@qq.com
 * @date 2023-08-18 16:03:49
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

