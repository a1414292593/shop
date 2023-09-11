package com.shop.product.service.impl;

import com.shop.product.entity.SpuInfoDescEntity;
import com.shop.product.service.*;
import com.shop.product.vo.SkuItemVo;
import com.shop.product.vo.SpuItemAttrGroupVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.common.utils.PageUtils;
import com.shop.common.utils.Query;
import com.shop.product.dao.SkuInfoDao;
import com.shop.product.entity.SkuInfoEntity;

import javax.annotation.Resource;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Resource
    private SkuImagesService skuImagesService;

    @Resource
    private SpuInfoDescService spuInfoDescService;

    @Resource
    private AttrGroupService attrGroupService;

    @Resource
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Resource
    private ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(w -> w.eq("sku_id", key).or().like("sku_name", key));
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && "0".equalsIgnoreCase(catelogId)) {
            wrapper.eq("catelog_id", catelogId);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && "0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }

        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)) {
            wrapper.ge("price", min);
        }

        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max)) {
            BigDecimal decimal = new BigDecimal(max);
            if (decimal.compareTo(new BigDecimal("0")) > 0) {
                wrapper.le("price", max);
            }
        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        return list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
    }

    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();

        //1、sku基本信息获取
        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfoEntity skuInfoEntity = getById(skuId);
            skuItemVo.setInfo(skuInfoEntity);
            return skuInfoEntity;
        }, executor);
        CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            //3、获取spu的销售属性组合
            skuItemVo.setSaleAttr(skuSaleAttrValueService.getSaleAttrsBySpuId(res.getSpuId()));
        }, executor);
        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((res) -> {
            //4、获取spu的介绍
            skuItemVo.setDesc(spuInfoDescService.getById(res.getSpuId()));
        }, executor);
        CompletableFuture<Void> groupAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            //5、spu的规格参数属性
            skuItemVo.setGroupAttrs(attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatalogId()));
        }, executor);
        CompletableFuture<Void> spuInfoFuture = infoFuture.thenAcceptAsync((res) -> {
            //4、获取spu的介绍
            skuItemVo.setDesc(spuInfoDescService.getById(res.getSpuId()));
        }, executor);

        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            //2、sku的图片信息
            skuItemVo.setImages(skuImagesService.getImagesBySkuId(skuId));
        }, executor);

        CompletableFuture.allOf(saleAttrFuture, descFuture, groupAttrFuture, spuInfoFuture, imageFuture).get();
        return skuItemVo;
    }

}