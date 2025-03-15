package com.sankuai.inf.leaf.server;

import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.server.dao.IDAllocDaoImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"}) //加载配置文件
public class SpringIDGenServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringIDGenServiceTest.class);
    @Autowired
    private IDGen idGen;
    @Autowired
    private IDAllocDaoImpl idAllocDao;

    @Test
    public void testGetId() throws Exception {
        int maxNumber = 999999;
        int maxCycle = 10;
        int step = 1000;
        idAllocDao.initTable(maxNumber, step);
        Set<Long> set = new HashSet<>();
        Set<String> set1 = new HashSet<>();
        OutputStream os = Files.newOutputStream(Paths.get("id.txt"));
        for (int i = 0; i < maxCycle; i++) {
            for (int j = 0; j < maxNumber + 10; j++) {
                Result r = idGen.get(Constants.TEST_KEY);
                set.add(r.getId());
                set1.add(r.getIdString());
                String s = String.valueOf(r.getId());
                os.write(s.getBytes());
                os.write("\n".getBytes());
                if (j % 1000 == 0) {
                    os.flush();
                }
                if (j == maxNumber) {
                    // 递增，连续，不重复
                    Assert.assertEquals(maxNumber, set.size());
                    Assert.assertEquals(maxNumber, set1.size());
                    set.clear();
                    set1.clear();
                    LOGGER.info("测试{}-{}通过", i, j);
                }
            }
        }
        os.close();
    }
}
