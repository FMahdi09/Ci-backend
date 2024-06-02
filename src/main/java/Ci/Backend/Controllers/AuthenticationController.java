package Ci.Backend.Controllers;

import Ci.Backend.Dtos.*;
import Ci.Backend.Services.Authentication.AuthenticationService;
import Ci.Backend.Services.Token.ExpiredTokenException;
import Ci.Backend.Services.Token.InvalidTokenException;
import Ci.Backend.Services.User.UsernameExistsException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/auth")
public class AuthenticationController
{
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
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
            String accessToken = authenticationService.refresh(refreshToken);

            AuthenticationResponseDto responseDto = new AuthenticationResponseDto(accessToken);

            return ResponseEntity.ok(responseDto);
        }
        catch (ExpiredTokenException exception)
        {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        catch (InvalidTokenException | UsernameNotFoundException exception)
        {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
