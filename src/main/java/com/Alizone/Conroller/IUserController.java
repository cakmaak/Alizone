package com.Alizone.Conroller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.Alizone.Dto.SignupRequest;
import com.Alizone.Dto.UserResponse;
import com.Alizone.Entity.User;

public interface IUserController {
	
	public ResponseEntity<Map<String, String>> SignUpUser(SignupRequest signupRequest)
	public ResponseEntity<?> forgotPassword(String email);
	public ResponseEntity<?> resetPassword(String token,String newPassword);

}
