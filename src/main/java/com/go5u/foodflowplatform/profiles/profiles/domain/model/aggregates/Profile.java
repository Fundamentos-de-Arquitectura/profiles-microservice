package com.go5u.foodflowplatform.profiles.profiles.domain.model.aggregates;


import com.go5u.foodflowplatform.profiles.profiles.domain.model.commands.CreateProfileCommand;
import com.go5u.foodflowplatform.profiles.profiles.domain.model.valueobjects.EmailAddress;
import com.go5u.foodflowplatform.profiles.profiles.domain.model.valueobjects.PersonName;
import com.go5u.foodflowplatform.profiles.profiles.domain.model.valueobjects.StreetAddress;
import com.go5u.foodflowplatform.profiles.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;

@Entity
public class Profile extends AuditableAbstractAggregateRoot<Profile> {
    @Embedded
    private PersonName name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "address", column = @Column(name = "email_address"))})
    private EmailAddress emailAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "street_address_street")),
            @AttributeOverride(name = "number", column = @Column(name = "street_address_number")),
            @AttributeOverride(name = "city", column = @Column(name = "street_address_city")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "street_address_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "street_address_country"))})
    private StreetAddress streetAddress;

    @Column(name = "restaurant_name")
    private String restaurantName;

    @Column(name = "restaurant_description", length = 500)
    private String restaurantDescription;

    @Column(name = "restaurant_phone")
    private String restaurantPhone;

    @Column(name = "account_id", unique = true)
    private Long accountId; // Link to IAM account

    public Profile(String firstName, String lastName, String email, String street, String number, String city, String state, String postalCode, String country, String restaurantName, String restaurantDescription, String restaurantPhone, Long accountId) {
        this.name = new PersonName(firstName, lastName);
        this.emailAddress = new EmailAddress(email);
        this.streetAddress = new StreetAddress(street, number, city, state, postalCode, country);
        this.restaurantName = restaurantName;
        this.restaurantDescription = restaurantDescription;
        this.restaurantPhone = restaurantPhone;
        this.accountId = accountId;
    }

    public Profile() {
        // Default constructor for JPA
    }

    public Profile(CreateProfileCommand command) {
        this(
                command.firstName(),
                command.lastName(),
                command.email(),
                command.street(),
                command.number(),
                command.city(),
                command.state(),
                command.postalCode(),
                command.country(),
                command.restaurantName(),
                command.restaurantDescription(),
                command.restaurantPhone(),
                command.accountId()
        );
    }

    public String getFullName() {
        return name.getFullName();
    }

    public String getEmailAddress() {
        return emailAddress.address();
    }

    public String getStreetAddress() {
        return streetAddress.getStreetAddress();
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getRestaurantDescription() {
        return restaurantDescription;
    }

    public String getRestaurantPhone() {
        return restaurantPhone;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void updateName(String firstName, String lastName) {
      this.name = new PersonName(firstName, lastName);
    }

    public void updateEmailAddress(String email) {
        this.emailAddress = new EmailAddress(email);
    }

    public void updateStreetAddress(String street, String number, String city, String state, String postalCode, String country) {
        this.streetAddress = new StreetAddress(street, number, city, state, postalCode, country);
    }

    public void updateRestaurantInfo(String restaurantName, String restaurantDescription, String restaurantPhone) {
        this.restaurantName = restaurantName;
        this.restaurantDescription = restaurantDescription;
        this.restaurantPhone = restaurantPhone;
    }
}