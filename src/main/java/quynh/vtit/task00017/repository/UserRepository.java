package quynh.vtit.task00017.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quynh.vtit.task00017.domain.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
