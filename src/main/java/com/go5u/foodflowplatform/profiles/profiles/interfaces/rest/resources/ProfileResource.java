package com.go5u.foodflowplatform.profiles.profiles.interfaces.rest.resources;

/**
 * Resource for a profile.
 */
public record ProfileResource(
        Long id,
        String fullName,
        String email,
        String streetAddress,
        String restaurantName,
        String restaurantDescription,
        String restaurantPhone,
        Long accountId) {
}
