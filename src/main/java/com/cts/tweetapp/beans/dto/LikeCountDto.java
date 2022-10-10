package com.cts.tweetapp.beans.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LikeCountDto {
	private String id;
	private long count;
	private boolean liked;
}
