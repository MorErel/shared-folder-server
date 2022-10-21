package com.project.sharedfolderserver.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.project.sharedfolderserver.BaseIT;
import com.project.sharedfolderserver.TestUtils;
import com.project.sharedfolderserver.v1.file.FileDto;
import com.project.sharedfolderserver.v1.utils.http.Response;
import com.project.sharedfolderserver.v1.utils.json.JSON;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
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


    @Nested
    class Get {

        @DisplayName("Success: Get file list")
        @Test
        @Sql(scripts = {SQL_SCRIPTS_PATH + "getFileList.sql"})
        void successGetFileList() throws IOException {
            initializeCaseTest("file/success-get-file-list");
            List<FileDto> expectedData = JSON.objectMapper.convertValue(expectedResult.get("data"), new TypeReference<>() {
            });

            ResponseEntity<Response<List<FileDto>>> response =
                    restTemplate.exchange(getUrl(preRequest.get("path").asText())
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

        private void initializeCaseTest(String caseName) throws IOException {
            caseObject = TestUtils.getTestCase(caseName);
            preRequest = caseObject.get("preRequest");
            expectedResult = caseObject.get("expectedResult");
        }
    }
}
