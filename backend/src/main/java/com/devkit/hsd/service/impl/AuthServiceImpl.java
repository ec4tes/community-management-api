package com.devkit.hsd.service.impl;

import com.devkit.hsd.common.enums.ErrorCode;
import com.devkit.hsd.config.security.JwtService;
import com.devkit.hsd.dto.request.LoginRequest;
import com.devkit.hsd.dto.request.RefreshTokenRequest;
import com.devkit.hsd.dto.request.RegisterRequest;
import com.devkit.hsd.dto.response.AuthResponse;
import com.devkit.hsd.entity.Community;
import com.devkit.hsd.entity.RefreshToken;
import com.devkit.hsd.entity.User;
import com.devkit.hsd.exception.ConflictException;
import com.devkit.hsd.exception.NotFoundException;
import com.devkit.hsd.exception.UnauthorizedException;
import com.devkit.hsd.mapper.UserMapper;
import com.devkit.hsd.repository.CommunityRepository;
import com.devkit.hsd.repository.RefreshTokenRepository;
import com.devkit.hsd.repository.UserRepository;
import com.devkit.hsd.service.abs.IAuthService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final CommunityRepository communityRepository;

    private final UserMapper userMapper;

    private final AuthenticationManager authenticationManager;


    public AuthServiceImpl(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder, JwtService jwtService, CommunityRepository communityRepository, UserMapper userMapper, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.communityRepository = communityRepository;
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        checkIfUserExists(registerRequest);
        Community community = checkCommunityIfExists(registerRequest);
        User user = buildUserFromRequest(registerRequest);
        user.setCommunity(community);
        User savedUser = userRepository.save(user);
        return buildRegisterResponse(savedUser);

    }

    @Value("${app.jwt.refresh-expiration-ms}")
    private Long refreshExpirationMs;

    private RefreshToken createRefreshToken(User user){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setCreatedAt(LocalDateTime.now());
        refreshToken.setExpiredDate(LocalDateTime.now().plus(Duration.ofMillis(refreshExpirationMs)));
        refreshToken.setRefreshToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);

        return refreshTokenRepository.save(refreshToken);
    }

    private AuthResponse createAuthResponse(String accessToken, String refreshToken, User user){
        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getCommunity() != null ? user.getCommunity().getId() : null,
                user.getSystemRole(),
                user.getRole()
        );
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {

           authenticationManager.authenticate(
                   new UsernamePasswordAuthenticationToken(
                           loginRequest.getUsername(),
                           loginRequest.getPassword()
                   )
           );
            User user = userRepository.findByUsername(loginRequest.getUsername()).
                    orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND,"Kullanıcı bulunamadı!"));

            String accessToken = jwtService.generateAccessToken(user);
            RefreshToken refreshToken = createRefreshToken(user);

            return createAuthResponse(accessToken, refreshToken.getRefreshToken() ,user);

    }

    @Override
    public User getAuthenticatedUser() {
        Authentication authUser =
                SecurityContextHolder.getContext().getAuthentication();
        String authenticationName = authUser.getName();

        User user = userRepository.findByUsername(authenticationName)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "Bu kullanıcı ismine ait biri bulunamadı!"));

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new UnauthorizedException(ErrorCode.BAD_CREDENTIALS, "Bu kullanıcı hesabı devre dışı bırakılmıştır.");
        }

         return user;
            
    }
        
    

    private boolean isValidRefreshToken(LocalDateTime expiredDate){
        return LocalDateTime.now().isBefore(expiredDate);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByRefreshToken(refreshTokenRequest.getRefresh());
        if (optionalRefreshToken.isEmpty()){
            throw new NotFoundException(ErrorCode.REFRESH_TOKEN_NOT_FOUND, "Refresh Token bulunamadı!");
        }
        if (!isValidRefreshToken(optionalRefreshToken.get().getExpiredDate())){
            throw new UnauthorizedException(ErrorCode.REFRESH_TOKEN_EXPIRED,"Tokenın süresi dolmuş!");
        }


        User user = optionalRefreshToken.get().getUser();

        if(Boolean.FALSE.equals(user.getIsActive())){
            throw new UnauthorizedException(ErrorCode.USER_NOT_ACTIVE,"Bu kullanıcı hesabı devre dışı bırakılmıştır.");
        }


        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken savedRefreshToken = refreshTokenRepository.save(createRefreshToken(user));

        return createAuthResponse(accessToken, savedRefreshToken.getRefreshToken() ,user);
    }

    private void checkIfUserExists(RegisterRequest registerRequest){
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ConflictException(ErrorCode.EMAIL_ALREADY_EXISTS,"Bu email ile kayıtlı bir kullanıcı zaten var.");
        }



        if (userRepository.existsByUsernameAndCommunityId(registerRequest.getUsername(), registerRequest.getCommunityId())) {
            throw new ConflictException(ErrorCode.USERNAME_ALREADY_EXISTS,"Bu kullanıcı adına sahip bir kullanıcı zaten var.");
        }
    }

    private Community checkCommunityIfExists(RegisterRequest registerRequest){
        return communityRepository.findById(registerRequest.getCommunityId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND,"Boyle bir topluluk bulunamadi!"));
    }

    private User buildUserFromRequest(RegisterRequest registerRequest){
        User user = userMapper.toUser(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        return user;
    }



    private AuthResponse buildRegisterResponse(User savedUser){
        String accessToken = jwtService.generateAccessToken(savedUser);
        RefreshToken refreshToken = createRefreshToken(savedUser);
        return createAuthResponse(accessToken, refreshToken.getRefreshToken() ,savedUser);
    }




}
