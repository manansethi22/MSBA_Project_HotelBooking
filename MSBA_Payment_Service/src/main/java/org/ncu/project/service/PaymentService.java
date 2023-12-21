package org.ncu.project.service;
import org.springframework.stereotype.Service;
import org.ncu.project.dto.PaymentRequest;
import org.ncu.project.entity.Payment;
import org.ncu.project.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class PaymentService {

	@Autowired
    private PaymentRepository paymentRepository;
    
    public String performTransaction(PaymentRequest request){
    	Payment payment = new Payment();
        payment.setPaymentMode(request.getPaymentMode());
        payment.setBookingId(request.getBookingId());
        payment.setUpiId(request.getUpiId());
        payment.setCardNumber(request.getCardNumber());
        Payment savedPayment = paymentRepository.save(payment);
        return String.valueOf(savedPayment.getTransactionId());
    }
    
    public Payment getPaymentById(int transactionId) {
    	return paymentRepository.findById(transactionId).orElse(null);
    }
}
