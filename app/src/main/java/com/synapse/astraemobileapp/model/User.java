package com.synapse.astraemobileapp.model;

import java.util.List;
import java.util.Map;

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
    private Map<String, Double> location;
}
