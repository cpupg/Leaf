package com.sankuai.inf.leaf.segment.model;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 双buffer
 */
public class SegmentBuffer {
    /**
     * 是否有线程在更新号段。
     *
     * <p>当前号段使用量达到阈值时会异步更新另一个号段，此时值变为true。当更新完成后，值变为true。</p>
     */
    private final AtomicBoolean threadRunning;
    private final ReadWriteLock lock;
    private String key;
    private Segment[] segments;
    /**
     * 当前使用的号段下标。
     */
    private volatile int currentPos;
    /**
     * 下一个segment是否处于可切换状态。
     *
     * <p>号段更新完成后此值变为true，更新号段时变false。</p>
     */
    private volatile boolean nextReady;
    /**
     * 是否初始化完成。
     */
    private volatile boolean initOk; //
    private volatile int step;
    private volatile int minStep;
    private volatile long updateTimestamp;
    /**
     * 当前业务允许的最大id。
     */
    private long maxNumber;
    /**
     * 当前业务允许的最大id长度。
     */
    private int length;

    public SegmentBuffer() {
        segments = new Segment[]{new Segment(this), new Segment(this)};
        currentPos = 0;
        nextReady = false;
        initOk = false;
        threadRunning = new AtomicBoolean(false);
        lock = new ReentrantReadWriteLock();
    }

    /**
     * 将号段当前值改为1，步长改为1000。
     */
    public void resetSegment() {
        segments[0].setStep(1000);
        segments[0].setValue(new AtomicLong(1));
        segments[1].setStep(1000);
        segments[1].setValue(new AtomicLong(1));
    }

    public int nextPos() {
        return (currentPos + 1) % 2;
    }

    public void switchPos() {
        currentPos = nextPos();
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public Segment getCurrent() {
        return segments[currentPos];
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SegmentBuffer{");
        sb.append("key='").append(key).append('\'');
        sb.append(", segments=").append(Arrays.toString(segments));
        sb.append(", currentPos=").append(currentPos);
        sb.append(", nextReady=").append(nextReady);
        sb.append(", initOk=").append(initOk);
        sb.append(", threadRunning=").append(threadRunning);
        sb.append(", step=").append(step);
        sb.append(", minStep=").append(minStep);
        sb.append(", updateTimestamp=").append(updateTimestamp);
        sb.append('}');
        return sb.toString();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Segment[] getSegments() {
        return segments;
    }

    public boolean isInitOk() {
        return initOk;
    }

    public void setInitOk(boolean initOk) {
        this.initOk = initOk;
    }

    public boolean isNextReady() {
        return nextReady;
    }

    public void setNextReady(boolean nextReady) {
        this.nextReady = nextReady;
    }

    public AtomicBoolean getThreadRunning() {
        return threadRunning;
    }

    public Lock rLock() {
        return lock.readLock();
    }

    public Lock wLock() {
        return lock.writeLock();
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getMinStep() {
        return minStep;
    }

    public void setMinStep(int minStep) {
        this.minStep = minStep;
    }

    public long getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(long updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public long getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(long maxNumber) {
        this.maxNumber = maxNumber;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
