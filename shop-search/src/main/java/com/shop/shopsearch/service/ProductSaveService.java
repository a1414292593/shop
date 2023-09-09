package com.shop.shopsearch.service;

import com.shop.common.to.SkuEsModel;
import java.io.IOException;
import java.util.List;

public interface ProductSaveService {
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
