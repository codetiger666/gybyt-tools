package cn.gybyt.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;

/**
 * s3 配置类
 *
 * @program: gybyt-tools
 * @classname: GybytS3Properties
 * @author: codetiger
 * @create: 2023/12/31 18:44
 **/
@Configuration
@ConfigurationProperties(prefix = "upload.s3")
public class S3Property {
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
     * 区域
     */
    private String regionValue;
    /**
     * 存储桶
     */
    private String bucket;
    /**
     * 公开访问链接
     */
    private String pubUrl;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getPubUrl() {
        return pubUrl;
    }

    public void setPubUrl(String pubUrl) {
        this.pubUrl = pubUrl;
    }

    public String getRegionValue() {
        return regionValue;
    }

    public void setRegionValue(String regionValue) {
        this.region = Region.of(regionValue);
        this.regionValue = regionValue;
    }
}
