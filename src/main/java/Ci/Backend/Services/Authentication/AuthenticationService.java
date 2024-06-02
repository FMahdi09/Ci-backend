package Ci.Backend.Services.Authentication;

import Ci.Backend.Dtos.TokenResponseDto;

public interface AuthenticationService
{
    TokenResponseDto login(String username, String password);
}
