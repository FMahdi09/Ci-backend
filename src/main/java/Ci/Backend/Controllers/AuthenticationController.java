package Ci.Backend.Controllers;

import Ci.Backend.Dtos.AuthenticationResponseDto;
import Ci.Backend.Dtos.InvalidDtoException;
import Ci.Backend.Dtos.LoginDto;
import Ci.Backend.Dtos.RegisterDto;
import Ci.Backend.Models.UserEntity;
import Ci.Backend.Services.Token.InvalidTokenException;
import Ci.Backend.Services.Token.TokenService;
import Ci.Backend.Services.User.UserService;
import Ci.Backend.Services.User.UsernameExistsException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/auth")
public class AuthenticationController
{
    private final UserService userService;

    private final TokenService tokenService;

    @Autowired
    public AuthenticationController(UserService userService,
                                    TokenService tokenGeneratorService)
    {
        this.userService = userService;
        this.tokenService = tokenGeneratorService;
    }

    @PostMapping(path = "login")
    public ResponseEntity<AuthenticationResponseDto> login(HttpServletResponse response,
                                                           @RequestBody LoginDto loginDto)
            throws InvalidDtoException
    {
        loginDto.ensureValidDto();

        Authentication authentication = userService.authenticate(
                loginDto.getUsername(),
                loginDto.getPassword()
        );

        String accessToken = tokenService.generateAccessToken(authentication.getName());
        String refreshToken = tokenService.generateRefreshToken(authentication.getName());

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        AuthenticationResponseDto responseDto = new AuthenticationResponseDto(accessToken);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping(path = "register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto)
            throws InvalidDtoException
    {
        try
        {
            registerDto.ensureValidDto();

            userService.createNewUser(
                    registerDto.getUsername(),
                    registerDto.getPassword(),
                    registerDto.getEmail()
            );

            return new ResponseEntity<>(HttpStatus.OK);
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

            String accessToken = tokenService.generateAccessToken(user.getUsername());

            AuthenticationResponseDto responseDto = new AuthenticationResponseDto(accessToken);

            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        }
        catch (InvalidTokenException | UsernameNotFoundException exception)
        {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
