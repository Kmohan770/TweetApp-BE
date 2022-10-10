package com.cts.tweetapp.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.cts.tweetapp.beans.User;
import com.cts.tweetapp.exception.DataException;
import com.google.common.base.Preconditions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserServiceUtils {
	
	public boolean validateUserRegistration(User user) throws DataException{
		try {
			Preconditions.checkArgument(user != null, "object cannot be null");
			Preconditions.checkArgument(StringUtils.isNotBlank(user.getFirstName()), "First name cannot be null/empty");
			Preconditions.checkArgument(StringUtils.isNotBlank(user.getLastName()), "Last name cannot be null/empty");
			Preconditions.checkArgument(StringUtils.isNotBlank(user.getEmail()), "Email cannot be null/empty");
			Preconditions.checkArgument(user.getEmail().matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$"), "Email Address is not Valid");
			Preconditions.checkArgument(StringUtils.isNotBlank(user.getUserName()), "Login ID cannot be null/empty");
			Preconditions.checkArgument(StringUtils.isNotBlank(user.getPhoneNumber()), "Phone Number cannot be null/empty");
			Preconditions.checkArgument(user.getPhoneNumber().matches("^\\d{10}$"), "Phone Number should be of 10 Digits [0-9]");
			Preconditions.checkArgument(StringUtils.isNotBlank(user.getPassword()), "Password cannot be null/empty");
			Preconditions.checkArgument(StringUtils.isNotBlank(user.getConfirmPassword()), "Confirm Password cannot be null/empty");
			Preconditions.checkArgument(matchConfirmPassowrd(user), "Confirm Password is not same as Password");
		} catch(Exception e) {
			log.error("Registration Validation Error : {}", e.getMessage());
			throw new DataException(e.getMessage());
		}
		return true;
	}
	
	public boolean validateForgotPassword(User user) throws DataException{
		try {
			Preconditions.checkArgument(user != null, "object cannot be null");
			Preconditions.checkArgument(StringUtils.isNotBlank(user.getEmail()), "Email cannot be null/empty");
			Preconditions.checkArgument(user.getEmail().matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$"), "Email Address is not Valid");
			Preconditions.checkArgument(StringUtils.isNotBlank(user.getUserName()), "Login ID cannot be null/empty");
			Preconditions.checkArgument(StringUtils.isNotBlank(user.getPassword()), "Password cannot be null/empty");
			Preconditions.checkArgument(StringUtils.isNotBlank(user.getConfirmPassword()), "Confirm Password cannot be null/empty");
			Preconditions.checkArgument(matchConfirmPassowrd(user), "Confirm Password is not same as Password");
		} catch(Exception e) {
			log.error("Registration Validation Error : {}", e.getMessage());
			throw new DataException(e.getMessage());
		}
		return true;
	}
	
	public boolean matchConfirmPassowrd(User user) {
		return user != null && StringUtils.isNotBlank(user.getPassword()) && StringUtils.isNotBlank(user.getConfirmPassword())
				&& user.getPassword().equals(user.getConfirmPassword());
	}
}
