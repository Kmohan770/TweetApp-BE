package com.cts.tweetapp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

import com.cts.tweetapp.beans.ReplyTweet;

@EnableMongoRepositories
@Repository
public interface ReplyTweetRepository extends MongoRepository<ReplyTweet, String> {

}