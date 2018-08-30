package org.neal.cn.redistest.queueandasync.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 模拟另一个程序去处理消息队列里的任务
 * ContextRefreshedEvent spring 在初始化完毕后的事件
 * @author Neal
 */
@Component
public class ResolveListener implements ApplicationListener<ContextRefreshedEvent> {


    private Logger logger = LoggerFactory.getLogger(getClass());

    //redis 完成队列 KEY
    private static String REDIS_COMPLATE = "complete";

    //redis 准备队列 KEY
    private static String REDIS_MESSAGE = "prepare";

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        //由于处理队列方法是一个无限循环，需要单起一个线程，防止阻塞系统启动
        new Thread(()-> {
            while(true) {
                //获取任务队列中的任务ID
                Object prepareduuid = redisTemplate.opsForList().rightPop(REDIS_MESSAGE, 5000, TimeUnit.SECONDS);
                //非空判断
                if(null == prepareduuid) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(500);
                        continue;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                logger.info("读取消息队列待处理ID ; " + prepareduuid);
                /**
                 * 模拟任务处理过程 begin
                 */
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                /**
                 * end
                 */
                logger.info("完成订单处理，把处理ID放入完成队列");
                //将完成后的任务放入 任务结束队列
                redisTemplate.opsForList().leftPush(REDIS_COMPLATE, prepareduuid);
            }
        }).start();

    }
}
