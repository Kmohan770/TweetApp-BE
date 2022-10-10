package com.cts.tweetapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cts.tweetapp.beans.Message;
import com.cts.tweetapp.beans.Tweet;
import com.cts.tweetapp.beans.User;
import com.cts.tweetapp.beans.dto.TweetDto;
import com.cts.tweetapp.exception.BusinessException;
import com.cts.tweetapp.exception.DataException;
import com.cts.tweetapp.exception.TweetNotFoundException;
import com.cts.tweetapp.exception.UserExistException;
import com.cts.tweetapp.exception.UserNotFoundException;
import com.cts.tweetapp.messaging.TopicProducer;
import com.cts.tweetapp.security.jwt.JwtUtils;
import com.cts.tweetapp.service.TweetService;
import com.cts.tweetapp.service.UserService;
import com.cts.tweetapp.utils.TweetUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/v1.0/tweets")
@CrossOrigin(origins = "http://localhost:4200")
public class TweetController {

	@Autowired
	public TweetService tweetService;
	
	@Autowired
	public UserService userService;

	@Autowired
	private TopicProducer producer;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	private final String TOKEN_EXPIRED_OR_INVALID = "Invalid or Expired Authorization Token";

	public TweetController(TweetService tweetService, UserService userService) {
		super();
		this.tweetService = tweetService;
		this.userService = userService;
	}
	
	@PostMapping("/register")
	public Message insertUser(@RequestBody User user)  throws UserExistException, DataException, BusinessException{
		TweetUtils.logAsJson("[POST] Create Account Request", user);
		return userService.insertUser(user);
	}
	
	@PostMapping("/login")
	public User loginUser(@RequestBody User user) throws UserNotFoundException, BusinessException, UserExistException{
		TweetUtils.logAsJson("[POST] Login Request", user);
		return userService.getUser(user);
	}
	
	@PostMapping("/forgot/{userName}")
	public Message forgotPassword(@RequestBody User user, @PathVariable String userName) throws UserNotFoundException, DataException, BusinessException {
		TweetUtils.logAsJson("[POST]Forgot Password Request", user);
		return userService.forgotPassword(user);
	}
	
	@GetMapping("/all")
	public List<Tweet> getAllTweets(@RequestHeader("Authorization") String authorization) throws BusinessException, TweetNotFoundException {
		boolean isJwtValid = jwtUtils.validateJwtToken(authorization);
		log.info("jwt validation : "+isJwtValid);
		if(isJwtValid) {
			TweetUtils.logAsJson("[GET] all tweets for all users", "");
			log.info("Inside All Tweets");
			return tweetService.getAllTweets();
		}
		throw new JwtException(TOKEN_EXPIRED_OR_INVALID);
	}
	
	@GetMapping("/users/all")
	public List<User> getAllUsers(@RequestHeader("Authorization") String authorization) throws UserNotFoundException, BusinessException{
		boolean isJwtValid = jwtUtils.validateJwtToken(authorization);
		log.info("jwt validation : "+isJwtValid);
		if(isJwtValid) {
			TweetUtils.logAsJson("[GET] all user details", "");
			return userService.getAllUsers();
		}
		throw new JwtException(TOKEN_EXPIRED_OR_INVALID);
	}
	
	@GetMapping("/user/search/{userName}")
	public User getUserByUsername(@RequestHeader("Authorization") String authorization, @PathVariable String userName) throws UserNotFoundException, BusinessException {
		boolean isJwtValid = jwtUtils.validateJwtToken(authorization);
		log.info("jwt validation : "+isJwtValid);
		if(isJwtValid) {
			TweetUtils.logAsJson("[GET] user details for ", userName);
			return userService.getUserByuserName(userName);
		}
		throw new JwtException(TOKEN_EXPIRED_OR_INVALID);
	}

	@GetMapping("/all/{userName}")
	public List<Tweet> getAllTweetsByUserName(@RequestHeader("Authorization") String authorization, @PathVariable String userName) throws TweetNotFoundException, BusinessException {
		boolean isJwtValid = jwtUtils.validateJwtToken(authorization);
		log.info("jwt validation : "+isJwtValid);
		if(isJwtValid) {
			TweetUtils.logAsJson("[GET] all tweets for ", userName);
			return tweetService.getAllTweetsByUserName(userName);
		}
		throw new JwtException(TOKEN_EXPIRED_OR_INVALID);
	}
	
	@PostMapping("/{userName}/add")
	public TweetDto postNewTweet(@RequestHeader("Authorization") String authorization, @RequestBody Tweet tweet, @PathVariable String userName) 
			throws BusinessException, JsonProcessingException {
		boolean isJwtValid = jwtUtils.validateJwtToken(authorization);
		log.info("jwt validation : "+isJwtValid);
		if(isJwtValid) {
			TweetUtils.logAsJson("[POST] Add new tweet for " + userName, tweet);
			tweet.setUserName(userName);
			ObjectMapper mapper = new ObjectMapper();
			String message = mapper.writeValueAsString(tweet);
			producer.sendMessage(message);
			return new TweetDto(null, "Tweet Posted Successfully", 0);
//			return tweetService.postTweet(tweet);
		}
		throw new JwtException(TOKEN_EXPIRED_OR_INVALID);
	}

	@GetMapping("/logout/{userName}")
	private Message logOutUser(@PathVariable String userName) throws UserNotFoundException, DataException, BusinessException {
		TweetUtils.logAsJson("[GET] log out user ", userName);
		return userService.logOutUser(userName);
	}
	
	@PutMapping("/{userName}/update/{id}")
	public TweetDto updateTweet(@RequestHeader("Authorization") String authorization, @RequestBody Tweet tweet, @PathVariable String userName,
			@PathVariable("id") String id) throws TweetNotFoundException, BusinessException {
		boolean isJwtValid = jwtUtils.validateJwtToken(authorization);
		log.info("jwt validation : "+isJwtValid);
		if(isJwtValid) {
			TweetUtils.logAsJson("[PUT] update tweet request ", tweet);
			return tweetService.updateTweet(tweet, userName, id);
		}
		throw new JwtException(TOKEN_EXPIRED_OR_INVALID);
	}
	
	@DeleteMapping("/{userName}/delete/{id}")
	public Message deleteTweet(@RequestHeader("Authorization") String authorization, @PathVariable String userName, @PathVariable String id) 
			throws BusinessException, TweetNotFoundException {
		boolean isJwtValid = jwtUtils.validateJwtToken(authorization);
		log.info("jwt validation : "+isJwtValid);
		if(isJwtValid) {
			TweetUtils.logAsJson("[DELETE] Delete tweet for "+userName+" with id", userName);
			return tweetService.deleteTweet(userName, id);
		}
		throw new JwtException(TOKEN_EXPIRED_OR_INVALID);
	}
	
	@PutMapping("/{userName}/like/{id}")
	public TweetDto likeTweet(@RequestHeader("Authorization") String authorization, @PathVariable String userName, @PathVariable("id") String id)
			throws TweetNotFoundException, BusinessException {
		boolean isJwtValid = jwtUtils.validateJwtToken(authorization);
		log.info("jwt validation : "+isJwtValid);
		if(isJwtValid) {
			TweetUtils.logAsJson("[PUT] update tweet like count for "+userName+ " with id", userName);
			return tweetService.likeTweet(userName, id);
		}
		throw new JwtException(TOKEN_EXPIRED_OR_INVALID);
	}
	
	@PostMapping("/{loginId}/reply/{id}")
	public TweetDto replyToTweet(@RequestHeader("Authorization") String authorization, @RequestBody Tweet replytweet, @PathVariable String id, @PathVariable String loginId) 
			throws TweetNotFoundException, BusinessException {
		boolean isJwtValid = jwtUtils.validateJwtToken(authorization);
		log.info("jwt validation : "+isJwtValid);
		if(isJwtValid) {
			TweetUtils.logAsJson("[POST] reply to tweet with id "+ id, replytweet);
			return tweetService.replyPostTweet(replytweet, loginId, id);
		}
		throw new JwtException(TOKEN_EXPIRED_OR_INVALID);
	}
	
	@GetMapping("/{userName}/getReply/{tweetId}")
	public List<Tweet> getTweetReplies(@RequestHeader("Authorization") String authorization, @PathVariable String userName, @PathVariable String tweetId)
			throws TweetNotFoundException, BusinessException {
		boolean isJwtValid = jwtUtils.validateJwtToken(authorization);
		log.info("jwt validation : "+isJwtValid);
		if(isJwtValid) {
			TweetUtils.logAsJson("[GET] get tweet replies of "+ userName+ " with id ", tweetId);
			return tweetService.getTweetReplies(userName, tweetId);
		}
		throw new JwtException(TOKEN_EXPIRED_OR_INVALID);
	}
	
	@PostMapping("/generateToken")
	public String generateToken(@RequestBody User user) {
		return userService.generateToken(user);
	}
}
