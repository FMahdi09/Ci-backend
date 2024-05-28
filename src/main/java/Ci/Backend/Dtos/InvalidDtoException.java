package Ci.Backend.Dtos;

public class InvalidDtoException extends Exception
{
    public InvalidDtoException()
    {
    }

    public InvalidDtoException(String message)
    {
        super(message);
    }

    public InvalidDtoException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InvalidDtoException(Throwable cause)
    {
        super(cause);
    }
}
