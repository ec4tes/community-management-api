package com.devkit.hsd.service.abs;

import com.devkit.hsd.dto.request.AddSkillRequest;
import com.devkit.hsd.dto.request.AddSocialLinkRequest;
import com.devkit.hsd.dto.request.ChangePasswordRequest;
import com.devkit.hsd.dto.request.UpdateUserRequest;
import com.devkit.hsd.dto.response.UserResponse;


import java.util.List;

public interface IUserService {

    public UserResponse getUserById(Long id);

    public UserResponse getMe();

    public List<UserResponse> getAllUsers();

    public void changePassword(ChangePasswordRequest changePasswordRequest);

    public UserResponse updateUserProfile(UpdateUserRequest updateUserRequest);

    public void deactivateMyAccount();

    public void deactivateUser(Long userId);

    public void activateUser(Long userId);

    public void addSkill(AddSkillRequest addSkillRequest);

    public void deleteSkill(Long skillId);

    public void addSocialLink(AddSocialLinkRequest addSocialLinkRequest);

    public void deleteSocialLink(Long socialLinkId);

    


}

