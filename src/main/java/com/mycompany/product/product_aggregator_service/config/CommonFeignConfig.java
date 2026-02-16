package com.mycompany.product.product_aggregator_service.config;

import com.mycompany.product.product_aggregator_service.exception.UpstreamClientException;
import com.mycompany.product.product_aggregator_service.exception.UpstreamNotFoundException;
import com.mycompany.product.product_aggregator_service.exception.UpstreamServerException;
import feign.Logger;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonFeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return new CommonUpstreamErrorDecoder();
    }

    static class CommonUpstreamErrorDecoder implements ErrorDecoder {
        private final ErrorDecoder defaultDecoder = new Default();

        @Override
        public Exception decode(String methodKey, Response response) {
            int status = response.status();

            if (status == 404) return new UpstreamNotFoundException(methodKey + " returned 404");
            if (status >= 400 && status < 500) return new UpstreamClientException(methodKey + " returned " + status);
            if (status >= 500) return new UpstreamServerException(methodKey + " returned " + status);

            return defaultDecoder.decode(methodKey, response);
        }
    }
}