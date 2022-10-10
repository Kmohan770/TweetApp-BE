package com.cts.tweetapp.exception;

public class TweetNotFoundException extends Exception{


	private static final long serialVersionUID = 1L;

	public TweetNotFoundException(String message) {
		super(message);
	}
	
}