package com.sankuai.inf.leaf.server.dao;

import com.sankuai.inf.leaf.segment.dao.IDAllocDao;
import com.sankuai.inf.leaf.segment.model.LeafAlloc;
import com.sankuai.inf.leaf.server.Constants;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.util.List;

/**
 * dao实现。
 */
public class IDAllocDaoImpl implements IDAllocDao {

    private SqlSessionFactory sqlSessionFactory;

    public IDAllocDaoImpl(DataSource dataSource) {
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(IDAllocMapper.class);
        configuration.setMapUnderscoreToCamelCase(true);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    @Override
    public List<LeafAlloc> getAllLeafAllocs() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(false)) {
            IDAllocMapper mapper = sqlSession.getMapper(IDAllocMapper.class);
            return mapper.getAllLeafAllocs();
        }
    }

    @Override
    public LeafAlloc updateMaxIdAndGetLeafAlloc(String tag) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            LeafAlloc leafAlloc = sqlSession.getMapper(IDAllocMapper.class).updateMaxIdAndGetLeafAlloc(tag);
            sqlSession.commit();
            return leafAlloc;
        }
    }

    @Override
    public LeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(LeafAlloc leafAlloc) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            LeafAlloc result = sqlSession.getMapper(IDAllocMapper.class)
                    .updateMaxIdByCustomStepAndGetLeafAlloc(leafAlloc);
            sqlSession.commit();
            return result;
        }
    }

    @Override
    public List<String> getAllTags() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(false)) {
            return sqlSession.getMapper(IDAllocMapper.class).getAllTags();
        }
    }

    /**
     * 初始化号段表。
     */
    public void initTable(long maxNumber, int step) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(false)) {
            IDAllocMapper mapper = sqlSession.getMapper(IDAllocMapper.class);
            LeafAlloc leafAlloc = mapper.getLeafAlloc("test");
            if (leafAlloc == null) {
                leafAlloc = new LeafAlloc();
                leafAlloc.setKey(Constants.TEST_KEY);
                leafAlloc.setPrefix(Constants.TEST_KEY);
                leafAlloc.setMaxId(0);
                leafAlloc.setStep(Constants.DEFAULT_STEP);
                leafAlloc.setMaxNumber(maxNumber);
                leafAlloc.setDescription("测试用例");
                mapper.insertLeafAlloc(leafAlloc);
            }
            leafAlloc.setStep(step);
            leafAlloc.setMaxNumber(maxNumber);
            mapper.updateMaxIdByCustomStepAndGetLeafAlloc(leafAlloc);
            sqlSession.commit();
        }
    }

    @Override
    public LeafAlloc resetLeafAlloc(String key, int step) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(false)) {
            IDAllocMapper mapper = sqlSession.getMapper(IDAllocMapper.class);
            mapper.resetLeafAlloc(key, step);
            sqlSession.commit();
            return mapper.getLeafAlloc("test");
        }
    }
}
