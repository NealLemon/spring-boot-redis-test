package org.neal.cn.redistest.queueandasync.controller;



import org.neal.cn.redistest.queueandasync.async.DeferredResultHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/redis")
public class RedisController {

    private Logger logger = LoggerFactory.getLogger(getClass());
    //redis 准备队列
    private static String REDIS_MESSAGE = "prepare";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DeferredResultHolder deferredResultHolder;

    /**
     * 测试消息队列入口接口
     * @return
     */
    @GetMapping("/async")
    public DeferredResult<String> async() {
        logger.info("主线程开始");
        //生成唯一值 模拟任务ID初始化
        String uuid = UUID.randomUUID().toString();
        //将要任务的ID放入redis 待处理任务消息队列
        redisTemplate.opsForList().leftPush(REDIS_MESSAGE,uuid);

        DeferredResult<String> deferredResult = new DeferredResult<>();

        //将任务ID和 DeferredResult 对象绑定
        deferredResultHolder.getMap().put(uuid,deferredResult);

        logger.info("主线程返回");

        return deferredResult;

    }
}
