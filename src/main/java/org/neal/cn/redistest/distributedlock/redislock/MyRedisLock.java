package org.neal.cn.redistest.distributedlock.redislock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;

/**
 * @author Neal
 * 分布式锁
 */
@Component
public class MyRedisLock {
    //Only set the key if it does not already exist.
    private static final String IF_NOT_EXIST = "NX";
    // Set the specified expire time, in milliseconds.
    private static final String SET_EXPIRE_TIME = "PX";
    //超时时间为 500毫秒
    private static final int EXPIRE_TIME = 500;
    //加锁成功后返回的标识
    private static final String ON_LOCK = "OK";
   //LUA 解锁脚本
    private static final String LUA_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
    /**
     * 获取配置好的jedis pool 组件
     */
    @Autowired
    private JedisPool jedisPoolFactory;

    /***
     * redis 加锁方法
     * @param id  商品ID
     * @param uuid  模拟用户ID
     * @return 返回 true 加锁成功 false 解锁成功
     */
    public boolean redisLock(String id,String uuid) {
        //从 jedis 连接池中 获取jedis
        Jedis jedis = jedisPoolFactory.getResource();
        boolean locked = false;
        try{
            //使用Jedis 加锁
            locked = ON_LOCK.equals(jedis.set(id,uuid,IF_NOT_EXIST,SET_EXPIRE_TIME,EXPIRE_TIME));
        }finally {
            //将连接放回连接池
            jedis.close();
        }
        return locked;
    }

    /***
     * redis 解锁方法
     * @param id  商品ID
     * @param uuid  模拟用户ID
     * @return  由于是使用LUA脚本，则会保证原子性的特质
     */
    public void redisUnlock(String id,String uuid) {
        //从 jedis 连接池中 获取jedis
        Jedis jedis = jedisPoolFactory.getResource();
        try{
            //使用Jedis 的 eval解锁
            Object result = jedis.eval(LUA_SCRIPT, Collections.singletonList(id),Collections.singletonList(uuid));
            if(1L == (Long)result) {
                System.out.println("客户ID为:《" + uuid + "》   解锁成功！");
            }
        }finally {
            jedis.close();
        }
    }
}
