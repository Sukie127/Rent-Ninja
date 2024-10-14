package com.rundong;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class UploadPostTest {

    private UploadPost uploadPost;

    @Mock
    private DynamoDBMapper dynamoDBMapperMock;

    @Mock
    private Context contextMock;

    @Mock
    private LambdaLogger loggerMock;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        uploadPost = new UploadPost();

        // Inject the mocked DynamoDBMapper into UploadPost
        try {
            java.lang.reflect.Field dynamoDBMapperField = UploadPost.class.getDeclaredField("dynamoDBMapper");
            dynamoDBMapperField.setAccessible(true);
            dynamoDBMapperField.set(uploadPost, dynamoDBMapperMock);
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

        // Prepare the Post object
        Post post = new Post();
        post.setTitle("Sample Title");
        post.setContent("Sample content.");
        post.setContactInfo("contact@example.com");
        post.setPicUrls("http://example.com/image.png");
        post.setCountry("US");
        post.setState("NY");
        post.setCity("New York");
        post.setArea("Manhattan");

        // Prepare the request body
        RequestBody requestBody = new RequestBody(null, post.getTitle(), post.getContent(),
                post.getContactInfo(), post.getPicUrls(), post.getCountry(), post.getState(), post.getCity(), post.getArea());
        requestEvent.setBody(gson.toJson(requestBody));

        // Invoke the handler
        APIGatewayProxyResponseEvent response = uploadPost.handleRequest(requestEvent, contextMock);

        // Verify the response
        assertEquals(200, response.getStatusCode());

        // Check if the post was saved
        verify(dynamoDBMapperMock, times(1)).save(any(Post.class));
    }

    @Test
    void testHandleRequest_InvalidRequest_NoTitle() {
        // Prepare the APIGatewayProxyRequestEvent with no title
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        Map<String, Object> authorizer = new HashMap<>();
        Map<String, Object> claims = new HashMap<>();
        claims.put("cognito:username", "user1");
        authorizer.put("claims", claims);
        requestEvent.setRequestContext(new APIGatewayProxyRequestEvent.ProxyRequestContext() {
            @Override
            public Map<String, Object> getAuthorizer() {
                return authorizer;
            }
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

        // Prepare request body without title
        RequestBody requestBody = new RequestBody(null, "", "Sample content.", "contact@example.com", 
                "http://example.com/image.png", "US", "NY", "New York", "Manhattan");
        requestEvent.setBody(gson.toJson(requestBody));

        // Invoke the handler
        APIGatewayProxyResponseEvent response = uploadPost.handleRequest(requestEvent, contextMock);

        // Verify the response for missing title
        assertEquals(400, response.getStatusCode());
        Response<String> responseBody = gson.fromJson(response.getBody(),
                new com.google.gson.reflect.TypeToken<Response<String>>() {}.getType());

        assertNotNull(responseBody);
        assertEquals(400, responseBody.getHttpStatusCode());
        assertEquals("invalid request", responseBody.getError().getErrorMessage());
    }
}
