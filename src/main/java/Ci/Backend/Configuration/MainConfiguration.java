package Ci.Backend.Configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ci")
public class MainConfiguration implements JwtConfiguration
{
    private String accessTokenSecret;

    private String refreshTokenSecret;

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
}
