package Ci.Backend.Configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ci")
public class MainConfiguration implements JwtConfiguration
{
    private String accessTokenSecret;

    private String refreshTokenSecret;

    private int accessTokenExpiration;

    private int refreshTokenExpiration;

    @Override
    public String getAccessTokenSecret()
    {
        return accessTokenSecret;
    }

    public void setAccessTokenSecret(String accessTokenSecret)
    {
        this.accessTokenSecret = accessTokenSecret;
    }

    @Override
    public String getRefreshTokenSecret()
    {
        return refreshTokenSecret;
    }

    public void setRefreshTokenSecret(String refreshTokenSecret)
    {
        this.refreshTokenSecret = refreshTokenSecret;
    }

    @Override
    public int getAccessTokenExpiration()
    {
        return accessTokenExpiration;
    }

    public void setAccessTokenExpiration(int accessTokenExpiration)
    {
        this.accessTokenExpiration = accessTokenExpiration;
    }

    @Override
    public int getRefreshTokenExpiration()
    {
        return refreshTokenExpiration;
    }

    public void setRefreshTokenExpiration(int refreshTokenExpiration)
    {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }
}
