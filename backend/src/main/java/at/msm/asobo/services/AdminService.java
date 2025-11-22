package at.msm.asobo.services;

import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.dto.comment.UserCommentWithEventTitleDTO;
import at.msm.asobo.dto.user.UserDTO;
import at.msm.asobo.dto.user.UserFullDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.entities.UserComment;
import at.msm.asobo.exceptions.EventNotFoundException;
import at.msm.asobo.interfaces.UserCommentWithEventTitle;
import at.msm.asobo.mappers.UserCommentDTOUserCommentMapper;
import at.msm.asobo.mappers.UserCommentWithEventTitleToUserCommentWithEventTitleDTOMapper;
import at.msm.asobo.mappers.UserDTOUserMapper;
import at.msm.asobo.repositories.EventRepository;
import at.msm.asobo.repositories.UserCommentRepository;
import at.msm.asobo.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final UserCommentRepository userCommentRepository;
    private final UserDTOUserMapper userDTOUserMapper;
    private final UserCommentDTOUserCommentMapper userCommentDTOUserCommentMapper;
    private final UserCommentWithEventTitleToUserCommentWithEventTitleDTOMapper userCommentWithEventTitleToUserCommentWithEventTitleDTOMapper;

    public AdminService(UserRepository userRepository,
                        EventRepository eventRepository,
                        UserCommentRepository userCommentRepository,
                        UserDTOUserMapper userDTOUserMapper,
                        UserCommentDTOUserCommentMapper userCommentDTOUserCommentMapper,
                        UserCommentWithEventTitleToUserCommentWithEventTitleDTOMapper userCommentWithEventTitleToUserCommentWithEventTitleDTOMapper
    ) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.userCommentRepository = userCommentRepository;
        this.userDTOUserMapper = userDTOUserMapper;
        this.userCommentDTOUserCommentMapper = userCommentDTOUserCommentMapper;
        this.userCommentWithEventTitleToUserCommentWithEventTitleDTOMapper = userCommentWithEventTitleToUserCommentWithEventTitleDTOMapper;
    }

    public List<UserFullDTO> getAllUsers() {
        return this.userDTOUserMapper.mapUsersToUserFullDTOs(this.userRepository.findAll());
    }

    public List<UserCommentDTO> getAllUserComments() {
        List<UserComment> userComments = this.userCommentRepository.findAll();
        return this.userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(userComments);
    }

    public List<UserCommentWithEventTitleDTO> getUserCommentsWithEventTitle() {
        List<UserCommentWithEventTitle> userCommentsWithEventTitles = this.userCommentRepository.findCommentsWithEventTitles();
        return this.userCommentWithEventTitleToUserCommentWithEventTitleDTOMapper.toDTOList(userCommentsWithEventTitles);
    }
}
