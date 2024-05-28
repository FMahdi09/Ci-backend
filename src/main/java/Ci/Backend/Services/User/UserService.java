package Ci.Backend.Services.User;

import Ci.Backend.Models.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService
{
    Authentication authenticate(String username, String password);

    void createNewUser(String username, String password, String email)
            throws UsernameExistsException;

    UserEntity findByUsername(String username)
            throws UsernameNotFoundException;
}
