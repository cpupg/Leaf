package com.sankuai.inf.leaf.segment.model;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 号段。
 */
public class Segment {
    /**
     * 当前值。
     */
    private AtomicLong value = new AtomicLong(0);
    /**
     * 当前号段的最大值。
     */
    private volatile long max;
    /**
     * 步长。
     */
    private volatile int step;
    /**
     * 双缓存。
     */
    private SegmentBuffer buffer;

    public Segment(SegmentBuffer buffer) {
        this.buffer = buffer;
    }

    /**
     * 号段内剩余的id数量。
     *
     * @return 剩余id数量。
     */
    public long getIdle() {
        return this.getMax() - getValue().get();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Segment(");
        sb.append("value:");
        sb.append(value);
        sb.append(",max:");
        sb.append(max);
        sb.append(",step:");
        sb.append(step);
        sb.append(")");
        return sb.toString();
    }

    public AtomicLong getValue() {
        return value;
    }

    public void setValue(AtomicLong value) {
        this.value = value;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public SegmentBuffer getBuffer() {
        return buffer;
    }

}
