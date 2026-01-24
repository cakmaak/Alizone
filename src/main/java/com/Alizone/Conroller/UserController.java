package com.Alizone.Conroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Alizone.Dto.SignupRequest;
import com.Alizone.Dto.UserResponse;
import com.Alizone.Entity.User;
import com.Alizone.Service.BasketItemService;
import com.Alizone.Service.IUserService;
import com.Alizone.Service.MailService;

@RestController
@RequestMapping("/alizone/user")
public class UserController implements IUserController {

    
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private BasketItemService basketItemService;

   
	
	
	
	@PostMapping("/forgot-password")
	@Override
	public ResponseEntity<?> forgotPassword(@RequestParam String email) {
		// TODO Auto-generated method stub
		return userService.forgotPassword(email);
	}
	
	
	@PostMapping("/reset-password")
	@Override
	public ResponseEntity<?> resetPassword(@RequestParam String token,@RequestParam String newPassword) {
		
		return userService.resetPassword(token, newPassword);
	}

	
	@PostMapping("/signup")
	@Override
	public ResponseEntity<UserResponse> SignUpUser(@RequestBody SignupRequest signupRequest) {
		User signup = userService.signupRequest(signupRequest);

	    UserResponse res = new UserResponse();
	    res.setEmail(signup.getEmail());
	    res.setIsim(signup.getIsim());
	    res.setSoyisim(signup.getSoyisim());
	    res.setTelno(signup.getTelno());

	    return ResponseEntity.ok(res);
	}

	
	

}
