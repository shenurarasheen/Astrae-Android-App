package com.synapse.astraemobileapp.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    private String productId;
    private String title;
    private String description;
    private double price;
    private int stockCount;
    private String categoryId;
    private String sellerId;
    private List<String> productImages;
    private String certificateImage;
    private boolean status;
    private float rating;
    private int soldCount;
    private int viewedCount;
    private List<Attribute> attributes;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Attribute {
        private String name;
        private List<String> values;
    }
}
