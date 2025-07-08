package com.digiquad.dealkaro.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.digiquad.dealkaro.exceptions.customExceptions.*;
import com.digiquad.dealkaro.exceptions.customExceptions.auth.EmptyCredentialsException;
import com.digiquad.dealkaro.model.DTO.*;
import com.digiquad.dealkaro.service.RefreshTokenService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.digiquad.dealkaro.constants.EnumConstants;
import com.digiquad.dealkaro.entity.User;
import com.digiquad.dealkaro.entity.UserRole;
import com.digiquad.dealkaro.model.UserModel;
import com.digiquad.dealkaro.repository.UserRepository;
import com.digiquad.dealkaro.repository.UserRoleRepository;
import com.digiquad.dealkaro.service.UserService;
import com.digiquad.dealkaro.utility.ImageHandler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import static com.digiquad.dealkaro.constants.EnumConstants.Role.SUPER_ADMIN;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final ImageHandler imageHandler;
    private final EntityManager entityManager;
    private final RefreshTokenService refreshTokenService;
    @Autowired
    JwtServiceImpl  jwtServiceImpl; 
    
@Autowired
  public  AuthenticationManager authmanager;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @Override
    @Transactional
    public void registerUser(UserRegisterRequestDTO userDetails)  {

        try {
        UserRole userRole = userRoleRepository.findById(2)
                .orElseThrow(() -> new UserRoleNotFoundException("UserRole for ID: 2 is Not present in DB"));

        Optional<User> userOpt = userRepository.findByMobileNumber(userDetails.getMobileNumber())
                .or(() -> userRepository.findByUserName(userDetails.getUserName()))
                .or(() -> userRepository.findByEmail(userDetails.getEmail()));

        if (userOpt.isPresent()) {
            throw new UserAlreadyRegisteredException("User already registered with provided mobile, username, or email.");
        }
            User user = User.builder()
                    .userName(userDetails.getUserName())
                    .name(userDetails.getName())
                    .userType(userRole)
                    .mobileNumber(userDetails.getMobileNumber())
                    .email(StringUtils.hasText(userDetails.getEmail()) ? userDetails.getEmail() : null)
                    .approvalStatus(true)
                    .isActive(true)
                    .password(passwordEncoder.encode(userDetails.getPassword()))
                    .hasPassword(true)
                    .adhaarNumber(StringUtils.hasText(userDetails.getAdhaarNumber()) ? userDetails.getAdhaarNumber() : null)
                    .profileImageUrl(userDetails.getProfileImage() != null
                            ? imageHandler.handleImage(userDetails.getProfileImage()) : null)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .isDeleted(false)
                    .build();

          User user1=  userRepository.save(user);
            System.out.println("User persisted with ID: " + user1.getId());

        } catch (UserAlreadyRegisteredException ex) {
            throw ex;
        } catch (Exception e) {
            throw new UserNotSavedException("Error registering User");
        }
    }


    @Override
    @Transactional
    public void approveUser(String userId, String approverMail) {
        Optional<User> registeredUserOptional = userRepository.findById(userId);
        UserDTO loggedInUser = getUserDetails(approverMail);

        if (registeredUserOptional.isPresent() && (loggedInUser.getUserRole().getName()
                .equals(EnumConstants.Role.ADMIN.name()) ||
                loggedInUser.getUserRole().getName().equals(SUPER_ADMIN.name()))) {
            User registeredUserDetails = registeredUserOptional.get();

            registeredUserDetails.setApprovalStatus(true);
            registeredUserDetails.setApprovedBy(userRepository.findByEmail(approverMail).get());
            registeredUserDetails.setUpdatedAt(LocalDateTime.now());

            try {
                userRepository.save(registeredUserDetails);
            } catch (Exception e) {
                throw new UserNotSavedException("Error while approving user");
            }
        }
    }





    public UserDTO getUserDetails(String userMail) {
        try {
            User user = userRepository.findByEmailWithUserType(userMail)
                    .orElseThrow(() -> new UserNotFoundException("User not found for email: " + userMail));

            UserRole userRole = user.getUserType();
            UserRoleDTO userRoleDTO = null;

            if (userRole != null) {
                userRoleDTO = UserRoleDTO.builder()
                        .id(userRole.getId())
                        .name(userRole.getName())
                        .build();
            }

            return UserDTO.builder()
                    .id(user.getId().toString())
                    .userName(user.getUserName())
                    .name(user.getName())
                    .mobileNumber(user.getMobileNumber())
                    .email(user.getEmail())
                    .profileImageUrl(user.getProfileImageUrl())
                    .emailVerified(user.getEmailVerified())
                    .phoneVerified(user.getPhoneVerified())
                    .approvalStatus(user.getApprovalStatus())
                    .isActive(user.getIsActive())
                    .isDeleted(user.getIsDeleted())
                    .isLoggedIn(user.getIsLoggedIn())
                    .adhaarNumber(user.getAdhaarNumber())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .userRole(userRoleDTO)
                    .build();
        } catch (Exception e) {
            throw new UserNotFoundException("Error in fetching User Details for email: " + userMail);
        }
    }


    /**
     * Here we are checking whether the user is same user for which this API is going to update the details.
     *
     * @param userModel      - User Details to be updated.
     * @param loggedInUserMail - ID of the user who is currently logged in. It is fetched from AuthenticationPrincipal.
     *

     */
    @Override
    @Transactional
    public void updateUser(UserModel userModel, String loggedInUserMail) {

            try {
                User userDetails = userRepository.findByEmail(loggedInUserMail).orElseThrow(()-> new RuntimeException("user not found"));

                userDetails.setMobileNumber(userModel.getMobileNumber());
                userDetails.setEmail(userModel.getEmail());
                userDetails.setProfileImageUrl(imageHandler.handleImage(userModel.getProfileImage()));
                userDetails.setUpdatedAt(LocalDateTime.now());

                userRepository.save(userDetails);
            } catch (Exception e) {
                e.printStackTrace();
            }


    }

    /**
     * Deletes (soft deletes) the user. Only the user themself or a Super Admin can perform this action.
     *
     * @param userId          The ID of the user to be deleted.
     * @param loggedInUserEmail  The ID of the currently logged-in user performing the delete action.
     *
     * @author SIBARAM SAMAL
     * @since 2024-06-19
     */
    @Override
    @Transactional
    public void deleteUser(String userId, String loggedInUserEmail) {

        // 1. Fetch logged-in user safely
        User loggedInUser = userRepository.findByEmail(loggedInUserEmail)
                .orElseThrow(() -> new UserNotFoundException("Logged-in user not found"));

        // 2. Fetch the target user to delete
        User deleteUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User to be deleted not found"));

        // 3. Authorization check
        if (deleteUser.getEmail().equals(loggedInUserEmail) ||
                SUPER_ADMIN.name().equals(loggedInUser.getUserType().getName())) {
            try {
                deleteUser.setIsDeleted(true);
                userRepository.save(deleteUser);
            } catch (Exception e) {
                throw new UserNotSavedException("Error while deleting/disabling user");
            }
        } else {
            throw new EmptyCredentialsException("You are not allowed to delete this user");
        }
    }


    /**
     * Retrieves a paginated list of users based on search keyword and user role. Admins can only see all users, but
     * Super admin can see all users along with all admins.
     * <p>
     * Applies dynamic filtering and sorting using Spring Data JPA's Specification.
     *
     * @param loggedInUserEmail - ID of current Logged-in User
     * @param pageNumber     the page number to retrieve (zero-based index)
     * @param pageSize       the number of records per page
     * @param searchValue    the keyword for multi-field search
     * @param userRole       the user role to filter by (optional)
     * @return PaginationResponseDTO containing user list and total record count

     */
    @Override
    public WebResponseDTO<List<UserDTO>> getAllUsersList(String loggedInUserEmail,
                                                         Integer pageNumber,
                                                         Integer pageSize,
                                                         String searchValue,
                                                         EnumConstants.Role userRole) {
        UserDTO loggedInUser = getUserDetails(loggedInUserEmail);

        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "updatedAt"));
            Specification<User> userSpecification = getUserSpecification(searchValue, userRole, loggedInUser);

            Page<User> userPage = userRepository.findAll(userSpecification, pageable);

            List<UserDTO> userDTOList = userPage.getContent().stream()
                    .map(user -> {
                        UserRole userRoleEntity = user.getUserType();
                        UserRoleDTO userRoleDTO = userRoleEntity != null
                                ? UserRoleDTO.builder()
                                .id(userRoleEntity.getId())
                                .name(userRoleEntity.getName())
                                .build()
                                : null;

                        return UserDTO.builder()
                                .id(user.getId().toString())
                                .userName(user.getUserName())
                                .name(user.getName())
                                .mobileNumber(user.getMobileNumber())
                                .email(user.getEmail())
                                .profileImageUrl(user.getProfileImageUrl())
                                .emailVerified(user.getEmailVerified())
                                .phoneVerified(user.getPhoneVerified())
                                .approvalStatus(user.getApprovalStatus())
                                .isActive(user.getIsActive())
                                .isDeleted(user.getIsDeleted())
                                .isLoggedIn(user.getIsLoggedIn())
                                .adhaarNumber(user.getAdhaarNumber())
                                .createdAt(user.getCreatedAt())
                                .updatedAt(user.getUpdatedAt())
                                .userRole(userRoleDTO)
                                .build();
                    })
                    .collect(Collectors.toList());

            return WebResponseDTO.<List<UserDTO>>builder()
                    .flag(true)
                    .status(HttpStatus.OK.value())
                    .message("Users fetched successfully.")
                    .response(userDTOList)
                    .totalRecords(userPage.getTotalElements())
                    .build();

        } catch (Exception e) {
            throw new EmptyUserListException("Error while fetching Users List");
        }
    }


    /**
     * Builds user filtering criteria based on search input and user role.
     *
     * @param searchValue  Search keyword for filtering.
     * @param userRole     Role to filter (for Super Admin).
     * @param loggedInUser Logged-in user details for access control.
     * @return Specification for filtering users.
     *
     *
     */
    private static Specification<User> getUserSpecification(String searchValue, EnumConstants.Role userRole, UserDTO loggedInUser) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));

            Join<User, UserRole> userRoleJoin = root.join("userType", JoinType.LEFT);

            // Role-based filtration: If logged-in user is Admin, restrict to USER role only
            if (loggedInUser.getUserRole().getName().equals(EnumConstants.Role.ADMIN.name())) {
                predicates.add(criteriaBuilder.equal(userRoleJoin.get("name"), EnumConstants.Role.USER.name()));
            }

            if (searchValue != null && !searchValue.trim().isEmpty()) {
                String searchKeyword = "%" + searchValue.trim().toLowerCase() + "%";

                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("mobileNumber")), searchKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchKeyword),
                        criteriaBuilder.like(criteriaBuilder.upper(userRoleJoin.get("name")), "%" + searchValue.trim().toUpperCase() + "%")
                ));
            }

            // Apply user role filter if provided and logged-in user is Super Admin
            if (userRole != null && loggedInUser.getUserRole().getName().equals(SUPER_ADMIN.name())) {
                predicates.add(criteriaBuilder.equal(userRoleJoin.get("name"), userRole.name()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }




    @Override
    @Transactional
    public void setPassword(String newPassword, String loggedInUserEmail) {
        try {
            User user = userRepository.findByEmail(loggedInUserEmail)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            if (!user.getHasPassword()) {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setHasPassword(true);
                user.setUpdatedAt(LocalDateTime.now());

                userRepository.save(user);
            } else {
                throw new IllegalStateException("Password already set. Use change-password instead.");
            }
        } catch (UserNotFoundException | IllegalStateException ex) {
            throw ex; // re-throw to be handled in controller or global handler
        } catch (Exception ex) {
            throw new RuntimeException("Unable to set password at this time");
        }
    }








    @Override
    public LogoutResponseDTO logoutFromCurrentDevice(UserDetails userDetails, LogoutRequestDTO dto) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        refreshTokenService.logoutFromDevice(user, dto.getDeviceId());
        return LogoutResponseDTO.builder()
                .flag(true)
                .message("Successfully logged out from current device")
                .build();
    }

    @Override
    public LogoutResponseDTO logoutFromAllDevices(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        refreshTokenService.logoutFromAllDevices(user);
        return LogoutResponseDTO.builder()
                .flag(true)
                .message("Logged out from all devices")
                .build();
    }

    @Override
    public MySessionsResponseDTO getMySessions(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return refreshTokenService.getUserSessions(user);
    }



}
