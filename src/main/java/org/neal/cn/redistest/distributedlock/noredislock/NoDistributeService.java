package org.neal.cn.redistest.distributedlock.noredislock;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Neal
 * 测试无分布式锁service 层
 */
@Service
public class NoDistributeService {

    //模拟商品信息表
    private static Map<String,Integer> products;

    //模拟库存表
    private static Map<String,Integer> stock;

    //模拟订单表
    private static Map<String,String> orders;

    static {
        products = new HashMap<>();
        stock = new HashMap<>();
        orders = new HashMap<>();
        //模拟订单表数据 订单编号 112233 库存 100000
        products.put("112233",100000);
        //模拟库存表数据 订单编号112233 库存100000
        stock.put("112233",100000);
    }

    /**
     * 模拟查询秒杀成功返回的信息
     * @param pid 商品编号
     * @return  返回拼接的秒杀商品结果字符串
     */
    public String queryMap(String pid) {
        return "秒杀商品限量:" +  products.get(pid) + "份,还剩:"+stock.get(pid) +"份,成功下单:"+orders.size() + "人";
    }

    /**
     * 下单方法
     * @param pid  商品编号
     */
    public void order(String pid) {
        //从库存表中获取库存余量
        int stockNum = stock.get(pid);
        //如果库存为0 则输出库存不足
        if(stockNum == 0) {
            System.out.println("商品库存不足");
        }else{ //如果有库存
            //往订单表中插入数据 生成UUID作为用户ID pid
            orders.put(UUID.randomUUID().toString(),pid);
            //线程休眠 模拟其他操作
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //减库存操作
            stock.put(pid,stockNum-1);
        }
    }
}
