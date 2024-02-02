package com.atguigu.gulimail.coupon.service.impl;

import com.atguigu.gulimail.coupon.entity.SeckillSkuRelationEntity;
import com.atguigu.gulimail.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimail.common.utils.PageUtils;
import com.atguigu.gulimail.common.utils.Query;

import com.atguigu.gulimail.coupon.dao.SeckillSessionDao;
import com.atguigu.gulimail.coupon.entity.SeckillSessionEntity;
import com.atguigu.gulimail.coupon.service.SeckillSessionService;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Autowired
    SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    public List<SeckillSessionEntity> getLast3DaySession() {
        //计算近三天的秒杀商品
        List<SeckillSessionEntity> list = this.list(new QueryWrapper<SeckillSessionEntity>().between("startTime",startTime(),endTime()));

        if(list!=null&&list.size()>0){
            list.stream().map(session->{
                Long id = session.getId();
                List<SeckillSkuRelationEntity> relationEntities = seckillSkuRelationService.list(new QueryWrapper<SeckillSkuRelationEntity>().eq("promotion_session_id",id));

                return session;

            }).collect(Collectors.toList());
        }
        return list;
    }

    private String startTime(){
        LocalDate now = LocalDate.now();
        LocalDateTime min = LocalDateTime.MIN;
        LocalDateTime start = LocalDateTime.of(now, LocalTime.from(min));

        String format = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return format;
    }

    private String endTime(){
        LocalDate now = LocalDate.now();
        LocalDate localDate = now.plusDays(2);
        LocalDateTime of = LocalDateTime.of(localDate, LocalTime.MAX);
        String format = of.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return format;
    }

}