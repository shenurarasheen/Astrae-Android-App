package com.synapse.astraemobileapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String uId;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePicUrl;
    private String permanentAddressId;
    private Address[] addresses;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Address {
        private String addressId;
        private String firstName;
        private String lastName;
        private String email;
        private String mobile;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private Integer postalCode;
    }

}
