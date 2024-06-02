package Ci.Backend.Services.Authentication;

import Ci.Backend.Dtos.TokenResponseDto;
import Ci.Backend.Services.Token.ExpiredTokenException;
import Ci.Backend.Services.Token.InvalidTokenException;
import Ci.Backend.Services.User.UsernameExistsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AuthenticationService
{
    TokenResponseDto login(String username, String password);

    TokenResponseDto register(String username, String password, String email)
            throws UsernameExistsException;

    String refresh(String refreshToken)
            throws InvalidTokenException, ExpiredTokenException, UsernameNotFoundException;
}
