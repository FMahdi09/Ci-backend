package Ci.Backend.Repositories;

import Ci.Backend.Categories.IntegrationTest;
import Ci.Backend.Models.Role;
import Ci.Backend.Models.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@IntegrationTest
class UserRepositoryTest
{
    @Autowired
    private UserRepository userRepository;

    //region <testData>
    private static final List<Role> userRoles = Collections.singletonList(
            new Role("USER")
    );

    private static final UserEntity testUser1 = new UserEntity(
            "Tick",
            "Password",
            "Tick@example.com",
            userRoles
    );

    private static final UserEntity testUser2 = new UserEntity(
            "Trick",
            "Password",
            "Trick@example.com",
            userRoles
    );

    private static final UserEntity testUser3 = new UserEntity(
            "Track",
            "Password",
            "Track@example.com",
            userRoles
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
