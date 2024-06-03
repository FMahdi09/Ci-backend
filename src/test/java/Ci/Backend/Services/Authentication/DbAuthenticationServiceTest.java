package Ci.Backend.Services.Authentication;

import Ci.Backend.Categories.IntegrationTest;
import Ci.Backend.Dtos.TokenResponseDto;
import Ci.Backend.Models.Role;
import Ci.Backend.Models.UserEntity;
import Ci.Backend.Repositories.RoleRepository;
import Ci.Backend.Repositories.UserRepository;
import Ci.Backend.Services.Token.ExpiredTokenException;
import Ci.Backend.Services.Token.InvalidTokenException;
import Ci.Backend.Services.User.UsernameExistsException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@DirtiesContext
@IntegrationTest
@Transactional
class DbAuthenticationServiceTest
{
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    private DbAuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    //region <testData>
    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

    private static final Role userRole = new Role("USER");

    private static final UserEntity testUser1 = new UserEntity(
            "Tick",
            encoder.encode("password"),
            "Tick@example.com",
            new ArrayList<>()
    );

    private static final UserEntity testUser2 = new UserEntity(
            "Trick",
            encoder.encode("password"),
            "Trick@example.com",
            new ArrayList<>()
    );

    private static final UserEntity testUser3 = new UserEntity(
            "Track",
            encoder.encode("password"),
            "Track@example.com",
            new ArrayList<>()
    );
    //endregion

    @BeforeEach
    void setUp()
    {
        roleRepository.save(userRole);

        Role role = roleRepository.findByName("USER").orElseThrow();

        testUser1.setRoles(Collections.singletonList(role));
        testUser2.setRoles(Collections.singletonList(role));
        testUser3.setRoles(Collections.singletonList(role));

        userRepository.save(testUser1);
        userRepository.save(testUser2);
        userRepository.save(testUser3);
    }

    @AfterEach
    void tearDown()
    {
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @ParameterizedTest
    @CsvSource({
            "username, password",
            "nonExisitingUser, changeme"
    })
    void login_givenBadCredentials_throwBadCredentials(String username, String password)
    {
        // act & assert
        assertThrows(
                BadCredentialsException.class,
                () -> authenticationService.login(username, password)
        );
    }

    @ParameterizedTest
    @CsvSource({
            "Tick, password",
            "Trick, password",
            "Track, password"
    })
    void login_givenValidCredentaisl_returnTokens(String username, String password)
    {
        // act
        TokenResponseDto response = authenticationService.login(username, password);

        // assert
        assertAll(
                () -> assertNotNull(response.getAccessToken()),
                () -> assertNotNull(response.getRefreshToken())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"Tick", "Trick", "Track"})
    void register_givenExisitingUsername_throwUsernameExists(String existingUsername)
    {
        // act & assert
        assertThrows(
                UsernameExistsException.class,
                () -> authenticationService.register(existingUsername, "password", "email")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"Donald", "Dagobert", "Mickey"})
    void register_givenNonExisitingUsername_returnTokens(String username)
            throws UsernameExistsException
    {
        // act
        TokenResponseDto response = authenticationService.register(username, "password", "email");

        // assert
        assertAll(
                () -> assertNotNull(response.getAccessToken()),
                () -> assertNotNull(response.getRefreshToken())
        );
    }

    @ParameterizedTest
    @CsvSource({
            "Donald, password, donald@email.com",
            "Dagobert, password, dagobert@email.com",
            "Mickey, maus, mickey@email.com"
    })
    void register_givenNonExisitingUsername_createUser(String username, String password, String email)
            throws UsernameExistsException
    {
        // act
        authenticationService.register(username, password, email);

        // assert
        UserEntity createdUser = userRepository.findByUsername(username).orElseThrow();

        assertAll(
                () -> assertEquals(createdUser.getUsername(), username),
                () -> assertEquals(createdUser.getEmail(), email)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            ":)",
            "invalid",
            "asdhfashdfjashdfjhasdsdfhasfdfsdfsdfjasdfsdfjalösdfösldf"
    })
    void refresh_givenInvalidToken_throwInvalidToken(String invalidToken)
    {
        // act & assert
        assertThrows(
                InvalidTokenException.class,
                () -> authenticationService.refresh(invalidToken)
        );
    }

    @ValueSource(strings = {
            "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxNTE2MjM5MDIyfQ.iC1OZI-1WmIF6rNanRv5HcDWxbU7UG2lZYnqSZhx84cJJ5-5YEPu1a2GgK1gc6CkTF-txBARAElyf6vlNF7Hfw",
    })
    void refresh_givenExpiredToken_throwExpiredToken(String expiredToken)
    {
        // act & assert
        assertThrows(
                ExpiredTokenException.class,
                () -> authenticationService.refresh(expiredToken)
        );
    }
}
