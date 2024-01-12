package org.example.Entities.OneToOne_Unidirectional;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    //TODO: GenerationType.IDENTITY vs others/.....
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    private String password;

    @OneToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true,
            optional = true,
            targetEntity = AddressEntity.class)
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    public UserEntity() {
    }

    public UserEntity(Long id, String username, String password, AddressEntity addressEntity) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.address = addressEntity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity addressEntity) {
        this.address = addressEntity;
//        addressEntity.setUserEntity(this);
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", addressEntity=" + address +
                '}';
    }
}
