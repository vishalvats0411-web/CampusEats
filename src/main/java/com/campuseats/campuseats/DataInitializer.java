package com.campuseats.campuseats;

import com.campuseats.campuseats.model.Canteen;
import com.campuseats.campuseats.model.MenuItem;
import com.campuseats.campuseats.model.User;
import com.campuseats.campuseats.repository.CanteenRepository;
import com.campuseats.campuseats.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataInitializer {

    @Value("${app.admin.default-password}")
    private String adminPassword;

    @Bean
    CommandLineRunner initData(UserRepository userRepository, CanteenRepository canteenRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsById("admin")) {
                User admin = new User();
                admin.setCollegeId("admin");
                // Use the injected variable, not a hardcoded string
                admin.setPassword(passwordEncoder.encode(adminPassword));
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
                ravi.setDescription("");
                ravi.setLocation("Near Block A");
                ravi.setImageUrl("/images/ravi.jpg");

                List<MenuItem> raviItems = new ArrayList<>();
                raviItems.add(createItem("Masala Dosa", new BigDecimal("60.00"), "Ravi Canteen", "Crispy spiced crepe.", "/images/dosa.jpg", "Breakfast"));
                raviItems.add(createItem("Idli Sambar", new BigDecimal("40.00"), "Ravi Canteen", "Steamed rice cakes.", "/images/idli.jpg", "Breakfast"));
                raviItems.add(createItem("Samosa", new BigDecimal("15.00"), "Ravi Canteen", "Potato stuffed pastry.", "/images/samosa.jpg", "Snacks"));

                ravi.setMenuItems(raviItems);
                canteenRepository.save(ravi);

                // --- QBC ---
                Canteen qbc = new Canteen();
                qbc.setName("QBC");
                qbc.setDescription("Burgers and Fast Food.");
                qbc.setLocation("Student Centre");
                qbc.setImageUrl("/images/qbc.jpg");

                List<MenuItem> qbcItems = new ArrayList<>();
                qbcItems.add(createItem("Veg Burger", new BigDecimal("45.00"), "QBC", "Grilled veg patty.", "/images/burger.jpg", "Fast Food"));
                qbcItems.add(createItem("Cold Coffee", new BigDecimal("60.00"), "QBC", "Chilled creamy coffee.", "/images/coffee.jpg", "Beverages"));

                qbc.setMenuItems(qbcItems);
                canteenRepository.save(qbc);

                // --- GATE 1 ---
                Canteen gate1 = new Canteen();
                gate1.setName("Gate 1 Canteen");
                gate1.setDescription("Chinese and Rice.");
                gate1.setLocation("Main Gate");
                gate1.setImageUrl("/images/gate 1.jpg");

                List<MenuItem> gate1Items = new ArrayList<>();
                gate1Items.add(createItem("Fried Rice", new BigDecimal("70.00"), "Gate 1 Canteen", "Wok tossed rice.", "/images/friedrice.jpg", "Lunch"));
                gate1Items.add(createItem("Hakka Noodles", new BigDecimal("65.00"), "Gate 1 Canteen", "Spicy noodles.", "/images/noodles.jpg", "Lunch"));

                gate1.setMenuItems(gate1Items);
                canteenRepository.save(gate1);

                System.out.println("Database seeded successfully!");
            }
        };
    }

    private MenuItem createItem(String name, BigDecimal price, String canteenName, String desc, String img, String cat) {
        return new MenuItem(name, price, canteenName, desc, img, cat);
    }
}