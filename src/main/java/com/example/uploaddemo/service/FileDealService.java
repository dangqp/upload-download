package com.example.uploaddemo.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * Title:com.example.uploaddemo.service
 * Description:
 * Copyright: Copyright (c) 2018
 * Company: 北京思特奇信息技术股份有限公司
 *
 * @author dangqp
 * @version 1.0
 * @created 2018/11/14  10:43
 */
public interface FileDealService {

    String upload(InputStream inputStream, String fileName);

    void downLoad(HttpServletRequest request, HttpServletResponse response, String fileName, String path);

    void getPhoto(HttpServletResponse response, String path);
}
