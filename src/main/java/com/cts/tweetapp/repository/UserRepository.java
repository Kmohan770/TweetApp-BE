package com.cts.tweetapp.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

import com.cts.tweetapp.beans.User;

@Repository
@EnableMongoRepositories
public interface UserRepository extends MongoRepository<User, String> {
	Optional<User> findByUserName(String username);
	Optional<String> findByRoles(String role);
//	Optional<User> findByUsernameAndPassword(String username, String password);

	boolean existsByUserName(String username);

	boolean existsByEmail(String email);
}
