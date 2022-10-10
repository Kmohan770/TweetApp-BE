package com.cts.tweetapp.beans;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "user")
@JsonInclude(value = Include.NON_NULL)
public class User {
	@Id
	private String _id;
	
	@NotBlank(message = "First Name Cannot Be Empty")
	private String firstName;
	@NotBlank(message = "Last Name Cannot Be Empty")
	private String lastName;
	@Email(message = "Please Enter Valid Email Address")
	private String email;
	@NotBlank(message = "User Name Cannot Be Empty")
	@UniqueElements(message = "Username already taken")
	private String userName;
	@Min(value = 5, message = "Password Length should be greater than or equal to 5 characters")
	@Max(value = 10, message = "Password Length should be lesser than or equal to 10 characters")
	private String password;
	@NotBlank(message = "Confirm Passowrd cannot be blank")
	private String confirmPassword;
	@NotBlank(message = "Phone Number cannot be blank")
	@Pattern(regexp = "^\\d{10}$", message = "Phone Number should be 10 numeric digits in length")
	private String phoneNumber;
	private Set<String> roles = new HashSet<>();
	private String jwt;
	
	@JsonAlias("isLoggedIn")
	private boolean isLoggedIn;
	private String newLoginId;
	
	public User(String username, String email, String password) {
		this.userName = username;
		this.email = email;
		this.password = password;
	}
}
