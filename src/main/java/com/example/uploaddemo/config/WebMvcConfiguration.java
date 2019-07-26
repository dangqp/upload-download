package com.example.uploaddemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.MultipartConfigElement;
import java.io.File;

//@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    @Value("${image.upload.path}")
    private String imagesPath;

    @Value("${file.upload.path}")
    private String filePath;

    @Value("${file.down.path}")
    private String downPath;

    @Value("${file.staticDir}")
    private String staticDir;


    @Value("${server.tomcat.basedir}")
    private String temp;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //上传的图片在C盘下的uploadFiles目录下，访问路径如： http://localhost:8081/uploadFiles/d3cf0281-bb7f-40e0-ab77-406db95ccf2c.jpg
        //其中uploadFiles表示访问的前缀。”file:C:/uploadFiles/”是文件真实的存储路径
        registry
                //静态资源对外暴露的访问路径
                .addResourceHandler(staticDir+"**")
                //文件上传目录
                .addResourceLocations("file:"+filePath);
    }

    /**
     * 文件上传配置
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        String path = System.getProperty("user.dir") + temp;
        File file = new File(path);
        if (!file.exists()){
            file.mkdirs();
        }
        factory.setLocation(temp);
        //单个文件最大
        factory.setMaxFileSize("10240KB"); //KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize("102400KB");
        return factory.createMultipartConfig();
    }
}