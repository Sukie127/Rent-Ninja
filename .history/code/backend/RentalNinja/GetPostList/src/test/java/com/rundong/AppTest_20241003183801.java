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

class GetPostListTest {

    private GetPostList getPostList;

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
        getPostList = new GetPostList();

        // Inject the mocked DynamoDBMapper into GetPostList
        // Since DynamoDBMapper is static, we need to use reflection to set it
        try {
            java.lang.reflect.Field mapperField = GetPostList.class.getDeclaredField("dynamoDBMapper");
            mapperField.setAccessible(true);
            mapperField.set(null, dynamoDBMapperMock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Mock the logger
        when(contextMock.getLogger()).thenReturn(loggerMock);
    }

    @Test
    void testHandleRequest_Success() {
        // Prepare mock data
        Post post1 = new Post();
        post1.setPostId("post1");
        post1.setUserId("user1");
        post1.setContent("This is a sample post content.");
        post1.setStateCode("CA");
        post1.setCityCode("LA");
        post1.setDeleteFlag(0);
        post1.setUpdateTime(new Date());

        Post post2 = new Post();
        post2.setPostId("post2");
        post2.setUserId("user2");
        post2.setContent("Another post content with keyword.");
        post2.setStateCode("CA");
        post2.setCityCode("SF");
        post2.setDeleteFlag(0);
        post2.setUpdateTime(new Date());

        List<Post> mockPostList = Arrays.asList(post1, post2);

        // Mock DynamoDBMapper.scan to return the mockPostList
        when(dynamoDBMapperMock.scan(eq(Post.class), any())).thenReturn(mockPostList);

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
        // Create a SearchParam JSON
        SearchParam searchParam = new SearchParam() {
            @Override
            public int pageNum() {
                return 0;
            }

            @Override
            public int pageSize() {
                return 10;
            }

            @Override
            public String keyword() {
                return "sample";
            }

            @Override
            public String stateCode() {
                return "CA";
            }

            @Override
            public String cityCode() {
                return "LA";
            }
        };
        requestEvent.setBody(gson.toJson(searchParam));

        // Invoke the handler
        APIGatewayProxyResponseEvent response = getPostList.handleRequest(requestEvent, contextMock);

        // Verify the response
        assertEquals(200, response.getStatusCode());

        // Parse the response body
        Response<Map<String, Object>> responseBody = gson.fromJson(response.getBody(), 
            new com.google.gson.reflect.TypeToken<Response<Map<String, Object>>>() {}.getType());

        assertNotNull(responseBody);
        assertEquals(200, responseBody.getStatusCode());
        assertNull(responseBody.getError());

        Map<String, Object> postListResponse = responseBody.getData();
        assertNotNull(postListResponse);
        assertTrue(postListResponse.containsKey("post_list"));

        List<Map<String, Object>> posts = (List<Map<String, Object>>) postListResponse.get("post_list");
        assertNotNull(posts);
        assertEquals(1, posts.size());

        Map<String, Object> post = posts.get(0);
        assertEquals("post1", post.get("postId"));
        assertEquals(1, post.get("isFavorite"));
    }

    // Define the SearchParam interface for testing purposes
    interface SearchParam {
        int pageNum();
        int pageSize();
        String keyword();
        String stateCode();
        String cityCode();
    }

    // Define the Response class for deserialization
    class Response<T> {
        private int statusCode;
        private T data;
        private Error error;

        public Response(int statusCode, T data, Error error) {
            this.statusCode = statusCode;
            this.data = data;
            this.error = error;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public T getData() {
            return data;
        }

        public Error getError() {
            return error;
        }
    }

    // Define the Error class for deserialization
    class Error {
        private String errorCode;
        private String errorMessage;

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}
