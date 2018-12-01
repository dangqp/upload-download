package com.example.uploaddemo.controller;
import com.example.uploaddemo.service.server.FileDealServiceServerImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * Title:com.example.uploaddemo.controller
 * Description:
 * Copyright: Copyright (c) 2018
 * Company: 北京思特奇信息技术股份有限公司
 *
 * @author dangqp
 * @version 1.0
 * @created 2018/11/13  16:38
 */
@RestController
@RequestMapping("/demo")
public class UploadServerConttroller {

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${image.upload.path}")
    private String imagesPath;

    @Value("${file.upload.path}")
    private String filePath;

    /** 绝对路径 **/
    private static String absolutePath = "";

    /** 静态目录 **/
    private static String staticDir = "static";

    /** 文件存放的目录 **/
    private static String fileDir = "/upload/";
    
    @Autowired
    FileDealServiceServerImpl fileDealService;

    @PostMapping("/uploadServer")
    public String upload1(@RequestPart("file") MultipartFile file) {

        String filename = file.getOriginalFilename();
        if (StringUtils.isEmpty(filename))
            return null;


        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            return fileDealService.upload(inputStream,filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downloadFile(HttpServletRequest request, HttpServletResponse response,@RequestParam("fileName") String fileName) {

        fileDealService.downLoad(response,fileName);

    }

    @GetMapping("/getImage")
    public void show(HttpServletResponse response,@RequestParam("fileName") String fileName){
        fileDealService.getPhoto(response,fileName);
        
    }

}
