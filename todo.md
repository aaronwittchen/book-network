## 1. Refresh Tokens

- Add refresh tokens to allow clients to obtain new JWTs without logging in again.
- Store refresh tokens securely (in database or Redis).
- Invalidate refresh tokens on logout.

## 2. Exception Handling

- Implement a centralized exception handler using `@ControllerAdvice`.
- Handle exceptions such as:
  - `JwtException` for invalid JWTs
  - `ExpiredJwtException` for expired tokens
  - `AccessDeniedException` for unauthorized access

## 3. API Auditing / Logging

- Log JWT usage and authentication attempts.
- Record details such as:
  - Login timestamps
  - Failed login attempts
  - IP addresses and user agents
- Consider implementing rate-limiting to prevent brute-force login attacks.

testing
