package com.lucasleao.pocaws.controllers.integration;

import com.lucasleao.pocaws.Utils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import java.io.IOException;
import java.time.Duration;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WriteControllerTest {

    @Autowired
    MockMvc mockMvc;


    public static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
            .withServices(S3)
            .withEnv("DEBUG", "1")
            .withFileSystemBind("src/test/resources/scripts/01-create-bucket.sh", "/etc/localstack/init/ready.d/01-create-bucket.sh")
            .withStartupTimeout(Duration.ofMinutes(1))
            .waitingFor(Wait.forHealthcheck());

    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest")
            .withExposedPorts(27017);


    @BeforeAll
    static void beforeAll() {
        localStackContainer.start();
        mongoDBContainer.start();
    }

    @AfterAll
    static void afterAll() {
        localStackContainer.stop();
    }

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("aws.accesskey", localStackContainer::getAccessKey);
        registry.add("aws.secretkey", localStackContainer::getSecretKey);
        registry.add("aws.region", localStackContainer::getRegion);
        registry.add("app.localstack.url",() -> localStackContainer.getEndpointOverride(S3).toString());

        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }


    @Test
    public void shouldUploadFile() throws Exception {

        MockMultipartFile mockFile = Utils.generateMockFile();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/write")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Assertions.assertNotNull(mvcResult.getResponse().getHeaders("Location"));

    }

    @Test
    public void shouldNotUploadFileWithIOException() throws Exception {

        CustomMockMultipartFile mockFile = new CustomMockMultipartFile(
                "file",
                "test-file.txt",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "This is a test file".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/write")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

    }

    @Test
    public void shouldDeleteFile() throws Exception {

        MockMultipartFile mockFile = Utils.generateMockFile();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/write")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

       String objectUrl = mvcResult.getResponse().getHeader("Location");

       String objectId = Utils.getFileIdByUrl(objectUrl);

       mockMvc.perform(MockMvcRequestBuilders.delete("/write")
        .param("fileId", objectId)
       ).andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    public void shouldNotDeleteFile() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/write")
                .param("fileId", "123")
        ).andExpect(MockMvcResultMatchers.status().is4xxClientError());

    }

    private class CustomMockMultipartFile extends MockMultipartFile {

        public CustomMockMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
            super(name, originalFilename, contentType, content);
        }

        //Method is overrided, so that it throws an IOException, when it's called
        @Override
        public byte[] getBytes() throws IOException {
            throw new IOException();
        }
    }


}
