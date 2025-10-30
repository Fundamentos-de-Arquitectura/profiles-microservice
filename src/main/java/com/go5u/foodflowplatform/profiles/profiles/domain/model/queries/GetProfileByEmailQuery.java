package com.go5u.foodflowplatform.profiles.profiles.domain.model.queries;


import com.go5u.foodflowplatform.profiles.profiles.domain.model.valueobjects.EmailAddress;

public record GetProfileByEmailQuery(EmailAddress emailAddress) {
}
