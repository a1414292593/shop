package com.shop.authserver.feign;

import com.shop.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("shop-third-party")
public interface ThirdPartFeignService {

    @GetMapping("/sms/sendcode")
    R sendCode(@RequestParam("email") String email, @RequestParam("code") String code);

}
