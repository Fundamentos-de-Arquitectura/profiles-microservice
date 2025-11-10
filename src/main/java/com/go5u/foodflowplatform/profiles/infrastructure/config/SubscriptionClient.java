package com.go5u.foodflowplatform.profiles.infrastructure.config;

import com.go5u.foodflowplatform.profiles.interfaces.rest.dto.SubscriptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class SubscriptionClient {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionClient.class);
    private static final String SUBSCRIPTION_SERVICE = "http://subscription-service";

    private final RestClient restClient;

    public SubscriptionClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl(SUBSCRIPTION_SERVICE)
                .build();
    }

    /**
     * Obtiene una suscripción por ID desde el microservicio Subscription
     * @param subscriptionId ID de la suscripción
     * @return Optional con la suscripción o vacío si no existe
     */
    public Optional<SubscriptionResponse> getSubscriptionById(Long subscriptionId) {
        try {
            logger.info("Fetching subscription from Subscription service: {}", subscriptionId);

            SubscriptionResponse response = restClient.get()
                    .uri("/api/v1/subscriptions/by-id/{subscriptionId}", subscriptionId)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (request, response1) -> {
                        logger.warn("Error fetching subscription {} from Subscription service: Status {}", subscriptionId, response1.getStatusCode());
                        throw new RuntimeException("Subscription not found or error in Subscription service");
                    })
                    .body(SubscriptionResponse.class);

            return Optional.ofNullable(response);

        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            logger.warn("Subscription {} not found in Subscription service", subscriptionId);
            return Optional.empty();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            logger.warn("HTTP error fetching subscription {} from Subscription service: {}", subscriptionId, e.getStatusCode());
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error fetching subscription {} from Subscription service: {}", subscriptionId, e.getMessage(), e);
            return Optional.empty();
        }
    }
}

