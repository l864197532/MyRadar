package com.xqq.myradar.twoDataSourse;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * @program:
 * @description: 数据库配置2
 * @author: wys
 * @create: 2019-12-03 17:03
 **/
@Configuration
@MapperScan(basePackages = "com.xqq.myradar.radar.Dao",sqlSessionFactoryRef = "twoSqlSessionFactory")
public class TwoDataSourceConfig {
    @Value("${spring.datasource.two.driver-class-name}")
    String driverClass;
    @Value("${spring.datasource.two.url}")
    String url;
    @Value("${spring.datasource.two.username}")
    String userName;
    @Value("${spring.datasource.two.password}")
    String passWord;

    @Bean(name = "twoDataSource")
    @ConfigurationProperties("spring.datasource.two")
    public DataSource masterDataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(passWord);
        return dataSource;
    }
    @Bean(name = "twoSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("twoDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
//        factoryBean.setTypeAliasesPackage("com.example.domain");
        return factoryBean.getObject();
    }

    @Bean(name = "twoSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionFactoryTemplate(@Qualifier("twoSqlSessionFactory")SqlSessionFactory sqlSessionFactory ) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}

