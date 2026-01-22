package com.Alizone.Configuration;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.Alizone.Entity.User;
import com.Alizone.Enum.ROL;
import com.Alizone.Repository.UserRepository;
import com.Alizone.Service.IUserService;

import java.util.Optional;

import javax.management.relation.Role;

@Component
public class DataInitializer implements CommandLineRunner {

    private final IUserService userService;
    
    @Autowired
    UserRepository userRepo;

    public DataInitializer(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        Optional<User> adminOpt = userRepo.findByEmail("admin@admin.com");

        if (adminOpt.isEmpty()) {
            User admin = new User();
            admin.setIsim("Default Admin");
            admin.setSoyisim("Admin"); 
            admin.setEmail("admin@admin.com");
            admin.setPassword(new BCryptPasswordEncoder().encode("1234"));
            admin.setTelno("0000000000"); 
            admin.setVasıf(ROL.ADMIN);

            userService.saveuseradmin(admin);

            System.out.println("✅ Default admin oluşturuldu: admin@admin.com / 123456");
        }

        else {
            System.out.println("ℹ️ Admin zaten mevcut: " + adminOpt.get().getEmail());
        }
    }
}
