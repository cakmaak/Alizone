package com.Alizone.Conroller;

import org.springframework.http.ResponseEntity;

import com.Alizone.Dto.SignupRequest;
import com.Alizone.Entity.User;

public interface IUserController {
	
	public ResponseEntity<User> SignUpUser(SignupRequest signupRequest);
	public ResponseEntity<?> forgotPassword(String email);
	public ResponseEntity<?> resetPassword(String token,String newPassword);

}
