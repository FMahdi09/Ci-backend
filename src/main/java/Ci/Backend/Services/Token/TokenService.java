package Ci.Backend.Services.Token;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public interface TokenService
{
    String generateAccessToken(String subject, Date issuedAt, Date expiresAt);

    String generateRefreshToken(String subject, Date issuedAt, Date expiresAt);

    String getUsernameFromAccessToken(String token)
            throws InvalidTokenException, ExpiredTokenException;

    String getUsernameFromRefreshToken(String token)
            throws InvalidTokenException, ExpiredTokenException;

    void validateAccessToken(String token, UserDetails userDetails)
            throws InvalidTokenException, ExpiredTokenException;

    void validateRefreshToken(String token, UserDetails userDetails)
            throws InvalidTokenException, ExpiredTokenException;
}
