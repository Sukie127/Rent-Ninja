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

import java.util.*;

class GetCollectionListTest {

    private GetCollectionList getCollectionList;

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
        getCollectionList = new GetCollectionList();

        // Inject the mocked DynamoDBMapper into GetCollectionList
        try {
            java.lang.reflect.Field dynamoDBMapperField = GetCollectionList.class.getDeclaredField("dynamoDBMapper");
            dynamoDBMapperField.setAccessible(true);
            dynamoDBMapperField.set(getCollectionList, dynamoDBMapperMock);
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

        // Mock the collection return
        Collection collection = new Collection("user1", "samplePostId", new Date());
        List<Collection> mockCollectionList = Collections.singletonList(collection);
        when(dynamoDBMapperMock.query(eq(Collection.class), any())).thenReturn(mockCollectionList);

        // Mock the post retrieval
        Post post = new Post();
        post.setPostId("samplePostId");
        post.setUserId("user1");
        when(dynamoDBMapperMock.batchLoad(anyList())).thenReturn(Collections.singletonMap("posts", Collections.singletonList(post)));

        // Invoke the handler
        APIGatewayProxyResponseEvent response = getCollectionList.handleRequest(requestEvent, contextMock);

        // Verify the response
        assertEquals(200, response.getStatusCode());

        // Check if the response contains the expected post
        Response<Map<String, Object>> responseBody = gson.fromJson(response.getBody(),
                new com.google.gson.reflect.TypeToken<Response<Map<String, Object>>>() {}.getType());

        assertNotNull(responseBody);
        assertEquals(200, responseBody.getHttpStatusCode());
        assertNotNull(responseBody.getResponseBody());
        assertTrue(responseBody.getResponseBody().containsKey("list"));
        List<Post> posts = (List<Post>) responseBody.getResponseBody().get("list");
        assertNotNull(posts);
        assertEquals(1, posts.size());
        assertEquals("samplePostId", posts.get(0).getPostId());
    }

    @Test
    void testHandleRequest_NoClaims() {
        // Prepare the APIGatewayProxyRequestEvent with no claims
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setRequestContext(new APIGatewayProxyRequestEvent.ProxyRequestContext() {
            @Override
            public Map<String, Object> getAuthorizer() {
                return null; // No claims present
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

        // Invoke the handler
        APIGatewayProxyResponseEvent response = getCollectionList.handleRequest(requestEvent, contextMock);

        // Verify the response for missing claims
        assertEquals(400, response.getStatusCode());
        Response<Map<String, Object>> responseBody = gson.fromJson(response.getBody(),
                new com.google.gson.reflect.TypeToken<Response<Map<String, Object>>>() {}.getType());

        assertNotNull(responseBody);
        assertEquals(400, responseBody.getHttpStatusCode());
        assertEquals("auth header not valid.", responseBody.getError().getErrorMessage());
    }

    @Test
    void testHandleRequest_DBError() {
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

        // Mock the DynamoDB query to throw an exception
        when(dynamoDBMapperMock.query(eq(Collection.class), any())).thenThrow(new RuntimeException("DB Error"));

        // Invoke the handler
        APIGatewayProxyResponseEvent response = getCollectionList.handleRequest(requestEvent, contextMock);

        // Verify the response for DB error
        assertEquals(400, response.getStatusCode());
        Response<Map<String, Object>> responseBody = gson.fromJson(response.getBody(),
                new com.google.gson.reflect.TypeToken<Response<Map<String, Object>>>() {}.getType());

        assertNotNull(responseBody);
        assertEquals(400, responseBody.getHttpStatusCode());
        assertEquals("get list error", responseBody.getError().getErrorMessage());
    }
}
