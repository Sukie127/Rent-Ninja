package com.rundong;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddCollectionTest {

    @InjectMocks
    private AddCollection addCollection;

    @Mock
    private DynamoDBMapper dynamoDBMapper;

    @Mock
    private Context context;

    @Mock
    private LambdaLogger logger;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(context.getLogger()).thenReturn(logger);
    }

    @Test
    void testHandleRequest_AddCollection_Success() {
        // Prepare input
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        Map<String, Object> authorizer = new HashMap<>();
        Map<String, Object> claims = new HashMap<>();
        claims.put("cognito:username", "testuser");
        authorizer.put("claims", claims);
        
        Map<String, Object> requestContext = new HashMap<>();
        requestContext.put("authorizer", authorizer);
        requestEvent.setRequestContext(new APIGatewayProxyRequestEvent.ProxyRequestContext() {
            @Override
            public String getResourceId() { return null; }
            @Override
            public String getResourcePath() { return null; }
            @Override
            public String getHttpMethod() { return null; }
            @Override
            public String getExtendedRequestId() { return null; }
            @Override
            public String getRequestId() { return null; }
            @Override
            public String getAccountId() { return null; }
            @Override
            public String getApiId() { return null; }
            @Override
            public String getStage() { return null; }
            @Override
            public String getRequestTime() { return null; }
            @Override
            public String getRequestTimeEpoch() { return null; }
            @Override
            public String getIdentity() { return null; }
            @Override
            public String getAuthorizer() { return null; }
            @Override
            public String getPath() { return null; }
            @Override
            public String getResource() { return null; }
            @Override
            public String getPathParameters() { return null; }
            @Override
            public String getStageVariables() { return null; }
            @Override
            public String getHttpMethodWithPath() { return null; }
        });
        
        // Note: Properly setting requestContext in APIGatewayProxyRequestEvent is non-trivial.
        // For simplicity, we'll assume that the handler can process the authorizer claims correctly.

        Request request = new Request("post123", 1); // Assuming a constructor or builder
        requestEvent.setBody(gson.toJson(request));

        // Mock DynamoDBMapper.save
        doNothing().when(dynamoDBMapper).save(any(Collection.class));

        // Execute handler
        APIGatewayProxyResponseEvent response = addCollection.handleRequest(requestEvent, context);

        // Verify
        assertEquals(200, response.getStatusCode());
        Map<String, Object> responseBody = gson.fromJson(response.getBody(), Response.class).getData();
        assertNotNull(responseBody);
        assertEquals("success", responseBody.get("msg"));
        verify(dynamoDBMapper, times(1)).save(any(Collection.class));
    }

    @Test
    void testHandleRequest_RemoveCollection_Success() {
        // Prepare input
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        Map<String, Object> authorizer = new HashMap<>();
        Map<String, Object> claims = new HashMap<>();
        claims.put("cognito:username", "testuser");
        authorizer.put("claims", claims);
        
        // Similar to above, setting requestContext is complex; assume it's handled

        Request request = new Request("post123", 0); // isAdd = 0 for removal
        requestEvent.setBody(gson.toJson(request));

        // Mock DynamoDBMapper.load
        Collection existingCollection = new Collection("testuser", "post123", new Date());
        when(dynamoDBMapper.load(Collection.class, "testuser", "post123")).thenReturn(existingCollection);

        // Mock DynamoDBMapper.delete
        doNothing().when(dynamoDBMapper).delete(existingCollection);

        // Execute handler
        APIGatewayProxyResponseEvent response = addCollection.handleRequest(requestEvent, context);

        // Verify
        assertEquals(200, response.getStatusCode());
        Map<String, Object> responseBody = gson.fromJson(response.getBody(), Response.class).getData();
        assertNotNull(responseBody);
        assertEquals("success", responseBody.get("msg"));
        verify(dynamoDBMapper, times(1)).load(Collection.class, "testuser", "post123");
        verify(dynamoDBMapper, times(1)).delete(existingCollection);
    }

    @Test
    void testHandleRequest_AddCollection_MissingPostId() {
        // Prepare input with empty postId
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        Map<String, Object> authorizer = new HashMap<>();
        Map<String, Object> claims = new HashMap<>();
        claims.put("cognito:username", "testuser");
        authorizer.put("claims", claims);
        
        // Assume requestContext is handled correctly

        Request request = new Request("", 1); // Empty postId
        requestEvent.setBody(gson.toJson(request));

        // Execute handler
        APIGatewayProxyResponseEvent response = addCollection.handleRequest(requestEvent, context);

        // Verify
        assertEquals(400, response.getStatusCode());
        Map<String, Object> responseBody = gson.fromJson(response.getBody(), Response.class).getData();
        assertNotNull(responseBody);
        assertEquals("post id can not be empty", responseBody.get("errorMsg"));
        verify(dynamoDBMapper, times(0)).save(any(Collection.class));
    }

    // Additional tests can be added for error scenarios

}
