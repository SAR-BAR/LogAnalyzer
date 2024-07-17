package com.project.loganalysis.QueueImpl;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueueConsumer {

    private static final String QUEUE_NAME = "logQueue";

    @Autowired
    private RestHighLevelClient elasticsearchClient;

    @RabbitListener(queues = QUEUE_NAME)
    public void handleMessage(String logEntry) {
        System.out.println("Received log entry from queue '" + QUEUE_NAME + "': " + logEntry);
        sendToElasticsearch(logEntry);
    }

    private void sendToElasticsearch(String logEntry) {
        try {
            IndexRequest request = new IndexRequest("logs"); // Index name
            request.source(logEntry, XContentType.JSON);

            // Perform async index operation
            elasticsearchClient.indexAsync(request, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
                @Override
                public void onResponse(IndexResponse indexResponse) {
                    System.out.println("Successfully sent log entry to Elasticsearch: " + logEntry);
                }

                @Override
                public void onFailure(Exception e) {
                    System.err.println("Failed to send log entry to Elasticsearch: " + e.getMessage());
                    e.printStackTrace(); // Log full stack trace for detailed error analysis
                }
            });
        } catch (Exception e) {
            System.err.println("Exception while sending log entry to Elasticsearch: " + e.getMessage());
            e.printStackTrace(); // Log full stack trace for detailed error analysis
        }
    }



}
