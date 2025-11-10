package com.go5u.foodflowplatform.profiles.profiles.interfaces.rest;

import com.go5u.foodflowplatform.profiles.infrastructure.config.IamClient;
import com.go5u.foodflowplatform.profiles.infrastructure.config.SubscriptionClient;
import com.go5u.foodflowplatform.profiles.interfaces.rest.dto.UserWithSubscriptionResponse;
import com.go5u.foodflowplatform.profiles.profiles.domain.model.queries.GetAllProfilesQuery;
import com.go5u.foodflowplatform.profiles.profiles.domain.model.queries.GetProfileByIdQuery;
import com.go5u.foodflowplatform.profiles.profiles.domain.services.ProfileCommandService;
import com.go5u.foodflowplatform.profiles.profiles.domain.services.ProfileQueryService;
import com.go5u.foodflowplatform.profiles.profiles.interfaces.rest.resources.CreateProfileResource;
import com.go5u.foodflowplatform.profiles.profiles.interfaces.rest.resources.ProfileResource;
import com.go5u.foodflowplatform.profiles.profiles.interfaces.rest.transform.CreateProfileCommandFromResourceAssembler;
import com.go5u.foodflowplatform.profiles.profiles.interfaces.rest.transform.ProfileResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ProfilesController
 */
@RestController
@RequestMapping(value = "/api/v1/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Profiles", description = "Available Profile Endpoints")
public class ProfilesController {
    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;
    private final IamClient iamClient;
    private final SubscriptionClient subscriptionClient;

    /**
     * Constructor
     * @param profileCommandService The {@link ProfileCommandService} instance
     * @param profileQueryService The {@link ProfileQueryService} instance
     * @param iamClient The {@link IamClient} instance for IAM service communication
     * @param subscriptionClient The {@link SubscriptionClient} instance for Subscription service communication
     */
    public ProfilesController(ProfileCommandService profileCommandService, 
                             ProfileQueryService profileQueryService,
                             IamClient iamClient,
                             SubscriptionClient subscriptionClient) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
        this.iamClient = iamClient;
        this.subscriptionClient = subscriptionClient;
    }

    /**
     * Create a new profile
     * @param resource The {@link CreateProfileResource} instance
     * @return A {@link ProfileResource} resource for the created profile, or a bad request response if the profile could not be created.
     */
    @PostMapping
    @Operation(summary = "Create a new profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Profile created"),
            @ApiResponse(responseCode = "400", description = "Bad request")})
    public ResponseEntity<ProfileResource> createProfile(@RequestBody CreateProfileResource resource) {
        var createProfileCommand = CreateProfileCommandFromResourceAssembler.toCommandFromResource(resource);
        var profile = profileCommandService.handle(createProfileCommand);
        if (profile.isEmpty()) return ResponseEntity.badRequest().build();
        var createdProfile = profile.get();
        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(createdProfile);
        return new ResponseEntity<>(profileResource, HttpStatus.CREATED);
    }

    /**
     * Health check endpoint to verify controller is working
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok", "service", "profiles-service"));
    }

    /**
     * Get all profiles
     * @return A list of {@link ProfileResource} resources for all profiles (empty list if none found).
     */
    @GetMapping
    @Operation(summary = "Get all profiles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profiles found (may be empty)")})
    public ResponseEntity<List<ProfileResource>> getAllProfiles() {
        var profiles = profileQueryService.handle(new GetAllProfilesQuery());
        var profileResources = profiles.stream()
                .map(ProfileResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(profileResources);
    }

    /**
     * Get user with subscription data combined from IAM and Subscription services
     * IMPORTANT: This endpoint must be defined BEFORE /{profileId} to avoid route conflicts
     * @param userId The user ID
     * @return A {@link UserWithSubscriptionResponse} with combined user and subscription data
     */
    @GetMapping("/users/{userId}/with-subscription")
    @Operation(summary = "Get user with subscription data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User with subscription found"),
            @ApiResponse(responseCode = "404", description = "User or subscription not found")})
    public ResponseEntity<UserWithSubscriptionResponse> getUserWithSubscription(@PathVariable Long userId) {
        // Obtener datos del usuario desde IAM
        var userOpt = iamClient.getUserById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var user = userOpt.get();
        
        // Si el usuario no tiene suscripción asociada, devolver solo datos del usuario
        if (user.subscriptionId() == null) {
            var response = new UserWithSubscriptionResponse(
                    user.id(),
                    user.username(),
                    user.role(),
                    null,
                    null,
                    null,
                    null,
                    null
            );
            return ResponseEntity.ok(response);
        }

        // Obtener datos de la suscripción desde Subscription
        var subscriptionOpt = subscriptionClient.getSubscriptionById(user.subscriptionId());
        
        if (subscriptionOpt.isEmpty()) {
            // Usuario existe pero suscripción no encontrada
            var response = new UserWithSubscriptionResponse(
                    user.id(),
                    user.username(),
                    user.role(),
                    user.subscriptionId(),
                    null,
                    null,
                    null,
                    null
            );
            return ResponseEntity.ok(response);
        }

        var subscription = subscriptionOpt.get();
        
        // Combinar datos de usuario y suscripción
        var response = new UserWithSubscriptionResponse(
                user.id(),
                user.username(),
                user.role(),
                subscription.id(),
                subscription.planName(),
                subscription.status(),
                subscription.startDate(),
                subscription.endDate()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Get a profile by ID
     * IMPORTANT: This endpoint must be defined AFTER /users/{userId}/with-subscription to avoid route conflicts
     * @param profileId The profile ID
     * @return A {@link ProfileResource} resource for the profile, or a not found response if the profile could not be found.
     */
    @GetMapping("/{profileId}")
    @Operation(summary = "Get a profile by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile found"),
            @ApiResponse(responseCode = "404", description = "Profile not found")})
    public ResponseEntity<ProfileResource> getProfileById(@PathVariable Long profileId) {
        var getProfileByIdQuery = new GetProfileByIdQuery(profileId);
        var profile = profileQueryService.handle(getProfileByIdQuery);
        if (profile.isEmpty()) return ResponseEntity.notFound().build();
        var profileEntity = profile.get();
        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profileEntity);
        return ResponseEntity.ok(profileResource);
    }

}
