package at.msm.asobo.services;

import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.dto.user.UserDTO;
import at.msm.asobo.dto.user.UserFullDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.entities.UserComment;
import at.msm.asobo.mappers.UserCommentDTOUserCommentMapper;
import at.msm.asobo.mappers.UserDTOUserMapper;
import at.msm.asobo.repositories.UserCommentRepository;
import at.msm.asobo.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final UserCommentRepository userCommentRepository;
    private final UserDTOUserMapper userDTOUserMapper;
    private final UserCommentDTOUserCommentMapper userCommentDTOUserCommentMapper;

    public AdminService(UserRepository userRepository,
                        UserCommentRepository userCommentRepository,
                        UserDTOUserMapper userDTOUserMapper,
                        UserCommentDTOUserCommentMapper userCommentDTOUserCommentMapper
    ) {
        this.userRepository = userRepository;
        this.userCommentRepository = userCommentRepository;
        this.userDTOUserMapper = userDTOUserMapper;
        this.userCommentDTOUserCommentMapper = userCommentDTOUserCommentMapper;
    }

    public List<UserFullDTO> getAllUsers() {
        return this.userDTOUserMapper.mapUsersToUserFullDTOs(this.userRepository.findAll());
    }

    public List<UserCommentDTO> getAllUserComments() {
        List<UserComment> userComments = this.userCommentRepository.findAll();
        return this.userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(userComments);
    }
}
