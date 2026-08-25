package quynh.vtit.task00017.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import quynh.vtit.task00017.base.enums.UserRole;
import quynh.vtit.task00017.base.enums.UserStatus;
import quynh.vtit.task00017.domain.entity.User;
import quynh.vtit.task00017.repository.UserRepository;


@Component
@RequiredArgsConstructor
@Slf4j
public class InitApplicationRunner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .email("administrator@gmail.com")
                    .role(UserRole.ADMIN)
                    .fullName("Administrator")
                    .status(UserStatus.ACTIVE)
                    .username("admin")
                    .build();
            admin.setPasswordHash(passwordEncoder.encode("admin"));
            userRepository.save(admin);
            log.info("Create default admin successful with username: admin and default password: admin");
        }
    }
}
