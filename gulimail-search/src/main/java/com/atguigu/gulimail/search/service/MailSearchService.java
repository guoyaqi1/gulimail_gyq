package com.atguigu.gulimail.search.service;

import com.atguigu.gulimail.search.vo.SearchParam;
import com.atguigu.gulimail.search.vo.SearchResult;

/**
 * @Author:guoyaqi
 * @Date: 2023/8/24 19:48
 */
public interface MailSearchService {
    SearchResult search(SearchParam param);
}
