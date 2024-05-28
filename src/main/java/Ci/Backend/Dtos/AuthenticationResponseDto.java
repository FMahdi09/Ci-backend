package Ci.Backend.Dtos;

public class AuthenticationResponseDto
{
    private String accessToken;

    private String tokenType;

    public AuthenticationResponseDto(String accessToken)
    {
        this.accessToken = accessToken;
        this.tokenType = "Bearer ";
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public void setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;
    }

    public String getTokenType()
    {
        return tokenType;
    }

    public void setTokenType(String tokenType)
    {
        this.tokenType = tokenType;
    }
}
