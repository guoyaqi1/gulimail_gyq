package com.atguigu.gulimail.product.web;

import com.atguigu.gulimail.product.service.SkuInfoService;
import com.atguigu.gulimail.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

/**
 * @Author:guoyaqi
 * @Date: 2023/8/26 20:53
 */
@Controller
public class ItemController {

    @Autowired
    SkuInfoService skuInfoService;

    /**
     * 展示当前的sku的详情
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
        System.out.println("准备查询详情"+skuId);
        SkuItemVo vo =skuInfoService.item(skuId);
        model.addAttribute("item",vo);

        return "item";
    }
}
