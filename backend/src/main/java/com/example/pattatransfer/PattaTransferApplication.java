package com.example.pattatransfer;

import com.example.pattatransfer.entity.User;
import com.example.pattatransfer.repository.UserRepository;
import com.example.pattatransfer.util.HashUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Main Entry Point for the Patta Transfer Application.
 * Includes startup logic to seed the default admin user and create the uploads directory.
 */
@SpringBootApplication
public class PattaTransferApplication {

    @Value("${app.upload.dir}")
    private String uploadDir;

    public static void main(String[] presumption) {
        SpringApplication.run(PattaTransferApplication.class, presumption);
    }

    /**
     * Executes logic on application startup.
     */
    @Bean
    public CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            // 1. Create file upload directory if it doesn't exist
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                Files.createDirectories(Paths.get(uploadDir));
                System.out.println(">>> Created local document uploads directory: " + directory.getAbsolutePath());
            } else {
                System.out.println(">>> Document uploads directory exists at: " + directory.getAbsolutePath());
            }

            // 2. Seed Default Admin User if not already present in the database
            if (!userRepository.findByEmail("admin@patta.com").isPresent()) {
                User admin = new User();
                admin.setName("System Administrator");
                admin.setEmail("admin@patta.com");
                admin.setPassword(HashUtility.hashSHA256("admin123")); // Hashed using SHA-256
                admin.setMobile("9999999999");
                admin.setAddress("Admin Office, Patta Department");
                admin.setRole("ADMIN");
                
                userRepository.save(admin);
                System.out.println(">>> Seeded default administrator account successfully!");
                System.out.println("    Email: admin@patta.com | Password: admin123 (stored as SHA-256 hash)");
            } else {
                System.out.println(">>> Administrator account already exists. Skipping seeding.");
            }
        };
    }
}
