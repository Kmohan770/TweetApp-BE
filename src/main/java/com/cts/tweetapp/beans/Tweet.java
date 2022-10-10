package com.cts.tweetapp.beans;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "tweets")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class Tweet {
	
	@Id
	@JsonIgnore
	private String _id;
	private String tweetId;
	private String message;
	private String userName;
	private String emailId;
	private List<Tweet> replyTweets;
	private String postDTTM;
	@JsonIgnore
	private LocalDateTime ldtPostDTTM;
	private long postLikeCount;
	private boolean liked;
}
