//package com.cts.tweetapp.messaging;
//
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.configurationprocessor.json.JSONException;
//import org.springframework.boot.configurationprocessor.json.JSONObject;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//import com.cts.tweetapp.beans.Tweet;
//import com.cts.tweetapp.exception.BusinessException;
//import com.cts.tweetapp.service.TweetService;
//import com.google.gson.Gson;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Service
//public class TopicListener {
//
//	@Autowired
//	private TweetService tweetService;
//	
//    @KafkaListener(topics = "${topic.name.consumer}", groupId = "${topic.name.consumer}")
//    public void consume(ConsumerRecord<String, String> payload) throws JSONException, BusinessException{
//        log.info("Payload Received From Kafka Producer: {}", payload.value());
//        
//        Tweet newTweet = new Gson().fromJson(payload.value(), Tweet.class);
//        tweetService.postTweet(newTweet);
//
//    }
//
//}