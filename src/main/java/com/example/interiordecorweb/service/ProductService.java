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
import java.nio.file.StandardCopyOption; // Thêm import phục vụ ghi đè file an toàn
import java.util.UUID;

@Service
@Transactional
public class ProductService {

    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public java.util.List<Product> getAllActiveProductsList() {
        return productRepository.findAll();
    }

    public Page<Product> getProducts(String keyword, Integer categoryId,
                                     String sortBy, int page) {
        Sort sort = "priceAsc".equals(sortBy)
                ? Sort.by("price").ascending()
                : "priceDesc".equals(sortBy)
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

        // Đảm bảo các sản phẩm mới thêm mặc định sẽ ở trạng thái Active = true
        if (dto.getId() == null) {
            product.setIsActive(true);
        }

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setMaterial(dto.getMaterial());
        product.setColor(dto.getColor());
        product.setDimensions(dto.getDimensions());
        product.setCategory(categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại")));

        // XỬ LÝ LƯU FILE HÌNH ẢNH AN TOÀN TUYỆT ĐỐI
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            String filename = UUID.randomUUID() + "_" + dto.getImageFile().getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);

            // Tự động kiểm tra và khởi tạo thư mục lưu trữ nếu chưa có sẵn
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path targetPath = uploadPath.resolve(filename);

            // Sử dụng luồng InputStream kết hợp Files.copy để ép ghi dữ liệu dứt khoát
            Files.copy(dto.getImageFile().getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Gán đường dẫn lưu trữ mới
            product.setImageUrl("/uploads/" + filename);
        } else if (dto.getId() != null) {
            // Trường hợp chỉnh sửa (Edit) và không tải ảnh mới: giữ nguyên ảnh cũ đã có trong DB
            Product oldProduct = productRepository.findById(dto.getId()).orElse(null);
            if (oldProduct != null) {
                product.setImageUrl(oldProduct.getImageUrl());
            }
        }

        productRepository.save(product);
    }

    public void softDelete(Integer id) {
        Product p = findById(id);
        p.setIsActive(false);
        productRepository.save(p);
    }
}