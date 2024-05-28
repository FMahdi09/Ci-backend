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

    private final int accessTokenExpiration;
    private final int refreshTokenExpiration;

    public JwtTokenService(JwtConfiguration configuration)
    {
        accessTokenKey = getSigningKey(configuration.getAccessTokenSecret());
        refreshTokenKey = getSigningKey(configuration.getRefreshTokenSecret());
        accessTokenExpiration = configuration.getAccessTokenExpiration();
        refreshTokenExpiration = configuration.getRefreshTokenExpiration();
    }

    @Override
    public String generateAccessToken(String username)
    {
        return generateToken(username, accessTokenExpiration, accessTokenKey);
    }

    @Override
    public String generateRefreshToken(String username)
    {
        return generateToken(username, refreshTokenExpiration, refreshTokenKey);
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

    private String generateToken(String username, int expiresIn, Key key)
    {
        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + expiresIn);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(issuedAt)
                .setExpiration(expiresAt)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
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
