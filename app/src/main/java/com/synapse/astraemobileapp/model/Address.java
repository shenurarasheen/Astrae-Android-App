package com.synapse.astraemobileapp.model;

import com.google.firebase.firestore.DocumentId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @DocumentId
    private String addressId;
    private String firstName;
    private String lastName;
    private String mobile;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String postalCode;
}
