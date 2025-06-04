package com.example.emergencyassistb4b4.auth.service;

import com.example.emergencyassistb4b4.auth.security.CustomUserDetails;
import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.global.status.ErrorStatus;
import com.example.emergencyassistb4b4.user.domain.LoginType;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorStatus.USER_NOT_FOUND));

        // local인데 비밀번호가 null이면 예외
        if (user.getLoginType() == LoginType.LOCAL && user.getPassword() == null) {
            throw new ApiException(ErrorStatus.INVALID_PASSWORD);
        }
        return new CustomUserDetails(user);
    }
}
