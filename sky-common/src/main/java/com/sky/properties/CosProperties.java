package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sky.cos")
@Data
public class CosProperties {

    private String region;
    private String secretId;
    private String secretKey;
    private String bucketName;

}
