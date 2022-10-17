package com.project.sharedfolderserver.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.project.sharedfolderserver.BaseIT;
import com.project.sharedfolderserver.TestUtils;
import com.project.sharedfolderserver.v1.file.FileDto;
import com.project.sharedfolderserver.v1.utils.error.Error;
import com.project.sharedfolderserver.v1.utils.http.Response;
import com.project.sharedfolderserver.v1.utils.json.JSON;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.CollectionUtils;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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

    // todo - tests for each endpoint

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
            initializeCaseTest("file/success-download-file");
            FileDto expectedData = JSON.objectMapper.convertValue(expectedResult.get("data"), new TypeReference<>() {
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
            assertTrue(CollectionUtils.isEmpty(actualBody.getErrors()), "expect no errors");
            FileDto actualData = actualBody.getData();
            assertNotNull(actualData, "expect to have file list in the response");
            assertEquals(expectedData, actualData, "expect the same file list");
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

    @Nested
    class Post {

        @DisplayName("Success: upload file")
        @Test
        void successUploadFile() throws IOException, IllegalAccessException {
            initializeCaseTest("file/success-upload-file");
            FileDto expectedData = JSON.objectMapper.convertValue(expectedResult.get("data"), new TypeReference<>() {
            });

            ResponseEntity<Response<FileDto>> response =
                    restTemplate.exchange(getUrl(preRequest.get("path").asText())
                            , HttpMethod.valueOf(preRequest.get("method").asText())
                            , new HttpEntity<>(preRequest.get("body"))
                            , new ParameterizedTypeReference<>() {
                            });

            assertNotNull(response, "expect to have a response");
            Response<FileDto> actualBody = response.getBody();
            assertNotNull(actualBody, "expect to have a body in the response");
            assertTrue(CollectionUtils.isEmpty(actualBody.getErrors()), "expect no errors");
            FileDto actualData = actualBody.getData();
            assertNotNull(actualData, "expect to have fileDto in the response");
            TestUtils.assertEqualsExcludedFields(expectedData, actualData, "id", "dateAdded", "dateModified");
        }

        @DisplayName("Failed: upload file with existing name")
        @Test
        @Sql(scripts = {SQL_SCRIPTS_PATH + "cleanTables.sql", SQL_SCRIPTS_PATH + "uploadFileExistingName.sql"})
        void failedUploadExistingName() throws IOException {
            initializeCaseTest("file/failed-upload-file-existing-name");
            List<Error> expectedErrors = JSON.objectMapper.convertValue(expectedResult.get("errors"), new TypeReference<>() {
            });

            ResponseEntity<Response<FileDto>> response =
                    restTemplate.exchange(getUrl(preRequest.get("path").asText())
                            , HttpMethod.valueOf(preRequest.get("method").asText())
                            , new HttpEntity<>(preRequest.get("body"))
                            , new ParameterizedTypeReference<>() {
                            });

            assertNotNull(response, "expect to have a response");
            Response<FileDto> actualBody = response.getBody();
            assertNotNull(actualBody, "expect to have a body in the response");
            assertFalse(CollectionUtils.isEmpty(actualBody.getErrors()), "expected to get errors");
            FileDto actualData = actualBody.getData();
            assertNull(actualData, "expect data to be null");
            assertEquals(expectedErrors, actualBody.getErrors(), "expected the same errors");
        }

        @DisplayName("Failed: upload file with illegal name")
        @Test
        void failedUploadIllegalName() throws IOException {
            initializeCaseTest("file/failed-upload-file-illegal-name");
            List<Error> expectedErrors = JSON.objectMapper.convertValue(expectedResult.get("errors"), new TypeReference<>() {
            });

            ResponseEntity<Response<FileDto>> response =
                    restTemplate.exchange(getUrl(preRequest.get("path").asText())
                            , HttpMethod.valueOf(preRequest.get("method").asText())
                            , new HttpEntity<>(preRequest.get("body"))
                            , new ParameterizedTypeReference<>() {
                            });

            assertNotNull(response, "expect to have a response");
            Response<FileDto> actualBody = response.getBody();
            assertNotNull(actualBody, "expect to have a body in the response");
            assertFalse(CollectionUtils.isEmpty(actualBody.getErrors()), "expected to get errors");
            FileDto actualData = actualBody.getData();
            assertNull(actualData, "expect data to be null");
            assertEquals(expectedErrors, actualBody.getErrors(), "expected the same errors");
        }

        @DisplayName("Failed: upload file with illegal content")
        @Test
        void failedUploadIllegalContent() throws IOException {
            initializeCaseTest("file/failed-upload-file-illegal-content");
            List<Error> expectedErrors = JSON.objectMapper.convertValue(expectedResult.get("errors"), new TypeReference<>() {
            });

            ResponseEntity<Response<FileDto>> response =
                    restTemplate.exchange(getUrl(preRequest.get("path").asText())
                            , HttpMethod.valueOf(preRequest.get("method").asText())
                            , new HttpEntity<>(preRequest.get("body"))
                            , new ParameterizedTypeReference<>() {
                            });

            assertNotNull(response, "expect to have a response");
            Response<FileDto> actualBody = response.getBody();
            assertNotNull(actualBody, "expect to have a body in the response");
            assertFalse(CollectionUtils.isEmpty(actualBody.getErrors()), "expected to get errors");
            FileDto actualData = actualBody.getData();
            assertNull(actualData, "expect data to be null");
            assertEquals(expectedErrors, actualBody.getErrors(), "expected the same errors");
        }

        private void initializeCaseTest(String caseName) throws IOException {
            caseObject = TestUtils.getTestCase(caseName);
            preRequest = caseObject.get("preRequest");
            expectedResult = caseObject.get("expectedResult");
        }
    }

}
