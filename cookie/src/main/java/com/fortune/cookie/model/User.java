package com.fortune.cookie.model;

import com.fortune.cookie.business.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Schema(description = "Model of User")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Schema(description = "The unique id of the user",
            example = "1")
    private Long id;

    @Schema(description = "The email of the user",
            example = "example@email.com")
    @NonNull
    private String email;

    @Schema(description = "The password of the user",
            example = "password")
    @NonNull
    private String password;

    @Schema(description = "The role of the user",
            example = "USER")
    private Role role;

    @Schema(hidden = true)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Schema(hidden = true)
    @Override
    public String getUsername() {
        return email;
    }

    @Schema(hidden = true)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Schema(hidden = true)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Schema(hidden = true)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Schema(hidden = true)
    @Override
    public boolean isEnabled() {
        return true;
    }
}
