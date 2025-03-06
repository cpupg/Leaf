package com.sankuai.inf.leaf.segment;

import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.common.Status;
import com.sankuai.inf.leaf.segment.dao.IDAllocDao;
import com.sankuai.inf.leaf.segment.model.LeafAlloc;
import com.sankuai.inf.leaf.segment.model.Segment;
import com.sankuai.inf.leaf.segment.model.SegmentBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class SegmentIDGenImpl implements IDGen {
    private static final Logger LOGGER = LoggerFactory.getLogger(SegmentIDGenImpl.class);

    /**
     * IDCache未初始化成功时的异常码
     */
    private static final long EXCEPTION_ID_IDCACHE_INIT_FALSE = -1;
    /**
     * key不存在时的异常码
     */
    private static final long EXCEPTION_ID_KEY_NOT_EXISTS = -2;
    /**
     * SegmentBuffer中的两个Segment均未从DB中装载时的异常码
     */
    private static final long EXCEPTION_ID_TWO_SEGMENTS_ARE_NULL = -3;
    /**
     * 最大步长不超过100,0000
     */
    private static final int MAX_STEP = 1000000;
    /**
     * 一个Segment维持时间为15分钟
     */
    private static final long SEGMENT_DURATION = 15 * 60 * 1000L;
    private ExecutorService threadPoolExecutor = new ThreadPoolExecutor(5, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(), new UpdateThreadFactory());
    private volatile boolean initOK = false;
    private Map<String, SegmentBuffer> cache = new ConcurrentHashMap<>();
    private IDAllocDao dao;

    /**
     * 返回id结果。
     *
     * @param buffer 双号段缓存。
     * @param segment 号段。
     *
     * @return 结果。
     */
    private Result getResult(SegmentBuffer buffer, Segment segment) {
        long value = segment.getValue().getAndIncrement();
        if (value < segment.getMax()) {
            // 当日期变化或达到最大值时，重置id为1，步长为1000。
            Calendar instance = Calendar.getInstance();
            instance.setTime(new Date());
            int currentDay = instance.get(Calendar.DAY_OF_MONTH);
            instance.setTimeInMillis(buffer.getUpdateTimestamp());
            int segmentDay = instance.get(Calendar.DAY_OF_MONTH);
            if (value > buffer.getMaxNumber() || currentDay != segmentDay) {
                buffer.resetSegment();
                return new Result(buffer.getCurrent().getValue().get(), Status.SUCCESS);
            }
            return new Result(value, Status.SUCCESS);
        } else {
            return new Result(-1, Status.EXCEPTION);
        }
    }

    @Override
    public boolean init() {
        LOGGER.info("初始化id生成器");
        // 确保加载到kv后才初始化成功
        updateCacheFromDb();
        initOK = true;
        // 不需要每分钟更新业务类型，因为系统运行时不会更新业务类型，只有上线时会更新。
        return initOK;
    }

    /**
     * 将数据库中的号段按业务加载到缓存中，等待初始化。
     */
    private void updateCacheFromDb() {
        LOGGER.info("从数据库中更新号段缓存");
        try {
            List<String> dbTags = dao.getAllTags();
            if (dbTags == null || dbTags.isEmpty()) {
                LOGGER.warn("号段表中没有业务，不需要加载号段");
                return;
            }
            LOGGER.info("数据库中的号段:{}", dbTags);
            Set<String> insertTagsSet = new HashSet<>(dbTags);
            for (String tag : insertTagsSet) {
                SegmentBuffer buffer = new SegmentBuffer();
                buffer.setKey(tag);
                Segment segment = buffer.getCurrent();
                segment.setValue(new AtomicLong(0));
                segment.setMax(0);
                segment.setStep(0);
                cache.put(tag, buffer);
            }
            LOGGER.info("所有业务号段都已加载");
            // 程序运行过程中号段表里的业务不会变，因此不需要更新
        } catch (Exception e) {
            LOGGER.warn("加载号段失败", e);
        }
    }

    @Override
    public Result get(final String key) {
        if (!initOK) {
            return new Result(EXCEPTION_ID_IDCACHE_INIT_FALSE, Status.EXCEPTION);
        }
        if (cache.containsKey(key)) {
            SegmentBuffer buffer = cache.get(key);
            if (!buffer.isInitOk()) {
                synchronized(buffer) {
                    if (!buffer.isInitOk()) {
                        try {
                            updateSegmentFromDb(key, buffer.getCurrent());
                            LOGGER.info("更新业务{}对应的号段{}", key, buffer.getCurrent());
                            buffer.setInitOk(true);
                        } catch (Exception e) {
                            LOGGER.warn("更新号段{}失败", buffer.getCurrent(), e);
                        }
                    }
                }
            }
            return getIdFromSegmentBuffer(cache.get(key));
        }
        return new Result(EXCEPTION_ID_KEY_NOT_EXISTS, Status.EXCEPTION);
    }

    /**
     * 初始化指定业务的指定号段。
     *
     * @param key 业务类型。
     * @param segment 号段。
     */
    public void updateSegmentFromDb(String key, Segment segment) {
        LOGGER.info("更新号段key={},segment={}", key, segment);
        SegmentBuffer buffer = segment.getBuffer();
        LeafAlloc leafAlloc;
        if (!buffer.isInitOk()) {
            LOGGER.info("号段还未初始化{}", segment);
            // 没有初始化时查表获取当前值和步长。
            leafAlloc = dao.updateMaxIdAndGetLeafAlloc(key);
            buffer.setStep(leafAlloc.getStep());
            buffer.setMinStep(leafAlloc.getStep());//leafAlloc中的step为DB中的step
            if (buffer.getMaxNumber() == 0) {
                buffer.setMaxNumber(leafAlloc.getMaxNumber());
                buffer.setLength(String.valueOf(leafAlloc.getMaxNumber()).length());
            }
        } else if (buffer.getUpdateTimestamp() == 0) {
            LOGGER.info("第一次更新号段{}", segment);
            // 更新时间是long，默认值是0，说明还没有更新过，是第一次更新。
            leafAlloc = dao.updateMaxIdAndGetLeafAlloc(key);
            buffer.setUpdateTimestamp(System.currentTimeMillis());
            buffer.setStep(leafAlloc.getStep());
            buffer.setMinStep(leafAlloc.getStep());//leafAlloc中的step为DB中的step
        } else {
            // 初始化完成且更新过一次，此时就要判断是否增加步长。
            // 增加步长是根据两次获取id的间隔来确定，间隔小于阈值时变2倍，大于阈值小于2倍阈值时变0.5倍。
            long duration = System.currentTimeMillis() - buffer.getUpdateTimestamp();
            int nextStep = buffer.getStep();
            if (duration < SEGMENT_DURATION) {
                if (nextStep * 2 <= MAX_STEP) {
                    nextStep = nextStep * 2;
                }
            } else if (duration >= SEGMENT_DURATION * 2) {
                nextStep = nextStep / 2 >= buffer.getMinStep() ? nextStep / 2 : nextStep;
            }
            LOGGER.info("leafKey={}, step={}, duration={}, nextStep={}", key, buffer.getStep(), duration, nextStep);
            LeafAlloc temp = new LeafAlloc();
            temp.setKey(key);
            temp.setStep(nextStep);
            leafAlloc = dao.updateMaxIdByCustomStepAndGetLeafAlloc(temp);
            buffer.setUpdateTimestamp(System.currentTimeMillis());
            buffer.setStep(nextStep);
            // leafAlloc的step为DB中的step
            buffer.setMinStep(leafAlloc.getStep());
        }
        // must set value before set max
        long value = leafAlloc.getMaxId() - buffer.getStep();
        segment.getValue().set(value);
        segment.setMax(leafAlloc.getMaxId());
        segment.setStep(buffer.getStep());
    }

    /**
     * 从缓冲池中获取id。
     *
     * @param buffer 缓冲池。
     *
     * @return id。
     */
    public Result getIdFromSegmentBuffer(final SegmentBuffer buffer) {
        while (true) {
            buffer.rLock().lock();
            try {
                final Segment segment = buffer.getCurrent();
                // 一共两个号段，另一个号段已经准备好并且当前号段已使用90%时，异步更新另一个号段。
                // nextReady只在当前号段已用完切换另一个号段时变false。
                if (!buffer.isNextReady() && (segment.getIdle() < 0.9 * segment.getStep()) && buffer.getThreadRunning()
                        .compareAndSet(false, true)) {
                    threadPoolExecutor.execute(new Runnable() {
                        @Override
                        public void run() {

                            Segment next = buffer.getSegments()[buffer.nextPos()];
                            boolean updateOk = false;
                            try {
                                updateSegmentFromDb(buffer.getKey(), next);
                                updateOk = true;
                                LOGGER.info("更新业务{}对应的号段{}", buffer.getKey(), next);
                            } catch (Exception e) {
                                LOGGER.warn("号段{}更新失败", buffer.getKey(), e);
                            } finally {
                                if (updateOk) {
                                    buffer.wLock().lock();
                                    buffer.setNextReady(true);
                                    buffer.getThreadRunning().set(false);
                                    buffer.wLock().unlock();
                                } else {
                                    buffer.getThreadRunning().set(false);
                                }
                            }
                        }
                    });
                }
            } finally {
                buffer.rLock().unlock();
            }
            buffer.wLock().lock();
            try {
                Result value = getResult(buffer, buffer.getCurrent());
                if (value.getStatus() == Status.SUCCESS) {
                    return value;
                }
            } finally {
                buffer.wLock().unlock();
            }
            LOGGER.info("当前号段已用完，等待切换下一个号段");
            waitAndSleep(buffer);
            buffer.wLock().lock();
            try {
                final Segment segment = buffer.getCurrent();
                Result value = getResult(buffer, segment);
                if (value.getStatus() == Status.SUCCESS) {
                    return value;
                }
                if (buffer.isNextReady()) {
                    buffer.switchPos();
                    buffer.setNextReady(false);
                } else {
                    // 说明updateSegmentFromDb出错
                    LOGGER.error("两个号段缓存都没有准备好", buffer);
                    return new Result(EXCEPTION_ID_TWO_SEGMENTS_ARE_NULL, Status.EXCEPTION);
                }
            } finally {
                buffer.wLock().unlock();
            }
        }
    }

    /**
     * 等待另一个线程运行完成。
     *
     * <p>这是一个同步锁，缓冲池只允许一个线程操作，通过原子类AtomicBoolean实现。</p>
     *
     * @param buffer 缓冲池。
     */
    public void waitAndSleep(SegmentBuffer buffer) {
        int roll = 0;
        while (buffer.getThreadRunning().get()) {
            // roll加到1w很快，测试结果是1ms
            roll += 1;
            if (roll > 10000) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                    break;
                } catch (InterruptedException e) {
                    LOGGER.warn("Thread {} Interrupted", Thread.currentThread().getName());
                    break;
                }
            }
        }
    }

    public List<LeafAlloc> getAllLeafAllocs() {
        return dao.getAllLeafAllocs();
    }

    public Map<String, SegmentBuffer> getCache() {
        return cache;
    }

    public void setDao(IDAllocDao dao) {
        this.dao = dao;
    }

    public static class UpdateThreadFactory implements ThreadFactory {

        private static int threadInitNumber = 0;

        private static synchronized int nextThreadNum() {
            return threadInitNumber++;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Segment-Update-" + nextThreadNum());
        }
    }
}
