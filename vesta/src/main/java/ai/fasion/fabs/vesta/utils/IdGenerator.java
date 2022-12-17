package ai.fasion.fabs.vesta.utils;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
/**
 * @author peishuai
 */
public class IdGenerator {
    //工作机器 id
    private long workerId;
    //数据中心 id
    private long datacenterId;
    //序列号
    private long sequence = 0L;

    //基准时间，一般取系统的最近时间（一旦确定不能变动）
    private long twepoch;

    private long workerIdBits;
    private long datacenterIdBits;
    private long maxWorkerId;
    private long maxDatacenterId;

    //毫秒内自增位数
    private long sequenceBits;
    //位与运算保证毫秒内 Id 范围
    private long sequenceMask;

    //工作机器 id 需要左移的位数
    private long workerIdShift;
    //数据中心 id 需要左移位数
    private long datacenterIdShift;
    //时间戳需要左移位数
    private long timestampLeftShift;

    //上次生成 id 的时间戳，初始值为负数
    private long lastTimestamp = -1L;

    //true 表示毫秒内初始序列采用随机值
    private boolean randomSequence;
    //随机初始序列计数器
    private long count = 0L;

    //允许时钟回拨的毫秒数
    private long timeOffset;


    // 一个毫秒内生成随机数 校验重复集合
    private List<Long> randomDeduplicationList = new ArrayList<>();
    private final ThreadLocalRandom tlr = ThreadLocalRandom.current();

    //64位 雪花id  不指定机器id 工作id
    public static final IdGenerator shortIdGenerator = new IdGenerator(0, 0, false, 0, null, 8, 8, 6);




    /**
     * 无参构造器，自动生成 workerId/datacenterId
     */
    public IdGenerator() {
        this(false, 10, null, 5L, 5L, 12L);
    }

    /**
     * 有参构造器,调用者自行保证数据中心 ID+机器 ID 的唯一性
     * 标准 snowflake 实现
     *
     * @param workerId     工作机器 ID
     * @param datacenterId 数据中心 ID
     */
    public IdGenerator(long workerId, long datacenterId) {
        this(workerId, datacenterId, false, 10, null, 5L, 5L, 12L);
    }

    /**
     * @param randomSequence   true 表示每毫秒内起始序号使用随机值
     * @param timeOffset       允许时间回拨的毫秒数
     * @param epochDate        基准时间
     * @param workerIdBits     workerId 位数
     * @param datacenterIdBits datacenterId 位数
     * @param sequenceBits     sequence 位数
     */
    public IdGenerator(boolean randomSequence, long timeOffset, Date epochDate, long workerIdBits, long datacenterIdBits, long sequenceBits) {
        if (null != epochDate) {
            this.twepoch = epochDate.getTime();
        } else {
            // 2020/09/26 00:00:00 GMT  默认基准时间 可设置
            this.twepoch = 1577808000000L;
        }

        this.workerIdBits = workerIdBits;
        this.datacenterIdBits = datacenterIdBits;
        this.maxWorkerId = -1L ^ (-1L << workerIdBits);
        this.maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

        this.sequenceBits = sequenceBits;
        this.sequenceMask = -1L ^ (-1L << sequenceBits);

        this.workerIdShift = sequenceBits;
        this.datacenterIdShift = sequenceBits + workerIdBits;
        this.timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

        this.datacenterId = getDatacenterId(maxDatacenterId);
        this.workerId = getMaxWorkerId(datacenterId, maxWorkerId);
        this.randomSequence = randomSequence;
        this.timeOffset = timeOffset;
        String initialInfo = String.format("worker starting. timestamp left shift %d, datacenter id bits %d, worker id bits %d, sequence bits %d, datacenterid  %d, workerid %d",
                timestampLeftShift, datacenterIdBits, workerIdBits, sequenceBits, datacenterId, workerId);
        System.out.println(initialInfo);
    }

    /**
     * 自定义 workerId+datacenterId+其它初始配置
     * 调整 workerId、datacenterId、sequence 位数定制雪花算法,控制生成的 Id 的位数
     *
     * @param workerId         工作机器 ID
     * @param datacenterId     数据中心 ID
     * @param randomSequence   true 表示每毫秒内起始序号使用随机值
     * @param timeOffset       允许时间回拨的毫秒数
     * @param epochDate        基准时间
     * @param workerIdBits     workerId 位数
     * @param datacenterIdBits datacenterId 位数
     * @param sequenceBits     sequence 位数
     */
    public IdGenerator(long workerId, long datacenterId, boolean randomSequence, long timeOffset, Date epochDate, long workerIdBits, long datacenterIdBits, long sequenceBits) {

        this.workerIdBits = workerIdBits;
        this.datacenterIdBits = datacenterIdBits;
        this.maxWorkerId = -1L ^ (-1L << workerIdBits);
        this.maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
        //如果 没有 datacenterId workerId  就根据当前机器地址生成
        if (0l == datacenterId) {
            datacenterId =  getDatacenterId(maxDatacenterId);
        }
        if (0l == workerId) {
            workerId = getMaxWorkerId(datacenterId, maxWorkerId);
        }


        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0\r\n", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0\r\n", maxDatacenterId));
        }

        if (null != epochDate) {
            this.twepoch = epochDate.getTime();
        } else {
            // 2020/01/01 00:00:00 GMT  默认基准时间 可设置
            this.twepoch = 1577808000000L;
        }

        this.sequenceBits = sequenceBits;
        this.sequenceMask = -1L ^ (-1L << sequenceBits);

        this.workerIdShift = sequenceBits;
        this.datacenterIdShift = sequenceBits + workerIdBits;
        this.timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

        this.workerId = workerId;
        this.datacenterId = datacenterId;
        this.timeOffset = timeOffset;
        this.randomSequence = randomSequence;

        String initialInfo = String.format("worker starting. timestamp left shift %d, datacenter id bits %d, worker id bits %d, sequence bits %d, datacenterid  %d, workerid %d",
                timestampLeftShift, datacenterIdBits, workerIdBits, sequenceBits, datacenterId, workerId);
        System.out.println(initialInfo);
    }

    private static long getDatacenterId(long maxDatacenterId) {
        long id = 0L;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network == null) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                if (null != mac) {
                    id = ((0x000000FF & (long) mac[mac.length - 1]) | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
                    id = id % (maxDatacenterId + 1);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("GetDatacenterId Exception", e);
        }
        return id;
    }

    private static long getMaxWorkerId(long datacenterId, long maxWorkerId) {
        StringBuilder macIpPid = new StringBuilder();
        macIpPid.append(datacenterId);
        try {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            if (name != null && !name.isEmpty()) {
                //GET jvmPid
                macIpPid.append(name.split("@")[0]);
            }
            //GET hostIpAddress
            String hostIp = InetAddress.getLocalHost().getHostAddress();
            String ipStr = hostIp.replaceAll("\\.", "");
            macIpPid.append(ipStr);
        } catch (Exception e) {
            throw new RuntimeException("GetMaxWorkerId Exception", e);
        }
        //MAC + PID + IP 的 hashcode 取低 16 位
        return (macIpPid.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }
    //此方法 使用规则  如果对同一个对象 既调用 nextIdRandom 又调用 nextId 会造成数据错误 但是 可以新注册个bean实例使用  即 一个实例要么只能调用 nextIdRandom 要么 nextId
    //生成同一毫秒内顺序序列号的结果
    public synchronized long nextId() {
        long currentTimestamp = timeGen();

        //获取当前时间戳如果小于上次时间戳，则表示时间戳获取出现异常
        if (currentTimestamp < lastTimestamp) {
            // 校验时间偏移回拨量  暂时不用 偏移量的设置会导致生成随机数不是每毫秒变更
            long offset = lastTimestamp - currentTimestamp;
            if (offset > timeOffset) {
                throw new RuntimeException("Clock moved backwards, refusing to generate id for [" + offset + "ms]");
            }

            /*try {
                // 时间回退 timeOffset 毫秒内，则允许等待 2 倍的偏移量后重新获取，解决小范围的时间回拨问题
                this.wait(offset << 1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }*/

            currentTimestamp = timeGen();
            if (currentTimestamp < lastTimestamp) {
                throw new RuntimeException("Clock moved backwards, refusing to generate id for [" + offset + "ms]");
            }
        }

        //如果获取的当前时间戳等于上次时间戳（即同一毫秒内），则序列号自增
        if (lastTimestamp == currentTimestamp) {
            // randomSequence 为 true 表示随机生成允许范围内的起始序列,否则毫秒内起始值从 0L 开始自增
            long tempSequence = sequence + 1;
            if (randomSequence) {
                sequence = tempSequence & sequenceMask;
                count = (count + 1) & sequenceMask;
                if (count == 0) {
                    currentTimestamp = this.tillNextMillis(lastTimestamp);
                }
            } else {
                sequence = tempSequence & sequenceMask;
                if (sequence == 0) {
                    currentTimestamp = this.tillNextMillis(lastTimestamp);
                }
            }
        } else {
            sequence = randomSequence ? tlr.nextLong(sequenceMask + 1) : 0L;
            count = 0L;
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - twepoch) << timestampLeftShift) |
                (datacenterId << datacenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }
    //生成id  同一毫秒内 序列号随机   如果以随机数生成，会导致每毫秒不一定能生成满量的id 比如如果 6位 64个序列号 经测试每毫秒只能生成50多个
    public synchronized long nextIdRandom() {
        long currentTimestamp = timeGen();

        //获取当前时间戳如果小于上次时间戳，则表示时间戳获取出现异常
        if (currentTimestamp < lastTimestamp) {
            // 校验时间偏移回拨量  暂时不用 偏移量的设置会导致生成随机数不是每毫秒变更
            long offset = lastTimestamp - currentTimestamp;
            if (offset > timeOffset) {
                throw new RuntimeException("Clock moved backwards, refusing to generate id for [" + offset + "ms]");
            }

            /*try {
                // 时间回退 timeOffset 毫秒内，则允许等待 2 倍的偏移量后重新获取，解决小范围的时间回拨问题
                this.wait(offset << 1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }*/

            currentTimestamp = timeGen();
            if (currentTimestamp < lastTimestamp) {
                throw new RuntimeException("Clock moved backwards, refusing to generate id for [" + offset + "ms]");
            }
        }

        //如果获取的当前时间戳等于上次时间戳（即同一毫秒内），则序列号随机 去重
        if (lastTimestamp == currentTimestamp) {

            //如果当前毫秒内 不重复的随机序列号 已达到 序列号最大范围 就等待到下一毫秒生成
            do {

                sequence  = tlr.nextLong(sequenceMask + 1);

            }while(randomDeduplicationList.contains(sequence));

            randomDeduplicationList.add(sequence);

            //如果已经生成完所有的当前毫秒序列号，那就等待到下一毫秒 并把当前生成的序列号作为下一毫秒的序列号其一
            if (randomDeduplicationList.size() == sequenceMask+1) {
                currentTimestamp = this.tillNextMillis(lastTimestamp);
                //  到下一毫秒，重置毫秒序列号list
                randomDeduplicationList = new ArrayList<>();
                randomDeduplicationList.add(sequence);
            }
        } else {
            sequence =  tlr.nextLong(sequenceMask + 1);
            //  到下一毫秒，重置毫秒序列号list
            randomDeduplicationList = new ArrayList<>();
            randomDeduplicationList.add(sequence);
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - twepoch) << timestampLeftShift) |
                (datacenterId << datacenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    private long tillNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }








    /**
     * 测试
     * @param args
     */
    public static void main(String[] args) {

        // 使用时候 workerid  datacenterid 要么从 系统变量里取， 传空就默认 取ip,mac等信息中取位
        // 位数信息 1 位符号  41位 时间戳 已占用42 位   如果要求 48位 或者 64位  wokerid，datacenterid,seq序列号  这3个参数位数需要考量
        //支持 机器id workid 位数为0
        IdGenerator shortIdGenerator = new IdGenerator(0, 0, false, 0, null, 0, 0, 6);
        for (int j = 0; j < 1000; j++) {
            System.out.println(System.currentTimeMillis() + " " + shortIdGenerator.nextIdRandom()+"  "+shortIdGenerator.sequence);
        }


    }
}

