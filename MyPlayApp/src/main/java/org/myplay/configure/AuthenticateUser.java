package org.myplay.configure;

import java.util.Optional;

import org.myplay.models.User;
import org.myplay.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateUser implements UserDetailsService {
	@Autowired
	UserRepo userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user=userRepo.findByEmail(username);
		user.orElseThrow(() -> new UsernameNotFoundException("Not found"));	
		return user.map(UserAuth::new).get();
		 
	}
}
