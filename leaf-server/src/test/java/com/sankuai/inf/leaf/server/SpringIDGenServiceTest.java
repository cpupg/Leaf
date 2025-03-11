package com.sankuai.inf.leaf.server;

import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.server.dao.IDAllocDaoImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"}) //加载配置文件
public class SpringIDGenServiceTest {
    @Autowired
    IDGen idGen;
    @Autowired
    private IDAllocDaoImpl idAllocDao;

    @Test
    public void testGetId() throws Exception {
        int maxNumber = 999;
        int maxCycle = 10;
        int step = 100;
        idAllocDao.initTable(maxNumber, step);
        Set<Long> set = new HashSet<>();
        OutputStream os = new FileOutputStream("id.txt");
        for (int i = 0; i < maxCycle; i++) {
            for (int j = 0; j < maxNumber + 10; j++) {
                Result r = idGen.get(Constants.TEST_KEY);
                set.add(r.getId());
                String s = String.valueOf(r.getId());
                os.write(s.getBytes());
                os.write("\n".getBytes());
                if (j % 1000 == 0) {
                    os.flush();
                }
            }
        }
        os.close();
        Assert.assertEquals(maxNumber, set.size());
    }
}
