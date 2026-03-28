# api-gateway
Spring Cloud Gateway — single entry point for all VSMS services.
- Port: 8080
- Validates JWT against auth-service JWKS on every request
- Routes traffic to downstream services via Eureka lb:// URIs
- All new routes must be added to application.yml routes block