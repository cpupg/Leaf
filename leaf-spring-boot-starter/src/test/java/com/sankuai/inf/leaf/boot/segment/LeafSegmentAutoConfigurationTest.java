package com.sankuai.inf.leaf.boot.segment;

import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.segment.dao.IDAllocDao;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(classes = SpringBootConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class LeafSegmentAutoConfigurationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void testSpring() {
        Assert.assertNotNull(context);
        Assert.assertNotNull(context.getBean(IDAllocDao.class));
        Assert.assertNotNull(context.getBean(IDGen.class));
    }
}
