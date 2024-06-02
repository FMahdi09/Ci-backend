package Ci.Backend.Services.Authentication;

import Ci.Backend.Dtos.LoginReponseDto;
import Ci.Backend.Services.Token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DbAuthenticationService implements AuthenticationService
{
    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;

    @Autowired
    public DbAuthenticationService(AuthenticationManager authenticationManager,
                                   TokenService tokenService)
    {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @Override
    public LoginReponseDto login(String username, String password)
    {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );

        Date issuedAt = new Date();
        Date accessExpiration = new Date(issuedAt.getTime() + 70000);
        Date refreshExpiration = new Date(issuedAt.getTime() + 700000);

        String accessToken = tokenService.generateAccessToken(
                authentication.getName(),
                issuedAt,
                accessExpiration
        );

        String refreshToken = tokenService.generateRefreshToken(
                authentication.getName(),
                issuedAt,
                refreshExpiration
        );

        return new LoginReponseDto(refreshToken, accessToken);
    }
}
