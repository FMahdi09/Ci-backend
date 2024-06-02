package Ci.Backend.Controllers;

import Ci.Backend.Dtos.*;
import Ci.Backend.Models.UserEntity;
import Ci.Backend.Services.Authentication.AuthenticationService;
import Ci.Backend.Services.Token.ExpiredTokenException;
import Ci.Backend.Services.Token.InvalidTokenException;
import Ci.Backend.Services.Token.TokenService;
import Ci.Backend.Services.User.UserService;
import Ci.Backend.Services.User.UsernameExistsException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping(path = "api/auth")
public class AuthenticationController
{
    private final UserService userService;

    private final TokenService tokenService;

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(UserService userService,
                                    AuthenticationService authenticationService,
                                    TokenService tokenGeneratorService)
    {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.tokenService = tokenGeneratorService;
    }

    @PostMapping(path = "login")
    public ResponseEntity<AuthenticationResponseDto> login(HttpServletResponse httpResponse,
                                                           @RequestBody LoginDto loginDto)
            throws InvalidDtoException
    {
        loginDto.ensureValidDto();

        TokenResponseDto loginResponse = authenticationService.login(loginDto.getUsername(), loginDto.getPassword());

        Cookie cookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        httpResponse.addCookie(cookie);

        AuthenticationResponseDto reponseDto = new AuthenticationResponseDto(loginResponse.getAccessToken());

        return ResponseEntity.ok(reponseDto);
    }

    @PostMapping(path = "register")
    public ResponseEntity<AuthenticationResponseDto> register(HttpServletResponse httpResponse,
                                                              @RequestBody RegisterDto registerDto)
            throws InvalidDtoException
    {
        try
        {
            registerDto.ensureValidDto();

            TokenResponseDto loginResponse = authenticationService.register(
                    registerDto.getUsername(),
                    registerDto.getPassword(),
                    registerDto.getEmail()
            );

            Cookie cookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
            cookie.setSecure(true);
            cookie.setHttpOnly(true);
            httpResponse.addCookie(cookie);

            AuthenticationResponseDto reponseDto = new AuthenticationResponseDto(loginResponse.getAccessToken());

            return ResponseEntity.ok(reponseDto);
        }
        catch (UsernameExistsException exception)
        {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping(path = "refresh")
    public ResponseEntity<AuthenticationResponseDto> refresh(@CookieValue("refreshToken") String refreshToken)
    {
        try
        {
            String username = tokenService.getUsernameFromRefreshToken(refreshToken);

            UserEntity user = userService.findByUsername(username);

            tokenService.validateRefreshToken(refreshToken, user);

            Date issuedAt = new Date();
            Date refreshExpiration = new Date(issuedAt.getTime() + 700000);

            String accessToken = tokenService.generateAccessToken(user.getUsername(), issuedAt, refreshExpiration);

            AuthenticationResponseDto responseDto = new AuthenticationResponseDto(accessToken);

            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        }
        catch (InvalidTokenException | ExpiredTokenException | UsernameNotFoundException exception)
        {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
