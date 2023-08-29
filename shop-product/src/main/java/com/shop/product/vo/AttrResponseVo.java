package com.shop.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class AttrResponseVo extends AttrVo {

    /**
     * 分类名
     */
    private String catelogName;

    /**
     * 分组名
     */
    private String groupName;

    private Long[] catelogPath;

}
