package org.ncu.project.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.ncu.project.dto.BookingRequest;
import org.ncu.project.dto.PaymentRequest;
import org.ncu.project.entity.Booking;
import org.ncu.project.helper.RoomGenerator;
import org.ncu.project.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import org.springframework.cloud.client.discovery.DiscoveryClient;


@Service
public class BookingService {


    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private DiscoveryClient discoveryClient;
 

    public Booking createBooking(BookingRequest request) {
        
        Booking booking = new Booking();
        RoomGenerator roomGenerator = new RoomGenerator(); 
    	final int basePrice = 1000;
        LocalTime time = LocalTime.of(0, 0, 0);

        LocalDate tempFromDate = request.getFromDate(); 
        LocalDateTime fromDate = LocalDateTime.of(tempFromDate, time);
        booking.setFromDate(fromDate); 
        
        LocalDate tempToDate = request.getToDate();     
        LocalDateTime toDate =  LocalDateTime.of(tempToDate, time);
        booking.setToDate(toDate);
        
        String aadharNumber = request.getAadharNumber(); 
        booking.setAadharNumber(aadharNumber);
        
        int numOfRooms = request.getNumOfRooms();
        booking.setNumOfRooms(numOfRooms);
        
        String roomNumbers = roomGenerator.generateVacantRooms(numOfRooms);
        booking.setRoomNumbers(roomNumbers);
        
        long daysBetween = ChronoUnit.DAYS.between(fromDate.toLocalDate(), toDate.toLocalDate());
        int totalDays = (int) daysBetween; 
        
        int roomPrice = basePrice * numOfRooms * totalDays;
        booking.setRoomPrice(roomPrice);
        
        booking.setTransactionId(0);
        
        booking.setBookedOn(LocalDateTime.now());
        
        return bookingRepository.save(booking);
    }	

    
    public boolean doesBookingExist(int bookingId) {
    	Booking booking = bookingRepository.findByBookingId(bookingId);
        return booking != null; 
    }
    
	public Booking findBookingById(int id) {	
	    return bookingRepository.findByBookingId(id);
	}
    
    public Booking saveTransactionId(int bookingId, int transactionId) {
    	 Booking booking = findBookingById(bookingId);
    	 booking.setTransactionId(transactionId); // Set the transactionId in the booking object
         return bookingRepository.save(booking);
    }
    
    public Booking performPayment(int bookingId, PaymentRequest request)
    { 
    	List<ServiceInstance> instances = discoveryClient.getInstances("PAYMENT-SERVICE");	
    	String url = null;

    	 if (instances != null && !instances.isEmpty()) {
             ServiceInstance serviceInstance = instances.get(0); // Assuming one instance for simplicity
             url = serviceInstance.getUri().toString() + "/payment" + "/transaction";
             System.out.println("----------------------------");
             System.out.print("URL for Communication: ");
             System.out.println(url);
             System.out.println("----------------------------");
         }
         
         else
         {
         	System.out.println("NO MICROSERVICE RUNNING!!!");
         }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> requestEntity = new HttpEntity<>(request, headers);

        int transactionID = restTemplate.postForObject(url, requestEntity, Integer.class);
        
    	return saveTransactionId(bookingId, transactionID);
    }
}
