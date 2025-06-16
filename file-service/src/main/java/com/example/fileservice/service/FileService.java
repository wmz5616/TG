package com.example.fileservice.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Service
public class FileService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * 上传文件到 MinIO
     * @param file 用户上传的文件
     * @return 文件的访问 URL
     * @throws Exception 上传过程中可能发生的错误
     */
    public String uploadFile(MultipartFile file) throws Exception {
        // 1. 生成一个独一无二的文件名，避免重名覆盖
        // 格式：UUID-原始文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String objectName = UUID.randomUUID().toString() + fileExtension;

        // 2. 使用 MinIO 客户端执行上传
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName) // 目标存储桶
                        .object(objectName) // 文件名
                        .stream(file.getInputStream(), file.getSize(), -1) // 文件流
                        .contentType(file.getContentType()) // 文件类型
                        .build());

        // 3. 拼接并返回文件的完整访问 URL
        // 格式: http://minio-endpoint/bucket-name/object-name
        return endpoint + "/" + bucketName + "/" + objectName;
    }
}
