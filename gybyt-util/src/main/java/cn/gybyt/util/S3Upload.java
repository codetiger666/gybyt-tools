package cn.gybyt.util;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * s3上传
 *
 * @program: gybyt-tools
 * @classname: S3Upload
 * @author: codetiger
 * @create: 2023/12/28 18:53
 **/
@Slf4j
public class S3Upload implements FileUpload {

    /**
     * 服务器地址
     */
    private String endpoint;
    /**
     * 访问密钥
     */
    private String accessKey;
    /**
     * 密钥
     */
    private String secretKey;
    /**
     * 区域
     */
    private Region region;
    /**
     * s3客户端
     */
    private S3Client s3Client;
    /**
     * 存储桶
     */
    private String bucket;
    /**
     * 分隔符
     */
    private final String delimiter = "/";
    /**
     * 公开访问链接
     */
    private String pubUrl;
    /**
     * 线程池
     */
    private final static ThreadPoolExecutor POOL = new ThreadPoolExecutor(5, 5, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>());

    /**
     * 构造器
     * @return
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 生成路径
     *
     * @param path
     * @return
     */
    public String genPath(String path) {
        if (BaseUtil.isEmpty(path)) {
            return "";
        }
        if (path.startsWith(delimiter)) {
            path = path.substring(1);
        }
        if (path.endsWith(delimiter)) {
            path = path.substring(0, path.lastIndexOf(this.delimiter));
        }
        return path + this.delimiter;
    }

    /**
     * 上传文件
     * @param inputStream
     * @param fileName
     * @return
     */
    public FileInfo putFileParallel(InputStream inputStream, String filePath , String fileName) {
        int partSize = 5 * 1024 * 1024;
        if (BaseUtil.isEmpty(fileName)) {
            fileName = BaseUtil.genKey(20);
        }
        String path = fileName.substring(0, fileName.lastIndexOf(this.delimiter) == -1 ? 0 : fileName.lastIndexOf(this.delimiter) + 1);
        if (BaseUtil.isNotEmpty(filePath)) {
            path = this.genPath(filePath);
        }
        String rowFileName = fileName.substring(fileName.lastIndexOf(this.delimiter) == -1 ? 0 : fileName.lastIndexOf(this.delimiter) + 1);
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (rowFileName.equals(fileType)) {
            fileType = "";
        }
        String nowFileName = BaseUtil.genKey(20) + "." + fileType;
        String uploadFileName = path + nowFileName;
        CreateMultipartUploadResponse createMultipartUploadResponse = s3Client
                .createMultipartUpload(CreateMultipartUploadRequest.builder()
                .bucket(this.bucket)
                .key(uploadFileName)
                .build());
        String uploadId = createMultipartUploadResponse.uploadId();
        List<CompletableFuture<UploadPartResponse>> completableFutures = new ArrayList<>();
        ArrayList<CompletedPart> completedParts = new ArrayList<>();
        try {
            int parts = inputStream.available() / partSize + 1;
            for (int part = 1; part <= parts; part++) {
                int finalPart = part;
                byte[] data = new byte[partSize];
                int post = inputStream.read(data);
                if (post == -1) {
                    break;
                }
                byte[] dataByte = Arrays.copyOf(data, post);
                completableFutures.add(CompletableFuture.supplyAsync(() -> {
                    UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                            .bucket(this.bucket)
                            .key(uploadFileName)
                            .uploadId(uploadId)
                            .partNumber(finalPart)
                            .contentLength((long) post)
                            .build();
                    UploadPartResponse uploadPartResponse = s3Client.uploadPart(uploadPartRequest,
                            RequestBody.fromBytes(dataByte));
                    log.warn("{} 分片 {} 上传完成", uploadFileName, finalPart);
                    System.out.println(uploadFileName + " 分片 " + finalPart + " 上传完成");
                    return uploadPartResponse;
                }));
            }
            CompletableFuture<Void> allCompletable = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0]));
            allCompletable.get();
            for (int part = 1; part <= parts; part++) {
                completedParts.add(CompletedPart.builder()
                        .partNumber(part)
                        .eTag(completableFutures.get(part - 1).get().eTag())
                        .build());
            }
            CompleteMultipartUploadResponse completeMultipartUploadResponse = s3Client.completeMultipartUpload(CompleteMultipartUploadRequest.builder()
                    .bucket(this.bucket)
                    .key(uploadFileName)
                    .uploadId(uploadId)
                    .multipartUpload(CompletedMultipartUpload.builder()
                            .parts(completedParts)
                            .build())
                    .build());
            FileInfo fileInfo = new FileInfo();
            fileInfo.setUrl(BaseUtil.isNotEmpty(this.pubUrl) ?
                    this.pubUrl + this.delimiter + completeMultipartUploadResponse.key() :
                    this.endpoint + this.delimiter + completeMultipartUploadResponse.key());
            fileInfo.setName(nowFileName);
            fileInfo.setRawName(rowFileName);
            return fileInfo;
        } catch (Exception e) {
            log.error("S3上传失败", e);
            s3Client.abortMultipartUpload(AbortMultipartUploadRequest.builder()
                    .bucket(this.bucket)
                    .key(uploadFileName)
                    .uploadId(uploadId)
                    .build()
            );
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 上传文件
     * @param inputStream
     * @param fileName
     * @return
     */
    @Override
    public FileInfo putFile(InputStream inputStream, String filePath , String fileName) {
        int partSize = 5 * 1024 * 1024;
        if (BaseUtil.isEmpty(fileName)) {
            fileName = BaseUtil.genKey(20);
        }
        String path = fileName.substring(0, fileName.lastIndexOf(this.delimiter) == -1 ? 0 : fileName.lastIndexOf(this.delimiter) + 1);
        if (BaseUtil.isNotEmpty(filePath)) {
            path = this.genPath(filePath);
        }
        String rowFileName = fileName.substring(fileName.lastIndexOf(this.delimiter) == -1 ? 0 : fileName.lastIndexOf(this.delimiter) + 1);
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (rowFileName.equals(fileType)) {
            fileType = "";
        }
        String nowFileName = BaseUtil.genKey(20) + "." + fileType;
        String uploadFileName = path + nowFileName;
        CreateMultipartUploadResponse createMultipartUploadResponse = s3Client
                .createMultipartUpload(CreateMultipartUploadRequest.builder()
                .bucket(this.bucket)
                .key(uploadFileName)
                .build());
        List<CompletedPart> completedParts = new ArrayList<>();
        String uploadId = createMultipartUploadResponse.uploadId();
        try {
            int parts = inputStream.available() / partSize + 1;
            for (int part = 1; part <= parts; part++) {
                byte[] data = new byte[partSize];
                int post = inputStream.read(data);
                if (post == -1) {
                    break;
                }
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data, 0, post);
                UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                        .bucket(this.bucket)
                        .key(uploadFileName)
                        .uploadId(uploadId)
                        .partNumber(part)
                        .contentLength((long) post)
                        .build();
                UploadPartResponse uploadPartResponse = s3Client.uploadPart(uploadPartRequest,
                        RequestBody.fromInputStream(byteArrayInputStream, post));
                byteArrayInputStream.close();
                byteArrayInputStream.mark(0);
                byteArrayInputStream.reset();
                completedParts.add(CompletedPart.builder()
                        .partNumber(part)
                        .eTag(uploadPartResponse.eTag())
                        .build());
                log.info("{} , 共 {} 分片, 分片 {} 上传完成", uploadFileName, parts, part);
            }
            CompleteMultipartUploadResponse completeMultipartUploadResponse = s3Client.completeMultipartUpload(CompleteMultipartUploadRequest.builder()
                    .bucket(this.bucket)
                    .key(uploadFileName)
                    .uploadId(uploadId)
                    .multipartUpload(CompletedMultipartUpload.builder()
                            .parts(completedParts)
                            .build())
                    .build());
            FileInfo fileInfo = new FileInfo();
            fileInfo.setUrl(BaseUtil.isNotEmpty(this.pubUrl) ?
                    this.pubUrl + this.delimiter + completeMultipartUploadResponse.key() :
                    this.endpoint + this.delimiter + completeMultipartUploadResponse.key());
            fileInfo.setName(nowFileName);
            fileInfo.setRawName(rowFileName);
            return fileInfo;
        } catch (Exception e) {
            log.error("S3上传失败", e);
            s3Client.abortMultipartUpload(AbortMultipartUploadRequest.builder()
                .bucket(this.bucket)
                .key(uploadFileName)
                .uploadId(uploadId)
                .build()
            );
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 获取文件
     * @param filePath 文件路径
     * @param fileName 文件名
     * @return 输入流
     */
    @Override
    public InputStream getFile(String filePath, String fileName) {
        filePath = this.genPath(filePath);
        fileName = filePath + fileName;
        return s3Client.getObject(GetObjectRequest.builder()
                .bucket(this.bucket)
                .key(fileName)
                .build());
    }

    /**
     * 构造对象
     */
    public static class Builder {
        /**
         * 服务器地址
         */
        private String endpoint;
        /**
         * 访问密钥
         */
        private String accessKey;
        /**
         * 密钥
         */
        private String secretKey;
        /**
         * 区域
         */
        private Region region;
        /**
         * 存储桶
         */
        private String bucket;
        /**
         * 公开访问链接
         */
        private String pubUrl;

        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder accessKey(String accessKey) {
            this.accessKey = accessKey;
            return this;
        }

        public Builder secretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        public Builder region(Region region) {
            this.region = region;
            return this;
        }

        public Builder bucket(String bucket) {
            this.bucket = bucket;
            return this;
        }

        public Builder pubUrl(String pubUrl) {
            this.pubUrl = pubUrl;
            return this;
        }

        public S3Upload build () {
            S3Upload s3Upload = new S3Upload();
            s3Upload.accessKey = this.accessKey;
            s3Upload.secretKey = this.secretKey;
            s3Upload.endpoint = this.endpoint;
            s3Upload.region = this.region;
            s3Upload.bucket = this.bucket;
            s3Upload.pubUrl = this.pubUrl;
            s3Upload.s3Client = S3Client.builder()
                    .region(this.region)
                    .endpointOverride(URI.create(this.endpoint))
                    .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(this.accessKey, this.secretKey)))
                    .build();
            return s3Upload;
        }
    }

}
