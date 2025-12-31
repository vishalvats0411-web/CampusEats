package com.campuseats.campuseats;

import com.campuseats.campuseats.model.Canteen;
import com.campuseats.campuseats.model.MenuItem;
import com.campuseats.campuseats.model.User; // FIX: Import User model
import com.campuseats.campuseats.repository.CanteenRepository;
import com.campuseats.campuseats.repository.MenuItemRepository;
import com.campuseats.campuseats.repository.UserRepository; // FIX: Import UserRepository
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder; // FIX: Import PasswordEncoder

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(MenuItemRepository menuRepo,
                                   CanteenRepository canteenRepo,
                                   UserRepository userRepo,       // FIX: Inject UserRepository here
                                   PasswordEncoder passwordEncoder) { // FIX: Inject PasswordEncoder here
        return args -> {
            // 1. Initialize Canteens
            if (canteenRepo.count() == 0) {
                canteenRepo.save(new Canteen("Ravi Canteen", "Famous for Samosas"));
                canteenRepo.save(new Canteen("QBC", "Coffee and Snacks"));
                canteenRepo.save(new Canteen("Delicious Canteen", "Burgers and Fast Food"));
                canteenRepo.save(new Canteen("CS/IT Canteen", "Rolls and Wraps"));
                canteenRepo.save(new Canteen("Gate 1 Canteen", "Chinese and Rice"));
                canteenRepo.save(new Canteen("Gate 2 Canteen", "Maggi point"));
                System.out.println("Canteens Loaded!");
            }

            // 2. Initialize Menu Items
            if (menuRepo.count() == 0) {
                menuRepo.save(new MenuItem("Samosa", 15.0, "Ravi Canteen"));
                menuRepo.save(new MenuItem("Masala Dosa", 50.0, "Ravi Canteen"));
                menuRepo.save(new MenuItem("Cold Coffee", 40.0, "QBC"));
                menuRepo.save(new MenuItem("Veg Burger", 45.0, "Delicious Canteen"));
                menuRepo.save(new MenuItem("Paneer Roll", 60.0, "CS/IT Canteen"));
                menuRepo.save(new MenuItem("Fried Rice", 70.0, "Gate 1 Canteen"));
                menuRepo.save(new MenuItem("Maggi", 25.0, "Gate 2 Canteen"));
                System.out.println("Menu Items Loaded!");
            }

            // 3. Initialize Admin User
            // FIX: Uses userRepo and passwordEncoder passed in the method arguments above
            if (!userRepo.existsById("admin")) {
                User admin = new User();
                admin.setCollegeId("admin");
                admin.setName("Admin User");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ROLE_ADMIN");
                userRepo.save(admin);
                System.out.println("Admin User Created! (ID: admin, Pass: admin123)");
            } else {
                System.out.println("Admin User already exists. Skipping creation.");
            }
        };
    }
}