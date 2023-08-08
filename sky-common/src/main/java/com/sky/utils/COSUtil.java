package com.sky.utils;


import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.AnonymousCOSCredentials;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.StorageClass;
import com.qcloud.cos.model.UploadResult;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerConfiguration;
import com.qcloud.cos.transfer.Upload;
import com.sky.properties.CosProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author wpc
 * @date 2023/8/8 14:26
 */

@Data
@AllArgsConstructor
@Slf4j
public class COSUtil {
    private CosProperties cosProperties;

    // 创建 COSClient 实例
    private COSClient createCOSClient() {

        // 设置用户身份信息。
        COSCredentials cred = new BasicCOSCredentials(cosProperties.getSecretId(), cosProperties.getSecretKey());


        // ClientConfig 中包含了后续请求 COS 的客户端设置：
        ClientConfig clientConfig = new ClientConfig();


        // 设置 bucket 的地域
        clientConfig.setRegion(new Region(cosProperties.getRegion()));


        // 设置请求协议, http 或者 https
        clientConfig.setHttpProtocol(HttpProtocol.https);

        // 设置 socket 读取超时，默认 30s
        clientConfig.setSocketTimeout(30*1000);
        // 设置建立连接超时，默认 30s
        clientConfig.setConnectionTimeout(30*1000);

        // 生成 cos 客户端。
        return new COSClient(cred, clientConfig);

    }

    // 创建 TransferManager 实例，这个实例用来后续调用高级接口
    public TransferManager createTransferManager() {

        COSClient cosClient = createCOSClient();
        ExecutorService threadPool = Executors.newFixedThreadPool(32);

        // 传入一个 threadpool, 若不传入线程池，默认 TransferManager 中会生成一个单线程的线程池。
        TransferManager transferManager = new TransferManager(cosClient, threadPool);

        TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
        transferManagerConfiguration.setMultipartUploadThreshold(5*1024*1024);
        transferManagerConfiguration.setMinimumUploadPartSize(1*1024*1024);
        transferManager.setConfiguration(transferManagerConfiguration);

        return transferManager;
    }

    void shutdownTransferManager(TransferManager transferManager) {
        // 指定参数为 true, 则同时会关闭 transferManager 内部的 COSClient 实例。
        // 指定参数为 false, 则不会关闭 transferManager 内部的 COSClient 实例。
        transferManager.shutdownNow(true);
    }

    // 上传对象
    public String uploadAndGetUrl(byte[] bytes, String objectName) throws CosServiceException, CosClientException, IOException {

        String key = objectName;
        File file = bytesToFile(bytes,objectName);

        // 使用高级接口必须先保证本进程存在一个 TransferManager 实例，如果没有则创建
        TransferManager transferManager = createTransferManager();

        PutObjectRequest putObjectRequest = new PutObjectRequest(cosProperties.getBucketName(), key, file);

        try {
            // 高级接口会返回一个异步结果Upload
            // 可同步地调用 waitForUploadResult 方法等待上传完成，成功返回 UploadResult, 失败抛出异常
            log.info("开始上传文件...");
            Upload upload = transferManager.upload(putObjectRequest);
            UploadResult uploadResult = upload.waitForUploadResult();
        } catch (CosServiceException e) {
            e.printStackTrace();
        } catch (CosClientException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        COSClient cosClient = (COSClient) transferManager.getCOSClient();
        String url = String.valueOf(cosClient.getObjectUrl(cosProperties.getBucketName(), key));

        // 确定本进程不再使用 transferManager 实例之后，关闭即可
        shutdownTransferManager(transferManager);

        String filePath = "D:\\IDEA\\sky-take-out\\" + objectName;
        deleteLocalFile(filePath);
        return url;
    }


    public File bytesToFile(byte[] bytes,String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = new File(fileName);
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }
    public void deleteLocalFile(String fileName){
        log.info("删除本地文件:{}",fileName);
        File file = new File(fileName);
        if (file.exists()){
            file.delete();
        }
    }
}
