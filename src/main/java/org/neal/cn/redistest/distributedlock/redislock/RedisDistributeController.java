package org.neal.cn.redistest.distributedlock.redislock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.UUID;

/**
 * @author Neal
 * 测试分布式锁controller 层
 */
@RestController
@RequestMapping("/distribute")
public class RedisDistributeController {

    @Autowired
    private RedisDistributeService redisDistributeService;

    @Autowired
    private JedisPool jedisPoolFactory;

    @GetMapping("/query/{pid}")
    public String query(@PathVariable String pid) {
        return redisDistributeService.queryMap(pid);
    }

    @GetMapping("/{pid}")
    public String order(@PathVariable String pid) {
        redisDistributeService.order(pid, UUID.randomUUID().toString());
        return redisDistributeService.queryMap(pid);
    }
}
