package com.Alizone.Configuration;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.Alizone.Entity.User;
import com.Alizone.Enum.ROL;
import com.Alizone.Repository.UserRepository;
import com.Alizone.Service.IUserService;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.management.relation.Role;

@Component
public class DataInitializer implements CommandLineRunner {

    private final IUserService userService;
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${app.init-admin.enabled}")
    private boolean initAdminEnabled;

    @Value("${app.init-admin.email}")
    private String adminEmail;

    @Value("${app.init-admin.password}")
    private String adminPassword;

    @Value("${app.init-admin.name}")
    private String adminName;

    @Value("${app.init-admin.surname}")
    private String adminSurname;

    public DataInitializer(
            IUserService userService,
            UserRepository userRepo,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.userService = userService;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (!initAdminEnabled) {
            return; 
        }

        userRepo.findByEmail(adminEmail).ifPresentOrElse(
            user -> System.out.println("ℹ️ Admin zaten mevcut: " + user.getEmail()),
            () -> {
                User admin = new User();
                admin.setIsim(adminName);
                admin.setSoyisim(adminSurname);
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setTelno("0000000000");
                admin.setVasıf(ROL.ADMIN);
                admin.setKvkkAccepted(true);
                admin.setKvkkAcceptedAt(LocalDateTime.now());
                admin.setKvkkAcceptedIp("SYSTEM");

                userService.saveuseradmin(admin);

                System.out.println("✅ Admin oluşturuldu: " + adminEmail);
            }
        );
    }
}