package com.codingtest.springbootjms.config;

import java.util.ArrayList;
import java.util.List;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.DeadLetterStrategy;
import org.apache.activemq.broker.region.policy.IndividualDeadLetterStrategy;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.util.ErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class JmsConfig {
	
	@Bean
	 public Queue queue() {
	  return new ActiveMQQueue("shoppingcart");
	 }
	
	@Bean
	public JmsListenerContainerFactory<?> myListenerFactory(
	  ConnectionFactory connectionFactory,
	  DefaultJmsListenerContainerFactoryConfigurer configurer) {
	  DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
	  
	  factory.setErrorHandler(
	      new ErrorHandler() {
	        @Override
	        public void handleError(Throwable t) { 
	          //message will be sent to fallback DLQ when maximum retries exceed
	          log.error("Encountered an error while processing the transaction. message will be sent to DLQ after max retries exceed");
	        }
	      });
	  configurer.configure(factory, connectionFactory);
	  return factory;
	}
	
	@Bean
    public BrokerService broker() throws Exception {
        final BrokerService broker = new BrokerService();
        broker.addConnector("tcp://localhost:61616");
        broker.addConnector("vm://localhost");
        broker.setPersistent(false);
        broker.setDestinationPolicy(policyMap());
        return broker;
    }
	
	@Bean
    public PolicyMap policyMap() {
        PolicyMap destinationPolicy = new PolicyMap();
        List<PolicyEntry> entries = new ArrayList<PolicyEntry>();
        PolicyEntry queueEntry = new PolicyEntry();
        queueEntry.setQueue(">");
        queueEntry.setDeadLetterStrategy(deadLetterStrategy());
        entries.add(queueEntry);
        destinationPolicy.setPolicyEntries(entries);
        return destinationPolicy;
    }
	
	@Bean
    public DeadLetterStrategy deadLetterStrategy() { // this will create a fallback dead letter queue - ActiveMQ.DLQ.Queue.shoppingcart.dlq
        IndividualDeadLetterStrategy deadLetterStrategy = new IndividualDeadLetterStrategy();
        deadLetterStrategy.setQueueSuffix(".dlq");
        deadLetterStrategy.setUseQueueForQueueMessages(true);
        return deadLetterStrategy;
    }
	
	//this setting will try to re-deliver message 3 times if consumer fail to process message n throws any error
	@Bean
    public RedeliveryPolicy redeliveryPolicy() {
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(0);
        redeliveryPolicy.setBackOffMultiplier(2);
        redeliveryPolicy.setMaximumRedeliveryDelay(4000);
        redeliveryPolicy.setMaximumRedeliveries(3);
        return redeliveryPolicy;
    }

    @Bean
    public ConnectionFactory jmsConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        connectionFactory.setRedeliveryPolicy(redeliveryPolicy());
        return connectionFactory;
    }
}
