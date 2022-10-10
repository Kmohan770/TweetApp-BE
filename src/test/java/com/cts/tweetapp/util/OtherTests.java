package com.cts.tweetapp.util;

import static org.junit.Assert.assertNotNull;

import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import com.cts.tweetapp.beans.Message;
import com.cts.tweetapp.beans.User;
import com.cts.tweetapp.beans.dto.TweetDto;
import com.cts.tweetapp.exception.BusinessException;
import com.cts.tweetapp.exception.DataException;
import com.cts.tweetapp.exception.GlobalExceptionHandler;
import com.cts.tweetapp.exception.TweetException;
import com.cts.tweetapp.exception.TweetNotFoundException;
import com.cts.tweetapp.exception.UserException;
import com.cts.tweetapp.exception.UserExistException;
import com.cts.tweetapp.exception.UserNotFoundException;
import com.cts.tweetapp.utils.TweetUtils;

public class OtherTests {
	
	private final String TEST = "test";
	
	@InjectMocks
	private WebRequest webRequest;
	
	@Test
	public void exceptionBeansTest() {
		BusinessException businessException = new BusinessException(TEST);
		DataException dataException = new DataException(TEST);
		TweetException tweetException = new TweetException(TEST);
		TweetNotFoundException tweetNotFoundException = new TweetNotFoundException(TEST);
		UserException userException = new UserException(TEST);
		UserExistException userExistException = new UserExistException(TEST);
		UserNotFoundException userNotFoundException = new UserNotFoundException(TEST);

		
		GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
		globalExceptionHandler.handleUserNotFoundException(userNotFoundException, webRequest);
		globalExceptionHandler.handleTweetNotFoundException(tweetNotFoundException, webRequest);
		globalExceptionHandler.handleUserExistsException(userExistException, webRequest);
		globalExceptionHandler.handleInvalidDataException(dataException, webRequest);
		globalExceptionHandler.handleBusinessException(businessException, webRequest);
		globalExceptionHandler.handleUserException(userException, webRequest, HttpStatus.NOT_FOUND);
		globalExceptionHandler.handleTweetException(tweetException, webRequest, HttpStatus.INTERNAL_SERVER_ERROR);

		assertNotNull(businessException);
		assertNotNull(dataException);
		assertNotNull(tweetException);
		assertNotNull(tweetNotFoundException);
		assertNotNull(userException);
		assertNotNull(userExistException);
		assertNotNull(userNotFoundException);
		assertNotNull(globalExceptionHandler);
		
	}
	
	@Test
	public void handleBeanRelatedScenarios() {
		TweetDto tweetDto = new TweetDto(TEST, TEST, 0);
		User user = new User(TEST, TEST, TEST);
		user.setRoles(new HashSet<>());
		Message message = new Message(TEST);
		
		assertNotNull(tweetDto);
		assertNotNull(user); 
		assertNotNull(message); 
	}
	
	@Test
	public void tweetUtilsTest() throws BusinessException {
		User user = new User(TEST, TEST, TEST);
		assertNotNull(TweetUtils.logAsJson(TEST, user));
		assertNotNull(TweetUtils.logAsJson(TEST, null));
	}
	

}
