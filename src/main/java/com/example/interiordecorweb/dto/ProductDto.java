package com.example.interiordecorweb.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
public class ProductDto {
    private Integer id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    private String description;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;

    @NotNull @Min(0)
    private Integer stock;

    private String material;
    private String color;
    private String dimensions;
    private String imageUrl;

    @NotNull(message = "Vui lòng chọn danh mục")
    private Integer categoryId;

    private MultipartFile imageFile;
}
