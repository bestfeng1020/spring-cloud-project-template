package org.bestfeng.template;

import org.hswebframework.web.authorization.basic.configuration.EnableAopAuthorize;
import org.hswebframework.web.loggin.aop.EnableAccessLogger;
import org.hswebframework.web.service.file.FileService;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * @author zhouhao
 * @since 1.0
 */
@SpringCloudApplication
@EnableAccessLogger
@EnableAopAuthorize
public class FileServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileServerApplication.class, args);
    }

    @Bean
    @Primary
    public FileService fileService() {
        return new FixInputStreamCloseLocalFileService();
    }
}
