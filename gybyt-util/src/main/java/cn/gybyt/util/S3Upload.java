package cn.gybyt.util;

import cn.gybyt.concurrent.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.internal.util.Mimetype;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import cn.gybyt.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
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
    private final static ThreadPoolExecutor POOL = new ThreadPoolExecutor(5,
            10,
            5,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(5),
            new NamedThreadFactory("S3上传线程池"),
            // 默认调用线程处理超出的任务
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

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
     * @param inputStream 输入流
     * @param filePath 上传路径
     * @param fileName 文件名
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
        String uploadId = createMultipartUploadResponse.uploadId();
        List<CompletableFuture<UploadPartResponse>> completableFutures = new ArrayList<>();
        ArrayList<CompletedPart> completedParts = new ArrayList<>();
        log.info("文件: {}, 开始上传", uploadFileName);
        try {
            int parts = inputStream.available() / partSize + 1;
            for (int part = 1; part <= parts; part++) {
                byte[] data = new byte[partSize];
                int finalPart = part;
                int post = inputStream.read(data);
                if (post == -1 && part == 1) {
                    data = new byte[0];
                } else if (post == -1) {
                    break;
                }
                log.debug("文件: {} , 分片 {} 开始上传, 共 {} 片", uploadFileName, finalPart, parts);
                byte[] finalData = data;
                completableFutures.add(CompletableFuture.supplyAsync(() -> {
                    try (
                        InputStream partInputStream = new ByteArrayInputStream(finalData, 0, post)) {
                        UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                                .bucket(this.bucket)
                                .key(uploadFileName)
                                .uploadId(uploadId)
                                .partNumber(finalPart)
                                .contentLength((long) post)
                                .build();
                        UploadPartResponse uploadPartResponse = s3Client.uploadPart(uploadPartRequest,
                                RequestBody.fromContentProvider(() -> partInputStream, post, Mimetype.MIMETYPE_OCTET_STREAM));
                        log.debug("文件: {} , 分片 {} 上传完成, 共 {} 片", uploadFileName, finalPart, parts);
                        partInputStream.close();
                        return uploadPartResponse;
                    } catch (IOException e) {
                        log.error(BaseUtil.format("文件: {} , 分片 {} 上传失败", uploadFileName, finalPart), e);
                        throw new RuntimeException(e);
                    }
                }, POOL));
            }
            CompletableFuture<Void> allCompletable = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0]));
            allCompletable.get();
            for (int part = 1; part <= parts; part++) {
                completedParts.add(CompletedPart.builder()
                        .partNumber(part)
                        .eTag(completableFutures.get(part - 1).get().eTag())
                        .build());
            }
            log.info("文件: {}, 上传完成", uploadFileName);
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
    public FileInfo putFileNoParallel(InputStream inputStream, String filePath , String fileName) {
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
            log.info("文件: {}, 开始上传", uploadFileName);
            for (int part = 1; part <= parts; part++) {
                byte[] data = new byte[partSize];
                int post = inputStream.read(data);
                if (post == -1) {
                    break;
                }
                log.debug("文件: {} , 分片 {} 开始上传, 共 {} 片", uploadFileName, part, parts);
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
                completedParts.add(CompletedPart.builder()
                        .partNumber(part)
                        .eTag(uploadPartResponse.eTag())
                        .build());
                log.debug("文件: {} , 分片 {} 上传完成, 共 {} 片", uploadFileName, part, parts);
            }
            CompleteMultipartUploadResponse completeMultipartUploadResponse = s3Client.completeMultipartUpload(CompleteMultipartUploadRequest.builder()
                    .bucket(this.bucket)
                    .key(uploadFileName)
                    .uploadId(uploadId)
                    .multipartUpload(CompletedMultipartUpload.builder()
                            .parts(completedParts)
                            .build())
                    .build());
            log.info("文件: {}, 上传完成", uploadFileName);
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
            log.info("s3Upload初始化成功");
            return s3Upload;
        }
    }

}
