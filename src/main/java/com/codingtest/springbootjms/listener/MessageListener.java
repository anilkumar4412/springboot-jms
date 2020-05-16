package com.codingtest.springbootjms.listener;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.codingtest.springbootjms.entity.ProductOrder;
import com.codingtest.springbootjms.repo.ProductOrderRepository;

import ch.qos.logback.classic.Logger;


@Component
public class MessageListener {

	public static final Logger log = (Logger) LoggerFactory.getLogger(MessageListener.class.getName());
	@Autowired
	ProductOrderRepository productOrderRepository;

	@JmsListener(destination = "shoppingcart", containerFactory = "myListenerFactory")
	public void listener(String productId) {
		log.info("received product-id: {}", productId);
		if("DLQ".equalsIgnoreCase(productId)) {
			throw new RuntimeException("Product-id DLQ, should go to dlq") ;
		}
		ProductOrder po=productOrderRepository.save(ProductOrder.builder().productId(productId).build());
		log.info("shoppingcart queue received product-id: {}  saved to DB.  uuid : {}",productId,po.getId());

	}

	@JmsListener(destination = "ActiveMQ.DLQ.Queue.shoppingcart.dlq", containerFactory = "myListenerFactory")
	public void listener3(String productId) {
		log.info("Dead letter Queue ActiveMQ.DLQ.Queue.shoppingcart.dlq received product-id: {}", productId);

	}

}
