package com.digiquad.dealkaro.service.impl;


import com.digiquad.dealkaro.entity.User;
import com.digiquad.dealkaro.entity.UserRole;
import com.digiquad.dealkaro.exceptions.customExceptions.SuperAdminNotFoundException;
import com.digiquad.dealkaro.model.DTO.UserDTO;
import com.digiquad.dealkaro.model.UserModel;
import com.digiquad.dealkaro.repository.UserRepository;
import com.digiquad.dealkaro.service.SuperAdminService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static com.digiquad.dealkaro.constants.EnumConstants.Role.SUPER_ADMIN;


/**
 * Implementation of the {@link SuperAdminService} interface that handles operations
 * related to Super Admin user such as fetching details and updating profile information.
 *
 * <p>This service uses JPA Criteria API and file system storage for handling image uploads.</p>
 *
 * @author Saraswathi
 * @version 1.0
 */
@Service
public class SuperAdminServiceImpl implements SuperAdminService {
    private UserRepository userRepository;
    private EntityManager entityManager;
    @Autowired
    public SuperAdminServiceImpl(UserRepository userRepository,EntityManager entityManager){
    this.userRepository=userRepository;
    this.entityManager=entityManager;
}
    @Value("${upload.dir}")
    private String path;

    /**
     * Fetches the details of the Super Admin user.
     *
     * @return {@link UserDTO} containing Super Admin details.
     * @throws SuperAdminNotFoundException if Super Admin is not found.
     */
    @Override
    public UserDTO getSuperAdminDetails() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserDTO> query = criteriaBuilder.createQuery(UserDTO.class);
        Root<User> root = query.from(User.class);

        query.select(criteriaBuilder.construct(UserDTO.class,
                root.get("name"),
                root.get("userName"),
                root.get("mobileNumber"),
                root.get("email"),
                root.get("profileImageUrl"),
                root.get("emailVerified"),
                root.get("phoneVerified"),
                root.get("approvalStatus"),
                root.get("isActive"),
                root.get("isDeleted"),
                root.get("isLoggedIn"),
                root.get("adhaarNumber"),
                root.get("createdAt"),
                root.get("updatedAt")
        )).where(criteriaBuilder.equal(root.get("name"), "superadmin"));

        TypedQuery<UserDTO> typedQuery = entityManager.createQuery(query);

        try {
            return typedQuery.getSingleResult();
        } catch (Exception e) {
            throw new SuperAdminNotFoundException("Super Admin not found");
        }
    }

    /**
     * Updates only the profile image of the Super Admin.
     *
     * @param loggedInUserMail the ID of the currently logged-in Super Admin user.
     * @param file           the profile image file to be uploaded.
     * @return status message indicating success or failure.
     * @throws IOException               if an I/O error occurs during file upload.
     * @throws FileNotFoundException    if the file is null or not uploaded.
     * @throws SuperAdminNotFoundException if the user is not found.
     */
    @Override
    @Transactional
    public String updateSuperAdminDetailsWithImage(String loggedInUserMail, MultipartFile file) throws IOException {
        User user = userRepository.findByEmail(loggedInUserMail)
                .orElseThrow(() -> new SuperAdminNotFoundException("Super Admin not found with ID: " + loggedInUserMail));

        if (user.getUserType().getName().equals(SUPER_ADMIN.name())) {
            if (file == null) {
                throw new FileNotFoundException("Image is not uploaded");
            }

            String originalFilename = file.getOriginalFilename();
            Path filePath = Paths.get(path, originalFilename);
            Files.write(filePath, file.getBytes());

            user.setProfileImageUrl(originalFilename);
            userRepository.save(User.builder()
                    .profileImageUrl(originalFilename)
                    .name(user.getName())
                    .id(user.getId())
                    .mobileNumber(user.getMobileNumber())
                    .userType(user.getUserType())
                    .approvalStatus(true)
                    .userName(user.getUserName())
                    .build());

            return "Super Admin details and image updated successfully...";
        } else {
            throw new SuperAdminNotFoundException("Super Admin not found with ID: " + loggedInUserMail);
        }
    }

    /**
     * Updates all Super Admin details including profile image, email, mobile number, and Aadhaar number.
     *
     * @param loggedInUserMail the ID of the currently logged-in Super Admin user.
     * @param file           the profile image file to be uploaded.
     * @param model          the model containing updated Super Admin details.
     * @return status message indicating success or failure.
     * @throws IOException               if an I/O error occurs during file upload.
     * @throws SuperAdminNotFoundException if the user is not found.
     */
    @Override
    @Transactional
    public String updateSuperAdminAllDetails(String loggedInUserMail, MultipartFile file, UserModel model) throws IOException {
        User user = userRepository.findByEmail(loggedInUserMail)
                .orElseThrow(() -> new SuperAdminNotFoundException("Super Admin not found with ID: " + loggedInUserMail));

        if (file == null) {
            throw new FileNotFoundException("Image is not uploaded");
        }

        if (user.getUserType().getName().equals(SUPER_ADMIN.name())) {
            String originalFilename = file.getOriginalFilename();
            Path filePath = Paths.get(path, originalFilename);
            Files.write(filePath, file.getBytes());

            user.setUserType(new UserRole(0, "SUPER_ADMIN"));
            userRepository.save(User.builder()
                    .email(model.getEmail())
                    .profileImageUrl(originalFilename)
                    .mobileNumber(model.getMobileNumber())
                    .adhaarNumber(model.getAdhaarNumber())
                    .name(user.getName())
                    .userName(model.getUserName())
                    .id(user.getId())
                    .password(user.getPassword())
                    .approvalStatus(user.getApprovalStatus())
                    .isActive(user.getIsActive())
                    .userType(user.getUserType())
                    .updatedAt(LocalDateTime.now())
                    .build());

            return "Super Admin found and updated";
        }
        throw new SuperAdminNotFoundException("Super Admin not found");
    }
}
