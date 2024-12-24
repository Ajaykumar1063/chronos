package com.capstone.project.chronos.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import com.capstone.project.chronos.auth.entity.User;
import com.capstone.project.chronos.auth.model.UserProfile;
import com.capstone.project.chronos.auth.service.JwtService;
import com.capstone.project.chronos.auth.exception.TokenExpiredException;
import com.capstone.project.chronos.auth.model.LoginDto;
import com.capstone.project.chronos.auth.model.LoginResponse;
import com.capstone.project.chronos.auth.model.UserModel;
import com.capstone.project.chronos.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/user")
public class RegistrationController {

    @Autowired
    private UserService _userService;

    @Autowired
    private JwtService _jwtService;

    /**
     *
     * @param user
     * @param request
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserModel user, HttpServletRequest request) {
        try {
            User userEntity = _userService.registerUser(user, request);
            return ResponseEntity.ok(userEntity);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to register user: " + e.getMessage());
        }
    }

    /**
     *
     * @param token
     * @return
     * @throws TokenExpiredException
     */
    @GetMapping("/verifyRegistration")
    public ResponseEntity<String> verifyRegistration(@RequestParam String token) throws TokenExpiredException {
        try {
            boolean isValid = _userService.validateTokenAndEnableUser(token);
            if (!isValid) {
                return ResponseEntity.badRequest().body("Invalid token");
            }
            return ResponseEntity.internalServerError().body("User enabled successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to verify user: " + e.getMessage());
        }
    }

    /**
     *
     * @param loginUserDto
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginDto loginUserDto) {
        try {
            User authenticatedUser = _userService.autheticateUser(loginUserDto);

            String jwtToken = _jwtService.generateToken(authenticatedUser);

            LoginResponse loginResponse = new LoginResponse(jwtToken, _jwtService.getExpirationTime());

            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to login user: " + e.getMessage());
        }
    }

    /**
     *
     * @param email
     * @return
     */
    @GetMapping("user-profile")
    public ResponseEntity<UserProfile> getProfile(@RequestParam String email) {
        try {
            return ResponseEntity.ok().body(_userService.getUserProfile(email));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(null);
        }
    }

    /**
     *
     * @param updatedUser
     * @return
     */
    @PutMapping("/update-user-profile")
    public String updateProfile(@RequestBody UserModel updatedUser) {
        return _userService.updateUserProfile(updatedUser);
    }
}
