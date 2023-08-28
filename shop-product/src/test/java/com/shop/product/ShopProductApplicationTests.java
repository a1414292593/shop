package com.shop.product;

import com.shop.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;

@Slf4j
@SpringBootTest
class ShopProductApplicationTests {

    @Resource
    private CategoryService categoryService;

    @Test
    void contextLoads() {
        Long[] catelogPath = categoryService.findCatelogPath(225L);
        log.info("完整路径: {}", Arrays.asList(catelogPath));
    }

}
