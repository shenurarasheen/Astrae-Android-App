package com.synapse.astraemobileapp.model;

import com.google.firebase.firestore.DocumentId;

import java.util.List;

import lk.payhere.androidsdk.model.StatusResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @DocumentId
    private String orderId;
    private String userId;
    private double totalAmount;
    private String status;
    private long orderDate;
    private long deliveryDate;
    private List<OrderItem> orderItems;
    private String addressId;
    private StatusResponse statusResponse;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItem {
        private String productId;
        private double unitPrice;
        private int quantity;
    }
}
