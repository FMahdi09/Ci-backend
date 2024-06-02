package Ci.Backend.Services.Authentication;

import Ci.Backend.Dtos.LoginReponseDto;

public interface AuthenticationService
{
    LoginReponseDto login(String username, String password);
}
