package com.formreleaf.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;

@SqlResultSetMapping(name = "profile", classes = {@ConstructorResult(targetClass = Profile.class, columns = {
        @ColumnResult(name = "id", type = Long.class),
        @ColumnResult(name = "name", type = String.class),
        @ColumnResult(name = "profileName", type = String.class),
        @ColumnResult(name = "profileDataType", type = String.class),
        @ColumnResult(name = "profileData", type = String.class)})})
@TypeDefs({@TypeDef(name = "StringJsonbObject", typeClass = StringJsonbUserType.class)})
@Entity
@JsonIgnoreProperties
public class Profile extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String profileDataType;

    @Type(type = "StringJsonbObject")
    @Column(nullable = false)
    private String profileData;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean enabled;

    private String profileName;

    public Profile() {
    }

    public Profile(Long id, String name, String type, String profileData) {
        this.id = id;
        this.name = name;
        this.profileDataType = type;
        this.profileData = profileData;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileData() {
        return profileData;
    }

    public void setProfileData(String profileData) {
        this.profileData = profileData;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getProfileDataType() {
        return profileDataType;
    }

    public void setProfileDataType(String profileDataType) {
        this.profileDataType = profileDataType;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", profileDataType='" + profileDataType + '\'' +
                ", profileData='" + profileData + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
