package Ci.Backend.Repositories;

import Ci.Backend.Categories.IntegrationTest;
import Ci.Backend.Exceptions.RoleNotFoundException;
import Ci.Backend.Models.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext
@IntegrationTest
class RoleRepositoryTest
{
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    private RoleRepository roleRepository;

    //region <testData>
    private static final Role userRole = new Role("USER");

    private static final Role adminRole = new Role("AMDIN");

    private static final Role editorRole = new Role("EDITOR");

    private static List<Role> getExistingRoles()
    {
        return Arrays.asList(
                userRole,
                adminRole,
                editorRole
        );
    }
    //endregion

    @BeforeEach
    void setUp()
    {
        roleRepository.save(userRole);
        roleRepository.save(adminRole);
        roleRepository.save(editorRole);
    }

    @AfterEach
    void tearDown()
    {
        roleRepository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("getExistingRoles")
    void findByName_givenExistingName_returnRole(Role existingRole)
    {
        // act
        Role role = roleRepository.findByName(existingRole.getName()).orElseThrow();

        // assert
        assertEquals(role.getName(), existingRole.getName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"IDontExist", "MeNeither"})
    void findByName_givenNonExistingName_throwRoleNotFound(String nonExitingName)
    {
        // act
        RoleNotFoundException exception = assertThrows(
                RoleNotFoundException.class,
                () -> roleRepository.findByName(nonExitingName).orElseThrow(
                        () -> new RoleNotFoundException(String.format("Role %s not found", nonExitingName))
                )
        );

        // assert
        String exceptionMessage = exception.getMessage();
        assertTrue(exceptionMessage.contains(nonExitingName));
    }
}
