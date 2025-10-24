package com.foodie.application.config;

import com.foodie.application.domain.Menu;
import com.foodie.application.domain.MenuItem;
import com.foodie.application.domain.Product;
import com.foodie.application.repository.MenuItemRepository;
import com.foodie.application.repository.MenuRepository;
import com.foodie.application.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(ProductRepository productRepository, MenuRepository menuRepository, MenuItemRepository menuItemRepository) {
        return args -> {
            if (productRepository.count() == 0) { // evita duplicados al reiniciar
                productRepository.save(Product.builder()
                        .name("Hamburguesa Clásica")
                        .price(7.99)
                        .description("Jugosa carne con queso cheddar y pan brioche")
                        .allergens(Set.of("Gluten", "Lácteos"))
                        .imageUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/b/b0/Hamburger_%2812164386105%29.jpg/1200px-Hamburger_%2812164386105%29.jpg")
                        .build());

                productRepository.save(Product.builder()
                        .name("Pizza Margarita")
                        .price(8.50)
                        .description("Base de tomate, mozzarella fresca y albahaca")
                        .allergens(Set.of("Gluten"))
                        .imageUrl("https://shorturl.at/XfeVn")
                        .build());

                productRepository.save(Product.builder()
                        .name("Wrap de Pollo")
                        .price(6.75)
                        .description("Pollo crujiente, ensalada y mayonesa")
                        .allergens(Set.of("Huevo", "Gluten"))
                        .imageUrl("https://shorturl.at/nQtP9")
                        .build());
                productRepository.save(Product.builder()
                        .name("Wrap de Pollo")
                        .price(6.75)
                        .description("Pollo crujiente, ensalada y mayonesa")
                        .allergens(Set.of("Huevo", "Gluten"))
                        .imageUrl("https://shorturl.at/nQtP9")
                        .build());

                productRepository.save(Product.builder()
                        .name("Hamburguesa Clásica")
                        .price(8.50)
                        .description("Carne de res, lechuga, tomate y queso cheddar")
                        .allergens(Set.of("Gluten", "Lácteos"))
                        .imageUrl("https://shorturl.at/bxH34")
                        .build());

                productRepository.save(Product.builder()
                        .name("Hamburguesa Vegetariana")
                        .price(7.50)
                        .description("Hamburguesa de garbanzos, lechuga y tomate")
                        .allergens(Set.of("Gluten"))
                        .imageUrl("https://shorturl.at/fxZ03")
                        .build());

                productRepository.save(Product.builder()
                        .name("Pizza Margarita")
                        .price(9.00)
                        .description("Tomate, mozzarella y albahaca fresca")
                        .allergens(Set.of("Gluten", "Lácteos"))
                        .imageUrl("https://shorturl.at/kzGT6")
                        .build());

                productRepository.save(Product.builder()
                        .name("Pizza Pepperoni")
                        .price(10.50)
                        .description("Pepperoni, queso mozzarella y salsa de tomate")
                        .allergens(Set.of("Gluten", "Lácteos"))
                        .imageUrl("https://shorturl.at/btyU5")
                        .build());

                productRepository.save(Product.builder()
                        .name("Ensalada César")
                        .price(6.00)
                        .description("Lechuga, pollo, queso parmesano y aderezo César")
                        .allergens(Set.of("Lácteos", "Huevo", "Pescado"))
                        .imageUrl("https://shorturl.at/fvFZ6")
                        .build());

                productRepository.save(Product.builder()
                        .name("Sushi Variado")
                        .price(12.00)
                        .description("Selección de sushi y nigiri con salsa de soja")
                        .allergens(Set.of("Pescado", "Gluten"))
                        .imageUrl("https://shorturl.at/hkMR2")
                        .build());

                productRepository.save(Product.builder()
                        .name("Tacos de Carne Asada")
                        .price(7.25)
                        .description("Tortillas de maíz, carne asada, cebolla y cilantro")
                        .allergens(Set.of())
                        .imageUrl("https://shorturl.at/iqN46")
                        .build());

                productRepository.save(Product.builder()
                        .name("Tacos de Pollo")
                        .price(6.50)
                        .description("Tortillas de maíz, pollo sazonado y pico de gallo")
                        .allergens(Set.of())
                        .imageUrl("https://shorturl.at/muvY1")
                        .build());

                productRepository.save(Product.builder()
                        .name("Pasta Alfredo")
                        .price(8.75)
                        .description("Pasta con salsa cremosa de queso parmesano")
                        .allergens(Set.of("Gluten", "Lácteos"))
                        .imageUrl("https://shorturl.at/ijRZ0")
                        .build());

                productRepository.save(Product.builder()
                        .name("Pasta Boloñesa")
                        .price(9.50)
                        .description("Pasta con salsa de carne y tomate")
                        .allergens(Set.of("Gluten"))
                        .imageUrl("https://shorturl.at/dkMZ3")
                        .build());

                productRepository.save(Product.builder()
                        .name("Smoothie de Fresa")
                        .price(4.50)
                        .description("Fresas frescas, yogur y miel")
                        .allergens(Set.of("Lácteos"))
                        .imageUrl("https://shorturl.at/ajLQ1")
                        .build());

                productRepository.save(Product.builder()
                        .name("Smoothie Verde")
                        .price(4.75)
                        .description("Espinaca, kiwi, manzana y agua de coco")
                        .allergens(Set.of())
                        .imageUrl("https://shorturl.at/bmGJ3")
                        .build());

                productRepository.save(Product.builder()
                        .name("Helado de Vainilla")
                        .price(3.50)
                        .description("Helado cremoso de vainilla natural")
                        .allergens(Set.of("Lácteos"))
                        .imageUrl("https://shorturl.at/fjLR9")
                        .build());

                productRepository.save(Product.builder()
                        .name("Brownie de Chocolate")
                        .price(4.25)
                        .description("Brownie de chocolate con nueces")
                        .allergens(Set.of("Gluten", "Lácteos", "Huevo", "Frutos secos"))
                        .imageUrl("https://shorturl.at/hjKY8")
                        .build());


                System.out.println("✅ Productos de prueba añadidos correctamente.");
        };
            if (menuRepository.count() == 0) {
                Menu menu = new Menu();
                menu.setName("Menu Estudiante");
                menuRepository.save(menu);

                productRepository.findAll().
                        forEach(product ->
                                menuItemRepository.save(
                                        MenuItem.builder()
                                                .product(product)
                                                .discountPercentage(0)
                                                .featured(false)
                                                .menu(menu)
                                                .build()
                                ));

                menuRepository.save(menu);

                Menu menu2 = new Menu();
                menu2.setName("Menu Adulto");
                menuRepository.save(menu2);

                productRepository.findAll().
                        forEach(product ->
                                menuItemRepository.save(
                                        MenuItem.builder()
                                                .product(product)
                                                .discountPercentage(0)
                                                .featured(false)
                                                .menu(menu2)
                                                .build()
                                ));

                menuRepository.save(menu2);
            }
        };

    }
}
