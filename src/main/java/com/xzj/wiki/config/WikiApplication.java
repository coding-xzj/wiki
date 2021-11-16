package com.xzj.wiki.config;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

//@ComponentScan({"com.xzj.wiki","com.xzj.test"})
@ComponentScan("com.xzj.wiki")
@MapperScan("com.xzj.wiki.mapper")
@SpringBootApplication
public class WikiApplication {

    private static final Logger LOG = LoggerFactory.getLogger(WikiApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext app = SpringApplication.run(WikiApplication.class, args);
        Environment env = app.getEnvironment();
        LOG.info("启动成功！！");
        LOG.info("地址：\thttps://127.0.0.1:{}", env.getProperty("server.port"));
    }

}
