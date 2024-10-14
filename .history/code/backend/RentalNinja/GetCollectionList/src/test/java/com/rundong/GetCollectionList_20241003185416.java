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
}