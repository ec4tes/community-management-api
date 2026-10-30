package com.devkit.hsd.service.impl;

import com.devkit.hsd.common.enums.ErrorCode;
import com.devkit.hsd.common.enums.Role;
import com.devkit.hsd.common.enums.SystemRole;
import com.devkit.hsd.dto.request.AddSkillRequest;
import com.devkit.hsd.dto.request.AddSocialLinkRequest;
import com.devkit.hsd.dto.request.ChangePasswordRequest;
import com.devkit.hsd.dto.request.UpdateUserRequest;
import com.devkit.hsd.dto.response.UserResponse;
import com.devkit.hsd.entity.Skill;
import com.devkit.hsd.entity.SocialLink;
import com.devkit.hsd.entity.User;
import com.devkit.hsd.exception.*;
import com.devkit.hsd.mapper.UserMapper;
import com.devkit.hsd.repository.SkillRepository;
import com.devkit.hsd.repository.SocialLinkRepository;
import com.devkit.hsd.repository.UserRepository;
import com.devkit.hsd.service.abs.IAuthService;
import com.devkit.hsd.service.abs.IUserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {


   

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final IAuthService authService;

    private final SkillRepository skillRepository;

    private final SocialLinkRepository socialLinkRepository;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, IAuthService authService, SkillRepository skillRepository, SocialLinkRepository socialLinkRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
        this.skillRepository = skillRepository;
        this.socialLinkRepository = socialLinkRepository;
    }

    private boolean isSuperAdmin(User user) {
        return user.getSystemRole() == SystemRole.SUPER_ADMIN;
    }

    private User findAccessibleUser(Long userId, User authenticatedUser) {
        if (isSuperAdmin(authenticatedUser)) {
            return userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "Kullanıcı bulunamadı!"));
        }

        return userRepository.findByIdAndCommunityId(userId, authenticatedUser.getCommunity().getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "Kullanıcı bulunamadı!"));
    }

    private User getAuthorizedUser() {

        User user = authService.getAuthenticatedUser();

        if (isSuperAdmin(user) || user.getSystemRole() == SystemRole.COMMUNITY_ADMIN) {
            return user;
        }

        if (!(user.getRole().equals(Role.BASKAN)
                || user.getRole().equals(Role.BASKAN_YARDIMCISI)
                || user.getRole().equals(Role.KOMITE_LIDERI)
                || user.getRole().equals(Role.KOMITE_YARDIMCISI))) {

            throw new ForbiddenException(
                    ErrorCode.ACCESS_DENIED,
                    "Bu işlem için yetkiniz bulunmamaktadır."
            );
        }

        return user;
    }

    @Override
    public UserResponse getUserById(Long id) {

        User authenticatedUser = authService.getAuthenticatedUser();
        User user = findAccessibleUser(id, authenticatedUser);

        return userMapper.toUserResponse(user);
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "Kullanıcı bulunamadı!"));
    }


    @Override
    public UserResponse getMe() {

        User usernameByAuth = getAuthenticatedUser();

        Optional<User> user = userRepository.findByUsername(usernameByAuth.getUsername());
        return user.map(userMapper::toUserResponse).orElse(null);


    }

    @Override
    public List<UserResponse> getAllUsers() {
        User authenticatedUser = authService.getAuthenticatedUser();
        List<User> usersList = isSuperAdmin(authenticatedUser)
                ? userRepository.findAll()
                : userRepository.findByCommunityId(authenticatedUser.getCommunity().getId());

        return usersList.stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    private void changePasswordValidation(ChangePasswordRequest changePasswordRequest, User authenticatedUser) {

        String currentPassword = changePasswordRequest.getCurrentPassword();
        String newPassword = changePasswordRequest.getNewPassword();

        if (!passwordEncoder.matches(currentPassword, authenticatedUser.getPassword())) {
            throw new UnauthorizedException(ErrorCode.BAD_CREDENTIALS, "Mevcut şifre hatalı");
        }

        if (newPassword.length() < 5) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "Şifre 5 karakterden düşük olamaz");
        }

        if (passwordEncoder.matches(newPassword, authenticatedUser.getPassword())) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "Yeni şifre eski şifreyle aynı olamaz");
        }
    }

    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest) {

        User authenticatedUser = getAuthenticatedUser();

        changePasswordValidation(changePasswordRequest, authenticatedUser);

        String encodedPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
        authenticatedUser.setPassword(encodedPassword);
        userRepository.save(authenticatedUser);
    }


    private boolean isRequestEmpty(UpdateUserRequest request) {
        return request.getUsername() == null
                && request.getEmail() == null
                && request.getFirstName() == null
                && request.getLastName() == null
                && request.getProfileImage() == null
                && request.getDepartment() == null
                && request.getGrade() == null;
    }

    private void validateUpdateUserRequest(UpdateUserRequest request) {
        if (request == null || isRequestEmpty(request)) {
            throw new BadRequestException(
                    ErrorCode.INVALID_REQUEST, "Güncellenecek en az bir alan gönderilmelidir"
            );

        }

        validateRequiredStringIfPresent(request.getUsername(), "Username");
        validateRequiredStringIfPresent(request.getEmail(), "Email");
        validateRequiredStringIfPresent(request.getFirstName(), "Ad");
        validateRequiredStringIfPresent(request.getLastName(), "Soyad");
        validateRequiredStringIfPresent(request.getDepartment(), "Bölüm");
        validateGradeIfPresent(request.getGrade());

    }

    private void validateGradeIfPresent(Integer grade) {
        if (grade != null && (grade < 1 || grade > 6)) {
            throw new BadRequestException(
                    ErrorCode.INVALID_REQUEST,
                    "Sınıf 1 ile 6 arasında olmalıdır"
            );
        }
    }

    private void validateRequiredStringIfPresent(String value, String fieldName) {
        if (value != null && value.trim().isEmpty()) {
            throw new BadRequestException(
                    ErrorCode.INVALID_REQUEST,
                    fieldName + " boş olamaz"
            );
        }
    }

    private void validateUsernameUniqueness(String username, Long currentUserId,Long communityId) {
        if (username != null && userRepository.existsByUsernameAndCommunityIdAndIdNot(username, communityId, currentUserId)) {
            throw new ConflictException(
                    ErrorCode.USERNAME_ALREADY_EXISTS,
                    "Bu username zaten kullanılıyor"
            );
        }
    }

    private void validateEmailUniqueness(String email, Long currentUserId) {
        if (email != null && userRepository.existsByEmailAndIdNot(email, currentUserId)) {
            throw new ConflictException(
                    ErrorCode.EMAIL_ALREADY_EXISTS,
                    "Bu email zaten kullanılıyor"
            );
        }
    }

    @Override
    public UserResponse updateUserProfile(UpdateUserRequest request) {
        User user = authService.getAuthenticatedUser();
        validateUpdateUserRequest(request);
        validateUsernameUniqueness(request.getUsername(), user.getId(),user.getCommunity().getId());
        validateEmailUniqueness(request.getEmail(), user.getId());
        userMapper.updateUserFromRequest(request, user);
        User savedUser = userRepository.save(user);
        return userMapper.toUserResponse(savedUser);
    }

    @Override
    public void deactivateMyAccount() {
        User user = authService.getAuthenticatedUser();

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST,"Hesabınız zaten devre dışı bırakılmış!");
        }

        user.setIsActive(false);
        user.setIsOnline(false);
        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void deactivateUser(Long userId) {
        User currentUser = getAuthorizedUser();
        User user = findAccessibleUser(userId, currentUser);

        user.setIsActive(false);
        user.setIsOnline(false);
        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void activateUser(Long userId) {
        User currentUser = getAuthorizedUser();
        User user = findAccessibleUser(userId, currentUser);

        user.setIsActive(true);
        userRepository.save(user);
    }

    @Override
    public void addSkill(AddSkillRequest addSkillRequest) {
        User user = authService.getAuthenticatedUser();
        Skill skill = userMapper.toSkill(addSkillRequest);
        skill.setUser(user);
        skillRepository.save(skill);
    }

    @Override
    public void deleteSkill(Long skillId) {
        User user = authService.getAuthenticatedUser();
        Skill skill = skillRepository.findById(skillId).orElseThrow(() -> new NotFoundException(ErrorCode.SKILL_NOT_FOUND,"Yetenek bulunamadı!"));

        if (!skill.getUser().getCommunity().getId().equals(user.getCommunity().getId()) ||
                !skill.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException(ErrorCode.ACCESS_DENIED,"Bu yeteneği silmek için yetkiniz yok!");
        }

        skillRepository.delete(skill);
    }

    @Override
    public void addSocialLink(AddSocialLinkRequest addSocialLinkRequest) {
        User user = authService.getAuthenticatedUser();
        SocialLink socialLink = userMapper.toSocialLink(addSocialLinkRequest);
        socialLink.setUser(user);
        socialLinkRepository.save(socialLink);
    }

    @Override
    public void deleteSocialLink(Long socialLinkId) {
        User user = authService.getAuthenticatedUser();
        SocialLink socialLink = socialLinkRepository.findById(socialLinkId).orElseThrow(() -> new NotFoundException(ErrorCode.SOCIAL_LINK_NOT_FOUND,"Sosyal bağlantı bulunamadı!"));

        if (!socialLink.getUser().getCommunity().getId().equals(user.getCommunity().getId()) ||
                !socialLink.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException(ErrorCode.ACCESS_DENIED,"Bu sosyal bağlantıyı silmek için yetkiniz yok!");
        }

        socialLinkRepository.delete(socialLink);
    }

    



        

}
