package com.sankuai.inf.leaf.boot.segment;

import com.sankuai.inf.leaf.segment.SegmentIDGenImpl;
import com.sankuai.inf.leaf.segment.dao.IDAllocDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动配置号段算法。
 *
 * @author chen
 */
@Configuration
@ConditionalOnProperty(value = "leaf.segment.enable", havingValue = "true")
@ConditionalOnBean(IDAllocDao.class)
@ConditionalOnMissingBean(SegmentIDGenImpl.class)
public class LeafSegmentAutoConfiguration {
    public LeafSegmentAutoConfiguration() {
        System.out.println("init leaf segment");
    }

    /**
     * 创建号段id生成器。
     *
     * @param idAllocDao dao。
     *
     * @return 生成器。
     */
    @Bean
    public SegmentIDGenImpl createIDGen(@Autowired IDAllocDao idAllocDao) {
        SegmentIDGenImpl idGen = new SegmentIDGenImpl();
        idGen.setDao(idAllocDao);
        idGen.init();
        LeafSegmentGenerator.setIdGen(idGen);
        return idGen;
    }
}
