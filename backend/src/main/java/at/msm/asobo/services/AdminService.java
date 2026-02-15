package at.msm.asobo.services;

import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.dto.comment.UserCommentWithEventTitleDTO;
import at.msm.asobo.dto.filter.MediumFilterDTO;
import at.msm.asobo.dto.filter.UserCommentFilterDTO;
import at.msm.asobo.dto.filter.UserFilterDTO;
import at.msm.asobo.dto.medium.MediumWithEventTitleDTO;
import at.msm.asobo.dto.user.UserAdminSummaryDTO;
import at.msm.asobo.dto.user.UserFullDTO;
import at.msm.asobo.entities.Medium;
import at.msm.asobo.entities.User;
import at.msm.asobo.entities.UserComment;
import at.msm.asobo.mappers.*;
import at.msm.asobo.repositories.MediumRepository;
import at.msm.asobo.repositories.UserCommentRepository;
import at.msm.asobo.repositories.UserRepository;
import at.msm.asobo.specifications.MediumSpecification;
import at.msm.asobo.specifications.UserCommentSpecification;
import at.msm.asobo.specifications.UserSpecification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
  private final UserRepository userRepository;
  private final UserCommentRepository userCommentRepository;
  private final MediumRepository mediumRepository;
  private final UserDTOUserMapper userDTOUserMapper;
  private final UserCommentDTOUserCommentMapper userCommentDTOUserCommentMapper;
  private final UserCommentToUserCommentWithEventTitleDTOMapper
      userCommentToUserCommentWithEventTitleDTOMapper;
  private final MediumToMediumWithEventTitleDTOMapper mediumToMediumWithEventTitleDTOMapper;

  public AdminService(
      UserRepository userRepository,
      UserCommentRepository userCommentRepository,
      MediumRepository mediumRepository,
      UserDTOUserMapper userDTOUserMapper,
      UserCommentDTOUserCommentMapper userCommentDTOUserCommentMapper,
      UserCommentToUserCommentWithEventTitleDTOMapper
          userCommentToUserCommentWithEventTitleDTOMapper,
      MediumToMediumWithEventTitleDTOMapper mediumToMediumWithEventTitleDTOMapper) {
    this.userRepository = userRepository;
    this.userCommentRepository = userCommentRepository;
    this.mediumRepository = mediumRepository;
    this.userDTOUserMapper = userDTOUserMapper;
    this.userCommentDTOUserCommentMapper = userCommentDTOUserCommentMapper;
    this.userCommentToUserCommentWithEventTitleDTOMapper =
        userCommentToUserCommentWithEventTitleDTOMapper;
    this.mediumToMediumWithEventTitleDTOMapper = mediumToMediumWithEventTitleDTOMapper;
  }

  public Page<UserAdminSummaryDTO> getAllUsersPaginated(
      UserFilterDTO filterDTO, Pageable pageable) {

    Page<User> users =
        this.userRepository.findAll(UserSpecification.withFilters(filterDTO), pageable);
    return this.userDTOUserMapper.mapUsersToAdminSummaryDTOs(users);
  }

  public List<UserFullDTO> getAllUsers(UserFilterDTO filterDTO) {
    List<User> users = userRepository.findAll(UserSpecification.withFilters(filterDTO));
    return this.userDTOUserMapper.mapUsersToUserFullDTOsAsList(users);
  }

  public List<UserCommentDTO> getAllUserComments(UserCommentFilterDTO filterDTO) {
    List<UserComment> userComments =
        this.userCommentRepository.findAll(UserCommentSpecification.withFilters(filterDTO));
    return this.userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(userComments);
  }

  public Page<UserCommentWithEventTitleDTO> getAllUserCommentsWithEventTitle(
      UserCommentFilterDTO filterDTO, Pageable pageable) {

    Page<UserComment> userCommentsWithEventTitles =
        this.userCommentRepository.findAll(
            UserCommentSpecification.withFilters(filterDTO), pageable);

    return userCommentsWithEventTitles.map(
        this.userCommentToUserCommentWithEventTitleDTOMapper::toDTO);
  }

  public Page<MediumWithEventTitleDTO> getAllMediaWithEventTitle(
      MediumFilterDTO filterDTO, Pageable pageable) {
    Page<Medium> mediaListWithEventTitles =
        this.mediumRepository.findAll(MediumSpecification.withFilters(filterDTO), pageable);
    return mediaListWithEventTitles.map(this.mediumToMediumWithEventTitleDTOMapper::toDTO);
  }
}
