package com.project.sharedfolderserver.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.project.sharedfolderserver.BaseIT;
import com.project.sharedfolderserver.TestUtils;
import com.project.sharedfolderserver.v1.file.FileDto;
import com.project.sharedfolderserver.v1.utils.error.Error;
import com.project.sharedfolderserver.v1.utils.http.Response;
import com.project.sharedfolderserver.v1.utils.json.JSON;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class FileHttpControllerIT extends BaseIT {
    private static final String SQL_SCRIPTS_PATH = "/sql/file/";
    private JsonNode caseObject;
    private JsonNode preRequest;
    private JsonNode expectedResult;

    @BeforeEach
    void clearCase() {
        caseObject = null;
        preRequest = null;
        expectedResult = null;
    }

    @Nested
    class Get {

        @DisplayName("Success: Get file list")
        @Test
        @Sql(scripts = {SQL_SCRIPTS_PATH + "cleanTables.sql", SQL_SCRIPTS_PATH + "getFileList.sql"})
        void successGetFileList() throws IOException {
            initializeCaseTest("file/success-get-file-list");
            List<FileDto> expectedData = JSON.objectMapper.convertValue(expectedResult.get("data"), new TypeReference<>() {
            });

            ResponseEntity<Response<List<FileDto>>> response =
                    restTemplate.exchange(
                            getUrl(preRequest.get("path").asText())
                            , HttpMethod.valueOf(preRequest.get("method").asText())
                            , null
                            , new ParameterizedTypeReference<>() {
                            });

            assertNotNull(response, "expect to have a response");
            Response<List<FileDto>> actualBody = response.getBody();
            assertNotNull(actualBody, "expect to have a body in the response");
            assertTrue(CollectionUtils.isEmpty(actualBody.getErrors()), "expect no errors");
            List<FileDto> actualData = actualBody.getData();
            assertNotNull(actualData, "expect to have file list in the response");
            assertEquals(expectedData, actualData, "expect the same file list");
        }

        @DisplayName("Success: Download file")
        @Test
        @Sql(scripts = {SQL_SCRIPTS_PATH + "cleanTables.sql", SQL_SCRIPTS_PATH + "downloadFile.sql"})
        void successDownload() throws IOException {
            //preparing
            initializeCaseTest("file/success-download-file");
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpGet getRequest = new HttpGet(getUrl(preRequest.get("path").asText()));

                // executing
                try (CloseableHttpResponse response = client.execute(getRequest)) {

                    // validating
                    assertNotNull(response, "expect to have a response");
                    assertNotNull(response.getStatusLine(), "expect to have a status");
                    assertEquals(HttpStatus.OK.value(), response.getStatusLine().getStatusCode()," expect ok status code");
                    byte[] fileContent = response.getEntity().getContent().readAllBytes();
                    assertNotNull(fileContent, "expect to have a body in the response");
                    JsonNode expectedData = expectedResult.get("data");
                    assertEquals(Long.valueOf(expectedData.get("size").asText().split(" ")[0]), fileContent.length, "expect the same length");
                    assertEquals(expectedData.get("content").asText().getBytes().length, fileContent.length, "expect the same length");
                    for (int i=0; i<fileContent.length; i++) {
                        assertEquals(expectedData.get("content").asText().getBytes()[i], fileContent[i], "expect the same bytes");
                    }
                }
            }
        }

        @DisplayName("Failed: Download file")
        @Test
        void failedDownload() throws IOException {
            initializeCaseTest("file/failed-download-file");
            List<Error> expectedErrors = JSON.objectMapper.convertValue(expectedResult.get("errors"), new TypeReference<>() {
            });

            ResponseEntity<Response<FileDto>> response =
                    restTemplate.exchange(
                            getUrl(preRequest.get("path").asText())
                            , HttpMethod.valueOf(preRequest.get("method").asText())
                            , null
                            , new ParameterizedTypeReference<>() {
                            });

            assertNotNull(response, "expect to have a response");
            Response<FileDto> actualBody = response.getBody();
            assertNotNull(actualBody, "expect to have a body in the response");
            List<Error> actualErrors = actualBody.getErrors();
            assertFalse(CollectionUtils.isEmpty(actualErrors), "expect errors");
            FileDto actualData = actualBody.getData();
            assertNull(actualData, "expect data to be null");
            assertEquals(expectedErrors, actualErrors, "expected the same errors");
        }

        private void initializeCaseTest(String caseName) throws IOException {
            caseObject = TestUtils.getTestCase(caseName);
            preRequest = caseObject.get("preRequest");
            expectedResult = caseObject.get("expectedResult");
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    class Post {

        public static final String FIELD_NAME_REPLACE_SIGN = "%fieldName%";
        public static final String TYPE_NAME_REPLACE_SIGN = "%typeName%";


        @DisplayName("Success: upload file")
        @Test
        void successUploadFile() throws Exception {
            // preparing
            initializeCaseTest("file/success-upload-file");
            FileDto expectedData = JSON.objectMapper.convertValue(expectedResult.get("data"), new TypeReference<>() {
            });
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost postRequest = new HttpPost(getUrl(preRequest.get("path").asText()));
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                builder.addBinaryBody("file",  preRequest.get("body").get("content").asText().getBytes(), ContentType.DEFAULT_BINARY, expectedData.getName());
                org.apache.http.HttpEntity entity = builder.build();
                postRequest.setEntity(entity);

                // executing
                try (CloseableHttpResponse response = client.execute(postRequest)) {

                    // validating
                    assertNotNull(response, "expect to have a response");
                    assertNotNull(response.getStatusLine(), "expect to have a status");
                    assertEquals(HttpStatus.CREATED.value(), response.getStatusLine().getStatusCode()," expect created status code");
                    Response<FileDto> actualBody = JSON.objectMapper.readValue(response.getEntity().getContent(), new TypeReference<>() {
                    });
                    assertNotNull(actualBody, "expect to have a body in the response");
                    assertTrue(CollectionUtils.isEmpty(actualBody.getErrors()), "expect no errors");
                    FileDto actualData = actualBody.getData();
                    assertNotNull(actualData, "expect to have a file in the response");
                    TestUtils.assertEqualsExcludedFields(expectedData, actualData, "id", "dateAdded", "dateModified");
                }
            }

        }

        @DisplayName("Failed: upload file with existing name")
        @Test
        @Sql(scripts = {SQL_SCRIPTS_PATH + "cleanTables.sql", SQL_SCRIPTS_PATH + "uploadFileExistingName.sql"})
        void failedUploadExistingName() throws IOException {
            // preparing
            initializeCaseTest("file/failed-upload-file-existing-name");
            List<Error> expectedErrors = JSON.objectMapper.convertValue(expectedResult.get("errors"), new TypeReference<>() {
            });
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost postRequest = new HttpPost(getUrl(preRequest.get("path").asText()));
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                builder.addBinaryBody("file",  preRequest.get("body").get("content").asText().getBytes(), ContentType.DEFAULT_BINARY,  preRequest.get("body").get("name").asText());
                org.apache.http.HttpEntity entity = builder.build();
                postRequest.setEntity(entity);

                // executing
                try (CloseableHttpResponse response = client.execute(postRequest)) {

                    // validating
                    assertNotNull(response, "expect to have a response");
                    assertNotNull(response.getStatusLine(), "expect to have a status");
                    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusLine().getStatusCode()," expect bad request status code");
                    Response<FileDto> actualBody = JSON.objectMapper.readValue(response.getEntity().getContent(), new TypeReference<>() {
                    });
                    assertNotNull(actualBody, "expect to have a body in the response");
                    assertFalse(CollectionUtils.isEmpty(actualBody.getErrors()), "expected to get errors");
                    FileDto actualData = actualBody.getData();
                    assertNull(actualData, "expect data to be null");
                    assertEquals(expectedErrors, actualBody.getErrors(), "expected the same errors");
                }
            }
        }

        @DisplayName("Failed: upload file with illegal name")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"badname", "usik^*.ld", "=-0", " ggg.file.s"})
        void failedUploadIllegalName(String badname) throws IOException {
            // preparing
            initializeCaseTest("file/failed-upload-file-illegal-name");
            List<Error> expectedErrors = JSON.objectMapper.convertValue(expectedResult.get("errors"), new TypeReference<>() {
            });
            String errorMessage;
            if (badname != null && badname.isEmpty()) {
                errorMessage = "file could not be created. file name can not be empty";
            } else {
                errorMessage = String.format("file could not be created. Illegal file name %s, file name must be in the form of NAME.KIND, using letters, numbers. some special characters are illegal", badname);
            }
            expectedErrors.stream().findFirst().ifPresent(error -> error.setMessage(errorMessage));
            ((ObjectNode) (preRequest.get("body"))).put("name", badname);
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost postRequest = new HttpPost(getUrl(preRequest.get("path").asText()));
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                builder.addBinaryBody("file", preRequest.get("body").get("content").asText().getBytes(), ContentType.DEFAULT_BINARY, preRequest.get("body").get("name").asText());
                org.apache.http.HttpEntity entity = builder.build();
                postRequest.setEntity(entity);

                // executing
                try (CloseableHttpResponse response = client.execute(postRequest)) {

                    // validating
                    assertNotNull(response, "expect to have a response");
                    assertNotNull(response.getStatusLine(), "expect to have a status");
                    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusLine().getStatusCode(), " expect bad request status code");
                    Response<FileDto> actualBody = JSON.objectMapper.readValue(response.getEntity().getContent(), new TypeReference<>() {
                    });
                    assertNotNull(actualBody, "expect to have a body in the response");
                    assertFalse(CollectionUtils.isEmpty(actualBody.getErrors()), "expected to have errors");
                    FileDto actualData = actualBody.getData();
                    assertNull(actualData, "expect data to be null");
                    assertEquals(expectedErrors, actualBody.getErrors(), "expected the same errors");

                }
            }
        }

        @DisplayName("Failed: upload file with illegal content")
        @ParameterizedTest
        @MethodSource("generateIllegalUploadRequestParameters")
        void failedUploadIllegalRequestParameters( JsonNode illegalFieldContent, String errorMessage) throws IOException {
            // preparing
            initializeCaseTest("file/failed-upload-file-illegal-content");
            List<Error> expectedErrors = JSON.objectMapper.convertValue(expectedResult.get("errors"), new TypeReference<>() {
            });
            Error expectedError = expectedErrors.stream().findFirst().orElseThrow();
            ((ObjectNode) (preRequest.get("body"))).put("name",  illegalFieldContent.toString());
            expectedError.setMessage(expectedError.getMessage().replace(FIELD_NAME_REPLACE_SIGN, errorMessage));

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost postRequest = new HttpPost(getUrl(preRequest.get("path").asText()));
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                builder.addBinaryBody("file", preRequest.get("body").get("content").asText().getBytes(), ContentType.DEFAULT_BINARY, illegalFieldContent.toString());
                org.apache.http.HttpEntity entity = builder.build();
                postRequest.setEntity(entity);

                // executing
                try (CloseableHttpResponse response = client.execute(postRequest)) {

                    // validating
                    assertNotNull(response, "expect to have a response");
                    assertNotNull(response.getStatusLine(), "expect to have a status");
                    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusLine().getStatusCode(), " expect bad request status code");
                    Response<FileDto> actualBody = JSON.objectMapper.readValue(response.getEntity().getContent(), new TypeReference<>() {
                    });
                    assertNotNull(actualBody, "expect to have a body in the response");
                    assertFalse(CollectionUtils.isEmpty(actualBody.getErrors()), "expected to have errors");
                    FileDto actualData = actualBody.getData();
                    assertNull(actualData, "expect data to be null");
                    assertEquals(expectedErrors, actualBody.getErrors(), "expected the same errors");
                }
            }
        }

        private Stream<Arguments> generateIllegalUploadRequestParameters() {
            return Stream.of(
                    Arguments.of(JSON.objectMapper.createArrayNode(), "[]"),
                    Arguments.of( new IntNode(8), "8"),
                    Arguments.of( new LongNode(8L), "8"),
                    Arguments.of( JSON.object(),"{}")

            );
        }

        private void initializeCaseTest(String caseName) throws IOException {
            caseObject = TestUtils.getTestCase(caseName);
            preRequest = caseObject.get("preRequest");
            expectedResult = caseObject.get("expectedResult");
        }
    }
}
