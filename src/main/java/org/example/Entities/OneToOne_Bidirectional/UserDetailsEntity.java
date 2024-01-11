package org.example.Entities.OneToOne_Bidirectional;

import jakarta.persistence.*;

@Entity
@Table(name = "user_details")
public class UserDetailsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String lastname;
    private String email;

    @OneToOne(targetEntity = UserEntity.class,
            fetch = FetchType.EAGER,
            mappedBy = "userDetails",
            optional = false,
            orphanRemoval = true,
            cascade = CascadeType.ALL)
    private UserEntity userEntity;

    public UserDetailsEntity() {
    }

    public UserDetailsEntity(Long id, String name, String lastname, String email, UserEntity userEntity) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.userEntity = userEntity;
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

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    @Override
    public String toString() {
        return "UserDetailsEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
//                ", userEntity=" + userEntity +
                '}';
    }
}