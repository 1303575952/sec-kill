package com.doudizu.seckill.redis;

import com.alibaba.fastjson.JSON;
import com.doudizu.seckill.conf.PropertiesConf;
import com.doudizu.seckill.domain.Product;
import com.doudizu.seckill.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Pipeline;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class RedisClusterService {
    @Autowired
    JedisCluster jedisCluster;
    @Autowired
    PropertiesConf propertiesConf;
    @Autowired
    ProductService productService;


    public String get(String key) {
        try {
            String str = null;
            str = jedisCluster.get(key);
            return str;
        } catch (Exception ex) {
            //log.error("redis cluster get {key:" + key + "}", ex);
        }
        return null;
    }

    public void sadd(String key, String field) {
        jedisCluster.sadd(key, field);
    }

    public boolean setproduct(String pid, String detail) {
        try {
            String res = jedisCluster.set(pid, detail, "nx", "ex", propertiesConf.getRedisclusterProductlife());
            //log.info("setProduct res:" + pid + " " + res);
            return true;
        } catch (Exception ex) {
            //log.error("redis cluster setproduct {key:" + pid + ",detail:" + detail, ex);
        }
        return false;
    }

    public String fromMySQL(String pid) {
        try {
            Product res = productService.getProductByPid(Long.valueOf(pid));
            if (res == null)
                return null;
            String detail = "" + res.getPrice() + "-" + res.getDetail();
            setproduct(pid, detail);
            return detail;
        } catch (Exception ex) {
            //log.error("redis cluster fromMySQL {pid:" + pid + "}", ex);
        }
        return null;
    }

    /**
     * 返回 price-detail或者null
     *
     * @param pid
     * @return
     */

    private String getproductdetail(String pid) {
        try {
            String res = jedisCluster.get(pid);
            if (res == null) {
                return fromMySQL(pid);
            }
            return res;
        } catch (Exception ex) {
            //log.error("redis cluster get product detail {key:" + pid + "}", ex);
        }
        return null;
    }

    /**
     * 返回null或count-price-detail
     *
     * @param pid
     * @return
     */
    public String getproduct(String pid) {
        try {
            String product = getproductdetail(pid);
            if (product == null)
                return null;
            long count = propertiesConf.getRedisclusterProductnum() - jedisCluster.scard("order:pid:" + pid);
            return count + "-" + product;
        } catch (Exception ex) {
            //log.error("redis cluster get product {key:" + pid + "}", ex);
        }
        return null;
    }


    public boolean set(String key, String value) {
        try {
            System.out.println(jedisCluster.set(key, value));
            System.out.println("key:" + key);
            System.out.println("value:" + value);
            return true;
        } catch (Exception ex) {
            //log.error("redis cluster set {key:" + key + ",value:" + value + "}", ex);
        }
        return false;
    }

    public boolean existsproduct(String pid) {
        try {
            if (jedisCluster.exists(pid)) {
                //log.info("pid存在:" + pid);
                return true;
            }
            String detail = fromMySQL(pid);
            if (detail == null) {
                //log.info("MySQL中无数据");
                return false;
            }
            setproduct(pid, detail);
            return true;
        } catch (Exception ex) {
            //log.error("redis cluster exists product{pid:" + pid + "}", ex);
        }
        return false;
    }

    public Long incr(String key) {
        try {
            return jedisCluster.incr(key);
        } catch (Exception ex) {
            //log.error("redis cluster incr {key:" + key + "}", ex);
        }
        return Long.MIN_VALUE;
    }

    public Long decr(String key) {
        try {
            return jedisCluster.decr(key);
        } catch (Exception ex) {
            //log.error("redis cluster decr {key:" + key + "}", ex);
        }
        return Long.MAX_VALUE;
    }

    public String getOrderId(String uid, String pid) {
        String orderuid = "order:uid:" + uid;
        String res = jedisCluster.hget(orderuid, pid);
        if (res == null) {
            return null;
        }
        String[] strArr = res.split("~");
        return strArr[2];
    }

    public boolean createpay(String uid, String pid, String token) {
        try {
            String orderpid = "order:pid:" + pid;
            String orderuid = "order:uid:" + uid;
            String payuid = "pay:uid:" + uid;
            if (!existsproduct(pid) || !jedisCluster.sismember(orderpid, uid))
                return false;
            /*String lockkey = getlockkey("pay",pid);
            String lockvalue = getlockvalue(uid);
            if(!lock(lockkey,lockvalue))
                return false;*/
            //可以删除，但是可能订单超过限制
            if (jedisCluster.hexists(payuid, pid)) {
                /*releaselock(lockkey,lockvalue);*/
                return false;
            }
            String result = jedisCluster.hget(orderuid, pid);
            result += "-" + token;
            jedisCluster.hset(payuid, pid, result);//payuid为order_id-token

            jedisCluster.hdel(orderuid, pid);

            /*releaselock(lockkey,lockvalue);*/
            jedisCluster.sadd("reset", payuid);
            return true;
        } catch (Exception ex) {
            //log.error("redis cluster create pay {uid:" + uid + ",pid:" + pid);
        }
        return false;
    }

    public boolean createorder(String uid, String pid, String order_id) {
        try {
            String orderpid = "order:pid:" + pid;
            String orderuid = "order:uid:" + uid;
            if (!existsproduct(pid) || jedisCluster.sismember(orderpid, uid) ||
                    jedisCluster.scard(orderpid) >= propertiesConf.getRedisclusterProductnum()) {
                return false;
            }
            String lockkey = getlockkey("order", pid);
            String lockvalue = getlockvalue(uid);
            //进入临界区
            if (!lock(lockkey, lockvalue)) {
                //log.info("获取锁失败");
                return false;
            }
            log.info("已获取到锁");
            //可以删除,但是有可能会超订
            if (jedisCluster.sismember(orderpid, uid) || jedisCluster.scard(orderpid) >= propertiesConf.getRedisclusterProductnum()) {
                releaselock(lockkey, lockvalue);
                //log.info("重复下单");
                return false;
            }
            jedisCluster.sadd(orderpid, uid);
            //log.info("正常下单" + orderpid);
            releaselock(lockkey, lockvalue);

            jedisCluster.hset(orderuid, pid, order_id);

            jedisCluster.zadd(orderuid + ":pid", Integer.valueOf(pid), pid);

            jedisCluster.sadd("reset", orderpid, orderuid, orderuid + ":pid");
            return true;
        } catch (Exception ex) {
            log.error("redis cluster create pay {uid:" + uid + ",pid:" + pid + "}", ex);
        }
        return false;
    }

    public void flush() {
        try {
            Set<String> keys = jedisCluster.smembers("reset");
            //log.info("获取所有成员");
            for (String key : keys) {
                jedisCluster.del(key);
                log.info("reset:" + key);
            }
            jedisCluster.del("reset");
        } catch (Exception ex) {
            log.error("redis cluster flush", ex);
        }
    }

    /**
     * 返回用户订的所有订单,内部结构为：
     * pid-price-detail-status-order_id-token
     *
     * @param uid
     * @return
     */
    public String[] getallorder(String uid) {
        String orderuid = "order:uid:" + uid;
        String payuid = "pay:uid:" + uid;
        Set<String> pids = jedisCluster.zrange(orderuid + ":pid", 0, -1);
        if (pids == null)
            return null;
        Iterator<String> it = pids.iterator();
        String[] result = new String[pids.size()];
        int i = 0;
        while (it.hasNext()) {
            String pid = it.next(); //pid
            String detail = getproductdetail(pid);//price-detail
            String res = pid + "-" + detail;    //pid-price-detail
            if ((detail = jedisCluster.hget(payuid, pid)) != null)//成功支付
            {
                res += "-1-" + detail;//pid-price-detail-status-order_id-token
            } else {
                detail = jedisCluster.hget(orderuid, pid);//order_id
                res += "-0-" + detail + "-";//pid-price-detail-status-order_id-token
            }
            result[i] = res;
            ++i;
        }
        return result;
    }

    /**
     * 获取分布式锁的key
     *
     * @param prefix
     * @param pid
     * @return
     */
    private String getlockkey(String prefix, String pid) {
        return "mutex:" + prefix + ":" + pid;
    }

    /**
     * 获取分布式锁的值
     *
     * @param uid
     * @return
     */
    private String getlockvalue(String uid) {
        int randno = UUID.randomUUID().toString().hashCode();
        if (randno < 0)
            randno = -randno;
        return uid + String.format("%16d", randno);
    }

    /**
     * 上锁，会尝试几次
     *
     * @param key
     * @param value
     * @return
     */
    private boolean lock(String key, String value) throws InterruptedException {
        try {
            for (int j = propertiesConf.getRedisclusterMutexnum(); j > 0; --j) {
                if (trylock(key, value, propertiesConf.getRedisclusterMutextime())) {
                    return true;
                }
                int mili = (int) (Math.random() * 16);
                Thread.sleep(mili);
            }
        } catch (Exception ex) {
            log.error("redis cluster get lock {key:" + key + ",value:" + value + "}", ex);
        }
        return false;
    }

    private boolean trylock(String key, String value, int expire) {
        try {
            String result = jedisCluster.set(key, value, "nx", "ex", expire);
            return (result != null) && ("OK".equals(result) || "+OK".equals(result));
        } catch (Exception ex) {
            log.error("redis cluster try lock {key:" + key + ",value:" + value + "}", ex);
        }
        return false;
    }

    private boolean releaselock(String key, String value) {
        try {
            if (value.equals(jedisCluster.get(key))) {
                jedisCluster.del(key);
                return true;
            } else
                return false;
        } catch (Exception ex) {
            log.error("redis cluster release lock {key:" + key + ",value:" + value + "}", ex);
        }
        return false;
    }

    /**
     * 验证用户是否订了很多订单，但是未支付，如果是，认为属于作弊用户
     *
     * @param uid
     * @return
     */
    public boolean verify(String uid) {
        if (jedisCluster.hlen("order:uid:" + uid) > propertiesConf.getRedisclusterMaxorder())
            return false;
        return true;
    }

    /**
     * @param prefix uid 或者 IP
     * @param value  对应的ID或者IP地址
     * @return
     */
    public boolean verifypre(String prefix, String value) {

        String cheatname = "cheat:" + prefix;
        if (jedisCluster.sismember(cheatname, value))
            return false;
        Long current = System.currentTimeMillis() % propertiesConf.getRedisverifyTimes();

        String timename = "Time:" + prefix + ":" + current;
        String countname = "count:" + prefix;

        if (jedisCluster.sismember(timename, value)) {
            Long count = jedisCluster.hincrBy(countname, value, 1);
            if (count > propertiesConf.getRedisverifyNum()) {
                jedisCluster.sadd(cheatname, value);
                return false;
            }
        } else {
            jedisCluster.sadd(timename, value);
            jedisCluster.expire(timename, propertiesConf.getRedisverifyTimes() * 2);
            jedisCluster.hset(countname, value, String.valueOf(0));
        }
        return true;

    }

    public void flushcheat() {
        jedisCluster.del("cheat:IP", "cheat:uid");
    }

    public boolean verifyall(String uid, String session, String IP) {
        return uid.equals(jedisCluster.get(session)) && verifypre("uid", uid) && verifypre("IP", IP);
    }

    public <T> String beanToString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String) value;
        } else if (clazz == long.class || clazz == Long.class) {
            return "" + value;
        } else {
            return JSON.toJSONString(value);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T stringToBean(String str, Class<T> clazz) {
        if (str == null || str.length() <= 0 || clazz == null) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(str);
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }
}
