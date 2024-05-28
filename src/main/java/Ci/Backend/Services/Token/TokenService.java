package Ci.Backend.Services.Token;

import org.springframework.security.core.userdetails.UserDetails;

public interface TokenService
{
    String generateAccessToken(String username);

    String generateRefreshToken(String username);

    String getUsernameFromAccessToken(String token)
            throws InvalidTokenException;

    String getUsernameFromRefreshToken(String token)
            throws InvalidTokenException;

    void validateAccessToken(String token, UserDetails userDetails)
            throws InvalidTokenException;

    void validateRefreshToken(String token, UserDetails userDetails)
            throws InvalidTokenException;
}
