package com.Alizone.Service;

import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.Alizone.Dto.SignupRequest;
import com.Alizone.Entity.Order;
import com.Alizone.Entity.User;

import jakarta.servlet.http.HttpServletRequest;

public interface IUserService {
	
	public User getUserbyEmail(String email);
	public User saveuseradmin(User admin);
	public User signupRequest(SignupRequest signupRequest,HttpServletRequest request);
	public ResponseEntity<String> forgotPassword(String email);
	public ResponseEntity<?> resetPassword(String token,String newPassword);
	public Optional<User> findByEmail(String email);
	
	
	

}
