package Ci.Backend.Dtos;

public class RegisterDto
{
    private String username;

    private String email;

    private String password;

    public RegisterDto(String username, String email, String password)
    {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void ensureValidDto()
            throws InvalidDtoException
    {
        if (username == null || password == null || email == null)
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

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
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
