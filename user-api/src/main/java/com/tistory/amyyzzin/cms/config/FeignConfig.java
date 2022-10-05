package com.tistory.amyyzzin.cms.config;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    @Qualifier(value = "mailgun")
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor("api",
            "f3faec0a7950b31c978058aab7d43986-4534758e-68b090d0");
    }

}
