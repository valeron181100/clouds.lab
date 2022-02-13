package yandex.cloud.examples.serverless.todo.queue;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

import javax.jms.JMSException;

public class QueueConnectionFactory {

    private final SQSConnectionFactory connectionFactory;

    public QueueConnectionFactory() {
        connectionFactory = new SQSConnectionFactory(
                new ProviderConfiguration(),
                AmazonSQSClientBuilder.standard()
                        .withRegion("ru-central1")
                        .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                                "https://message-queue.api.cloud.yandex.net",
                                "ru-central1"
                        ))
        );
    }

    public SQSConnection getConnection() throws JMSException {
        return this.connectionFactory.createConnection();
    }

}
