package com.sankuai.inf.leaf.boot.segment;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class SegmentConfig {
    @Bean
    public SqlSessionFactory getSqlSessionFactory() throws Exception {
        Properties properties = new Properties();
        properties.load(SegmentConfig.class.getResourceAsStream("/leaf.properties"));
        PooledDataSource dataSource = new PooledDataSource();
        dataSource.setDriverProperties(properties);
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration(
                environment);
        configuration.addMapper(SegmentDao.class);
        configuration.setMapUnderscoreToCamelCase(true);
        return new SqlSessionFactoryBuilder().build(configuration);
    }
}
