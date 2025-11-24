package at.msm.asobo.services;

import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.dto.comment.UserCommentWithEventTitleDTO;
import at.msm.asobo.dto.medium.MediumWithEventTitleDTO;
import at.msm.asobo.dto.user.UserFullDTO;
import at.msm.asobo.entities.User;
import at.msm.asobo.entities.UserComment;
import at.msm.asobo.interfaces.MediumWithEventTitle;
import at.msm.asobo.interfaces.UserCommentWithEventTitle;
import at.msm.asobo.mappers.*;
import at.msm.asobo.repositories.MediumRepository;
import at.msm.asobo.repositories.UserCommentRepository;
import at.msm.asobo.repositories.UserRepository;
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

    public List<UserFullDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return this.userDTOUserMapper.mapUsersToUserFullDTOs(users);
    }

    public List<UserCommentDTO> getAllUserComments() {
        List<UserComment> userComments = this.userCommentRepository.findAll();
        return this.userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(userComments);
    }

    public List<UserCommentWithEventTitleDTO> getAllUserCommentsWithEventTitle() {
        List<UserCommentWithEventTitle> userCommentsWithEventTitles = this.userCommentRepository.findAllCommentsWithEventTitles();
        return this.userCommentWithEventTitleToUserCommentWithEventTitleDTOMapper.toDTOList(userCommentsWithEventTitles);
    }

    public List<MediumWithEventTitleDTO> getAllMediaWithEventTitle() {
        List<MediumWithEventTitle> mediaListWithEventTitles = this.mediumRepository.findAllMediaWithEventTitles();
        return this.mediumWithEventTitleToMediumWithEventTitleDTOMapper.toDTOList(mediaListWithEventTitles);
    }
}
