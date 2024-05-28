package Ci.Backend.Services.Token;

public class InvalidTokenException extends Exception
{
    public InvalidTokenException()
    {
    }

    public InvalidTokenException(String message)
    {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InvalidTokenException(Throwable cause)
    {
        super(cause);
    }
}
