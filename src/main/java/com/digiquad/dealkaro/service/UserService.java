package com.digiquad.dealkaro.service;

import com.digiquad.dealkaro.constants.EnumConstants;
import com.digiquad.dealkaro.model.DTO.*;
import com.digiquad.dealkaro.model.UserModel;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    void registerUser(UserRegisterRequestDTO user) ;

    void approveUser(String userId, String approverId);

    UserDTO getUserDetails(String userId);

    void updateUser(UserModel user, String loggedInUserId);

    void deleteUser(String userId, String loggedInUserId);

    <T> T getAllUsersList(String loggedInUserId, Integer pageNumber, Integer pageSize, String searchValue,
            EnumConstants.Role userRole);
    void setPassword(String newPassword, String loggedInUserId);



    LogoutResponseDTO logoutFromCurrentDevice(UserDetails userDetails, LogoutRequestDTO dto);
    LogoutResponseDTO logoutFromAllDevices(UserDetails userDetails);
    MySessionsResponseDTO getMySessions(UserDetails userDetails);
}
