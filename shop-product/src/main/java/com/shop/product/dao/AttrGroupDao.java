package com.shop.product.dao;

import com.shop.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.product.vo.SpuItemAttrGroupVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 属性分组
 * 
 * @author ziyang
 * @email czy200205@qq.com
 * @date 2023-08-18 16:03:49
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long skuId, Long catalogId);
}
