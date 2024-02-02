package com.atguigu.gulimail.search.service;

import com.atguigu.gulimail.common.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @Author:guoyaqi
 * @Date: 2023/8/21 1:05
 */
public interface ProductSaveService {
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
