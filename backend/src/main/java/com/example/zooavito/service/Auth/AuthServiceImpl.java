package com.example.zooavito.service.Auth;

import com.example.zooavito.Exception.BusinessErrorType;
import com.example.zooavito.Exception.BusinessException;
import com.example.zooavito.service.Recaptcha.RecaptchaService;
import com.example.zooavito.service.Security.JwtTokenProvider;
import com.example.zooavito.model.User;
import com.example.zooavito.request.AuthRequest;
import com.example.zooavito.response.AuthResponse;
import com.example.zooavito.response.UserRegistrationResponse;
import com.example.zooavito.service.User.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final RecaptchaService recaptchaService;

    @Override
    public AuthResponse authenticateUser(AuthRequest authRequest) {
        try {
            String token = authRequest.getRecaptchaToken();
            if (token == null || token.isEmpty()) {
                log.warn("reCAPTCHA token отсутствует для email: {}", authRequest.getEmail());
                throw new BusinessException(
                        BusinessErrorType.RECAPTCHA_MISSING);
            }

            boolean isValid = recaptchaService.verifyToken(token);
            if (!isValid) {
                log.warn("reCAPTCHA verification failed for email: {}", authRequest.getEmail());
                throw new BusinessException(
                        BusinessErrorType.RECAPTCHA_FAILED);
            }

            User user = userService.findByEmail(authRequest.getEmail());

            if (user == null) {
                throw new BadCredentialsException("Неверный email или пароль");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );

            if (!user.isEnabled()) {
                log.warn("Попытка входа заблокированного пользователя: {}", authRequest.getEmail());
                throw new BusinessException(BusinessErrorType.ACCOUNT_BLOCKED);
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtTokenProvider.generateToken(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User authenticatedUser = userService.findByEmail(userDetails.getUsername());

            UserRegistrationResponse userResponse = userService.buildUserResponse(authenticatedUser);

            return AuthResponse.builder()
                    .token(jwt)
                    .user(userResponse)
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("Неудачная попытка входа для email: {}", authRequest.getEmail());
            throw new BadCredentialsException("Неверный email или пароль");
        }
    }
}