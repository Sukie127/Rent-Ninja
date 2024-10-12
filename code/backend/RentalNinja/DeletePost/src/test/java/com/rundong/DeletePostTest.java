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

class DeletePostTest {

    private DeletePost deletePost;

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
        deletePost = new DeletePost();

        // Inject the mocked DynamoDBMapper into DeletePost
        try {
            java.lang.reflect.Field dynamoDBMapperField = DeletePost.class.getDeclaredField("dynamoDBMapper");
            dynamoDBMapperField.setAccessible(true);
            dynamoDBMapperField.set(deletePost, dynamoDBMapperMock);
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
        requestEvent.setBody("{\"postId\":\"samplePostId\"}");
        Map<String, Object> authorizer = new HashMap<>();
        Map<String, Object> claims = new HashMap<>();
        claims.put("cognito:username", "user1");
        authorizer.put("claims", claims);
        requestEvent.setRequestContext(new APIGatewayProxyRequestEvent.ProxyRequestContext() {
            @Override
            public Map<String, Object> getAuthorizer() {
                return authorizer;
            }
        });

        // Mock the post retrieval
        Post post = new Post();
        post.setPostId("samplePostId");
        post.setUserId("user1");
        when(dynamoDBMapperMock.load(Post.class, "samplePostId")).thenReturn(post);

        // Invoke the handler
        APIGatewayProxyResponseEvent response = deletePost.handleRequest(requestEvent, contextMock);

        // Verify the response
        assertEquals(200, response.getStatusCode());

        // Check if the response contains the success message
        Response<Map<String, String>> responseBody = gson.fromJson(response.getBody(),
                new com.google.gson.reflect.TypeToken<Response<Map<String, String>>>() {}.getType());

        assertNotNull(responseBody);
        assertEquals(200, responseBody.getHttpStatusCode());
        assertNotNull(responseBody.getResponseBody());
        assertEquals("success", responseBody.getResponseBody().get("msg"));
    }

    @Test
    void testHandleRequest_NotAuthorized() {
        // Prepare the APIGatewayProxyRequestEvent
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setBody("{\"postId\":\"samplePostId\"}");
        Map<String, Object> authorizer = new HashMap<>();
        Map<String, Object> claims = new HashMap<>();
        claims.put("cognito:username", "user2"); // Different user
        authorizer.put("claims", claims);
        requestEvent.setRequestContext(new APIGatewayProxyRequestEvent.ProxyRequestContext() {
            @Override
            public Map<String, Object> getAuthorizer() {
                return authorizer;
            }
        });

        // Mock the post retrieval
        Post post = new Post();
        post.setPostId("samplePostId");
        post.setUserId("user1");
        when(dynamoDBMapperMock.load(Post.class, "samplePostId")).thenReturn(post);

        // Invoke the handler
        APIGatewayProxyResponseEvent response = deletePost.handleRequest(requestEvent, contextMock);

        // Verify the response for not authorized
        assertEquals(403, response.getStatusCode());
        Response<Map<String, String>> responseBody = gson.fromJson(response.getBody(),
                new com.google.gson.reflect.TypeToken<Response<Map<String, String>>>() {}.getType());

        assertNotNull(responseBody);
        assertEquals(403, responseBody.getHttpStatusCode());
        assertEquals("not authorized", responseBody.getError().getErrorMessage());
    }

    @Test
    void testHandleRequest_DBError() {
        // Prepare the APIGatewayProxyRequestEvent
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setBody("{\"postId\":\"samplePostId\"}");
        Map<String, Object> authorizer = new HashMap<>();
        Map<String, Object> claims = new HashMap<>();
        claims.put("cognito:username", "user1");
        authorizer.put("claims", claims);
        requestEvent.setRequestContext(new APIGatewayProxyRequestEvent.ProxyRequestContext() {
            @Override
            public Map<String, Object> getAuthorizer() {
                return authorizer;
            }
        });

        // Mock the DynamoDB load to throw an exception
        when(dynamoDBMapperMock.load(Post.class, "samplePostId")).thenThrow(new RuntimeException("DB Error"));

        // Invoke the handler
        APIGatewayProxyResponseEvent response = deletePost.handleRequest(requestEvent, contextMock);

        // Verify the response for DB error
        assertEquals(500, response.getStatusCode());
        Response<Map<String, String>> responseBody = gson.fromJson(response.getBody(),
                new com.google.gson.reflect.TypeToken<Response<Map<String, String>>>() {}.getType());

        assertNotNull(responseBody);
        assertEquals(500, responseBody.getHttpStatusCode());
        assertEquals("db error, please try again", responseBody.getError().getErrorMessage());
    }
}