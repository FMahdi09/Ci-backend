package Ci.Backend.Controllers;

import Ci.Backend.Categories.UnitTest;
import Ci.Backend.Dtos.AuthenticationResponseDto;
import Ci.Backend.Dtos.InvalidDtoException;
import Ci.Backend.Dtos.LoginDto;
import Ci.Backend.Dtos.RegisterDto;
import Ci.Backend.Models.UserEntity;
import Ci.Backend.Services.Token.InvalidTokenException;
import Ci.Backend.Services.Token.TokenService;
import Ci.Backend.Services.User.UserService;
import Ci.Backend.Services.User.UsernameExistsException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

@UnitTest
@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest
{
    @Mock
    private UserService userService;

    @Mock
    private TokenService tokenService;

    @Mock
    private HttpServletResponse HttpResponse;

    @Mock
    private Authentication authentication;

    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp()
    {
        authenticationController = new AuthenticationController(userService, tokenService);
    }

    @Test
    void register_givenValidData_returnOK() throws InvalidDtoException
    {
        // arrange
        RegisterDto registerDto = new RegisterDto(
                "username",
                "email",
                "password"
        );

        // act
        ResponseEntity<String> reponse = authenticationController.register(registerDto);

        // assert
        assertEquals(reponse.getStatusCode(), HttpStatus.OK);
    }

    @ParameterizedTest
    @CsvSource({",password,email", "username,,email", "username,password,"})
    void register_givenInvalidData_throwInvalidDTO(String username, String password, String email)
    {
        // arrange
        RegisterDto registerDto = new RegisterDto(
                username,
                email,
                password
        );

        // act & assert
        assertThrows(InvalidDtoException.class, () -> authenticationController.register(registerDto));
    }

    @Test
    void register_givenExisitingUsername_returnCONFLICT()
            throws InvalidDtoException, UsernameExistsException
    {
        // arrange
        RegisterDto registerDto = new RegisterDto(
                "exisitingUsername",
                "email",
                "password"
        );

        doThrow(new UsernameExistsException())
                .when(userService)
                .createNewUser(anyString(), anyString(), anyString());

        // act
        ResponseEntity<String> reponse = authenticationController.register(registerDto);

        // assert
        assertEquals(reponse.getStatusCode(), HttpStatus.CONFLICT);
    }

    @Test
    void refresh_givenValidToken_returnAccessToken()
    {
        // arrange
        String refreshToken = "validToken";

        given(userService.findByUsername(any()))
                .willReturn(new UserEntity());

        given(tokenService.generateAccessToken(any()))
                .willReturn("accessToken");

        // act
        ResponseEntity<AuthenticationResponseDto> response = authenticationController.refresh(refreshToken);

        AuthenticationResponseDto body = response.getBody();
        String accessToken = body != null ? body.getAccessToken() : null;

        // assert
        assertAll(
                () -> assertEquals(response.getStatusCode(), HttpStatus.OK),
                () -> assertEquals(accessToken, "accessToken")
        );
    }

    @Test
    void refresh_givenInvalidToken_returnFORBIDDEN() throws InvalidTokenException
    {
        // arrange
        String invalidToken = "invalidToken";

        doThrow(new InvalidTokenException())
                .when(tokenService)
                .getUsernameFromRefreshToken("invalidToken");

        // act
        ResponseEntity<AuthenticationResponseDto> response = authenticationController.refresh(invalidToken);

        // assert
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @ParameterizedTest
    @CsvSource({",password", "username,"})
    void login_givenInvalidData_throwInvalidDTO(String username, String password)
    {
        // arrange
        LoginDto loginDto = new LoginDto(username, password);

        // act & assert
        assertThrows(InvalidDtoException.class, () -> authenticationController.login(HttpResponse, loginDto));
    }

    @ParameterizedTest
    @CsvSource({"username,password"})
    void login_givenValidData_returnTokens(String username, String password)
            throws InvalidDtoException
    {
        // arrange
        LoginDto loginDto = new LoginDto(username, password);

        given(userService.authenticate(username, password))
                .willReturn(authentication);

        given(authentication.getName())
                .willReturn(username);

        given(tokenService.generateAccessToken(username))
                .willReturn("accessToken");

        given(tokenService.generateRefreshToken(username))
                .willReturn("refreshToken");

        // act
        ResponseEntity<AuthenticationResponseDto> response = authenticationController.login(HttpResponse, loginDto);

        AuthenticationResponseDto body = response.getBody();
        String accessToken = body != null ? body.getAccessToken() : null;

        // assert
        assertAll(
                () -> assertEquals(response.getStatusCode(), HttpStatus.OK),
                () -> assertEquals(accessToken, "accessToken")
        );
    }
}
