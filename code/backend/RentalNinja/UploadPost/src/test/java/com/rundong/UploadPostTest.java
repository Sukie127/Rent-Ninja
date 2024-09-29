package com.rundong;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UploadPostTest {

    private AutoCloseable closeable;

    @Mock
    private DynamoDBMapper dynamoDBMapper;

    @Mock
    private Context context;

    @Mock
    private LambdaLogger logger;

    @InjectMocks
    private UploadPost uploadPost;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        when(context.getLogger()).thenReturn(logger);
        doAnswer(call -> {
            System.out.println("haha");
            return null;
        }).when(logger).log(anyString());
        uploadPost = new UploadPost(dynamoDBMapper);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    @Test
    public void testHandleRequestSuccess() {
        when(context.getFunctionName()).thenReturn("handleRequest");
        // Mock the input event
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        Post post = new Post();
        post.setUserId("123");
        post.setTitle("Test Title");
        post.setContent("Test Content");
        event.setBody(gson.toJson(post));

        // Call the handleRequest method
        APIGatewayProxyResponseEvent responseEvent = uploadPost.handleRequest(event, context);
        verify(dynamoDBMapper, times(1)).save(any(Post.class));
        verify(logger, times(2)).log(anyString(), any());

        APIGatewayProxyResponseEvent expectedRsp = new APIGatewayProxyResponseEvent().withBody(gson.toJson(new Response<String>(200, "success!", new Error())));
        assertEquals(200, responseEvent.getStatusCode());
        assertEquals(expectedRsp.getBody(), responseEvent.getBody());
    }

    @Test
    public void testHandleRequestDbError() {
        // Mock the input event
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        Post post = new Post();
        post.setPostId("123");
        post.setUserId("user-1");
        post.setTitle("Test Title");
        post.setContent("Test Content");
        event.setBody(gson.toJson(post));

        // Mock DynamoDB to throw an exception
        doThrow(new RuntimeException("DynamoDB save failed")).when(dynamoDBMapper).save(any(Post.class));

        // Call the handleRequest method
        APIGatewayProxyResponseEvent responseEvent = uploadPost.handleRequest(event, context);

        // Verify DynamoDB save was called
        verify(dynamoDBMapper).save(any(Post.class));

        // Verify correct response
        assertEquals(500, responseEvent.getStatusCode());
        assertNotNull(responseEvent.getBody());
        assertTrue(responseEvent.getBody().contains("db error"));
    }
}
