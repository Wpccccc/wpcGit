package com.sky.config;

import com.sky.properties.CosProperties;
import com.sky.utils.COSUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wpc
 * @date 2023/8/8 14:00
 */

@Configuration
@Slf4j
public class CosConfig {

    @Bean
    @ConditionalOnMissingBean
    public COSUtil cosUtil(CosProperties cosProperties) {
        log.info("开始创建腾讯云文件上传对象:{}", cosProperties);
        return new COSUtil(cosProperties);
    }
}
