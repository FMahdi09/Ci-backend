package Ci.Backend.Services.User;

import Ci.Backend.Models.Role;
import Ci.Backend.Models.UserEntity;
import Ci.Backend.Repositories.RoleRepository;
import Ci.Backend.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class DbUserService implements UserService
{
    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    @Autowired
    public DbUserService(AuthenticationManager authenticationManager,
                         PasswordEncoder passwordEncoder,
                         UserRepository userRepository,
                         RoleRepository roleRepository)
    {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Authentication authenticate(String username, String password)
    {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );
    }

    @Override
    public void createNewUser(String username, String password, String email)
            throws UsernameExistsException
    {
        if (userRepository.existsByUsername(username))
        {
            throw new UsernameExistsException();
        }

        String encodedPassword = passwordEncoder.encode(password);

        Role role = roleRepository.findByName("USER").orElseThrow();
        List<Role> roles = Collections.singletonList(role);

        UserEntity user = new UserEntity(
                username,
                encodedPassword,
                email,
                roles
        );

        userRepository.save(user);
    }

    @Override
    public UserEntity findByUsername(String username)
            throws UsernameNotFoundException
    {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username from token does not exist"));
    }
}
