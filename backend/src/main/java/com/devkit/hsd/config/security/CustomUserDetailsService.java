package com.devkit.hsd.config.security;

import com.devkit.hsd.common.enums.ErrorCode;
import com.devkit.hsd.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {


        return userRepository.findByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException("Bu isimle bir kullanıcı bulunamadı:" + username));
    }
}
