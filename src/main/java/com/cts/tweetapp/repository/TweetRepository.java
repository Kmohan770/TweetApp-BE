package com.cts.tweetapp.repository;

import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

import com.cts.tweetapp.beans.Tweet;

@Repository
@EnableMongoRepositories
public interface TweetRepository extends MongoRepository<Tweet,String> {
	@DeleteQuery
	public long deleteByTweetId(String tweetId);

}