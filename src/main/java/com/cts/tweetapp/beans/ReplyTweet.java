package com.cts.tweetapp.beans;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "tweets")
@Getter
@Setter
@NoArgsConstructor
public class ReplyTweet {
	
	private String replyTweetId;
	private String userName;
	private String repliedMessage;
	private List<Tweet> repliedTweetsList;
	private LocalDateTime repliedDTTM;
	private Long replyMsglike;
	private boolean liked;
}
