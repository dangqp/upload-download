package com.example.uploaddemo.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Title:com.example.uploaddemo.service
 * Description:
 * Copyright: Copyright (c) 2018
 * Company: 北京思特奇信息技术股份有限公司
 *
 * @author dangqp
 * @version 1.0
 * @created 2018/11/14  10:45
 */
@Service
public class FileDealServiceImpl implements FileDealService {

    private static final Logger log = LoggerFactory.getLogger(FileDealServiceImpl.class);

    /**
     * 绝对路径
     **/
    private String absolutePath = "";

//    /** 静态目录 **/
//    private String staticDir="static";
//
//    /** 文件存放的目录 **/
//    private String fileDir="/upload/file/";

    /**
     * 静态目录
     **/
    @Value("${file.staticDir}")
    private String staticDir;

    /**
     * 文件存放的目录
     **/
    @Value("${file.fileDir}")
    private String fileDir;

    @Value("${image.upload.path}")
    private String imagesPath;

    @Value("${file.upload.path}")
    private String filePath;
    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * 返回文件相对路径 ，如static\\file\\1542176429098.txt
     *
     * @param inputStream
     * @param fileName
     * @return
     */
    @Override
    public String upload(InputStream inputStream, String fileName) {

        log.info("文件上传开始");
        /**创建文件路径**/
        createDirIfNotExists();

        File fileToSave = null;
        String priFix = StringUtils.substring(fileName, fileName.indexOf("."));
        /**
         * file.staticDir=resource/static/
         * file.fileDir=file/
         *
         * 文件名称
         */
        String saveFileName = staticDir + fileDir + System.currentTimeMillis() + priFix;
        try {
            fileToSave = new File(absolutePath, saveFileName);
            FileUtils.copyInputStreamToFile(inputStream, fileToSave);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**
         * D:\\personal_work\\springboot-learning\\upload-demo\\target\\classes\\static\\file\\1542176429098.txt
         */

        //return fileToSave.getAbsolutePath();
        /**
         * static\file\1542176429098.txt
         */

        log.info("文件上传真是路径"+fileToSave.getAbsolutePath());
        
        log.info("文件上传结束");
        return saveFileName;
    }

    /**
     * 文件下载
     *
     * @param request
     * @param response
     * @param fileName
     * @param path
     */
    @Override
    public void downLoad(HttpServletRequest request, HttpServletResponse response, String fileName, String path) {

        log.info("文件下载开始");
        ServletOutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            Resource resource = resourceLoader.getResource("classpath:"+path + fileName);
            String path2 = resource.getURI().getPath();
            log.info("文件下载开始"+path2);
            String path1 = ResourceUtils.getURL("classpath:").getPath();
            response.setHeader("content-type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            String encodeName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
            response.setHeader("Content-Disposition", "attachment;filename=" + encodeName);
            inputStream = resource.getInputStream();
            outputStream = response.getOutputStream();
            //IOUtils.copy(inputStream,outputStream);
            FileCopyUtils.copy(inputStream, outputStream);
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 召唤jvm的垃圾回收器
            System.gc();
        }
    }

    /**
     * 显示图片
     * @param response
     * @param path
     * @throws Exception
     */
    public  void getPhoto(HttpServletResponse response, String path) {
        log.info("文件展示");
        //获取要读取的路径
        Resource resource = resourceLoader.getResource("classpath:" + path);
        FileInputStream fis;
        try {
            String path1 = resource.getURI().getPath();
            String path2 = ResourceUtils.getURL("classpath:").getPath();
             log.info("----------------"+path1+"----dddd---"+path2);
            File file = new File(path1);
            fis = new FileInputStream(file);
            long size = file.length();
            byte[] temp = new byte[(int) size];
            fis.read(temp, 0, (int) size);
            fis.close();
            byte[] data = temp;
            response.setContentType("image/png");
            OutputStream out = response.getOutputStream();
            out.write(data);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 创建文件夹路径
     */
    private void createDirIfNotExists() {

        if (!absolutePath.isEmpty())
            return;
        //获取跟目录
        File file = null;
        try {
            Resource resource = resourceLoader.getResource("classpath:");
            String path = resource.getURL().getPath();
            log.info("-------------path----------------" + path);
            String path1 = ResourceUtils.getURL("classpath:").getPath();
            //这个中方法也可以
            //file = new File(ResourceUtils.getURL("classpath:").getPath());
            file = new File(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("获取根目录失败，无法创建上传目录！");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!file.exists()) {
            file = new File("");
        }

        absolutePath = file.getAbsolutePath();

//        File upload = new File(absolutePath, staticDir + fileDir);
        File upload = new File(absolutePath);
        if (!upload.exists()) {
            upload.mkdirs();
        }
    }

    /**
     * 删除文件
     *
     * @param path 文件访问的路径upload开始 如： /upload/image/test.jpg
     * @return true 删除成功； false 删除失败
     */
    public boolean delete(String path) {
        File file = new File(absolutePath, staticDir + path);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
}
