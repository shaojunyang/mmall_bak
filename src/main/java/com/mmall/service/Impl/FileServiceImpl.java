package com.mmall.service.Impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件处理 服务类
 *
 * @author
 * @create 2017-12-06 下午6:26
 **/
@Service
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    /**
     * 上传文件完成后返回 上传之后的文件名
     *
     * @param file
     * @param path
     * @return
     */
    @Override
    public String upload(MultipartFile file, String path) {
        // 拿到上传文件的原始文件名
        String filename = file.getOriginalFilename();
        // 获取 文件扩展名
        String fileExtensionName = filename.substring(filename.lastIndexOf(".") + 1);
        //上传完成的文件名称
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        // 使用 logger打印 上传日志
        logger.info("开始上传文件,上传文件的文件名是:{}, 上传的路径是:{},新文件名是:{}", filename, path, uploadFileName);

        // 上传文件
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            // 创建 文件夹
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path, uploadFileName);
        try {
            file.transferTo(targetFile);
            //     文件上传成功
            //    //将targetFile上传到ftp服务器上
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            // 已经上传到ftp服务器上
            // 上传到ftp之后、删除upload下面的文件
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常", e);
            return null;
        }

        return targetFile.getName();
    }
}
