package com.Alizone.Conroller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Alizone.Dto.LoginRequest;
import com.Alizone.Entity.User;
import com.Alizone.Exception.BusinessException;
import com.Alizone.Security.JwtService;
import com.Alizone.Service.IUserService;
import com.Alizone.Service.MailService;



@RestController
@RequestMapping("/alizone")
public class AuthController {
	
	@Autowired
	private MailService mailService;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final IUserService userService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          IUserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        User user = userService.findByEmail(request.getEmail())
            .orElseThrow(() -> new BusinessException("Bu mail adresi ile kayıtlı hesap bulunamadı"));

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );
        } catch (BadCredentialsException e) {
            throw new BusinessException("Email veya Şifre hatalı");
        }

        // Login başarılı → mail gönder (test amaçlı)
        mailService.sendHtmlMail(
            "test@localhost",
            "Test Login",
            "Kullanıcı " + user.getEmail() + " başarıyla giriş yaptı."
        );

        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(Map.of("token", token));
    }

}