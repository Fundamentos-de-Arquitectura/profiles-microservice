package com.go5u.foodflowplatform.profiles.infrastructure.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Redirect legacy /swagger-ui.html requests to the springdoc UI path (/swagger-ui/index.html).
 * Placed under the application's base package so Spring Boot component scanning picks it up.
 */
@Controller
public class SwaggerUiRedirectController {

    @GetMapping("/swagger-ui.html")
    public String redirectToSwaggerUi() {
        return "redirect:/swagger-ui/index.html";
    }
}

