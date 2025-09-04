package co.com.bancolombia.config;

public final class SecurityConstants {

    // --- Paths ---
    public static final String LOAN_REQUESTS_PATH = "/api/v1/requests";
    public static final String[] SWAGGER_PUBLIC_PATHS = {
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/webjars/**"
    };

    // --- Roles ---
    public static final String ROLE_CLIENT = "ROLE_CLIENT";
    public static final String ROLE_ADVISER = "ROLE_ADVISER";

    private SecurityConstants() {
        // Prevent instantiation
    }
}
