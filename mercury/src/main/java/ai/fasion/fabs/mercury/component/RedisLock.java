package ai.fasion.fabs.mercury.component;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Function:分布式锁
 *
 * @author miluo
 * Date: 2021/8/26 18:20
 * @since JDK 1.8
 */
@Component
public class RedisLock {
    private static final Logger log = LoggerFactory.getLogger(RedisLock.class);

    public final StringRedisTemplate stringRedisTemplate;

    public RedisLock(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    /**
     * 加锁
     *
     * @param key   id - 锁的唯一标志
     * @param value 当前时间+超时时间 也就是时间戳
     * @return
     */
    public boolean lock(String key, String value) {
        //对应setnx命令
        if (stringRedisTemplate.opsForValue().setIfAbsent(key, value)) {
            //可以成功设置,也就是key不存在
            return true;
        }

        //判断锁超时 - 防止原来的操作异常，没有运行解锁操作  防止死锁
        String currentValue = stringRedisTemplate.opsForValue().get(key);
        //如果锁过期
        //currentValue不为空且小于当前时间
        if (!StringUtils.isEmpty(currentValue) && Long.parseLong(currentValue) < System.currentTimeMillis()) {
            //获取上一个锁的时间value
            //对应getset，如果key存在
            String oldValue = stringRedisTemplate.opsForValue().getAndSet(key, value);

            //假设两个线程同时进来这里，因为key被占用了，而且锁过期了。获取的值currentValue=A(get取的旧的值肯定是一样的),两个线程的value都是B,key都是K.锁时间已经过期了。
            //而这里面的getAndSet一次只会一个执行，也就是一个执行之后，上一个的value已经变成了B。只有一个线程获取的上一个值会是A，另一个线程拿到的值是B。
            //oldValue不为空且oldValue等于currentValue，也就是校验是不是上个对应的商品时间戳，也是防止并发
            return !StringUtils.isEmpty(oldValue) && oldValue.equals(currentValue);
        }
        return false;
    }


    /**
     * 加锁
     *
     * @param key   id - 锁的唯一标志
     * @param value 当前时间+超时时间 也就是时间戳
     * @return
     */
    public boolean lock(String key, String value, long timeout) {
        //对应setnx命令
        if (stringRedisTemplate.opsForValue().setIfAbsent(key, value, timeout, TimeUnit.SECONDS)) {
            //可以成功设置,也就是key不存在
            return true;
        }

        //判断锁超时 - 防止原来的操作异常，没有运行解锁操作  防止死锁
        String currentValue = stringRedisTemplate.opsForValue().get(key);
        //如果锁过期
        //currentValue不为空且小于当前时间
        if (!StringUtils.isEmpty(currentValue) && Long.parseLong(currentValue) < System.currentTimeMillis()) {
            //获取上一个锁的时间value
            //对应getset，如果key存在
            String oldValue = stringRedisTemplate.opsForValue().getAndSet(key, value);

            //假设两个线程同时进来这里，因为key被占用了，而且锁过期了。获取的值currentValue=A(get取的旧的值肯定是一样的),两个线程的value都是B,key都是K.锁时间已经过期了。
            //而这里面的getAndSet一次只会一个执行，也就是一个执行之后，上一个的value已经变成了B。只有一个线程获取的上一个值会是A，另一个线程拿到的值是B。
            //oldValue不为空且oldValue等于currentValue，也就是校验是不是上个对应的商品时间戳，也是防止并发
            return !StringUtils.isEmpty(oldValue) && oldValue.equals(currentValue);
        }
        return false;
    }


    /**
     * 解锁
     *
     * @param key
     * @param value
     */
    public void unlock(String key, String value) {
        try {
            String currentValue = stringRedisTemplate.opsForValue().get(key);
            if (!StringUtils.isEmpty(currentValue) && currentValue.equals(value)) {
                //删除key
                stringRedisTemplate.opsForValue().getOperations().delete(key);
            }
        } catch (Exception e) {
            log.error("[Redis分布式锁] 解锁出现异常了，{}", e);
        }
    }


}
