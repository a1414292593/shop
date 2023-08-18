package com.shop.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.shop.product.dao")
public class ShopProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopProductApplication.class, args);
    }

}
