package com.shop.product.service.impl;

import com.shop.common.utils.PageUtils;
import com.shop.common.utils.Query;
import com.shop.product.entity.AttrEntity;
import com.shop.product.service.AttrService;
import com.shop.product.vo.AttrGroupWithAttrsVo;
import com.shop.product.vo.SpuItemAttrGroupVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;



import com.shop.product.dao.AttrGroupDao;
import com.shop.product.entity.AttrGroupEntity;
import com.shop.product.service.AttrGroupService;

import javax.annotation.Resource;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Resource
    AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long cateLogId) {
        String key = (String) params.get("key");
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(obj -> {
                obj.eq("attr_group_id", key).or().like("attr_group_name", key);
            });
        }
        if (cateLogId != 0) {
            wrapper.eq("catelog_id", cateLogId);
        }
        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

    /**
     * 根据分类Id查出所有的分组以及这些组里的信息
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        List<AttrGroupEntity> attrGroupEntities = list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        return attrGroupEntities.stream().map(group -> {
                AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
                BeanUtils.copyProperties(group, attrGroupWithAttrsVo);
                List<AttrEntity> attrEntities = attrService.getRelationAttr(attrGroupWithAttrsVo.getAttrGroupId());
                attrGroupWithAttrsVo.setAttrs(attrEntities);
                return attrGroupWithAttrsVo;
                }).collect(Collectors.toList());
    }

    @Override
    public List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
        return baseMapper.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
    }

}