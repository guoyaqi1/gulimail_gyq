package com.atguigu.gulimail.search.thread;

import java.util.concurrent.*;

/**
 * @Author:guoyaqi
 * @Date: 2023/8/25 20:06
 */
public class ThreadTest {

    //使用线程池
    public static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException,InterruptedException {

        /*  //写法1  .runAsync
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            //异步任务的内容
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }, executor);*/

        /*CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            //异步任务的内容
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            return i;
        }, executor).whenComplete((res,exception)->{
            System.out.println("异步任务成功完成了...结果是："+res+"；异常是："+exception);
        }).exceptionally(throwable -> {
            return 10;
        });

        Integer integer = future.get();
        System.out.println("main....end...."+integer);
        //future.get()可以获取到结果*/


        /**
         * 组合任务-两个都要完成
         */
        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
            //异步任务的内容
            System.out.println("任务1线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("任务1线程结束：" + i);
            return i;
        }, executor);

        CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2线程："  + Thread.currentThread().getId());
            System.out.println("任务2线程结束"  );
            return "Hello";
        }, executor);

        /*组合两个future  不需要获取future结果**
        future01.runAfterBothAsync(future02,()->{
            System.out.println("任务3开始...");
        },executor);*/

        /*
        future01.thenAcceptBothAsync(future02,(f1,f2)->{
            System.out.println("任务3开始...之前的结果："+f1+"---》"+f2);
        },executor);
        */

        CompletableFuture<String> future = future01.thenCombineAsync(future02, (f1, f2) -> {

            return f1 + ":" + f2;
        }, executor);


        System.out.println("main....end....");
        //future.get()可以获取到结果



        //service.execute(new Runable01());

    }

    private static void threadPool() {

        ExecutorService threadPool = new ThreadPoolExecutor(
                200,
                10,
                10L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>(10000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );

        //定时任务的线程池
        ExecutorService service = Executors.newScheduledThreadPool(2);
    }

    //1.继承thread
    public static class Thread01 extends Thread {
        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }
    }

    public static class Runable01 implements Runnable{

        @Override
        public void run() {
            System.out.println("当前线程："+Thread.currentThread().getId());
            int i = 10/2;
            System.out.println("运行结果："+i);
        }
    }

    public static class Callable01 implements Callable<Integer>{

        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程："+Thread.currentThread().getId());
            int i = 10/2;
            System.out.println("运行结果："+i);
            return i;
        }
    }
}
