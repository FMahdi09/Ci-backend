package Ci.Backend.Services.User;

import Ci.Backend.Categories.UnitTest;
import Ci.Backend.Models.Role;
import Ci.Backend.Models.UserEntity;
import Ci.Backend.Repositories.RoleRepository;
import Ci.Backend.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@UnitTest
@ExtendWith(MockitoExtension.class)
class DbUserServiceTest
{
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    private DbUserService userService;

    @BeforeEach
    void setUp()
    {
        userService = new DbUserService(
                passwordEncoder,
                userRepository,
                roleRepository
        );
    }

    @ParameterizedTest
    @CsvSource({"username,password,email"})
    void createNewUser_givenNonExistingName_createUser(String username, String password, String email)
            throws UsernameExistsException
    {
        // arrange
        given(passwordEncoder.encode(anyString()))
                .willReturn("encodedPassword");

        given(roleRepository.findByName("USER"))
                .willReturn(Optional.of(new Role("USER")));

        given(userRepository.existsByUsername(username))
                .willReturn(false);

        ArgumentCaptor<UserEntity> userArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);

        // act
        userService.createNewUser(username, password, email);

        verify(userRepository).save(userArgumentCaptor.capture());
        UserEntity userEntity = userArgumentCaptor.getValue();

        // assert
        assertAll(
                () -> assertEquals(userEntity.getUsername(), username),
                () -> assertEquals(userEntity.getPassword(), "encodedPassword"),
                () -> assertEquals(userEntity.getEmail(), email)
        );
    }

    @ParameterizedTest
    @CsvSource({"username,password,email"})
    void createNewUser_givenExistingName_throwUsernameExists(String username, String password, String email)
    {
        // arrange
        given(userRepository.existsByUsername(username))
                .willReturn(true);

        // act & assert
        assertThrows(UsernameExistsException.class, () -> userService.createNewUser(username, password, email));
    }

    @ParameterizedTest
    @CsvSource({"username"})
    void findByUsername_givenNonExistingName_throwUsernameNotFound(String username)
    {
        // arrange
        given(userRepository.findByUsername(username))
                .willReturn(Optional.empty());

        // act & assert
        assertThrows(UsernameNotFoundException.class, () -> userService.findByUsername(username));
    }

    @ParameterizedTest
    @CsvSource({"username"})
    void findByUsername_givenExistingName_returnUserEntity(String username)
    {
        // arrange
        UserEntity existingUser = new UserEntity(
                username,
                "password",
                "email",
                new ArrayList<>()
        );

        given(userRepository.findByUsername(username))
                .willReturn(Optional.of(existingUser));

        // act
        UserEntity foundUser = userService.findByUsername(username);

        // assert
        assertEquals(existingUser, foundUser);
    }
}
