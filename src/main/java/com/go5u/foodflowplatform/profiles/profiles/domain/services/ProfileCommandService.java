package com.go5u.foodflowplatform.profiles.profiles.domain.services;

import com.go5u.foodflowplatform.profiles.profiles.domain.model.aggregates.Profile;
import com.go5u.foodflowplatform.profiles.profiles.domain.model.commands.CreateProfileCommand;

import java.util.Optional;

/**
 * Profile Command Service
 */
public interface ProfileCommandService {

    Optional<Profile> handle(CreateProfileCommand command);
}
