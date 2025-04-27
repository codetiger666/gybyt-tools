package cn.gybyt.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 文件上传配置
 *
 * @program: gybyt-tools
 * @classname: FileUploadProperties
 * @author: codetiger
 * @create: 2023/12/31 18:51
 **/
@Configuration
@Component
@ConfigurationProperties(prefix = "upload")
public class FileUploadProperty {

    /**
     * 类型
     */
    private String type;
    /**
     * s3配置
     */
    private S3Property s3 = new S3Property();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public S3Property getS3() {
        return s3;
    }

    public void setS3(S3Property s3) {
        this.s3 = s3;
    }
}
