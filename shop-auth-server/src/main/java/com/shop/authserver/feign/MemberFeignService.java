package com.shop.authserver.feign;

import com.shop.authserver.vo.UserLoginVo;
import com.shop.authserver.vo.UserRegisterVo;
import com.shop.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("shop-member")
public interface MemberFeignService {

    @PostMapping("/member/member/regist")
    R register(@RequestBody UserRegisterVo vo);

    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo vo);

}
