package Ci.Backend.Repositories;

import Ci.Backend.Models.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest
{
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    private UserRepository userRepository;

    //region <testData>
    private static final UserEntity testUser1 = new UserEntity(
            "Tick",
            "Password",
            "Tick@example.com",
            new ArrayList<>()
    );

    private static final UserEntity testUser2 = new UserEntity(
            "Trick",
            "Password",
            "Trick@example.com",
            new ArrayList<>()
    );

    private static final UserEntity testUser3 = new UserEntity(
            "Track",
            "Password",
            "Track@example.com",
            new ArrayList<>()
    );

    private static List<UserEntity> getExisitingUsers()
    {
        return Arrays.asList(
                testUser1,
                testUser2,
                testUser3
        );
    }
    //endregion

    @BeforeEach
    void setUp()
    {
        userRepository.save(testUser1);
        userRepository.save(testUser2);
        userRepository.save(testUser3);
    }

    @AfterEach
    void tearDown()
    {
        userRepository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("getExisitingUsers")
    void findByUsername_givenExisitingUsername_returnUser(UserEntity existingUser)
    {
        // act
        UserEntity user = userRepository.findByUsername(existingUser.getUsername()).orElseThrow();

        // assert
        assertAll(
                () -> assertEquals(user.getUsername(), existingUser.getUsername()),
                () -> assertEquals(user.getPassword(), existingUser.getPassword()),
                () -> assertEquals(user.getEmail(), existingUser.getEmail())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"Donald", "Dagobert", "Mickey", "Goofy"})
    void findByUsername_givenNonExistingUsername_throwUsernameNotFound(String nonExitingUsername)
    {
        // act
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userRepository.findByUsername(nonExitingUsername).orElseThrow(
                        () -> new UsernameNotFoundException(String.format("Username %s not found", nonExitingUsername))
                )
        );

        // assert
        String exceptionMessage = exception.getMessage();
        assertTrue(exceptionMessage.contains(nonExitingUsername));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Tick", "Trick", "Track"})
    void existsByUsername_givenExistingUsername_returnTrue(String existingUsername)
    {
        // act
        boolean result = userRepository.existsByUsername(existingUsername);

        // assert
        assertTrue(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Donald", "Dagobert", "Micky", "Goofy"})
    void existsByUsername_givenNonExistingUsername_returnFalse(String nonExistingUsername)
    {
        // act
        boolean result = userRepository.existsByUsername(nonExistingUsername);

        // assert
        assertFalse(result);
    }
}
