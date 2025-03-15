package com.sankuai.inf.leaf.boot.segment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@MapperScan("com.sankuai.inf.leaf.boot.segment")
@SpringBootConfiguration
public class SpringBootConfig {
}
