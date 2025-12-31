package com.foodie.application.service;

import com.foodie.application.domain.Allergen;
import com.foodie.application.domain.Product;
import com.foodie.application.dto.ProductDto;
import com.foodie.application.repository.AllergenRepository;
import com.foodie.application.repository.IngredientRepository;
import com.foodie.application.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final AllergenRepository allergenRepository;
    private final IngredientRepository ingredientRepository;
    private final IngredientService ingredientService;

    public ProductService(ProductRepository productRepository, AllergenRepository allergenRepository,
                         IngredientRepository ingredientRepository, IngredientService ingredientService) {
        this.allergenRepository = allergenRepository;
        this.productRepository = productRepository;
        this.ingredientRepository = ingredientRepository;
        this.ingredientService = ingredientService;
    }

    @Transactional
    public void removeProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            throw new EntityNotFoundException("Product not found with id: " + productId);
        }
        productRepository.deleteById(productId);
    }

    @Transactional
    public List<Product> getAllProducts(Integer menuId) {
        List<Product> products = productRepository.findAll();
        // Eagerly load allergens within the transaction to avoid LazyInitializationException
        for (Product product : products) {
            if (product.getAllergens() != null) {
                product.getAllergens().size(); // Force initialization
            }
        }
        return products;
    }

    /**
     * Retrieves all products as DTOs with all relationships eagerly initialized.
     * This method ensures that lazy relationships are loaded within the transaction context.
     *
     * @return a list of ProductDto objects with all data properly loaded
     */
    @Transactional
    public List<ProductDto> getAllProductsAsDto() {
        List<Product> products = productRepository.findAll();

        // Eagerly initialize all lazy collections within the transaction
        for (Product product : products) {
            if (product.getAllergens() != null) {
                product.getAllergens().size(); // Force initialization
            }
            if (product.getIngredients() != null) {
                product.getIngredients().size(); // Force initialization
            }
        }

        // Convert to DTOs within the transaction
        return products.stream()
                .map(ProductDto::fromProduct)
                .collect(Collectors.toList());
    }

    /**
     * Gets a product by ID with allergens eagerly loaded.
     * This method ensures that allergens are loaded before the session closes,
     * preventing LazyInitializationException.
     *
     * @param productId the ID of the product to retrieve
     * @return the Product with allergens loaded
     */
    @Transactional
    public Product getProductWithAllergens(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        // Force loading of allergens within the transaction
        if (product.getAllergens() != null) {
            product.getAllergens().size();
        }
        return product;
    }

    @Transactional
    public void updateProductPrice(Integer productId, Double newPrice) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        product.setPrice(newPrice);
    }

    @Transactional
    public void updateProductDescription(Integer productId, String newDescription) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        product.setDescription(newDescription);
    }

    @Transactional
    public void updateProductName(Integer productId, String newName) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        product.setName(newName);
    }

    @Transactional
    public void addProductIngredients(Integer productId, Set<String> newIngredients) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        var ingredients = ingredientRepository.findByNameIn(newIngredients);

        product.getIngredients().addAll(ingredients);
    }

    @Transactional
    public void removeProductIngredients(Integer productId, Set<String> ingredientsToRemove) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        var ingredients = ingredientRepository.findByNameIn(ingredientsToRemove);

        product.getIngredients().removeAll(ingredients);
    }

    @Transactional
    public void addProductAllergens(Integer productId, java.util.Set<String> newAllergens) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        var allergens = allergenRepository.findByNameIn(newAllergens);

        product.getAllergens().addAll(allergens);
    }

    @Transactional
    public void removeProductAllergens(Integer productId, java.util.Set<String> allergensToRemove) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        var allergens = allergenRepository.findByNameIn(allergensToRemove);

        product.getAllergens().removeAll(allergens);
    }

    /**
     * Creates a new product and saves it to the database.
     *
     * @param name the name of the product
     * @param description the description of the product
     * @param price the price of the product
     * @param imageUrl the image URL of the product
     * @return the created product with generated ID
     */
    @Transactional
    public Product createProduct(String name, String description, Double price, String imageUrl) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setImageUrl(imageUrl);
        return productRepository.save(product);
    }

    /**
     * Creates a new product with allergens (by allergen names) and saves it to the database.
     *
     * @param name the name of the product
     * @param description the description of the product
     * @param price the price of the product
     * @param imageUrl the image URL of the product
     * @param allergenNames the set of allergen names to associate with the product
     * @return the created product with generated ID
     */
    @Transactional
    public Product createProductWithAllergenNames(String name, String description, Double price, String imageUrl,
                                                   Set<String> allergenNames) {
        Set<Allergen> allergens = allergenRepository.findByNameIn(allergenNames);
        return createProductWithAllergens(name, description, price, imageUrl, allergens);
    }

    /**
     * Creates a new product with allergens and ingredients and saves it to the database.
     *
     * @param name the name of the product
     * @param description the description of the product
     * @param price the price of the product
     * @param imageUrl the image URL of the product
     * @param allergenNames the set of allergen names to associate with the product
     * @param ingredientNames the set of ingredient names to associate with the product
     * @return the created product with generated ID
     */
    @Transactional
    public Product createProductWithAllergenNamesAndIngredients(String name, String description, Double price,
                                                               String imageUrl, Set<String> allergenNames,
                                                               Set<String> ingredientNames) {
        Set<Allergen> allergens = allergenRepository.findByNameIn(allergenNames);
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setImageUrl(imageUrl);
        product.setAllergens(allergens);

        // Create/find ingredients and add them
        if (ingredientNames != null && !ingredientNames.isEmpty()) {
            Set<com.foodie.application.domain.Ingredient> ingredients = ingredientService.findOrCreateByNames(ingredientNames);
            product.setIngredients(ingredients);
        }

        return productRepository.save(product);
    }

    /**
     * Creates a new product with allergens and saves it to the database.
     *
     * @param name the name of the product
     * @param description the description of the product
     * @param price the price of the product
     * @param imageUrl the image URL of the product
     * @param allergens the set of allergens associated with the product
     * @return the created product with generated ID
     */
    @Transactional
    public Product createProductWithAllergens(String name, String description, Double price, String imageUrl,
                                             java.util.Set<Allergen> allergens) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setImageUrl(imageUrl);
        product.setAllergens(allergens);
        return productRepository.save(product);
    }

    /**
     * Updates an existing product with all its properties.
     *
     * @param productId the ID of the product to update
     * @param name the new name
     * @param description the new description
     * @param price the new price
     * @param imageUrl the new image URL
     */
    @Transactional
    public void updateProduct(Integer productId, String name, String description, Double price, String imageUrl) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setImageUrl(imageUrl);
        productRepository.save(product);
    }

    /**
     * Updates a product with all its properties including allergens (by allergen names).
     *
     * @param productId the ID of the product to update
     * @param name the new name
     * @param description the new description
     * @param price the new price
     * @param imageUrl the new image URL
     * @param allergenNames the set of allergen names to associate with the product
     */
    @Transactional
    public void updateProductWithAllergenNames(Integer productId, String name, String description, Double price,
                                               String imageUrl, Set<String> allergenNames) {
        Set<Allergen> allergens = allergenRepository.findByNameIn(allergenNames);
        updateProductWithAllergens(productId, name, description, price, imageUrl, allergens);
    }

    /**
     * Updates a product with all its properties including allergens and ingredients (by names).
     *
     * @param productId the ID of the product to update
     * @param name the new name
     * @param description the new description
     * @param price the new price
     * @param imageUrl the new image URL
     * @param allergenNames the set of allergen names to associate with the product
     * @param ingredientNames the set of ingredient names to associate with the product
     */
    @Transactional
    public void updateProductWithAllergenNamesAndIngredients(Integer productId, String name, String description,
                                                            Double price, String imageUrl, Set<String> allergenNames,
                                                            Set<String> ingredientNames) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setImageUrl(imageUrl);

        Set<Allergen> allergens = allergenRepository.findByNameIn(allergenNames);
        product.setAllergens(allergens);

        // Clear existing ingredients and add new ones
        if (product.getIngredients() != null) {
            product.getIngredients().clear();
        }

        if (ingredientNames != null && !ingredientNames.isEmpty()) {
            Set<com.foodie.application.domain.Ingredient> ingredients = ingredientService.findOrCreateByNames(ingredientNames);
            product.setIngredients(ingredients);
        } else {
            product.setIngredients(new java.util.HashSet<>());
        }

        productRepository.save(product);
    }

    /**
     * Updates a product with all its properties including allergens.
     *
     * @param productId the ID of the product to update
     * @param name the new name
     * @param description the new description
     * @param price the new price
     * @param imageUrl the new image URL
     * @param allergens the set of allergens to associate with the product
     */
    @Transactional
    public void updateProductWithAllergens(Integer productId, String name, String description, Double price,
                                          String imageUrl, java.util.Set<Allergen> allergens) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setImageUrl(imageUrl);

        // Clear existing allergens and add new ones
        if (product.getAllergens() != null) {
            product.getAllergens().clear();
        }
        product.setAllergens(allergens);

        productRepository.save(product);
    }

    public List<Product> getProducts(int i) {
        return null;
    }
}
