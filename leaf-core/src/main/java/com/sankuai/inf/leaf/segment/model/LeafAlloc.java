package com.sankuai.inf.leaf.segment.model;

/**
 * 号段表实体类。
 *
 * <p>开发者实际使用的实体类必须继承此类。</p>
 *
 * @author chen
 */
public class LeafAlloc {
    /**
     * 业务类型。
     */
    private String key;
    /**
     * 当前号段的最大id。
     */
    private long maxId;
    /**
     * 步长。
     */
    private int step;
    /**
     * 更新时间。
     */
    private String updateTime;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getMaxId() {
        return maxId;
    }

    public void setMaxId(long maxId) {
        this.maxId = maxId;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
