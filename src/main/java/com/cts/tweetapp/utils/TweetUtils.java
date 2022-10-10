package com.cts.tweetapp.utils;

import com.cts.tweetapp.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TweetUtils {

	private static final ObjectMapper mapper = new ObjectMapper();
	
	public static String logAsJson(String message, Object object) throws BusinessException {
		String response = null;
		try {
			response = mapper.writeValueAsString(object);
			log.info("TweetApp | {} : {}",message, response);
		} catch(JsonProcessingException e) {
			log.error("Request Transformation Error in TweetUtils : {}", e.getMessage());
			throw new BusinessException("Request Transformation Error in TweetUtils");
		}
		return response;
	}
}
