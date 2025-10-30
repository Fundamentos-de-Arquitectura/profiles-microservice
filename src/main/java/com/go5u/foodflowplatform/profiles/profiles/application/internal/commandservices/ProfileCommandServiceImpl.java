package com.go5u.foodflowplatform.profiles.profiles.application.internal.commandservices;


import com.go5u.foodflowplatform.profiles.profiles.domain.model.aggregates.Profile;
import com.go5u.foodflowplatform.profiles.profiles.domain.model.commands.CreateProfileCommand;
import com.go5u.foodflowplatform.profiles.profiles.domain.model.valueobjects.EmailAddress;
import com.go5u.foodflowplatform.profiles.profiles.domain.services.ProfileCommandService;
import com.go5u.foodflowplatform.profiles.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Profile Command Service Implementation
 */
@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {
    private final ProfileRepository profileRepository;

    /**
     * Constructor
     *
     * @param profileRepository The {@link ProfileRepository} instance
     */
    public ProfileCommandServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    // inherited javadoc
    @Override
    public Optional<Profile> handle(CreateProfileCommand command) {
        var emailAddress = new EmailAddress(command.email());
        if (profileRepository.existsByEmailAddress(emailAddress)) {
            throw new IllegalArgumentException("Profile with email address already exists");
        }
        var profile = new Profile(command);
        profileRepository.save(profile);
        return Optional.of(profile);
    }
}
