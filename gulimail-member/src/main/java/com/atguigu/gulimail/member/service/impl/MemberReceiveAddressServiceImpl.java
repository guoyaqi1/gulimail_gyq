package com.atguigu.gulimail.member.service.impl;

import com.atguigu.gulimail.member.dao.MemberReceiveAddressDao;
import com.atguigu.gulimail.member.entity.MemberReceiveAddressEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimail.common.utils.PageUtils;
import com.atguigu.gulimail.common.utils.Query;

import com.atguigu.gulimail.member.service.MemberReceiveAddressService;


@Service("memberReceiveAddressService")
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressDao, MemberReceiveAddressEntity> implements MemberReceiveAddressService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberReceiveAddressEntity> page = this.page(
                new Query<MemberReceiveAddressEntity>().getPage(params),
                new QueryWrapper<MemberReceiveAddressEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 获取用户的收货地址列表
     * @param memberId
     * @return
     */
    @Override
    public List<MemberReceiveAddressEntity> getAddress(Long memberId) {
        return this.list(new QueryWrapper<MemberReceiveAddressEntity>().eq("member_id", memberId));

    }

}