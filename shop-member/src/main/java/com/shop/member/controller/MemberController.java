package com.shop.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.shop.common.exception.BizCodeEnum;
import com.shop.member.exception.PhoneExistException;
import com.shop.member.exception.UsernameExistException;
import com.shop.member.feign.CouponFeignService;
import com.shop.member.vo.MemberLoginVo;
import com.shop.member.vo.MemberRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.shop.member.entity.MemberEntity;
import com.shop.member.service.MemberService;
import com.shop.common.utils.PageUtils;
import com.shop.common.utils.R;

import javax.annotation.Resource;


/**
 * 会员
 *
 * @author ziyang
 * @email czy200205@qq.com
 * @date 2023-08-22 16:05:06
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Resource
    private CouponFeignService couponFeignService;

    @RequestMapping("/coupons")
    public R test() {
        MemberEntity member = new MemberEntity();
        member.setNickname("张三");
        R coupons = couponFeignService.memberCoupons();
        return R.ok().put("memeber", member).put("coupons", coupons.get("coupons"));
    }

    @PostMapping("/regist")
    public R register(@RequestBody MemberRegisterVo vo) {
        try {
            memberService.regist(vo);
        } catch (PhoneExistException e) {
            return R.error(BizCodeEnum.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnum.PHONE_EXIST_EXCEPTION.getMsg());
        } catch (UsernameExistException e) {
            return R.error(BizCodeEnum.USER_EXIST_EXCEPTION.getCode(), BizCodeEnum.USER_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo vo) {
        MemberEntity member = memberService.login(vo);
        if (member == null) {
            return R.error(BizCodeEnum.LOGINACCT_PASSWORD_INVALID_EXCEPTION.getCode(),
                    BizCodeEnum.LOGINACCT_PASSWORD_INVALID_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
