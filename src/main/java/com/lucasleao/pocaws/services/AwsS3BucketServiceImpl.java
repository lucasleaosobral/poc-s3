package com.lucasleao.pocaws.services;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;


@Service
@ConditionalOnProperty(
        value="app.localstack",
        havingValue = "false")
public class AwsS3BucketServiceImpl extends GenericBucketService implements BucketService {


    public AwsS3BucketServiceImpl(AmazonS3 s3Client, ApplicationEventPublisher applicationEventPublisher) {
        super(s3Client, applicationEventPublisher);
    }

    @Override
    public String buildFileUrl(String name) {
        return "https://" + super.getBucketName() + ".s3." + super.getRegion() + ".amazonaws.com/" + name;
    }

}
