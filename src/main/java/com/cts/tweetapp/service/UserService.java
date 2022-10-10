package com.cts.tweetapp.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.tweetapp.beans.Message;
import com.cts.tweetapp.beans.User;
import com.cts.tweetapp.exception.BusinessException;
import com.cts.tweetapp.exception.DataException;
import com.cts.tweetapp.exception.UserExistException;
import com.cts.tweetapp.exception.UserNotFoundException;
import com.cts.tweetapp.repository.UserRepository;
import com.cts.tweetapp.security.jwt.JwtUtils;
import com.cts.tweetapp.utils.TweetUtils;
import com.cts.tweetapp.utils.UserServiceUtils;

import lombok.var;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	private User user;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private UserServiceUtils userServiceUtils;
	
	@Transactional
	public String generateToken(User user) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		return jwt;
	}

	@Transactional
	public List<User> getAllUsers() throws UserNotFoundException, BusinessException {
		Query query = new Query();
		var allUsers = mongoTemplate.find(query, User.class, "user");
		if (ObjectUtils.isNotEmpty(allUsers)) {
			allUsers.stream().forEach(user -> removeConfidentialInfoFromResponse(user));
			TweetUtils.logAsJson("Get All User Details Response ", allUsers);
			return allUsers;
		}
		throw new UserNotFoundException("No User Found in DB");

	}

	@Transactional
	public User getUser(User user) throws UserNotFoundException, BusinessException, UserExistException {
		
		Query query = new Query(Criteria.where("userName").is(user.getUserName()));
		User dbUser = mongoTemplate.findOne(query, User.class, "user");
		if (dbUser != null) {
			if(encoder.matches(user.getPassword(), dbUser.getPassword())) {
				if(!dbUser.isLoggedIn()) {
					String jwt = generateToken(user);
					log.info("{} logged in successfully!!", user.getUserName());
					dbUser.setLoggedIn(true);
					userRepository.save(dbUser);
					dbUser.setJwt(jwt);
					removeConfidentialInfoFromResponse(dbUser);
					TweetUtils.logAsJson("Login Response ", dbUser);
					return dbUser;
				} else {
					throw new UserExistException("User already logged In");
				} 
			} else {
				throw new UserNotFoundException("Invalid Username or Password");
			} 
		} else {
			throw new UserNotFoundException("Invalid Username or Password");
		}

		
	}

	private void removeConfidentialInfoFromResponse(User user) {
		user.set_id(null);
		user.setPassword(null);
		user.setConfirmPassword(null);
		user.setRoles(null);
	}

	@Transactional
	public User getUserByuserName(String userName) throws UserNotFoundException, BusinessException {
		Query query = new Query(Criteria.where("userName").is(userName));
		user = mongoTemplate.findOne(query, User.class, "user");
		if (user != null) {
			removeConfidentialInfoFromResponse(user);
			TweetUtils.logAsJson("getUserByUserName Response ", user);
			return user;
		} else {
			throw new UserNotFoundException("User not found");
		}
	}

	@Transactional
	public Message logOutUser(String userName) throws UserNotFoundException, DataException, BusinessException {
		if(StringUtils.isNotBlank(userName)) {
			Query query = new Query(Criteria.where("userName").is(userName));
			User dbUser = mongoTemplate.findOne(query, User.class, "user");
			if (dbUser != null) {
				if(dbUser.isLoggedIn()) {
					dbUser.setLoggedIn(false);
					userRepository.save(dbUser);
					Message message =  new Message("Logged out Successfully");
					TweetUtils.logAsJson("Logout Response", message);
					return message;
				} else {
					throw new BusinessException("User not logged In!!!");
				}
			} else {
				throw new UserNotFoundException("User not found");
			}
		} else {
			throw new DataException("userName cannot be null");
		}

	}

	@Transactional
	public Message forgotPassword(User loggedInUser) throws UserNotFoundException, DataException, BusinessException {
		if (userServiceUtils.validateForgotPassword(loggedInUser)) {
			Query query = new Query(Criteria.where("userName").is(loggedInUser.getUserName())
					.andOperator(Criteria.where("email").is(loggedInUser.getEmail())));
			User dbUser = mongoTemplate.findOne(query, User.class, "user");
			if (dbUser != null) {
				boolean isPwdMatches = encoder.matches(loggedInUser.getPassword(), dbUser.getPassword()); 
				if (isPwdMatches) {
					throw new DataException("New Password should not be same as old password");
				} else {
					userRepository.delete(dbUser);
					dbUser.setLoggedIn(loggedInUser.isLoggedIn());
					dbUser.setPassword(encoder.encode(loggedInUser.getPassword()));
					dbUser.setConfirmPassword(encoder.encode(loggedInUser.getConfirmPassword()));
					
					userRepository.save(dbUser);
					Message message = new Message("New Password updated");
					TweetUtils.logAsJson("Forgot Pasword Response", message);
					return message;
				}

			} else {
				throw new UserNotFoundException("User not found for given userName and email address");
			}

		} else {
			log.error("Validation unsuccessfull for incoming request for forgot password call");
			throw new DataException("Validation unsuccessfull for incoming request for forgot password call");
		}

	}
	

	@Transactional
	public Message insertUser(User userDetails) throws UserExistException, DataException, BusinessException {
		if (userServiceUtils.validateUserRegistration(userDetails)) {
			if (userRepository.existsByUserName(userDetails.getUserName())) {
				throw new UserExistException("Error: Username is already taken!");
			}

			if (userRepository.existsByEmail(userDetails.getEmail())) {
				throw new UserExistException("Error: Email is already in use!");
			}

			// Create new user's account
			User user = new User();
			UUID uuid = UUID.randomUUID();
			String uuidAsString = uuid.toString();
			user.set_id(uuidAsString);
			user.setFirstName(userDetails.getFirstName());
			user.setLastName(userDetails.getLastName());
			user.setEmail(userDetails.getEmail());
			user.setPhoneNumber(userDetails.getPhoneNumber());
			user.setUserName(userDetails.getUserName());
			user.setPassword(encoder.encode(userDetails.getPassword()));
			user.setConfirmPassword(encoder.encode(userDetails.getConfirmPassword()));

			Set<String> roles = new HashSet<>();
			roles.add("ROLE_USER");
			user.setRoles(roles);
			
			userRepository.save(user);
			Message message = new Message("User registered successfully!");
			TweetUtils.logAsJson("Create Account Response", message);
			return message;
		}
		return null;
	}
}
