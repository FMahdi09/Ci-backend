package Ci.Backend.Repositories;

import Ci.Backend.Models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer>
{
    Optional<UserEntity> findByUsername(String username);

    Boolean existsByUsername(String username);
}
