package com.sankuai.inf.leaf.boot.segment;

import com.sankuai.inf.leaf.segment.SegmentIDGenImpl;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class LeafSegmentAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withPropertyValues("leaf.segment.enable=true")
            .withConfiguration(AutoConfigurations.of(LeafSegmentAutoConfiguration.class));

    @Test
    public void test() {
        contextRunner.withUserConfiguration(IDAllocDaoConfig.class).run(context -> {
            Assert.assertNotNull(context.getBean(SegmentIDGenImpl.class));
            Assert.assertNotNull(context.getBean(IdAllocDaoImpl.class));
        });
    }

    @Configuration(proxyBeanMethods = false)
    static class IDAllocDaoConfig {

        @Bean
        IdAllocDaoImpl getIDAllocDao() {
            return new IdAllocDaoImpl();
        }
    }
}
