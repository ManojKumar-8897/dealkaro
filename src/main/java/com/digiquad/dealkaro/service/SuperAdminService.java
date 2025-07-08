package com.digiquad.dealkaro.service;

import com.digiquad.dealkaro.model.DTO.UserDTO;
import com.digiquad.dealkaro.model.UserModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface SuperAdminService {
    public UserDTO getSuperAdminDetails();
    public String updateSuperAdminDetailsWithImage(String loggedInUserId, MultipartFile file) throws IOException;
    public String updateSuperAdminAllDetails(String loggedInUserId, MultipartFile file, UserModel model) throws IOException;
}
