package Ci.Backend.Dtos;

public class LoginDto
{
    private String username;

    private String password;

    public LoginDto(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    public void ensureValidDto()
            throws InvalidDtoException
    {
        if (username == null || password == null)
        {
            throw new InvalidDtoException();
        }
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
