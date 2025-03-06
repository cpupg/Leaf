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
     * id前缀，可以为空。
     */
    private String prefix;
    /**
     * 当前号段的最大id。
     */
    private long maxId;
    /**
     * 步长。
     */
    private int step;
    /**
     * 当前业务允许的最大id，可以用来控制id长度。
     */
    private long maxNumber;
    /**
     * 描述。
     */
    private String description;
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

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
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

    public long getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(long maxNumber) {
        this.maxNumber = maxNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
