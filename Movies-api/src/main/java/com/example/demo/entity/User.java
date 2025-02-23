package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails , Principal {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(name = "first_name", length = 50, nullable = false)
    @NotNull(message = "First name cannot be null")
    private String firstName ;

    @Column(name = "last_name", length = 50, nullable = false)
    @NotNull(message = "Last name cannot be null")
    private String lastName ;


    @Column(name = "password", length = 255, nullable = false)
    @NotNull(message = "Password cannot be null")
    private String password ;


    @Column(unique = true)
    @Email(message = "Email should be valid")
    @NotNull(message = "Email cannot be null")
    private String email ;

    private boolean enabled ;
    private boolean accountLocked ;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles ;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate ;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime lastModifiedDate ;

    @Override
    public String getName() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
