package com.capstone.project.chronos.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import com.capstone.project.chronos.auth.entity.User;
import com.capstone.project.chronos.auth.model.LoginDto;
import com.capstone.project.chronos.auth.model.UserModel;
import com.capstone.project.chronos.auth.exception.TokenExpiredException;
import com.capstone.project.chronos.auth.model.UserProfile;


public interface UserService {
  User registerUser(UserModel userModel, HttpServletRequest request) throws TokenExpiredException;

  User autheticateUser(LoginDto loginDto) throws TokenExpiredException;

  void createVerificationToken(User user, String token) throws TokenExpiredException;

  boolean validateTokenAndEnableUser(String token) throws TokenExpiredException;

  User getUserById(Long userId) throws TokenExpiredException ;

  UserProfile getUserProfile(String email);

   String updateUserProfile(UserModel updatedUser);

  User getCurrentUser() throws Exception;

  User getUserByEmail(String assignedTo);
}
