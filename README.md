# springboot-jms
Spring boot application with in-memory ActiveMQ broker, destination queue and DLQ

SpringBoot version: 2.2.7.RELEASE

Java Version: 1.8

Apache ActiveMQ 5.15.12

DataBase-  H2


In memory Broker URL : tcp://localhost:61616


Destination Queue: shoppingcart

DLQ:    ActiveMQ.DLQ.Queue.shoppingcart.dlq


GET URL to send message to destation queue:  http://localhost:8080/api/cart/add/{Product-id}
  
example http://localhost:8080/api/cart/add/BIKE

Redelivery policy conficured as below:
RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();

redeliveryPolicy.setInitialRedeliveryDelay(0);

redeliveryPolicy.setBackOffMultiplier(2);

redeliveryPolicy.setMaximumRedeliveryDelay(4000);

redeliveryPolicy.setMaximumRedeliveries(3);




