package com.shop.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shop.common.utils.PageUtils;
import com.shop.ware.entity.PurchaseEntity;
import com.shop.ware.vo.MergeVo;
import com.shop.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author ziyang
 * @email czy200205@qq.com
 * @date 2023-08-22 16:11:53
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceivePurchase(Map<String, Object> params);

    void mergePurchase(MergeVo vo);

    void received(List<Long> ids);

    void done(PurchaseDoneVo vos);
}

