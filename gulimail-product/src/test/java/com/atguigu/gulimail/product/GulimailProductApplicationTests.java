package com.atguigu.gulimail.product;

import com.atguigu.gulimail.product.entity.BrandEntity;
import com.atguigu.gulimail.product.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GulimailProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Test
    void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();

//        brandEntity.setName("华为");
//        brandService.save(brandEntity);
//        System.out.println("输出成功");


        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", "1L"));
        list.forEach((item)->{
            System.out.println(item);
        });

    }

}
