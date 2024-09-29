# Use the public AWS Lambda base image for Java 17
FROM public.ecr.aws/lambda/java:17

COPY target/classes ${LAMBDA_TASK_ROOT}
COPY target/dependency/* ${LAMBDA_TASK_ROOT}/lib/
# Copy the project files
COPY pom.xml .
COPY src ./src


# Set the entry point for the Lambda function when deployed
CMD ["com.rundong.UploadPost::handleRequest"]
