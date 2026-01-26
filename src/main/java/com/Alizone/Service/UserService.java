package com.Alizone.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Alizone.Dto.SignupRequest;
import com.Alizone.Entity.Basket;
import com.Alizone.Entity.User;
import com.Alizone.Enum.ROL;
import com.Alizone.Exception.BusinessException;
import com.Alizone.Repository.BasketRepository;
import com.Alizone.Repository.UserRepository;


@Service
public class UserService implements	IUserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	private BasketRepository basketRepository;

	@Override
	public User signupRequest(SignupRequest signupRequest) {
	    if (userRepository.existsByEmail(signupRequest.getEmail())) {
	        throw new BusinessException("Bu mail adresine kayıtlı hesap zaten mevcut");
	    }

	    User user = new User();
	    user.setEmail(signupRequest.getEmail());
	    user.setIsim(signupRequest.getIsim());
	    user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
	    user.setKayittarihi(LocalDateTime.now());
	    user.setSoyisim(signupRequest.getSoyisim());
	    user.setVasıf(ROL.USER);
	    user.setTelno(signupRequest.getTelno());
	    userRepository.save(user);

	    
	    try {
	        //mailService.sendwelcomemail(user);
	    } catch (Exception e) {
	        System.out.println("Mail gönderilemedi: " + e.getMessage());
	    }

	    return user;
	}

	
	@Override
	public User getUserbyEmail(String email) {
		System.out.println("DB'de aranıyor (IgnoreCase): " + email);

	    Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email.trim());

	    if(userOpt.isPresent()) {
	        User user = userOpt.get();

	        System.out.println("Kullanıcı bulundu: " + user.getEmail());

	        return user;
	    } else {
	        throw new RuntimeException("kullanıcı bulunamadi");
	    }
	}


	@Override
	public User saveuseradmin(User admin) {
		admin.setVasıf(ROL.ADMIN);
		userRepository.save(admin);
		return admin;
	}


	@Override
	public ResponseEntity<String> forgotPassword(String email) {
		

	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    String token = UUID.randomUUID().toString();

	    user.setResetToken(token);
	    user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));

	    userRepository.save(user);

	    //mailService.sendResetPasswordEmail(user, token);

	    return ResponseEntity.ok("Mail gönderildi");
	}


	@Override
	public ResponseEntity<?> resetPassword(String token, String newPassword) {
		User user = userRepository.findByResetToken(token)
	            .orElseThrow(() -> new BusinessException("Geçersiz token"));

	    if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
	        throw new BusinessException("Token süresi dolmuş,Lütfen yeniden giriş yapınız");
	    }

	    user.setPassword(passwordEncoder.encode(newPassword));
	    user.setResetToken(null);
	    user.setResetTokenExpiry(null);

	    userRepository.save(user);

	    return ResponseEntity.ok("Şifre başarıyla güncellendi");
	}
	
	@Override
	public Optional<User> findByEmail(String email) {
	    return userRepository.findByEmail(email);
	}
	
	
	







}
