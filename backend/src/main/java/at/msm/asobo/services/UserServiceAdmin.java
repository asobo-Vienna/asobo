package at.msm.asobo.services;

import at.msm.asobo.dto.user.UserDTO;
import at.msm.asobo.mappers.UserDTOUserMapper;
import at.msm.asobo.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceAdmin {
    private final UserRepository userRepository;
    private final UserDTOUserMapper userDTOUserMapper;

    public UserServiceAdmin(UserRepository userRepository,
                       UserDTOUserMapper userDTOUserMapper
    ) {
        this.userRepository = userRepository;
        this.userDTOUserMapper = userDTOUserMapper;
    }

    public List<UserDTO> getAllUsers() {
        return this.userDTOUserMapper.mapUsersToUserDTOs(this.userRepository.findAll());
    }
}
