package com.devkit.hsd.mapper;


import com.devkit.hsd.dto.request.AddSkillRequest;
import com.devkit.hsd.dto.request.AddSocialLinkRequest;
import com.devkit.hsd.dto.request.RegisterRequest;
import com.devkit.hsd.dto.request.UpdateUserRequest;
import com.devkit.hsd.dto.response.CommitteeMemberResponse;
import com.devkit.hsd.dto.response.UserResponse;
import com.devkit.hsd.entity.Skill;
import com.devkit.hsd.entity.SocialLink;
import com.devkit.hsd.entity.User;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "community", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "socialLinks", ignore = true)
    @Mapping(target = "committees", ignore = true)
    User toUser(RegisterRequest registerRequest);

    @Mapping(target = "communityId", source = "community.id")
    UserResponse toUserResponse(User user);

    CommitteeMemberResponse toCommitteeMemberResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromRequest(UpdateUserRequest request, @MappingTarget User user);

    Skill toSkill(AddSkillRequest addSkillRequest);

    SocialLink toSocialLink(AddSocialLinkRequest addSocialLinkRequest);

}
