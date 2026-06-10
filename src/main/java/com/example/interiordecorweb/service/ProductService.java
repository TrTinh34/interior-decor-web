package com.example.interiordecorweb.service;

import com.example.interiordecorweb.dto.ProductDto;
import com.example.interiordecorweb.entity.Product;
import com.example.interiordecorweb.repository.CategoryRepository;
import com.example.interiordecorweb.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Transactional
public class ProductService {

    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public Page<Product> getProducts(String keyword, Integer categoryId,
                                     String sortBy, int page) {
        Sort sort = "price_asc".equals(sortBy)
                ? Sort.by("price").ascending()
                : "price_desc".equals(sortBy)
                  ? Sort.by("price").descending()
                  : Sort.by("createdAt").descending();

        Pageable pageable = PageRequest.of(page, 9, sort);

        if (keyword != null && !keyword.isBlank() && categoryId != null) {
            return productRepository
                    .findByIsActiveTrueAndNameContainingIgnoreCaseAndCategoryId(
                            keyword, categoryId, pageable);
        } else if (keyword != null && !keyword.isBlank()) {
            return productRepository
                    .findByIsActiveTrueAndNameContainingIgnoreCase(keyword, pageable);
        } else if (categoryId != null) {
            return productRepository
                    .findByIsActiveTrueAndCategoryId(categoryId, pageable);
        }
        return productRepository.findByIsActiveTrue(pageable);
    }

    public Product findById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
    }

    public void save(ProductDto dto) throws IOException {
        Product product = (dto.getId() != null)
                ? productRepository.findById(dto.getId()).orElse(new Product())
                : new Product();

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setMaterial(dto.getMaterial());
        product.setColor(dto.getColor());
        product.setDimensions(dto.getDimensions());
        product.setCategory(categoryRepository.findById(dto.getCategoryId())
                .orElseThrow());

        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            String filename = UUID.randomUUID() + "_" + dto.getImageFile().getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);
            dto.getImageFile().transferTo(uploadPath.resolve(filename).toFile());
            product.setImageUrl("/uploads/" + filename);
        }

        productRepository.save(product);
    }

    public void softDelete(Integer id) {
        Product p = findById(id);
        p.setIsActive(false);
        productRepository.save(p);
    }
}