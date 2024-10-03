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

import java.util.HashMap;
import java.util.Map;

class GetSpecificPostDetailTest {

    private GetSpecificPostDetail getSpecificPostDetail;

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
        getSpecificPostDetail = new GetSpecificPostDetail();

        // Inject the mocked DynamoDBMapper into GetSpecificPostDetail
        try {
            java.lang.reflect.Field dynamoDBMapperField = GetSpecificPostDetail.class.getDeclaredField("dynamoDBMapper");
            dynamoDBMapperField.setAccessible(true);
            dynamoDBMapperField.set(getSpecificPostDetail, dynamoDBMapperMock);
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

        // Prepare request body
        Request request = new Request();
        request.setPostId(postId);
        requestEvent.setBody(gson.toJson(request));

        // Invoke the handler
        APIGatewayProxyResponseEvent response = getSpecificPostDetail.handleRequest(requestEvent, contextMock);

        // Verify the response
        assertEquals(200, response.getStatusCode());

        // Parse the response body
        Response<Map<String, Object>> responseBody = gson.fromJson(response.getBody(),
                new com.google.gson.reflect.TypeToken<Response<Map<String, Object>>>() {}.getType());

        assertNotNull(responseBody);
        assertEquals(200, responseBody.getHttpStatusCode());
        assertNull(responseBody.getError());

        Map<String, Object> postResponse = responseBody.getResponseBody();
        assertNotNull(postResponse);
        assertTrue(postResponse.containsKey("post"));

        Post returnedPost = gson.fromJson(gson.toJson(postResponse.get("post")), Post.class);
        assertEquals(postId, returnedPost.getPostId());
        assertEquals("This is a sample post.", returnedPost.getContent());
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
        Request request = new Request();
        request.setPostId(null);
        requestEvent.setBody(gson.toJson(request));

        // Invoke the handler
        APIGatewayProxyResponseEvent response = getSpecificPostDetail.handleRequest(requestEvent, contextMock);

        // Verify the response for missing postId
        assertEquals(400, response.getStatusCode());
        Response<Map<String, Object>> responseBody = gson.fromJson(response.getBody(),
                new com.google.gson.reflect.TypeToken<Response<Map<String, Object>>>() {}.getType());

        assertNotNull(responseBody);
        assertEquals(400, responseBody.getHttpStatusCode());
        assertEquals("post id not present", responseBody.getError().getErrorMessage());
    }
}
