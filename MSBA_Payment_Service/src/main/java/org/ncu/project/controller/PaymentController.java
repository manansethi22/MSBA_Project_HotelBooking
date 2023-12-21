package org.ncu.project.controller;

import org.ncu.project.dto.PaymentRequest;
import org.ncu.project.dto.PaymentResponse;
import org.ncu.project.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {
	
	 	@Autowired
	    private PaymentService paymentService;

	    @PostMapping("/transaction")
	    public ResponseEntity<String> performTransaction(@RequestBody PaymentRequest paymentRequest) {
	        String transactionId = paymentService.performTransaction(paymentRequest);
	        return new ResponseEntity<>(transactionId, HttpStatus.OK);
	    }
		
	    @GetMapping("transaction/{transactionId}")
	    public ResponseEntity<?> getTransactionDetails(@PathVariable int transactionId) {
	    	PaymentResponse transactionDetails = new PaymentResponse(paymentService.getPaymentById(transactionId));
	        return ResponseEntity.ok(transactionDetails);
	       
	    }
}
