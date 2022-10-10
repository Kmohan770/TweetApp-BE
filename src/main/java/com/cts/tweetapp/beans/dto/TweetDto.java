package com.cts.tweetapp.beans.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class TweetDto {
	private String tweetId;
	private String message;
	@JsonInclude(value = Include.NON_DEFAULT)
	private long likeCount;
}
