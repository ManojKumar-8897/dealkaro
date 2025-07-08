package com.digiquad.dealkaro.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.digiquad.dealkaro.entity.User;

public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    Optional<User> findByUserName(String inputKey);

    Optional<User> findByMobileNumber(String mobileNumber);

    Optional<User> findByEmail(String emailId);

    boolean existsByUserName(String superAdmin);

    @Query("SELECT u FROM User u JOIN FETCH u.userType WHERE u.id = :id")
    Optional<User> findByIdWithRole(@Param("id") UUID id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userType WHERE u.email = :email")
    Optional<User> findByEmailWithUserType(@Param("email") String email);



    @Modifying
    @Query("UPDATE User u SET u.isDeleted = true WHERE u.id = :adminId")
    void deleteUser(@Param("adminId") String adminId);

}

