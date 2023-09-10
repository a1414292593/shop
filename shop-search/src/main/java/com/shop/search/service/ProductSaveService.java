package com.shop.search.service;

import com.shop.common.to.SkuEsModel;
import java.io.IOException;
import java.util.List;

public interface ProductSaveService {
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
