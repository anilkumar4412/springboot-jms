package com.codingtest.springbootjms;

import javax.jms.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {
	
	@Autowired
	JmsTemplate jmsTemplate;
	
	@Autowired
	Queue queue;
	
	@GetMapping("/add/{message}")
	 public String publish(@PathVariable("message") final String message) {

	  jmsTemplate.convertAndSend(queue,message);
	 
	  return "Product-Id published to jms queue - shoppingcart : "+message;

	 }

}
