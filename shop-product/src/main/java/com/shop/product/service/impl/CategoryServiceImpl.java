package com.shop.product.service.impl;

import com.shop.common.utils.Query;
import com.shop.common.utils.PageUtils;
import com.shop.product.service.CategoryBrandRelationService;
import org.springframework.stereotype.Service;

import java.util.*;
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

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
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

    @Override
    @Transactional
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    private void finParentPath(Long catelogId, List<Long> paths) {
        CategoryEntity byId = this.getById(catelogId);
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