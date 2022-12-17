package ai.fasion.fabs.apollo.component;

import ai.fasion.fabs.vesta.LimitingUtil.Limiting;
import ai.fasion.fabs.vesta.enums.CacheEnum;
import ai.fasion.fabs.vesta.http.IpUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * Function: 规定时间内，每个ip访问次数限制
 *
 * @author miluo Date: 2020/4/23 8:32 下午
 * @since JDK 1.8
 */
@Component
@Aspect
public class RequestLimitAspect {
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 缓存工具类
     */
    private final RedisTemplate<String, Integer> redisTemplate;

    public RequestLimitAspect(RedisTemplate<String, Integer> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Before("execution(public * cn.miluo.server.controller.*.*(..)) && @annotation(limit)")
    public void requestLimit(JoinPoint joinpoint, Limiting limit) {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            String ip = IpUtil.getIpAddr(request);
            String url = request.getRequestURL().toString();
            String key = CacheEnum.REQUEST_LIMIT.getKey().concat("_").concat(url).concat(ip);

            //加1后看看值
            long count = redisTemplate.opsForValue().increment(key, 1);
            //刚创建
            if (count == 1) {
                //设置过期时间
                redisTemplate.expire(key, limit.time(), TimeUnit.SECONDS);
            }
            if (count > limit.count()) {
                log.info("用户IP[" + ip + "]访问地址[" + url + "]超过了限定的次数[" + limit.count() + "]");
                throw new RuntimeException("当前ip调用当前接口已超出访问次数限制！");
            }
        }

    }
}
