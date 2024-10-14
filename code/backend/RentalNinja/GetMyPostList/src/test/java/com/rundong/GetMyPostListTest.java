package com.rundong;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GetMyPostListTest {

    @Mock
    private Context mockContext;

    @Mock
    private DynamoDBMapper mockDynamoDBMapper;

    @Mock
    private AmazonDynamoDB mockAmazonDynamoDB;

    @Mock
    private LambdaLogger mockLambdaLogger;

    private GetMyPostList getMyPostList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(mockContext.getLogger()).thenReturn(mockLambdaLogger);

        getMyPostList = new GetMyPostList(mockAmazonDynamoDB);
        getMyPostList.setDynamoDBMapper(mockDynamoDBMapper);
    }

    @Test
    void testHandleRequestSuccess() {
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        Map<String, Object> claims = new HashMap<>();
        claims.put("cognito:username", "test_user");
        
        Map<String, Object> authorizer = new HashMap<>();
        authorizer.put("claims", claims);
        
        APIGatewayProxyRequestEvent.ProxyRequestContext requestContext = new APIGatewayProxyRequestEvent.ProxyRequestContext();
        requestContext.setAuthorizer(authorizer);
        
        event.setRequestContext(requestContext);
        event.setBody("{\"pageNum\": 0, \"pageSize\": 10}");

        List<Post> mockPostList = new ArrayList<>();
        Post post1 = createMockPost("post1", "test_user", "Test Title 1");
        Post post2 = createMockPost("post2", "test_user", "Test Title 2");
        mockPostList.add(post1);
        mockPostList.add(post2);
        
        PaginatedScanList<Post> mockPaginatedScanList = mock(PaginatedScanList.class);
        when(mockPaginatedScanList.stream()).thenReturn(mockPostList.stream());
        when(mockDynamoDBMapper.scan(eq(Post.class), any(DynamoDBScanExpression.class))).thenReturn(mockPaginatedScanList);

        APIGatewayProxyResponseEvent responseEvent = getMyPostList.handleRequest(event, mockContext);

        assertEquals(200, responseEvent.getStatusCode());
        // Add more assertions as needed to verify the response body
    }

    private Post createMockPost(String postId, String userId, String title) {
        Post post = new Post();
        post.setPostId(postId);
        post.setUserId(userId);
        post.setTitle(title);
        post.setContent("Test content");
        post.setContactInfo("test@example.com");
        post.setPicUrls("http://example.com/pic1.jpg,http://example.com/pic2.jpg");
        post.setCountry("US");
        post.setStateCode("MA");
        post.setCityCode("BOS");
        post.setArea("Downtown");
        post.setCreateTime(new Date());
        post.setUpdateTime(new Date());
        post.setDeleteFlag(0);
        return post;
    }

    // Add more test methods for different scenarios
}