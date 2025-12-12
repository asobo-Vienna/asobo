package at.msm.asobo.services;

import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.dto.comment.UserCommentWithEventTitleDTO;
import at.msm.asobo.dto.medium.MediumWithEventTitleDTO;
import at.msm.asobo.dto.user.UserAdminSummaryDTO;
import at.msm.asobo.entities.User;
import at.msm.asobo.entities.UserComment;
import at.msm.asobo.interfaces.MediumWithEventTitle;
import at.msm.asobo.interfaces.UserCommentWithEventTitle;
import at.msm.asobo.mappers.*;
import at.msm.asobo.repositories.MediumRepository;
import at.msm.asobo.repositories.UserCommentRepository;
import at.msm.asobo.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final UserCommentRepository userCommentRepository;
    private final MediumRepository mediumRepository;

    private final UserDTOUserMapper userDTOUserMapper;
    private final UserCommentDTOUserCommentMapper userCommentDTOUserCommentMapper;
    private final UserCommentWithEventTitleToUserCommentWithEventTitleDTOMapper userCommentWithEventTitleToUserCommentWithEventTitleDTOMapper;
    private final MediumWithEventTitleToMediumWithEventTitleDTOMapper mediumWithEventTitleToMediumWithEventTitleDTOMapper;

    public AdminService(UserRepository userRepository,
                        UserCommentRepository userCommentRepository,
                        MediumRepository mediumRepository,
                        UserDTOUserMapper userDTOUserMapper,
                        UserCommentDTOUserCommentMapper userCommentDTOUserCommentMapper,
                        UserCommentWithEventTitleToUserCommentWithEventTitleDTOMapper userCommentWithEventTitleToUserCommentWithEventTitleDTOMapper,
                        MediumWithEventTitleToMediumWithEventTitleDTOMapper mediumWithEventTitleToMediumWithEventTitleDTOMapper
    ) {
        this.userRepository = userRepository;
        this.userCommentRepository = userCommentRepository;
        this.mediumRepository = mediumRepository;
        this.userDTOUserMapper = userDTOUserMapper;
        this.userCommentDTOUserCommentMapper = userCommentDTOUserCommentMapper;
        this.userCommentWithEventTitleToUserCommentWithEventTitleDTOMapper = userCommentWithEventTitleToUserCommentWithEventTitleDTOMapper;
        this.mediumWithEventTitleToMediumWithEventTitleDTOMapper =  mediumWithEventTitleToMediumWithEventTitleDTOMapper;
    }

    public Page<UserAdminSummaryDTO> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return this.userDTOUserMapper.mapUsersToAdminSummaryDTOs(users);
    }

    public List<UserCommentDTO> getAllUserComments() {
        List<UserComment> userComments = this.userCommentRepository.findAll();
        return this.userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(userComments);
    }

    public Page<UserCommentWithEventTitleDTO> getAllUserCommentsWithEventTitle(Pageable pageable) {
        Page<UserCommentWithEventTitle> userCommentsWithEventTitles =
                this.userCommentRepository.findAllCommentsWithEventTitle(pageable);

        return userCommentsWithEventTitles.map(
                this.userCommentWithEventTitleToUserCommentWithEventTitleDTOMapper::toDTO
        );
    }

    public Page<MediumWithEventTitleDTO> getAllMediaWithEventTitle(Pageable pageable) {
        Page<MediumWithEventTitle> mediaListWithEventTitles = this.mediumRepository.findAllMediaWithEventTitle(pageable);
        return mediaListWithEventTitles.map(this.mediumWithEventTitleToMediumWithEventTitleDTOMapper::toDTO);
    }
}
