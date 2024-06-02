package Ci.Backend.Services.Authentication;

import Ci.Backend.Dtos.TokenResponseDto;
import Ci.Backend.Services.User.UsernameExistsException;

public interface AuthenticationService
{
    TokenResponseDto login(String username, String password);

    TokenResponseDto register(String username, String password, String email)
            throws UsernameExistsException;
}
