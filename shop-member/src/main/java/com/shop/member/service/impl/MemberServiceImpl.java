package com.shop.member.service.impl;

import com.shop.member.dao.MemberLevelDao;
import com.shop.member.entity.MemberLevelEntity;
import com.shop.member.exception.PhoneExistException;
import com.shop.member.exception.UsernameExistException;
import com.shop.member.vo.MemberLoginVo;
import com.shop.member.vo.MemberRegisterVo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.common.utils.PageUtils;
import com.shop.common.utils.Query;
import com.shop.member.dao.MemberDao;
import com.shop.member.entity.MemberEntity;
import com.shop.member.service.MemberService;
import javax.annotation.Resource;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Resource
    private MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(MemberRegisterVo vo) {
        MemberEntity member = new MemberEntity();
        //设置默认等级
        MemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultLevel();
        member.setLevelId(memberLevelEntity.getId());
        //检查用户名和手机号是否唯一
        checkUsernameUnique(vo.getUserName());
        checkPhoneUnique(vo.getPhone());
        member.setMobile(vo.getPhone());
        member.setUsername(vo.getUserName());

        member.setNickname(vo.getUserName());
        //密码加密存储
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(vo.getPassword());
        member.setPassword(encode);
        //其他的默认信息


        baseMapper.insert(member);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (count > 0) {
            throw new PhoneExistException();
        }
    }

    @Override
    public void checkUsernameUnique(String username) throws UsernameExistException {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if (count > 0) {
            throw new PhoneExistException();
        }
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String loginacct = vo.getLoginacct();
        MemberEntity member = baseMapper
                .selectOne(new QueryWrapper<MemberEntity>().eq("username", loginacct).or().eq("mobile", loginacct));
        if (member == null) {
            return null;
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(vo.getPassword(), member.getPassword())) {
            return null;
        }
        return member;
    }

}