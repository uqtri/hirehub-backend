package org.example.hirehub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import com.cloudinary.utils.ObjectUtils;
import com.cloudinary.Cloudinary;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;
    @Value("${cloudinary.api-key}")
    private String apiKey;
    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Bean
    Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true));

    }
}
