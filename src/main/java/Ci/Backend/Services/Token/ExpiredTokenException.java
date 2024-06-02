package Ci.Backend.Services.Token;

public class ExpiredTokenException extends Exception
{
    public ExpiredTokenException()
    {
    }

    public ExpiredTokenException(String message)
    {
        super(message);
    }

    public ExpiredTokenException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ExpiredTokenException(Throwable cause)
    {
        super(cause);
    }
}
