package com.atguigu.gulimail.product.web;

import com.atguigu.gulimail.product.entity.CategoryEntity;
import com.atguigu.gulimail.product.service.CategoryService;
import com.atguigu.gulimail.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author:guoyaqi
 * @Date: 2023/8/21 15:04
 */
@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;

    @Autowired
    RedissonClient redisson;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping({"/","/index.html"})
    public String indexPage(Model model){
        //TODO 1.查出所有的1级分类
        List<CategoryEntity> categoryEntities =  categoryService.getLevel1Categorys();

        //视图解析器进行拼串
        model.addAttribute("categorys",categoryEntities);
        return "index";
    }

    //index/catalog.json
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String,List<Catelog2Vo>> getCatelogJson(){

        Map<String,List<Catelog2Vo>> catelogJson =  categoryService.getCatelogJson();
        return catelogJson;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        //1.获取锁 同一把锁 名字一样
        RLock lock = redisson.getLock("my-lock");

        //2.加锁
        lock.lock(30, TimeUnit.SECONDS); //阻塞等待

        try{
            System.out.println("加锁成功，执行业务......"+Thread.currentThread().getId());
            Thread.sleep(30000);

        }catch (Exception e){

        }finally {
            //3.解锁
            System.out.println("释放锁..."+Thread.currentThread().getId());
            lock.unlock();
        }
      return "hello";
    }


    @ResponseBody
    @GetMapping("/write")
    public String writeValue(){
        RReadWriteLock lock = redisson.getReadWriteLock("rw-lock");
        String s ="";
        RLock rLock = lock.writeLock();
        try {
            rLock.lock();
            System.out.println("写锁加锁成功..."+Thread.currentThread().getId());
            s= UUID.randomUUID().toString();
            Thread.sleep(30000);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
           rLock.unlock();
            System.out.println("写锁释放成功"+Thread.currentThread().getId());
        }
        return s;
    }

    @ResponseBody
    @GetMapping("/read")
    public String readValue(){
        RReadWriteLock lock = redisson.getReadWriteLock("rw-lock");
        String s ="";
        RLock rLock = lock.readLock();
        try {
            rLock.lock();
            System.out.println("读锁加锁成功..."+Thread.currentThread().getId());

            s= stringRedisTemplate.opsForValue().get("writeValue");
            Thread.sleep(30000);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            rLock.unlock();
            System.out.println("读锁释放成功"+Thread.currentThread().getId());
        }
        return s;
    }

}



