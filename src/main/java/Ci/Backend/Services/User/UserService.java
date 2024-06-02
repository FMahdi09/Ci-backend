package Ci.Backend.Services.User;

import Ci.Backend.Models.UserEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService
{
    void createNewUser(String username, String password, String email)
            throws UsernameExistsException;

    UserEntity findByUsername(String username)
            throws UsernameNotFoundException;
}
