package cn.gybyt.config;

import cn.gybyt.config.properties.FileUploadProperty;
import cn.gybyt.config.properties.S3Property;
import cn.gybyt.util.BaseUtil;
import cn.gybyt.util.FileUpload;
import cn.gybyt.util.S3Upload;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import software.amazon.awssdk.regions.Region;

/**
 * S3文件上传配置
 *
 * @program: gybyt-tools
 * @classname: FileUploadConfig
 * @author: codetiger
 * @create: 2023/12/31 18:53
 **/
@Configuration
@ConditionalOnClass(Region.class)
@Import(FileUploadProperty.class)
public class FileUploadConfig {

    @Bean
    public FileUpload fileUpload(FileUploadProperty fileUploadProperties) {
        if (BaseUtil.isEmpty(fileUploadProperties.getType())) {
            return null;
        }
        if (fileUploadProperties.getType().equals("s3")) {
            S3Property s3Properties = fileUploadProperties.getS3();
            S3Upload.Builder builder = S3Upload.builder();
            return builder.pubUrl(s3Properties.getPubUrl())
                    .bucket(s3Properties.getBucket())
                    .endpoint(s3Properties.getEndpoint())
                    .region(s3Properties.getRegion())
                    .accessKey(s3Properties.getAccessKey())
                    .secretKey(s3Properties.getSecretKey())
                    .build();
        }
        return null;
    }

}
