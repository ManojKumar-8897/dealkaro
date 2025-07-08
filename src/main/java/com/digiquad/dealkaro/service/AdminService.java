package com.digiquad.dealkaro.service;


import com.digiquad.dealkaro.model.AdminModel;
import com.digiquad.dealkaro.model.DTO.AdminDTO;

public interface AdminService {
    void createAdmin(AdminModel adminDetails);

    AdminDTO getAdminDetails(String adminId);

    void updateAdminDetails(AdminModel adminDetails);

    void deleteAdminDetails(String adminId, String loggedInUserId);
}
