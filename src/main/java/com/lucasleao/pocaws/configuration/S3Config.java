package com.lucasleao.pocaws.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Value("${app.localstack}")
    private boolean islocalStack;

    @Value("${app.localstack.url}")
    private String endpoint;


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Example API")
                        .version("1.0")
                        .description("A sample API to demonstrate OpenAPI documentation with Spring Boot and springdoc-openapi."));
    }

    @Bean
    public AmazonS3 s3Client(BasicAWSCredentials awsCredentials, S3Properties s3Properties) {

        if (islocalStack) {
            return AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, s3Properties.region()))
                    .enablePathStyleAccess()
                    .build();
        }

        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(s3Properties.region())
                .build();
    }

    @Bean
    BasicAWSCredentials awsCredentialsProvider(S3Properties s3Properties) {
        return new BasicAWSCredentials(s3Properties.accessKey(), s3Properties.secretKey());
    }

}
