package com.fortune.cookie.model;

import com.fortune.cookie.business.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;

    @NonNull
    private String email;

    @NonNull
    private String password;

    @NonNull
    private Role role;
}
