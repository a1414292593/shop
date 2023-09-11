package com.shop.thirdparty.controller;

import com.shop.common.utils.R;
import com.shop.thirdparty.config.SmsComponent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
@RequestMapping("/sms")
public class MallController {

    @Resource
    private SmsComponent smsComponent;

    @GetMapping("/sendcode")
    public R sendCode(@RequestParam("email") String email, @RequestParam("code") String code) {
        smsComponent.sendCode(email, code);
        return R.ok();
    }

}
