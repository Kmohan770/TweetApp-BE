package com.cts.tweetapp.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.cts.tweetapp.beans.User;
import com.cts.tweetapp.exception.DataException;
import com.cts.tweetapp.utils.UserServiceUtils;

public class UserServiceUtilsTest {

	private UserServiceUtils userServiceUtils = new UserServiceUtils();
	private final String TEST = "test";
	
	@Test
	public void ValidateUserRegistration() throws DataException {
		User user = new User();
		user.setFirstName(TEST);
		user.setLastName(TEST);
		user.setEmail("test@test.com");
		user.setUserName(TEST);
		user.setPhoneNumber("1234567890");
		user.setPassword(TEST);
		user.setConfirmPassword(TEST);
		
		assertTrue(userServiceUtils.validateUserRegistration(user));
		assertTrue(userServiceUtils.validateForgotPassword(user));
		
	}
	
	@Test()
	public void ValidateUserRegistrationException() throws DataException {
		assertThrows(DataException.class, () ->{
			userServiceUtils.validateUserRegistration(null);
		});
		assertThrows(DataException.class, () ->{
			userServiceUtils.validateForgotPassword(null);
		});
		
	}
	
	@Test
	public void matchConfirmPassword(){
		User user = new User();
		user.setPassword(TEST);
		user.setConfirmPassword(TEST);
		
		assertFalse(userServiceUtils.matchConfirmPassowrd(null));
		assertTrue(userServiceUtils.matchConfirmPassowrd(user));
		
		
		
	}
	
	
}
