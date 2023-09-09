package com.shop.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.shop.common.utils.Query;
import com.shop.common.utils.PageUtils;
import com.shop.product.service.CategoryBrandRelationService;
import com.shop.product.vo.Catelog2Vo;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;



import com.shop.product.dao.CategoryDao;
import com.shop.product.entity.CategoryEntity;
import com.shop.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Resource
    CategoryBrandRelationService categoryBrandRelationService;

    @Resource
    StringRedisTemplate redisTemplate;

    @Resource
    RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        // 1. 查出所有分类
        List<CategoryEntity> list = list();
        // 2. 组装父子的树形结构
        // 2.1 找到所有的一级分类
        return list.stream()
                .filter(category -> category.getCatLevel() == 1)
                .peek(menu -> menu.setChildren(getChildrens(menu, list)))
                .sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
                .collect(Collectors.toList());
    }

    @Override
    public void removeMenuByIds(List<Long> list) {
        //TODO 检查当前删除的菜单，是否被别的地方引用
        baseMapper.deleteBatchIds(list);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        finParentPath(catelogId, paths);
        return paths.toArray(new Long[0]);
    }

    @CacheEvict(value = "category", allEntries = true)
    @Override
    @Transactional
    public void updateCascade(CategoryEntity category) {
        updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    @Cacheable(value = {"category"}, key = "#root.method.name")
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

//    @Cacheable(value = {"category"}, key = "#root.method.name")
//    @Override
//    public Map<String, List<Catelog2Vo>> getCatalogJson() {
//        List<CategoryEntity> selectList = baseMapper.selectList(null);
//
//        List<CategoryEntity> level1Categorys = getParentCid(selectList, 0L);
//
//        return level1Categorys.stream().collect(Collectors.toMap(item -> item.getCatId().toString(), v -> {
//            List<CategoryEntity> categoryEntities = getParentCid(selectList, v.getCatId());
//            List<Catelog2Vo> catelog2Vos = null;
//            if (categoryEntities != null) {
//                catelog2Vos = categoryEntities.stream().map(l2 -> {
//                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
//                    List<CategoryEntity> level3Catalog = getParentCid(selectList, l2.getCatId());
//                    if (level3Catalog != null) {
//                        List<Catelog2Vo.Catelog3Vo> catelog3Vos = level3Catalog.stream()
//                                .map(l3 -> new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName()))
//                                .collect(Collectors.toList());
//                        catelog2Vo.setCatalog3List(catelog3Vos);
//                    }
//
//                    return catelog2Vo;
//                }).collect(Collectors.toList());
//            }
//            return catelog2Vos;
//        }));
//    }

    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {

        String catalogJson = redisTemplate.opsForValue().get("catalogJson");

        if (!StringUtils.isEmpty(catalogJson)) {
            return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>(){});
        }

        return getCatalogJsonByDBWithRedisLock();
    }

    private Map<String, List<Catelog2Vo>> getCatalogJsonByDBWithRedisLock() {
        RLock lock = redissonClient.getLock("catalogJson-lock");
        lock.lock();

        Map<String, List<Catelog2Vo>> catalogJsonByDB;
        try {
            String catalogJson = redisTemplate.opsForValue().get("catalogJson");
            if (!StringUtils.isEmpty(catalogJson)) {
                return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>(){});
            }
            catalogJsonByDB = getCatalogJsonByDB();
        } finally {
            lock.unlock();
        }

        return catalogJsonByDB;
    }

    private Map<String, List<Catelog2Vo>> getCatalogJsonByDB() {
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        List<CategoryEntity> level1Categorys = getParentCid(selectList, 0L);

        Map<String, List<Catelog2Vo>> collect = level1Categorys.stream().collect(Collectors.toMap(item -> item.getCatId().toString(), v -> {
            List<CategoryEntity> categoryEntities = getParentCid(selectList, v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    List<CategoryEntity> level3Catalog = getParentCid(selectList, l2.getCatId());
                    if (level3Catalog != null) {
                        List<Catelog2Vo.Catelog3Vo> catelog3Vos = level3Catalog.stream()
                                .map(l3 -> new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName()))
                                .collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(catelog3Vos);
                    }

                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        redisTemplate.opsForValue().set("catalogJson", JSON.toJSONString(collect), 1, TimeUnit.DAYS);
        return collect;
    }

    private List<CategoryEntity> getParentCid(List<CategoryEntity> selectList, Long parent_cid) {
        return selectList.stream()
                .filter(item -> Objects.equals(item.getParentCid(), parent_cid))
                .collect(Collectors.toList());
    }

    private void finParentPath(Long catelogId, List<Long> paths) {
        CategoryEntity byId = getById(catelogId);
        if (byId.getParentCid() != 0) {
            finParentPath(byId.getParentCid(), paths);
        }
        paths.add(catelogId);
    }

    // 递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        return all.stream()
                .filter(category -> Objects.equals(category.getParentCid(), root.getCatId()))
                .peek(category -> category.setChildren(getChildrens(category, all)))
                .sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
                .collect(Collectors.toList());
    }

}