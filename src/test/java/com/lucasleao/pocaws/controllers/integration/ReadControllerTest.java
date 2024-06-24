package com.lucasleao.pocaws.controllers.integration;


import com.lucasleao.pocaws.Utils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
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
import java.time.Duration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
    public void shouldReturnEmptyFilesList() throws Exception {

        mockMvc.perform(
                        get("/read/list")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

    }


    @Test
    public void shouldDownloadFile() throws Exception {

        MockMultipartFile mockFile = Utils.generateMockFile();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/write")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String objectUrl = mvcResult.getResponse().getHeader("Location");

        String objectId = Utils.getFileIdByUrl(objectUrl);

        mockMvc.perform(MockMvcRequestBuilders.get("/read/download/{fileId}", objectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + objectId + "\""))
                .andExpect(content().bytes("This is a test file".getBytes()));
    }

    @Test
    public void shouldNotFoundFile() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/read/download/{fileId}", "123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @Test
    public void shouldReturnFileUrl() throws Exception {

        MockMultipartFile mockFile = Utils.generateMockFile();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/write")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String objectUrl = mvcResult.getResponse().getHeader("Location");

        String objectId = Utils.getFileIdByUrl(objectUrl);


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/read/{fileId}", objectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Assertions.assertEquals(objectUrl, result.getResponse().getContentAsString());
    }

}
