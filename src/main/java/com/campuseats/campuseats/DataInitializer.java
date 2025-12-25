package com.campuseats.campuseats;

import com.campuseats.campuseats.model.MenuItem;
import com.campuseats.campuseats.repository.MenuItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(MenuItemRepository repository) {
        return args -> {
            if (repository.count() == 0) { // Only add if table is empty
                repository.save(new MenuItem("Samosa", 15.0, "Ravi Canteen"));
                repository.save(new MenuItem("Masala Dosa", 50.0, "Ravi Canteen"));
                repository.save(new MenuItem("Cold Coffee", 40.0, "QBC"));
                repository.save(new MenuItem("Veg Burger", 45.0, "Delicious Canteen"));
                repository.save(new MenuItem("Paneer Roll", 60.0, "CS/IT Canteen"));
                repository.save(new MenuItem("Fried Rice", 70.0, "Gate 1 Canteen"));
                repository.save(new MenuItem("Maggi", 25.0, "Gate 2 Canteen"));
                System.out.println("Sample Menu Items Loaded!");
            }
        };
    }
}