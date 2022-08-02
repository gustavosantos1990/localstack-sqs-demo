package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.HashMap;

@Testcontainers
@SpringBootTest
class DemoApplicationTests {

	static final HashMap<String, String > ENV;

	static {
		ENV = new HashMap<>();
		ENV.put("DEFAULT_REGION", "us-east-1");
		ENV.put("AWS_DEFAULT_REGION", "us-east-1");
		ENV.put("EDGE_PORT", "4566");
		ENV.put("SERVICES", "sqs");
		ENV.put("LOCALSTACK_HOSTNAME", "localhost");
		ENV.put("HOSTNAME_EXTERNAL", "localhost");
	}

	@Container
	static LocalStackContainer localStack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
			.withServices(LocalStackContainer.Service.SQS)
			.withEnv(ENV);

	@DynamicPropertySource
	static void overrideConfiguration(DynamicPropertyRegistry registry) throws IOException, InterruptedException {
		localStack.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", "order-queue");
		registry.add("cloud.aws.end-point.uri", () -> localStack.getEndpointOverride(LocalStackContainer.Service.SQS));
	}

	@Autowired
	private QueueMessagingTemplate queueMessagingTemplate;

	@Test
	void contextLoads() {
		queueMessagingTemplate.send("order-queue", new GenericMessage<>("test"));
	}

}
