/**
 * 
 * Controller Layer**

- **AuthenticationController** endpoints:
    
    - `/auth/register`:
        
        - Success case
            
        - Duplicate email rejection
            
        - Invalid input handling
            
    - `/auth/login`:
        
        - Success returns JWT
            
        - Failure returns 401
            
    - `/auth/activate-account`:
        
        - Success activates account
            
        - Invalid/expired token returns error
            

**Tools:** Spring MockMvc, WebTestClient

Full Authentication Flow (Integration / E2E)**

- Simulate a **complete workflow**:
    
    1. Register a user
        
    2. Receive activation token
        
    3. Activate account
        
    4. Login with credentials
        
    5. Access a secured endpoint using JWT
        

**Tools:** `@SpringBootTest` with `TestRestTemplate` or `WebTestClient`, H2 in-memory database
 */