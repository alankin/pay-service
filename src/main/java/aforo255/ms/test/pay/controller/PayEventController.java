package aforo255.ms.test.pay.controller;


import aforo255.ms.test.pay.domain.Operation;
import aforo255.ms.test.pay.producer.PaymentEventProducer;
import aforo255.ms.test.pay.service.IOperationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PayEventController {

    private Logger logger = LoggerFactory.getLogger(PayEventController.class);

    @Autowired
    PaymentEventProducer paymentEventProducer;

    @Autowired
    private IOperationService operationService;


    @PostMapping("/payments")
    public ResponseEntity<Operation> postPaymentEvent(@RequestBody Operation operation) throws JsonProcessingException {
        Operation operationSQL = operationService.save(operation);

        paymentEventProducer.sendPaymentEvent(operationSQL);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(operationSQL);
    }

    @GetMapping("/payments")
    public List<Operation> listPayments() {
        return (List<Operation>) operationService.findAll();
    }
}
