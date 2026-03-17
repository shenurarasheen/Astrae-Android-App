package com.synapse.astraemobileapp.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem {
    @DocumentId
    private String documentId;
    private String productId;
    private int quantity;

    public CartItem(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
