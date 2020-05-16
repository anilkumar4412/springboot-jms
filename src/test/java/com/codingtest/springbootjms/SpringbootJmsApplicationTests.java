package com.codingtest.springbootjms;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import javax.jms.Queue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.codingtest.springbootjms.listener.MessageListener;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;  

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SpringbootJmsApplicationTests {
	
	
	
	@Autowired
	JmsTemplate jmsTemplate;
	
	@Autowired
	Queue queue;
	
	@Autowired
	CartController cartController;
	
	@Autowired
	ActiveMQConnectionFactory connectionFactory;
	
	@Autowired
	MessageListener messageListener;
	
	@Mock private Appender<ILoggingEvent> mockAppender;

	private static final String DEFAULT_NETWORK_BROKER_URL = "tcp://localhost:61616";
	
	private static final int InitialRedeliveryDelay = 0;
	private static final int BackOffMultiplier = 2;
	private static final int max_redeliveries = 3;
	private static final int MaximumRedeliveryDelay = 4000;
	
	
	
	@Test
	void testConnectionFactoryBrokerURL() {
		Assertions.assertTrue(connectionFactory.getBrokerURL().equals(DEFAULT_NETWORK_BROKER_URL));
		Assertions.assertEquals(connectionFactory.getRedeliveryPolicy().getMaximumRedeliveries(),max_redeliveries);
		Assertions.assertEquals(connectionFactory.getRedeliveryPolicy().getInitialRedeliveryDelay(),InitialRedeliveryDelay);
		Assertions.assertEquals(connectionFactory.getRedeliveryPolicy().getBackOffMultiplier(),BackOffMultiplier);
		Assertions.assertEquals(connectionFactory.getRedeliveryPolicy().getMaximumRedeliveryDelay(),MaximumRedeliveryDelay);
	}
	
	@Test
	void testMessageSender()throws Exception {
		Logger logger = (Logger) LoggerFactory.getLogger(MessageListener.class.getName());
        logger.addAppender(mockAppender);
		
		String response = cartController.publish("P1");
		
		assertEquals(response,"Product-Id published to jms queue - shoppingcart : P1");
		
		verify(mockAppender).doAppend(ArgumentMatchers.argThat(argument -> {
			cartController.publish("P1");
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Assertions.assertTrue(argument.getFormattedMessage().contains("P1"));
            return true;
        }));
		
		
	}
	
	

	
}
