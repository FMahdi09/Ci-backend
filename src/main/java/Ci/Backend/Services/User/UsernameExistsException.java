package Ci.Backend.Services.User;

public class UsernameExistsException extends Exception
{
    public UsernameExistsException()
    {
    }

    public UsernameExistsException(String message)
    {
        super(message);
    }

    public UsernameExistsException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public UsernameExistsException(Throwable cause)
    {
        super(cause);
    }
}
