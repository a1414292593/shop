package com.shop.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shop.common.utils.PageUtils;
import com.shop.member.entity.MemberEntity;
import com.shop.member.vo.MemberLoginVo;
import com.shop.member.vo.MemberRegisterVo;

import java.util.Map;

/**
 * 会员
 *
 * @author ziyang
 * @email czy200205@qq.com
 * @date 2023-08-22 16:05:06
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(MemberRegisterVo vo);

    void checkPhoneUnique(String phone);

    void checkUsernameUnique(String username);

    MemberEntity login(MemberLoginVo vo);
}

