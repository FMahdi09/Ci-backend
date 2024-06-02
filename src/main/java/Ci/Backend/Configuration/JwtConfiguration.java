package Ci.Backend.Configuration;

public interface JwtConfiguration
{
    String getAccessTokenSecret();

    String getRefreshTokenSecret();
}
