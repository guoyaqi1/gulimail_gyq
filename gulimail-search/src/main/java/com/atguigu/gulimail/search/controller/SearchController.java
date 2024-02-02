package com.atguigu.gulimail.search.controller;

import com.atguigu.gulimail.search.service.MailSearchService;
import com.atguigu.gulimail.search.vo.SearchParam;
import com.atguigu.gulimail.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author:guoyaqi
 * @Date: 2023/8/24 17:25
 */
@Controller
public class SearchController {

    @Autowired
    MailSearchService mailSearchService;

    /**
     * 自动将页面提交过来的所有请求查询参数封装成指定的对象
     * @param param
     * @return
     */
    @GetMapping("/list.html")
    public String listPage(SearchParam param, Model model){

        SearchResult result = mailSearchService.search(param);
        model.addAttribute("result",result);
        return "list";

    }
}
