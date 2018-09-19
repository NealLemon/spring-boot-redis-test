package org.neal.cn.redistest.distributedlock.noredislock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Neal
 * 测试无分布式锁controller 层
 */
@RestController
@RequestMapping("/order")
public class NoDistributeController {

    //无分布式锁service
    @Autowired
    NoDistributeService redisDistributeService;

    /**
     * 查询剩余订单结果接口
     * @param pid  订单编号
     * @return
     */
    @GetMapping("/query/{pid}")
    public String query(@PathVariable String pid) {
        return redisDistributeService.queryMap(pid);
    }

    /**
     * 下单接口
     * @param pid  订单编号
     * @return
     */
    @GetMapping("/{pid}")
    public String order(@PathVariable String pid) {
        redisDistributeService.order(pid);
        return redisDistributeService.queryMap(pid);
    }
}
