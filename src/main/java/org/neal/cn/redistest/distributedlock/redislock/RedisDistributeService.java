package org.neal.cn.redistest.distributedlock.redislock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Neal
 * 测试分布式锁service 层
 */
@Service
public class RedisDistributeService {


    //模拟商品信息表
    private static Map<String,Integer> products;

    //模拟库存表
    private static Map<String,Integer> stock;

    //模拟订单表
    private static Map<String,String> orders;

    //redis 锁组件
    @Autowired
    MyRedisLock myRedisLock;

    static {
        products = new HashMap<>();
        stock = new HashMap<>();
        orders = new HashMap<>();
        products.put("112233",100000);
        stock.put("112233",100000);
    }

    /**
     * 模拟查询秒杀成功返回的信息
     * @param pid 商品名称
     * @return
     */
    public String queryMap(String pid) {
        return "秒杀商品限量:" +  products.get(pid) + "份,还剩:"+stock.get(pid) +"份,成功下单:"+orders.size() + "人";
    }

    /**
     * 下单方法
     * @param pid  商品名称
     */
    public void order(String pid,String uuid) {

        //redis 加锁
        if(!myRedisLock.redisLock(pid,uuid)) {  //如果没获得锁则直接返回，不执行下面的代码
            System.out.println("客户ID为:《"+ uuid +"》未获得锁");
            return;
        }

        System.out.println("客户ID为:《"+ uuid +"》获得锁");
        //从库存表中获取库存余量
        int stockNum = stock.get(pid);
        if(stockNum == 0) {
            System.out.println("商品库存不足");
        }else{
            //往订单表中插入数据
            orders.put(uuid,pid);
            //线程休眠 模拟其他操作
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //减库存操作
            stock.put(pid,stockNum-1);
        }

        //redis 解锁
        myRedisLock.redisUnlock(pid,uuid);
    }
}
