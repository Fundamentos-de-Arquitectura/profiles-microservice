package com.go5u.foodflowplatform.profiles.infrastructure.config;

import com.go5u.foodflowplatform.profiles.interfaces.rest.dto.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class IamClient {

    private static final Logger logger = LoggerFactory.getLogger(IamClient.class);
    private static final String IAM_SERVICE = "http://iam-microservice";

    private final RestClient restClient;

    public IamClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl(IAM_SERVICE)
                .build();
    }

    /**
     * Obtiene un usuario por ID desde el microservicio IAM
     * @param userId ID del usuario
     * @return Optional con el usuario o vacío si no existe
     */
    public Optional<UserResponse> getUserById(Long userId) {
        try {
            logger.info("Fetching user from IAM service: {}", userId);

            UserResponse response = restClient.get()
                    .uri("/api/v1/accounts/{userId}/user-response", userId)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (request, response1) -> {
                        logger.warn("Error fetching user {} from IAM service: Status {}", userId, response1.getStatusCode());
                        throw new RuntimeException("User not found or error in IAM service");
                    })
                    .body(UserResponse.class);

            return Optional.ofNullable(response);

        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            logger.warn("User {} not found in IAM service", userId);
            return Optional.empty();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            logger.warn("HTTP error fetching user {} from IAM service: {}", userId, e.getStatusCode());
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error fetching user {} from IAM: {}", userId, e.getMessage(), e);
            return Optional.empty();
        }
    }
}

