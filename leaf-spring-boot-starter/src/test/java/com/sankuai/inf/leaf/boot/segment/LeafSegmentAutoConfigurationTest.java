package com.sankuai.inf.leaf.boot.segment;

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
    }
}
