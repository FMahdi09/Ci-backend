package Ci.Backend.Exceptions;

public class RoleNotFoundException extends Exception
{
    public RoleNotFoundException()
    {
    }

    public RoleNotFoundException(String message)
    {
        super(message);
    }

    public RoleNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public RoleNotFoundException(Throwable cause)
    {
        super(cause);
    }
}
