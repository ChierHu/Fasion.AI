-   新建一个切面处理逻辑
```$xslt

@Component
@Aspect
public class RequestLimitAspect {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
 
    @Before("execution(public * com.tianyalei.controller.*.*(..)) && @annotation(limit)")
    public void requestLimit(JoinPoint joinpoint, RequestLimit limit) {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
 
        String ip = HttpRequestUtil.getIpAddr(request);
        String url = request.getRequestURL().toString();
        String key = "req_limit_".concat(url).concat(ip);
 
        //加1后看看值
        long count = redisTemplate.opsForValue().increment(key, 1);
        //刚创建
        if (count == 1) {
            //设置1分钟过期
            redisTemplate.expire(key, limit.time(), TimeUnit.MILLISECONDS);
        }
        if (count > limit.count()) {
            logger.info("用户IP[" + ip + "]访问地址[" + url + "]超过了限定的次数[" + limit.count() + "]");
            throw new RuntimeException("超出访问次数限制");
        }
    }
}
```