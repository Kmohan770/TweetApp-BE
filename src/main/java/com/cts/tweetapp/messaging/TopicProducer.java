//package com.cts.tweetapp.messaging;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class TopicProducer {
//
//    @Value("${topic.name.producer}")
//    private String topicName;
//
//    private final KafkaTemplate<String, String> kafkaTemplate;
//
//	public void sendMessage(String message) {
//		log.info("Payload Sent To Kafka : {}", message);
//        kafkaTemplate.send(topicName, message);
//        log.info("Payload sent successfully");
//		
//	}
//
//}