package com.go5u.foodflowplatform.profiles.interfaces.rest.dto;

import java.time.LocalDateTime;

/**
 * DTO para respuesta combinada de usuario con suscripción
 */
public record UserWithSubscriptionResponse(
        Long userId,
        String username,
        String role,
        Long subscriptionId,
        String planName,
        String subscriptionStatus,
        LocalDateTime subscriptionStartDate,
        LocalDateTime subscriptionEndDate
) {}

