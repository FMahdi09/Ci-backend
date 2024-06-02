package Ci.Backend.Services.Token;

import Ci.Backend.Configuration.JwtConfiguration;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenService implements TokenService
{
    private final Key accessTokenKey;
    private final Key refreshTokenKey;

    public JwtTokenService(JwtConfiguration configuration)
    {
        accessTokenKey = getSigningKey(configuration.getAccessTokenSecret());
        refreshTokenKey = getSigningKey(configuration.getRefreshTokenSecret());
    }

    @Override
    public String generateAccessToken(String subject, Date issuedAt, Date expiresAt)
    {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expiresAt)
                .signWith(accessTokenKey, SignatureAlgorithm.HS512)
                .compact();
    }

    @Override
    public String generateRefreshToken(String subject, Date issuedAt, Date expiresAt)
    {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expiresAt)
                .signWith(refreshTokenKey, SignatureAlgorithm.HS512)
                .compact();
    }

    @Override
    public String getUsernameFromAccessToken(String token)
            throws InvalidTokenException
    {
        return extractClaim(token, accessTokenKey, Claims::getSubject);
    }

    @Override
    public String getUsernameFromRefreshToken(String token)
            throws InvalidTokenException
    {
        return extractClaim(token, refreshTokenKey, Claims::getSubject);
    }

    @Override
    public void validateAccessToken(String token, UserDetails userDetails)
            throws InvalidTokenException
    {
        String username = getUsernameFromAccessToken(token);

        if (!username.equals(userDetails.getUsername()))
        {
            throw new InvalidTokenException("access token does not match provided UserDetails");
        }
    }

    @Override
    public void validateRefreshToken(String token, UserDetails userDetails)
            throws InvalidTokenException
    {
        String username = getUsernameFromRefreshToken(token);

        if (!username.equals(userDetails.getUsername()))
        {
            throw new InvalidTokenException("refresh token does not match provided UserDetails");
        }
    }

    private <T> T extractClaim(String token, Key key, Function<Claims, T> claimsResolver)
            throws InvalidTokenException
    {
        final Claims claims = extractAllClaims(token, key);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token, Key key)
            throws InvalidTokenException
    {
        try
        {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }
        catch (JwtException ex)
        {
            throw new InvalidTokenException("Invalid or expired token provided");
        }
    }

    private Key getSigningKey(String key)
    {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
