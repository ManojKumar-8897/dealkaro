package com.digiquad.dealkaro.config;


import com.digiquad.dealkaro.constants.EnumConstants;
import com.digiquad.dealkaro.entity.User;
import com.digiquad.dealkaro.entity.UserRole;
import com.digiquad.dealkaro.repository.UserRepository;
import com.digiquad.dealkaro.repository.UserRoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.List;
import static com.digiquad.dealkaro.constants.Constants.DUMMY_MOBILE_NUMBER;
import static com.digiquad.dealkaro.constants.Constants.SUPER_ADMIN;
import static com.digiquad.dealkaro.constants.EnumConstants.Role.ADMIN;
import static com.digiquad.dealkaro.constants.EnumConstants.Role.USER;


@Configuration
@AllArgsConstructor
public class PostConstructInitializer {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {

        // Saving Roles if not present in DB
        if (userRoleRepository.count() == 0) {
            List<UserRole> roles = List.of(
                    UserRole.builder().id(0).name(EnumConstants.Role.SUPER_ADMIN.toString()).build(),
                    UserRole.builder().id(1).name(ADMIN.toString()).build(),
                    UserRole.builder().id(2).name(USER.toString()).build()
            );

            userRoleRepository.saveAll(roles);
            userRoleRepository.flush();
        }

        // Add superadmin if not present
        if (!userRepository.existsByUserName(SUPER_ADMIN)) {
            UserRole superRole = userRoleRepository.findById(0).orElseThrow();

            userRepository.save(User.builder()
                    .userName(SUPER_ADMIN)
                    .name(SUPER_ADMIN)
                    .userType(superRole)
                    .mobileNumber(DUMMY_MOBILE_NUMBER)
                    .approvalStatus(true)
                    .isActive(true)
                    .password(passwordEncoder.encode(SUPER_ADMIN))
                     .hasPassword(false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .isDeleted(false)
                    .build());
        }
    }
}
