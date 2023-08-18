package com.shop.product;

import com.shop.product.entity.BrandEntity;
import com.shop.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class ShopProductApplicationTests {

    @Resource
    BrandService brandService;

    @Test
    void contextLoads() {
        List<BrandEntity> list = brandService.list();
        for (BrandEntity brandEntity : list) {
            System.out.println(brandEntity.getName());
        }
    }

}
