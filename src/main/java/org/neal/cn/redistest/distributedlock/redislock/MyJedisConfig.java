package org.neal.cn.redistest.distributedlock.redislock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Neal
 * 初始化jedis 连接池
 */
@Component
public class MyJedisConfig {

    /**
     * 自定义jedis配置bean
     */
    @Autowired
    private MyJedisBean myJedisBean;

    @Bean
    public JedisPool jedisPoolFactory() {

        //声明jedispool 配置类
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(myJedisBean.getPoolMaxIdle());
        jedisPoolConfig.setMaxTotal(myJedisBean.getPoolMaxTotal());
        jedisPoolConfig.setMaxWaitMillis(myJedisBean.getPoolMaxWait() *1000);
        /**
         * 利用Jedis的构造方法 生成 jedispool
         */
        JedisPool jedisPool = new JedisPool(jedisPoolConfig,myJedisBean.getHost(),myJedisBean.getPort(),myJedisBean.getTimeout()*1000,myJedisBean.getPassword(),0);
        return jedisPool;
    }

}
