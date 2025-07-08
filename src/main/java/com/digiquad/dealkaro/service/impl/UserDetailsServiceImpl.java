package com.digiquad.dealkaro.service.impl;

import java.util.Collections;

import com.digiquad.dealkaro.exceptions.customExceptions.auth.InvalidLoginException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.digiquad.dealkaro.entity.User;
import com.digiquad.dealkaro.repository.UserRepository;

@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {

@Autowired
private  UserRepository userRepo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String inputKey) throws UsernameNotFoundException {
    	
    	System.err.println("loadUserByUsername.............................");
    User user = userRepo.findByUserName(inputKey)
        .or(() -> userRepo.findByMobileNumber(inputKey))
        .or(() -> userRepo.findByEmail(inputKey))
        .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + inputKey));
    String roleName = user.getUserType().getName();

        return buildUserDetails(user);
    }

    @Transactional
    private UserDetails buildUserDetails(User user) {
        if (Boolean.FALSE.equals(user.getApprovalStatus())) {
            throw new InvalidLoginException("User is not approved");
        }
        String userRole = userRepo.findByIdWithRole(user.getId())
                .orElseThrow(() -> new RuntimeException("User role empty or null in : " + user.getId()))
                .getUserType().getName();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + userRole);
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), Collections.singleton(authority));
    }

}
