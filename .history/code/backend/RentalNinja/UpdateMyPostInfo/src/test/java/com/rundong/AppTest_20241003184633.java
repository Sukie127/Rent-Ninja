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

class UpdateMyPostInfoTest {

    private UpdateMyPostInfo updateMyPostInfo;

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
        updateMyPostInfo = new UpdateMyPostInfo();

        // Inject the mocked DynamoDBMapper into UpdateMyPostInfo
        try {
            java.lang.reflect.Field dynamoDBMapperField = UpdateMyPostInfo.class.getDeclaredField("dynamoDBMapper");
            dynamoDBMapperField.setAccessible(true);
            dynamoDBMapperField.set(updateMyPostInfo, dynamoDBMapperMock);
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

        // Mock the Post object
        String postId = "samplePostId";
        Post post = new Post();
        post.setPostId(postId);
        post.setUserId("user1");
        post.setContent("This is a sample post.");
        
        // Mock DynamoDBMapper to return the Post object
        when(dynamoDBMapperMock.load(Post.class, postId)).thenReturn(post);

        // Prepare the request body
        RequestBody requestBody = new RequestBody(postId, "Updated Title", "Updated Content", "Updated Contact", 
            "Updated PicUrls", "Updated Country", "Updated State", "Updated City", "Updated Area");
        requestEvent.setBody(gson.toJson(requestBody));

        // Invoke the handler
        APIGatewayProxyResponseEvent response = updateMyPostInfo.handleRequest(requestEvent, contextMock);

        // Verify the response
        assertEquals(200, response.getStatusCode());

        // Check if the post was updated
        verify(dynamoDBMapperMock, times(1)).save(post);
        assertEquals("Updated Title", post.getTitle());
        assertEquals("Updated Content", post.getContent());
    }

    @Test
    void testHandleRequest_NoPostId() {
        // Prepare the APIGatewayProxyRequestEvent with no postId
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

        // Prepare request body without postId
        RequestBody requestBody = new RequestBody(null, "Updated Title", "Updated Content", "Updated Contact", 
            "Updated PicUrls", "Updated Country", "Updated State", "Updated City", "Updated Area");
        requestEvent.setBody(gson.toJson(requestBody));

        // Invoke the handler
        APIGatewayProxyResponseEvent response = updateMyPostInfo.handleRequest(requestEvent, contextMock);

        // Verify the response for missing postId
        assertEquals(400, response.getStatusCode());
        Response<Map<String, Object>> responseBody = gson.fromJson(response.getBody(),
                new com.google.gson.reflect.TypeToken<Response<Map<String, Object>>>() {}.getType());

        assertNotNull(responseBody);
        assertEquals(400, responseBody.getHttpStatusCode());
        assertEquals("post id not present", responseBody.getError().getErrorMessage());
    }

    @Test
    void testHandleRequest_NotAuthorized() {
        // Prepare the APIGatewayProxyRequestEvent with a different user ID
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        Map<String, Object> authorizer = new HashMap<>();
        Map<String, Object> claims = new HashMap<>();
        claims.put("cognito:username", "user2"); // Different user
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

        // Mock the Post object with userId "user1"
        String postId = "samplePostId";
        Post post = new Post();
        post.setPostId(postId);
        post.setUserId("user1"); // Post belongs to user1

        // Mock DynamoDBMapper to return the Post object
        when(dynamoDBMapperMock.load(Post.class, postId)).thenReturn(post);

        // Prepare the request body
        RequestBody requestBody = new RequestBody(postId, "Updated Title", "Updated Content", "Updated Contact", 
            "Updated PicUrls", "Updated Country", "Updated State", "Updated City", "Updated Area");
        requestEvent.setBody(gson.toJson(requestBody));

        // Invoke the handler
        APIGatewayProxyResponseEvent response = updateMyPostInfo.handleRequest(requestEvent, contextMock);

        // Verify the response for unauthorized access
        assertEquals(403, response.getStatusCode());
        Response<Map<String, Object>> responseBody = gson.fromJson(response.getBody(),
                new com.google.gson.reflect.TypeToken<Response<Map<String, Object>>>() {}.getType());

        assertNotNull(responseBody);
        assertEquals(403, responseBody.getHttpStatusCode());
        assertEquals("not authorized", responseBody.getError().getErrorMessage());
    }
}
