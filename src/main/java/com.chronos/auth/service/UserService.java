package com.chronos.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import com.chronos.auth.entity.User;
import com.chronos.auth.model.LoginDto;
import com.chronos.auth.model.UserModel;
import com.chronos.auth.exception.TokenExpiredException;
import com.chronos.auth.model.UserProfile;

import java.net.http.HttpRequest;


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
