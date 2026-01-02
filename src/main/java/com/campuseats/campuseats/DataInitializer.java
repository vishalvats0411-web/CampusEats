package com.campuseats.campuseats; // FIXED PACKAGE NAME

import com.campuseats.campuseats.model.Canteen;
import com.campuseats.campuseats.model.MenuItem;
import com.campuseats.campuseats.model.User;
import com.campuseats.campuseats.repository.CanteenRepository;
import com.campuseats.campuseats.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UserRepository userRepository, CanteenRepository canteenRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Initialize Admin User
            if (!userRepository.existsById("admin")) {
                User admin = new User();
                admin.setCollegeId("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setName("System Administrator");
                admin.setRole("ROLE_ADMIN");
                userRepository.save(admin);
                System.out.println("Admin user created successfully.");
            }

            // 2. Initialize Canteens
            if (canteenRepository.count() == 0) {
                System.out.println("Seeding database...");

                // --- RAVI CANTEEN ---
                Canteen ravi = new Canteen();
                ravi.setName("Ravi Canteen");
                ravi.setDescription("Best South Indian Breakfast.");
                ravi.setLocation("Near Block A");
                ravi.setImageUrl("/images/canteen1.jpg");

                List<MenuItem> raviItems = new ArrayList<>();
                raviItems.add(createItem("Masala Dosa", 60.0, "Ravi Canteen", "Crispy spiced crepe.", "/images/dosa.jpg", "Breakfast"));
                raviItems.add(createItem("Idli Sambar", 40.0, "Ravi Canteen", "Steamed rice cakes.", "/images/idli.jpg", "Breakfast"));
                raviItems.add(createItem("Samosa", 15.0, "Ravi Canteen", "Potato stuffed pastry.", "/images/samosa.jpg", "Snacks"));

                ravi.setMenuItems(raviItems);
                canteenRepository.save(ravi);

                // --- QBC ---
                Canteen qbc = new Canteen();
                qbc.setName("QBC");
                qbc.setDescription("Burgers and Fast Food.");
                qbc.setLocation("Student Centre");
                qbc.setImageUrl("/images/canteen2.jpg");

                List<MenuItem> qbcItems = new ArrayList<>();
                qbcItems.add(createItem("Veg Burger", 45.0, "QBC", "Grilled veg patty.", "/images/burger.jpg", "Fast Food"));
                qbcItems.add(createItem("Cold Coffee", 40.0, "QBC", "Chilled creamy coffee.", "/images/coffee.jpg", "Beverages"));

                qbc.setMenuItems(qbcItems);
                canteenRepository.save(qbc);

                // --- GATE 1 ---
                Canteen gate1 = new Canteen();
                gate1.setName("Gate 1 Canteen");
                gate1.setDescription("Chinese and Rice.");
                gate1.setLocation("Main Gate");
                gate1.setImageUrl("/images/canteen3.jpg");

                List<MenuItem> gate1Items = new ArrayList<>();
                gate1Items.add(createItem("Fried Rice", 70.0, "Gate 1 Canteen", "Wok tossed rice.", "/images/friedrice.jpg", "Lunch"));
                gate1Items.add(createItem("Hakka Noodles", 65.0, "Gate 1 Canteen", "Spicy noodles.", "/images/noodles.jpg", "Lunch"));

                gate1.setMenuItems(gate1Items);
                canteenRepository.save(gate1);

                System.out.println("Database seeded successfully!");
            }
        };
    }

    private MenuItem createItem(String name, Double price, String canteenName, String desc, String img, String cat) {
        return new MenuItem(name, price, canteenName, desc, img, cat);
    }
}