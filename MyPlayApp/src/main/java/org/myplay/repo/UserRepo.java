package org.myplay.repo;

import java.util.Optional;

import org.myplay.models.User;
import org.springframework.data.jpa.repository.JpaRepository;



public interface UserRepo extends JpaRepository<User,Integer>{

	Optional<User> findByEmail(String email);
	
}