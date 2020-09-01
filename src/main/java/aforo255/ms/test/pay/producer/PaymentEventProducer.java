package aforo255.ms.test.pay.producer;

import aforo255.ms.test.pay.domain.Operation;
import aforo255.ms.test.pay.service.IOperationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Arrays;
import java.util.List;


@Component
public class PaymentEventProducer {

    String topic = "transaction-events";

    private Logger log = LoggerFactory.getLogger(PaymentEventProducer.class);

    @Autowired
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;


    public ListenableFuture<SendResult<Integer, String>> sendPaymentEvent(Operation operation) throws JsonProcessingException {

        Integer key = operation.getIdOperation();
        String value = objectMapper.writeValueAsString(operation);

        ProducerRecord<Integer, String> producerRecord = buildProducerRecord(key, value, topic);

        ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.send(producerRecord);

        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                handleFailure(key, value, throwable);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                try {
                    handleSuccess(key, value, result);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });

        return listenableFuture;

    }


    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topic) {
        List<Header> recordHeaders = Arrays.asList(new RecordHeader("payment-event-source", "scanner".getBytes()));

        return new ProducerRecord<>(topic, null, key, value, recordHeaders);

    }


    private void handleFailure(Integer key, String value, Throwable ex) {
        log.error("Error Sending the Message and the exception is: {}", ex.getMessage());

        try {
            throw ex;
        } catch (Throwable throwable) {
            log.error("Error in OnFailure: {}", throwable.getMessage());
        }
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) throws JsonProcessingException {
        log.info("Message Sent Successfully for key: {} and value is: {} , partition is {}",
                key,
                value,
                result.getRecordMetadata().partition());
    }

}
