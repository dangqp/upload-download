package com.example.uploaddemo.service.server;

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
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
public class FileDealServiceServerImpl {

    private static final Logger log = LoggerFactory.getLogger(FileDealServiceServerImpl.class);


    @Value("${image.upload.path}")
    private String imagesPath;

    @Value("${file.upload.path}")
    private String filePath;

    @Value("${file.down.path}")
    private String downPath;

    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * 返回文件相对路径 ，如static\\file\\1542176429098.txt
     *
     * @param inputStream
     * @param fileName
     * @return
     */
    public String upload(InputStream inputStream, String fileName) {

        log.info("文件上传开始");
        /**创建文件路径**/
        //createDirIfNotExists();

        Path directory = Paths.get(filePath);
        File fileToSave = null;
        String offFix = StringUtils.substring(fileName, fileName.indexOf("."));
        String priFix = StringUtils.substring(fileName,0, fileName.indexOf("."));
        /**
         * file.staticDir=resource/static/
         * file.fileDir=file/
         *
         * 文件名称
         */
        String saveFileName = priFix+System.currentTimeMillis() + offFix;
        try {
            fileToSave = new File(saveFileName);
            // FileUtils.copyInputStreamToFile(inputStream, fileToSave);
             //文件目录不存在，创建
            if (!Files.exists(directory))
                Files.createDirectories(directory);
            //上传
            Files.copy(inputStream, directory.resolve(saveFileName));
            //FileUtils.copyInputStreamToFile(inputStream, fileToSave);
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

        log.info("文件上传真是路径" + fileToSave.getAbsolutePath());

        log.info("文件上传结束");
        return filePath+saveFileName;
    }

    /**
     * 文件下载
     */
    public void downLoad(HttpServletResponse response, String fileName) {

        File file = null;
        OutputStream fos = null;
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        /**
         * 此方法OK
         */
//        try {
//            String encodeName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
//            response.setHeader("Content-Disposition", "attachment;fileName=" + encodeName);
//            String path = ResourceUtils.getURL(filePath).getPath();
//            file = new File(filePath + "/" + fileName);
//            byte[] buff = new byte[1024];
//            fos = response.getOutputStream();
//            fis = new FileInputStream(file);
//            bis = new BufferedInputStream(fis);
//            int i = bis.read(buff);
//            while (i != -1) {
//                fos.write(buff);
//                i = bis.read(buff);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        /**
         * 此方法OK
         */
        try {
            log.info("下载文件名----" + fileName);
            String encodeName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
            response.setHeader("Content-Disposition", "attachment;fileName=" + encodeName);
            String path = ResourceUtils.getURL(filePath).getPath();
            log.info("下载文件路径---" + path);
            file = new File(filePath + "/" + fileName);
            fos = response.getOutputStream();
            fis = new FileInputStream(file);
            IOUtils.copy(fis, fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (fis != null) {
                    fis.close();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 显示图片
     *
     * @param response
     * @throws Exception
     */
    public void getPhoto(HttpServletResponse response, String fileName) {
        log.info("文件展示");
        //获取要读取的路径
        try {
            String encodeName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
            response.setContentType("image/jpeg;charset=utf-8");
            response.setHeader("Content-Disposition", "inline; filename="+encodeName);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(Files.readAllBytes(Paths.get(filePath).resolve(encodeName)));
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 创建文件夹路径
     */
    private void createDirIfNotExists() {

        log.info("文件路径不存在，开始创建路径");
        if (!filePath.isEmpty())
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

        filePath = file.getAbsolutePath();

        File upload = new File(filePath);
        if (!upload.exists()) {
            upload.mkdirs();
        }
        log.info("文件路径创建结束");
    }

    /**
     * 删除文件
     *
     * @param path 文件访问的路径upload开始 如： /upload/image/test.jpg
     * @return true 删除成功； false 删除失败
     */
    public boolean delete(String path) {
        File file = new File(path);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
}
