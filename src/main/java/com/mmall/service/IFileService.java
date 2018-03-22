package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件处理 的 service
 *
 * @author
 * @create 2017-12-06 下午6:26
 **/

public interface IFileService {

    /**
     * 上传文件完成后返回 上传之后的文件名
     * @param file
     * @return
     */
    String upload(MultipartFile file,String path);
}
