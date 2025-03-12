package com.sankuai.inf.leaf.boot.segment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 号段算法配置。
 *
 * @author chen
 */
@Component
@ConfigurationProperties(prefix = "leaf")
@PropertySource("classpath:leaf.properties")
public class LeafSegmentProperties {
    /**
     * 是否配置一个SegmentIDGenImpl。
     */
    private boolean enableSegment;
    /**
     * 是否配置LeafSegmentGenerator。
     */
    private boolean enableGenerator;

    public boolean getEnableSegment() {
        return enableSegment;
    }

    public void setEnableSegment(boolean enableSegment) {
        this.enableSegment = enableSegment;
    }

    public boolean getEnableGenerator() {
        return enableGenerator;
    }

    public void setEnableGenerator(boolean enableGenerator) {
        this.enableGenerator = enableGenerator;
    }
}
