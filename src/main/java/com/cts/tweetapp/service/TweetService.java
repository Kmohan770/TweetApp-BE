package com.cts.tweetapp.service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.tweetapp.beans.Message;
import com.cts.tweetapp.beans.Tweet;
import com.cts.tweetapp.beans.dto.TweetDto;
import com.cts.tweetapp.exception.BusinessException;
import com.cts.tweetapp.exception.TweetNotFoundException;
import com.cts.tweetapp.repository.TweetRepository;
import com.cts.tweetapp.utils.TweetUtils;

@Service
public class TweetService {

	@Autowired
	public TweetRepository tweetRepository;

	@Autowired
	MongoTemplate mongoTemplate;

	@Transactional
	public List<Tweet> getAllTweets() throws BusinessException, TweetNotFoundException {
		Query query = new Query();
		List<Tweet> tweets = mongoTemplate.find(query, Tweet.class, "tweets");
		if(ObjectUtils.isNotEmpty(tweets)) {
			Collections.sort(tweets, Comparator.comparing(Tweet::getLdtPostDTTM));
			Collections.reverse(tweets);
			TweetUtils.logAsJson("Get All Tweets Response", tweets);
			return tweets;
		}
		throw new TweetNotFoundException("No Tweet Posted By Any User");
	}

	@Transactional
	public List<Tweet> getAllTweetsByUserName(String userName) throws TweetNotFoundException, BusinessException {
		Query query = new Query(Criteria.where("userName").is(userName));
		List<Tweet> tweets = mongoTemplate.find(query, Tweet.class, "tweets");
		if(ObjectUtils.isNotEmpty(tweets)) {
			Collections.sort(tweets, Comparator.comparing(Tweet::getLdtPostDTTM));
			Collections.reverse(tweets);
			TweetUtils.logAsJson("Get All Tweets For "+userName+" Response", tweets);
			return tweets;
		} else {
			throw new TweetNotFoundException("No Tweet Available for this user");
		}
	}

	@Transactional
	public TweetDto postTweet(Tweet tweet) throws BusinessException {
		UUID uuid = UUID.randomUUID();
		String uuidAsString = uuid.toString();
		tweet.setTweetId(uuidAsString);
		tweet.setLdtPostDTTM(LocalDateTime.now());
		mongoTemplate.save(tweet, "tweets");
		TweetDto tweetDto = new TweetDto(uuidAsString, "Tweet inserted successfully", 0);
		TweetUtils.logAsJson("Add New Tweet Response", tweetDto);
		return tweetDto;
	}

	@Transactional
	public Tweet getTweetById(String id, String userName) throws BusinessException {
		Query query = new Query(Criteria.where("tweetId").is(id));
		Tweet dbTweet = mongoTemplate.findOne(query, Tweet.class, "tweets");
//		TweetUtils.logAsJson("Get Tweet By TweetId "+id, dbTweet);
		return dbTweet;
	}

	@Transactional
	public TweetDto replyPostTweet(Tweet replyTweet, String userName, String id) throws TweetNotFoundException, BusinessException {
		Tweet parentTweet = getTweetById(id, userName);
		if(parentTweet != null) {
			UUID uuid = UUID.randomUUID();
			String uuidAsString = uuid.toString();
			replyTweet.setTweetId(uuidAsString);
			LocalDateTime dateTime = LocalDateTime.now();
			replyTweet.setLdtPostDTTM(dateTime);
			if(ObjectUtils.isNotEmpty(parentTweet.getReplyTweets())) {
				parentTweet.getReplyTweets().add(replyTweet);
			} else {
				List<Tweet> replyTweets = new ArrayList<>();
				replyTweets.add(replyTweet);
				parentTweet.setReplyTweets(replyTweets);
			}
			mongoTemplate.save(parentTweet, "tweets");
		} else {
			throw new TweetNotFoundException("No Tweet found for given userName : "+userName+" and tweetId : "+ id) ;
		}
		TweetDto tweetDto = new TweetDto(id, "Reply Tweet inserted successfully", 0);
		TweetUtils.logAsJson("Add Reply for tweet with id "+id+" Response", tweetDto);
		return tweetDto;
	}

	@Transactional
	public TweetDto updateTweet(Tweet tweet, String userName, String id) throws TweetNotFoundException, BusinessException {
		Tweet oldtweet = getTweetById(id, userName);
		if (oldtweet != null && !oldtweet.getMessage().equalsIgnoreCase(tweet.getMessage())) {
			oldtweet.setMessage(tweet.getMessage());
			oldtweet.setPostDTTM(tweet.getPostDTTM());
			oldtweet.setLdtPostDTTM(LocalDateTime.now());
			mongoTemplate.save(oldtweet, "tweets");
			TweetDto tweetDto = new TweetDto(id, "Tweet updated successfully", 0);
			TweetUtils.logAsJson("Update Tweet with Tweet Id "+id+" Response", tweetDto);
			return tweetDto;
		} else if(oldtweet != null && oldtweet.getMessage().equalsIgnoreCase(tweet.getMessage())){
			throw new BusinessException("Updated Tweet Cannot be same as old tweet");
		} else {
			throw new TweetNotFoundException("No tweet Found!!");
		}
	}

	@Transactional
	public Message deleteTweet(String userName, String id) throws BusinessException, TweetNotFoundException {
		String message = null;
		Tweet tweet = getTweetById(id, userName);
		if (tweet != null) {
			if(userName.equalsIgnoreCase(tweet.getUserName()) && tweetRepository.deleteByTweetId(id) > 0) {
				message =  "Tweet Deleted Successfully";
			} else {
				throw new TweetNotFoundException("Tweet Not Found for user : " + userName + " with tweetId : "+id)  ;
			}
		} else {
			throw new TweetNotFoundException("Tweet Not Found!!");
		}
		Message response = new Message(message);
		TweetUtils.logAsJson("Delete Tweet with Id "+id+" Response", response);
		return response;
		
	}

	@Transactional
	public TweetDto likeTweet(String userName, String id) throws TweetNotFoundException, BusinessException {
		Tweet dbTweet = getTweetById(id, userName);
		if(dbTweet != null) {
			long count = dbTweet.getPostLikeCount() > 0 ? dbTweet.getPostLikeCount() + 1 : 1;
			dbTweet.setPostLikeCount(count);
		} else {
			throw new TweetNotFoundException("No Tweet Available");
		}
		mongoTemplate.save(dbTweet,"tweets");
		TweetDto tweetDto = new TweetDto(id, "Tweet Liked Successfully", dbTweet.getPostLikeCount());
		TweetUtils.logAsJson("Like Tweet with Id"+id+" Response", tweetDto);
		return tweetDto;
				
	}

	@Transactional
	public List<Tweet> getTweetReplies(String userName, String id) throws TweetNotFoundException, BusinessException {
		Tweet dbTweet = getTweetById(id, userName);
		if(ObjectUtils.isNotEmpty(dbTweet.getReplyTweets())) {
			Collections.sort(dbTweet.getReplyTweets(), Comparator.comparing(Tweet::getLdtPostDTTM));
			Collections.reverse(dbTweet.getReplyTweets());
			List<Tweet> replyTweeets = dbTweet.getReplyTweets();
			TweetUtils.logAsJson("Tweet Replies for tweet with Id"+id+" Response", replyTweeets);
			return replyTweeets;
		}
		throw new TweetNotFoundException("No Replies on Tweet Available");
	}

}

