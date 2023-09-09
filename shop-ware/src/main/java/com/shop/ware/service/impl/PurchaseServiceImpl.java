package com.shop.ware.service.impl;

import com.shop.common.constant.WareConstant;
import com.shop.ware.entity.PurchaseDetailEntity;
import com.shop.ware.service.PurchaseDetailService;
import com.shop.ware.service.WareSkuService;
import com.shop.ware.vo.MergeVo;
import com.shop.ware.vo.PurchaseDoneVo;
import com.shop.ware.vo.PurchaseItemDoneVo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.common.utils.PageUtils;
import com.shop.common.utils.Query;

import com.shop.ware.dao.PurchaseDao;
import com.shop.ware.entity.PurchaseEntity;
import com.shop.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Resource
    private PurchaseDetailService purchaseDetailService;

    @Resource
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceivePurchase(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", WareConstant.PurchaseStautsEnum.CREATED.getCode())
                        .or().eq("status", WareConstant.PurchaseStautsEnum.ASSIGNED.getCode())
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void mergePurchase(MergeVo vo) {
        Long purchaseId = vo.getPurchaseId();
        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStautsEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());;
            purchaseEntity.setUpdateTime(new Date());
            save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }

        //TODO 确认采购单状态是 0 或 1

        List<Long> items = vo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> detailEntities = items.stream().map(i -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(i);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatussEnum.ASSIGNED.getCode());

            return purchaseDetailEntity;
        }).collect(Collectors.toList());

        purchaseDetailService.updateBatchById(detailEntities);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        updateById(purchaseEntity);
    }

    @Override
    public void received(List<Long> ids) {
        List<PurchaseEntity> entities = ids.stream()
                .map(this::getById)
                .filter(Objects::nonNull)
                .filter(item -> item.getStatus() == WareConstant.PurchaseStautsEnum.CREATED.getCode() ||
                        item.getStatus() == WareConstant.PurchaseStautsEnum.ASSIGNED.getCode())
                .peek(item -> item.setStatus(WareConstant.PurchaseStautsEnum.RECEIVE.getCode()))
                .collect(Collectors.toList());

        updateBatchById(entities);

        entities.forEach(item -> {
            List<PurchaseDetailEntity> list = purchaseDetailService.listDetailByPurchaseId(item.getId());
            List<PurchaseDetailEntity> detailEntities = list.stream()
                    .peek(entity -> entity.setStatus(WareConstant.PurchaseDetailStatussEnum.BUYING.getCode()))
                    .collect(Collectors.toList());
            purchaseDetailService.updateBatchById(detailEntities);
        });


    }

    @Override
    @Transactional
    public void done(PurchaseDoneVo vo) {
        // 1、改变采购单状态
        Long purchaseId = vo.getId();


        // 2、改变采购项的状态
        boolean flag = true;
        List<PurchaseItemDoneVo> items = vo.getItems();

        List<PurchaseDetailEntity> updates = new ArrayList<>();
        for (PurchaseItemDoneVo item : items) {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            if (item.getStatus() == WareConstant.PurchaseDetailStatussEnum.HASERROR.getCode()) {
                flag = false;
                purchaseDetailEntity.setStatus(item.getStatus());
            } else {
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatussEnum.FINISH.getCode());
                // 3、将成功采购的进行入库
                Long itemId = item.getItemId();
                PurchaseDetailEntity detailEntity = purchaseDetailService.getById(itemId);

                wareSkuService.addStock(detailEntity.getSkuId(), detailEntity.getWareId(), detailEntity.getSkuNum());
            }
            purchaseDetailEntity.setId(item.getItemId());
            updates.add(purchaseDetailEntity);
        }
        purchaseDetailService.updateBatchById(updates);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setStatus(flag ? WareConstant.PurchaseStautsEnum.FINISH.getCode() : WareConstant.PurchaseStautsEnum.HASERROR.getCode());
        updateById(purchaseEntity);



    }

}