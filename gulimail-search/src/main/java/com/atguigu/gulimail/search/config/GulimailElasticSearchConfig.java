package com.atguigu.gulimail.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author:guoyaqi
 * @Date: 2023/8/19 14:42
 * 1.导入依赖
 * 2.编写配置
 */
@Configuration
public class GulimailElasticSearchConfig {

    public static final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        // builder.addHeader("Authorization", "Bearer " + TOKEN);
        // builder.setHttpAsyncResponseConsumerFactory(
        //         new HttpAsyncResponseConsumerFactory
        //                 .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

    @Bean
    public RestHighLevelClient esRestClient(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("192.168.80.128", 9200, "http")));
        return  client;
    }

    /*@Bean
    public RestHighLevelClient esRestClient(){
        *//*RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost",9200,"http")
                        //docker中linux的es
                        //new HttpHost("192.168.80.128",9200,"http")
                        ));*//*
        RestClientBuilder builder =null;
        builder =RestClient.builder(new HttpHost("localhost",9200,"http"));
        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;

    }*/
}
