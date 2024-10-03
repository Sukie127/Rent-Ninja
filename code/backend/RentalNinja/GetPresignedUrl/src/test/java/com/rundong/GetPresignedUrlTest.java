package com.rundong;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class GetPresignedUrlTest {

    private GetPresignedUrl getPresignedUrl;

    @Mock
    private AmazonS3 s3ClientMock;

    @Mock
    private Context contextMock;

    @Mock
    private LambdaLogger loggerMock;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        getPresignedUrl = new GetPresignedUrl();

        // Inject the mocked S3 client into GetPresignedUrl
        try {
            java.lang.reflect.Field s3ClientField = GetPresignedUrl.class.getDeclaredField("s3Client");
            s3ClientField.setAccessible(true);
            s3ClientField.set(getPresignedUrl, s3ClientMock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Mock the logger
        when(contextMock.getLogger()).thenReturn(loggerMock);
    }

    @Test
    void testHandleRequest_Success() {
        // Prepare the APIGatewayProxyRequestEvent
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        Map<String, Object> authorizer = new HashMap<>();
        Map<String, Object> claims = new HashMap<>();
        claims.put("cognito:username", "user1");
        authorizer.put("claims", claims);
        Map<String, Object> requestContext = new HashMap<>();
        requestContext.put("authorizer", authorizer);
        requestEvent.setRequestContext(new APIGatewayProxyRequestEvent.ProxyRequestContext() {
            @Override
            public Map<String, Object> getAuthorizer() {
                return authorizer;
            }

            // Implement other abstract methods as no-op or return null
            @Override public String getResourceId() { return null; }
            @Override public String getResourcePath() { return null; }
            @Override public String getHttpMethod() { return null; }
            @Override public String getPath() { return null; }
            @Override public String getRequestId() { return null; }
            @Override public String getAccountId() { return null; }
            @Override public String getApiId() { return null; }
            @Override public String getStage() { return null; }
            @Override public String getIdentitySource() { return null; }
            @Override public String getProtocol() { return null; }
            @Override public String getDomainName() { return null; }
            @Override public String getDomainPrefix() { return null; }
            @Override public String getHttpProtocol() { return null; }
            @Override public String getRequestTime() { return null; }
            @Override public Integer getRequestTimeEpoch() { return null; }
            @Override public String getStageVariables() { return null; }
            @Override public String getPathParameters() { return null; }
            @Override public String getQueryStringParameters() { return null; }
            @Override public String getHeaders() { return null; }
            @Override public String getRequestContextIdentity() { return null; }
            @Override public String getAuthorizerJwt() { return null; }
        });

        // Mock the S3 presigned URL generation
        String bucketName = "rentalninja";
        String objectKey = "123456789.png";
        String preSignedUrl = "https://rentalninja.s3.us-east-2.amazonaws.com/" + objectKey;
        when(s3ClientMock.generatePresignedUrl(any(GeneratePresignedUrlRequest.class)))
                .thenReturn(new java.net.URL(preSignedUrl));

        // Invoke the handler
        APIGatewayProxyResponseEvent response = getPresignedUrl.handleRequest(requestEvent, contextMock);

        // Verify the response
        assertEquals(200, response.getStatusCode());

        // Parse the response body
        Response<Map<String, String>> responseBody = gson.fromJson(response.getBody(),
                new com.google.gson.reflect.TypeToken<Response<Map<String, String>>>() {}.getType());

        assertNotNull(responseBody);
        assertEquals(200, responseBody.getHttpStatusCode());
        assertNull(responseBody.getError());

        Map<String, String> presignedUrlResponse = responseBody.getResponseBody();
        assertNotNull(presignedUrlResponse);
        assertTrue(presignedUrlResponse.containsKey("presignedUrl"));
        assertTrue(presignedUrlResponse.containsKey("objectKey"));

        assertEquals(preSignedUrl, presignedUrlResponse.get("presignedUrl"));
        assertEquals("https://rentalninja.s3.us-east-2.amazonaws.com/123456789.png", presignedUrlResponse.get("objectKey"));
    }
}
