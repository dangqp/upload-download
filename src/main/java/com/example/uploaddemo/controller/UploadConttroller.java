package com.example.uploaddemo.controller;

import com.example.uploaddemo.service.FileDealService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Title:com.example.uploaddemo.controller
 * Description:
 * Copyright: Copyright (c) 2018
 *
 * @author dangqp
 * @version 1.0
 * @created 2018/11/13  16:38
 */
@RestController
@RequestMapping("/demo")
public class UploadConttroller {

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
    
    @PostMapping("/upload")
    public String upload(@RequestPart("file") MultipartFile file) {

        //createDirIfNotExists();
        File fileToSave = null;
        String filename = file.getOriginalFilename();
        String resultPath = fileDir +System.currentTimeMillis()+ filename;
        try {
            byte[] bytes = file.getBytes();
            fileToSave = new File(absolutePath, staticDir + resultPath);
            //--------------one
            // FileCopyUtils.copy(bytes, fileToSave);
            //-----------------two
//            FileOutputStream fos = new FileOutputStream(resultPath);
//            fos.write(bytes);
            //----------------three   ok
            FileUtils.copyInputStreamToFile(file.getInputStream(),fileToSave);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return fileToSave.getAbsolutePath();
    }

    @Autowired
    FileDealService fileDealService;

    @PostMapping("/upload1")
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

    @RequestMapping(value = "/downloadFile", method = RequestMethod.GET)
    public void downloadFile(HttpServletRequest request, HttpServletResponse response,@RequestParam("fileName") String fileName) {

        //String fileName = "1542175634808.txt";
        String path = "static/file/";
        fileDealService.downLoad(request,response,fileName,path);

    }

    @GetMapping("/show")
    public void show(HttpServletRequest request, HttpServletResponse response,@RequestParam("fileName") String fileName){
        String fileName1 = fileName==null?"static/file/1542178030837.jpg":"static/file/"+fileName;
        fileDealService.getPhoto(response,fileName1);
        
    }

}
