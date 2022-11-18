package com.project.sharedfolderserver.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.project.sharedfolderserver.BaseIT;
import com.project.sharedfolderserver.TestUtils;
import com.project.sharedfolderserver.v1.file.FileDto;
import com.project.sharedfolderserver.v1.utils.error.Error;
import com.project.sharedfolderserver.v1.utils.http.Response;
import com.project.sharedfolderserver.v1.utils.json.JsonUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
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
            List<FileDto> expectedData = JsonUtil.objectMapper.convertValue(expectedResult.get("data"), new TypeReference<>() {
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
            FileDto expectedData = JsonUtil.objectMapper.convertValue(expectedResult.get("data"), new TypeReference<>() {
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
            assertNotNull(actualData, "expect to have a file in the response");
            assertEquals(expectedData, actualData, "expect the get a file with content");
        }

        @DisplayName("Failed: Download file which doesn't exist")
        @Test
        void failedDownload() throws IOException {
            initializeCaseTest("file/failed-download-file");
            List<Error> expectedErrors = JsonUtil.objectMapper.convertValue(expectedResult.get("errors"), new TypeReference<>() {
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
        void successUploadFile() throws IOException, IllegalAccessException {
            initializeCaseTest("file/success-upload-file");
            FileDto expectedData = JsonUtil.objectMapper.convertValue(expectedResult.get("data"), new TypeReference<>() {
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
            List<Error> expectedErrors = JsonUtil.objectMapper.convertValue(expectedResult.get("errors"), new TypeReference<>() {
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
        @ParameterizedTest
        @ValueSource(strings = {"badname", "usik^*.ld", "=-0", " ggg.file.s"})
        void failedUploadIllegalName(String badname) throws IOException {
            initializeCaseTest("file/failed-upload-file-illegal-name");
            List<Error> expectedErrors = JsonUtil.objectMapper.convertValue(expectedResult.get("errors"), new TypeReference<>() {
            });
            String errorMessage = String.format("file could not be created. Illegal file name %s, file name must be in the form of NAME.KIND, using letters, numbers. some special characters are illegal", badname);

            expectedErrors.stream().findFirst().ifPresent(error -> error.setMessage(errorMessage));
            ((ObjectNode) (preRequest.get("body"))).put("name", badname);

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

        @DisplayName("Failed: upload file with empty or null name")
        @ParameterizedTest
        @NullAndEmptySource
        void failedUploadNullOrEmptyName(String badname) throws IOException {
            initializeCaseTest("file/failed-upload-file-null-empty-name");
            List<Error> expectedErrors = JsonUtil.objectMapper.convertValue(expectedResult.get("errors"), new TypeReference<>() {
            });
            String errorType;
            if (badname == null) {
                errorType = "null found, string expected";
            }
            else {
                errorType = "must be at least 1 characters long";
            }
            String errorMessage = String.format("validation error [$.name: %s]", errorType);
            expectedErrors.stream().findFirst().ifPresent(error -> error.setMessage(errorMessage));
            ((ObjectNode) (preRequest.get("body"))).put("name", badname);

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
        @ParameterizedTest
        @MethodSource("generateIllegalUploadRequestParameters")
        void failedUploadIllegalRequestParameters(String fieldName, JsonNode illegalFieldContent, String errorMessage) throws IOException {
            initializeCaseTest("file/failed-upload-file-illegal-content");
            List<Error> expectedErrors = JsonUtil.objectMapper.convertValue(expectedResult.get("errors"), new TypeReference<>() {
            });
            Error expectedError = expectedErrors.stream().findFirst().orElseThrow();
            ((ObjectNode) (preRequest.get("body"))).set(fieldName, illegalFieldContent);
            expectedError.setMessage(expectedError.getMessage().replace(FIELD_NAME_REPLACE_SIGN, fieldName).replace(TYPE_NAME_REPLACE_SIGN, errorMessage));

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

        private Stream<Arguments> generateIllegalUploadRequestParameters() {
            return Stream.of(
                    Arguments.of("name", null, "null"),
                    Arguments.of("content", null, "null"),
                    Arguments.of("content", JsonUtil.objectMapper.createArrayNode(), "array"),
                    Arguments.of("name", JsonUtil.objectMapper.createArrayNode(), "array"),
                    Arguments.of("content", new IntNode(8), "integer"),
                    Arguments.of("name", new IntNode(8), "integer")
            );
        }

        private void initializeCaseTest(String caseName) throws IOException {
            caseObject = TestUtils.getTestCase(caseName);
            preRequest = caseObject.get("preRequest");
            expectedResult = caseObject.get("expectedResult");
        }
    }
}
