package com.shop.common.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpuBoundsTo {

    private Long SpuId;

    private BigDecimal buyBounds;

    private BigDecimal growBounds;

}
