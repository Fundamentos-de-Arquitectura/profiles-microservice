package com.go5u.foodflowplatform.profiles.profiles.interfaces.acl;

/**
 * ProfilesContextFacade
 */
public interface ProfilesContextFacade {
    /**
     * Create a new profile
     * @param firstName The first name
     * @param lastName The last name
     * @param email The email address
     * @param street The street address
     * @param number The street number
     * @param city The city
     * @param state The  state(optional)
     * @param postalCode The postal code
     * @param country The country
     * @param restaurantName The restaurant name
     * @param restaurantDescription The restaurant description
     * @param restaurantPhone The restaurant phone
     * @param accountId The account ID from IAM service
     * @return The profile ID
     */
    Long createProfile(String firstName, String lastName, String email, String street, String number, String city, String state, String postalCode, String country, String restaurantName, String restaurantDescription, String restaurantPhone, Long accountId);

    /**
     * Fetch a profile ID by email
     * @param email The email address
     * @return The profile ID
     */
    Long fetchProfileIdByEmail(String email);
}
