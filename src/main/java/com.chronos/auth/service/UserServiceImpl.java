package com.chronos.auth.service;


import com.chronos.auth.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import com.chronos.auth.entity.User;
import com.chronos.auth.entity.VerificationToken;
import com.chronos.auth.exception.TokenExpiredException;
import com.chronos.auth.model.UserProfile;
import com.chronos.auth.repository.UserRepository;
import com.chronos.auth.model.LoginDto;
import com.chronos.auth.model.UserModel;
import com.chronos.auth.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository _userRepository;

    @Autowired
    private VerificationTokenRepository _verificationTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * This method registers a new user
     *
     * @param userModel
     * @param request
     * @return
     * @throws TokenExpiredException
     */
    @Override
    public User registerUser(UserModel userModel, HttpServletRequest request) throws TokenExpiredException {
        try {
            User user = User.builder()
                    .password(passwordEncoder.encode(userModel.getPassword()))
                    .email(userModel.getEmail()).firstName(userModel.getFirstName())
                    .lastName(userModel.getLastName()).isEnabled(false).build();
            if (user != null) {
                _userRepository.save(user);
                String token = UUID.randomUUID().toString();
                String applicationUrl = getApplicationUrl(request) + "/api/user/verifyRegistration?token=" + token;
                this.createVerificationToken(user, token);
                this.sendHtmlVerificationEmail(user.getEmail(), applicationUrl);
            }
            return user;
        } catch (Exception e) {
            throw new TokenExpiredException("Unable to create user" + e.getMessage());
        }
    }

    /**
     * @param request
     * @return
     */
    private String getApplicationUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    /**
     * @param loginDto
     * @return
     * @throws TokenExpiredException
     */
    @Override
    public User autheticateUser(LoginDto loginDto) throws TokenExpiredException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getEmail(),
                            loginDto.getPassword()
                    )
            );
            log.info("User authenticated successfully: " + loginDto.getEmail());
            return _userRepository.findByEmail(loginDto.getEmail()).get();
        } catch (Exception e) {
            throw new TokenExpiredException("Unable to authenticate user" + e.getMessage());
        }
    }

    /**
     * @param user
     * @param token
     * @throws TokenExpiredException
     */
    @Override
    public void createVerificationToken(User user, String token) throws TokenExpiredException {
        try {
            if (token != null && user != null) {
                _verificationTokenRepository.save(new VerificationToken(token, user));
                log.info("Token created for user: " + user.getEmail());
            }
        } catch (Exception e) {
            throw new TokenExpiredException("Unable to create verification token" + e.getMessage());
        }
    }

    /**
     * @param token
     * @return
     * @throws TokenExpiredException
     */
    @Override
    public boolean validateTokenAndEnableUser(String token) throws TokenExpiredException {
        try {
            VerificationToken verificationToken = _verificationTokenRepository.findByToken(token);
            if (verificationToken == null) {
                return false;
            }
            if (verificationToken.getExpirationTime().getTime() > System.currentTimeMillis()) {
                User user = verificationToken.getUser();
                if (!user.isEnabled()) {
                    user.setEnabled(true);
                    _userRepository.save(user);
                    _verificationTokenRepository.delete(verificationToken);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new TokenExpiredException("Unable to validate token" + e.getMessage());
        }
    }

    @Override
    public User getUserById(Long userId) throws TokenExpiredException {
        Optional<User> user = _userRepository.findById(userId);
        return user.orElseGet(User::new);
    }

    /**
     * @param verificationUrl
     * @return
     */
    private String buildHtmlEmailTemplate(String verificationUrl) {
        return "<html>" +
                "<body>" +
                "<h2>Welcome to Our Service!</h2>" +
                "<p>Thank you for registering with us. Please click the link below to verify your email address and activate your account:</p>" +
                "<a href=\"" + verificationUrl + "\">Verify my email</a>" +
                "<p>If the button above doesn't work, you can copy and paste the following link into your browser:</p>" +
                "<p>" + verificationUrl + "</p>" +
                "<br>" +
                "<p>This link will expire in 24 hours. If you did not create this account, please ignore this email.</p>" +
                "<br>" +
                "<p>Best regards,<br>Chronos Team</p>" +
                "</body>" +
                "</html>";
    }

    /**
     * @param toEmail
     * @param verificationUrl
     * @throws MessagingException
     */
    public void sendHtmlVerificationEmail(String toEmail, String verificationUrl) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(toEmail);
        helper.setSubject("Account Verification - Please confirm your email");

        String htmlContent = buildHtmlEmailTemplate(verificationUrl);
        helper.setText(htmlContent, true);
        mailSender.send(mimeMessage);
        log.info("HTML Verification email sent to: {}", toEmail);
    }

    /**
     * @param email
     * @return
     */
    public UserProfile getUserProfile(String email) {
        Optional<User> user = _userRepository.findByEmail(email);
        if (user.isPresent()) {
            UserProfile userProfile = new UserProfile();
            userProfile.setName(user.get().getFirstName() + " " + user.get().getLastName());
            userProfile.setEmail(user.get().getEmail());
            return userProfile;
        } else {
            return new UserProfile();
        }
    }

    /**
     * @param updatedUser
     * @return
     */
    public String updateUserProfile(UserModel updatedUser) {
        User user = _userRepository.findByEmail(updatedUser.getEmail()).orElseGet(User::new);
        if (user.getEmail() == null) {
            return "User not found";
        } else {
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setEmail(updatedUser.getEmail());
            _userRepository.save(user);
            return "User profile updated successfully";
        }
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public User getCurrentUser() throws Exception {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        try {
            return getUser(username);
        } catch (Exception e) {
            final String error = "Current logged-in user details not fetched. " + e.getMessage();
            log.error(error);
            throw new Exception(error);
        }
    }

    @Override
    public User getUserByEmail(String assignedTo) {
        return _userRepository.findByEmail(assignedTo).orElse(null);
    }

    /**
     *
     * @param email
     * @return
     * @throws Exception
     */
    public User getUser(String email) throws Exception {
        try {
            Optional<User> user = _userRepository.findByEmail(email);
            if (user.isPresent()) return user.get();
        } catch (Exception exception) {
            log.error("Error with user  with error ", exception);
            throw new Exception(exception.getMessage());
        }
        return null;
    }
}
