package org.example.Entities.OneToOne_Bidirectional;

import jakarta.persistence.*;

@Entity
@Table(name = "addresses")
public class AddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String zipcode;
    private String state;

    // UNIDIRECTIONAL
    // if the relationship is unidirectional, then here shouldn't have any logic of relationship
    // -----------------------------------------------------------------------------

    // BIDIRECTIONAL relationship
    @OneToOne(
            targetEntity = UserEntity.class,
            fetch = FetchType.EAGER,
            mappedBy = "address",
            optional = false, // this doesn't have any effect -
            // (because the targetEntity don't have any field in this class), if the targetEntity
            // has a field in this class, setting 'false' then we can't persist
            orphanRemoval = false
//            cascade = CascadeType.ALL
    )
    private UserEntity userEntity;

    public AddressEntity() {
    }

    public AddressEntity(Long id, String name, String zipcode, String state, UserEntity userEntity) {
        this.id = id;
        this.name = name;
        this.zipcode = zipcode;
        this.state = state;
        this.userEntity = userEntity;

        if (userEntity != null) userEntity.setAddress(this);
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

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }
}