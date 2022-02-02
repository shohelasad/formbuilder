package com.formreleaf.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "Users")
public class User extends BaseEntity<Long> implements UserDetails {
    private static final Logger LOGGER = LoggerFactory.getLogger(User.class);
    
    private static final long serialVersionUID = 7526472295622776147L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<UserRole> userRoles = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Profile> profiles = new HashSet<>();

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Organization organization;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private boolean locked;

    @Size(max = 280)
    private String hashedPassword;

    @Size(max = 16)
    private String salt;
    
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Transient
    private List<SimpleGrantedAuthority> simpleGrantedAuthorityList;

    public User() {
    }

    public User(Long id, String email, String firstName, String lastName,
                String password) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    private boolean isLocked() {
        return locked;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (simpleGrantedAuthorityList == null && !CollectionUtils.isEmpty(getUserRoles())) {
            simpleGrantedAuthorityList = new ArrayList<>();

            simpleGrantedAuthorityList
                    .addAll(getUserRoles()
                            .stream()
                            .map(userRole -> new SimpleGrantedAuthority(userRole
                                    .getRole().name())).collect(Collectors.toList()));
        }

        return simpleGrantedAuthorityList;
    }

    @Override
    public String getPassword() {
        LOGGER.info("getPassword() called");

        return null;
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;    // Not implemented
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;    // Not Implemented
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(Set<Profile> profiles) {
        this.profiles = profiles;
    }

    @Override
    public String toString() {
        return "id=" + id + ", email=" + email + ", firstName=" + firstName + ", lastName=" + lastName;
    }

    public String getFullName() {

        return firstName + " " + lastName;
    }

}
