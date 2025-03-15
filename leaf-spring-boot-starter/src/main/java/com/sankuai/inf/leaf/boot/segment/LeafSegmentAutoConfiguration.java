package com.sankuai.inf.leaf.boot.segment;

import com.sankuai.inf.leaf.segment.SegmentIDGenImpl;
import com.sankuai.inf.leaf.segment.dao.IDAllocDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动配置号段算法。
 *
 * <p>conditionOnBean(IDAllocDao)不会对已存在的继承IDAllocDao的mapper生效，因此要注入
 * 上下文，从上下文手动获取IDAllocDao的实现类。</p>
 *
 * @author chen
 */
@Configuration
@ConditionalOnProperty(value = "leaf.segment.enable", havingValue = "true")
@EnableConfigurationProperties(LeafProperties.class)
@AutoConfiguration
public class LeafSegmentAutoConfiguration {

    /**
     * 创建号段id生成器。
     *
     * @param context 上下文。
     *
     * @return 生成器。
     */
    @Bean
    public SegmentIDGenImpl createIDGen(@Autowired ApplicationContext context) {
        SegmentIDGenImpl idGen = new SegmentIDGenImpl();
        idGen.setDao(context.getBean(IDAllocDao.class));
        idGen.init();
        LeafSegmentGenerator.setIdGen(idGen);
        return idGen;
    }
}
