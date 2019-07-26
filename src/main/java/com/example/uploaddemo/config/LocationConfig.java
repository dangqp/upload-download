package com.example.uploaddemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.MultipartConfigElement;
import java.io.File;

/**
 * Title:com.example.uploaddemo.config
 * Description:
 * Copyright: Copyright (c) 2018
 *
 * @author dangqp
 * @version 1.0
 * @created 2018/11/14  10:07
 */
//@Configuration
public class LocationConfig {

    @Value("${image.upload.path}")
    private String imagesPath;

    @Value("${file.upload.path}")
    private String filePath;

    /**
     * 配置文件上传临时路径
     * @return
     */
    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
//        String path = System.getProperty("user.dir") + filePath;
        //-----------------------------------------------
        String userDir = System.getProperty("user.dir");
        String path = userDir + filePath;
        File file = new File(path);
        if (!file.exists()){
            file.mkdirs();
        }
        //-----------------------------------------------
        /**
         * 不加上边代码，配置的临时保存路径为C:\Users\Administrator\AppData\Local\Temp\tomcat.xxx.port\filePath
         */
        factory.setLocation(path);
        return factory.createMultipartConfig();
    }

}
