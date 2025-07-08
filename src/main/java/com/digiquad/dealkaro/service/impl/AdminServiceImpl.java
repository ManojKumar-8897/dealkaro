package com.digiquad.dealkaro.service.impl;


import com.digiquad.dealkaro.entity.User;
import com.digiquad.dealkaro.entity.UserRole;
import com.digiquad.dealkaro.exceptions.customExceptions.UserNotFoundException;
import com.digiquad.dealkaro.model.AdminModel;
import com.digiquad.dealkaro.model.DTO.AdminDTO;
import com.digiquad.dealkaro.model.DTO.UserRoleDTO;
import com.digiquad.dealkaro.repository.UserRepository;
import com.digiquad.dealkaro.repository.UserRoleRepository;
import com.digiquad.dealkaro.service.AdminService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.digiquad.dealkaro.constants.Constants.ADMIN;


@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void createAdmin(AdminModel adminDetails) {
        UserRole adminRole = userRoleRepository.findById(1).orElseThrow();

        userRepository.save(User.builder()
                .userName(adminDetails.getMobileNumber())
                .name(adminDetails.getName())
                .userType(adminRole)
                .mobileNumber(adminDetails.getMobileNumber())
                .email(adminDetails.getEmail())
                .approvalStatus(true)
                .isActive(true)
                .password(passwordEncoder.encode(ADMIN))
                .adhaarNumber(adminDetails.getAdhaarNumber())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build());
    }



    @Override
    public AdminDTO getAdminDetails(String adminId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AdminDTO> projectionQuery = criteriaBuilder.createQuery(AdminDTO.class);

        Root<User> root = projectionQuery.from(User.class);

        projectionQuery.select(criteriaBuilder.construct(AdminDTO.class,
                        root.get("id"),
                        root.get("userName"),
                        root.get("name"),
                        root.get("mobileNumber"),
                        root.get("email"),
                        root.get("profileImageUrl"),
                        root.get("isActive"),
                        root.get("isDeleted"),
                        root.get("adhaarNumber"),
                        root.get("userType")))
                .where(criteriaBuilder.equal(root.get("id"), adminId));
        Query dataQuery = entityManager.createQuery(projectionQuery);

        return (AdminDTO) dataQuery.getSingleResult();
    }


    @Override
    @Transactional
    public void updateAdminDetails(AdminModel adminDetails) {
        User existingAdminDetails = userRepository.findById(adminDetails.getId()).get();

        existingAdminDetails.setName(adminDetails.getName());
        existingAdminDetails.setEmail(adminDetails.getEmail());
        existingAdminDetails.setMobileNumber(adminDetails.getMobileNumber());

        userRepository.save(existingAdminDetails);
    }

    /**
     * Here we are checking the user type before proceeding for delete operation. Usually Super-admin can delete any
     * admin, so in case of super admin login, we are letting him delete directly the ID which is coming as request. But
     * in case of any other ADMIN login, we can't let hi/her to delete any other admin. In that case, we are letting him
     * delete his account.
     *
     * @param adminId - ID of the ID which we want to delete/hibernate.
     * @param loggedInUserMail - ID of current logged-in user
     */
    @Override
    @Transactional
    public void deleteAdminDetails(String adminId, String loggedInUserMail) {
        if (userRepository.findByEmail(loggedInUserMail).get().getUserType().getId() == 0) {
            userRepository.deleteUser(adminId);
        } else {
            userRepository.deleteUser(loggedInUserMail);
        }
    }
}
