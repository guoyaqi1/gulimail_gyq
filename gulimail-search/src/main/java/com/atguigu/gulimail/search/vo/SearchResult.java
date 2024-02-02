package com.atguigu.gulimail.search.vo;

import com.atguigu.gulimail.common.es.SkuEsModel;
import lombok.Data;

import java.util.List;

/**
 * @Author:guoyaqi
 * @Date: 2023/8/24 20:12
 */
@Data
public class SearchResult {

    private List<SkuEsModel> products;

    private Integer pageNume;
    private Long total;
    private Integer totalPages;

    private List<BrandVo> brands;
    private List<CatalogVo> catalogs;
    private List<AttrVo> attrs;

    public void setProduct(List<SkuEsModel> esModels) {

    }

    /* 面包屑导航数据 */
    private List<NavVo> navs;

    @Data
    public static class NavVo {
        private String navName;
        private String navValue;
        private String link;
    }

    //===========================以上是返回给页面的所有信息============================//


    @Data
    public static class BrandVo {

        private Long brandId;

        private String brandName;

        private String brandImg;
    }


    @Data
    public static class AttrVo {

        private Long attrId;

        private String attrName;

        private List<String> attrValue;
    }


    @Data
    public static class CatalogVo {

        private Long catalogId;

        private String catalogName;
    }


}
