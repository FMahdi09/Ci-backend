package Ci.Backend.Services.Authentication;

import Ci.Backend.Dtos.TokenResponseDto;
import Ci.Backend.Models.UserEntity;
import Ci.Backend.Services.Token.ExpiredTokenException;
import Ci.Backend.Services.Token.InvalidTokenException;
import Ci.Backend.Services.Token.TokenService;
import Ci.Backend.Services.User.UserService;
import Ci.Backend.Services.User.UsernameExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DbAuthenticationService implements AuthenticationService
{
    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final TokenService tokenService;

    @Autowired
    public DbAuthenticationService(AuthenticationManager authenticationManager,
                                   UserService userService,
                                   TokenService tokenService)
    {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @Override
    public TokenResponseDto login(String username, String password)
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

        return new TokenResponseDto(refreshToken, accessToken);
    }

    @Override
    public TokenResponseDto register(String username, String password, String email)
            throws UsernameExistsException
    {
        userService.createNewUser(username, password, email);

        Date issuedAt = new Date();
        Date accessExpiration = new Date(issuedAt.getTime() + 70000);
        Date refreshExpiration = new Date(issuedAt.getTime() + 700000);

        String accessToken = tokenService.generateAccessToken(
                username,
                issuedAt,
                accessExpiration
        );

        String refreshToken = tokenService.generateRefreshToken(
                username,
                issuedAt,
                refreshExpiration
        );

        return new TokenResponseDto(refreshToken, accessToken);
    }

    @Override
    public String refresh(String refreshToken)
            throws InvalidTokenException, ExpiredTokenException, UsernameNotFoundException
    {
        String username = tokenService.getUsernameFromRefreshToken(refreshToken);

        UserEntity user = userService.findByUsername(username);

        tokenService.validateRefreshToken(refreshToken, user);

        Date issuedAt = new Date();
        Date refreshExpiration = new Date(issuedAt.getTime() + 700000);

        return tokenService.generateAccessToken(user.getUsername(), issuedAt, refreshExpiration);
    }
}
