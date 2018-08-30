package org.neal.cn.redistest.queueandasync.listener;

import org.neal.cn.redistest.queueandasync.async.DeferredResultHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

/**
 * 设置消息队列监听
 * ContextRefreshedEvent spring 在初始化完毕后的事件
 * @author Neal
 */
@Component
public class QueueListener implements ApplicationListener<ContextRefreshedEvent> {

    //日志
    private Logger logger = LoggerFactory.getLogger(getClass());

    //redis完成队列KEY
    private static String REDIS_COMPLATE = "complete";


    @Autowired
    private RedisTemplate redisTemplate;

    //DeferredResult管理类
    @Autowired
    private DeferredResultHolder deferredResultHolder;



    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent){
        //由于处理队列方法是一个无限循环，需要单起一个线程，防止阻塞系统启动
        new Thread(()->{
            while(true) {
                logger.info("读取消息队列完成订单 ");
                //从完成的队列中按顺序取出完成的任务ID
                Object uuid = redisTemplate.opsForList().rightPop(REDIS_COMPLATE,5000,TimeUnit.SECONDS);
                //为空判断
                if(null == uuid) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(500);
                        continue;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                logger.info("返回订单处理结果: " + uuid);
                //返回处理结果
                deferredResultHolder.getMap().get(uuid).setResult("success");
            }
        }).start();
    }
}
