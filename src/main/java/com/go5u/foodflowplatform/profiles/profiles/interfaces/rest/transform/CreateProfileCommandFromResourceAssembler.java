package com.go5u.foodflowplatform.profiles.profiles.interfaces.rest.transform;


import com.go5u.foodflowplatform.profiles.profiles.domain.model.commands.CreateProfileCommand;
import com.go5u.foodflowplatform.profiles.profiles.interfaces.rest.resources.CreateProfileResource;

/**
 * Assembler to convert a CreateProfileResource to a CreateProfileCommand.
 */
public class CreateProfileCommandFromResourceAssembler {
    /**
     * Converts a CreateProfileResource to a CreateProfileCommand.
     * @param resource The {@link CreateProfileResource} resource to convert.
     * @return The {@link CreateProfileCommand} command.
     */
    public static CreateProfileCommand toCommandFromResource(CreateProfileResource resource) {
        return new CreateProfileCommand(
                resource.firstName(),
                resource.lastName(),
                resource.email(),
                resource.street(),
                resource.number(),
                resource.city(),
                resource.state(),
                resource.postalCode(),
                resource.country());
    }
}
