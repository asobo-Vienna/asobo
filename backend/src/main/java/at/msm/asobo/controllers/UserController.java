package at.msm.asobo.controllers;

import at.msm.asobo.dto.user.UserDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.dto.user.UserRegisterDTO;
import at.msm.asobo.dto.user.UserUpdateDTO;
import at.msm.asobo.mappers.UserDTOToUserPublicDTOMapper;
import at.msm.asobo.services.PasswordService;
import at.msm.asobo.services.UserService;
import at.msm.asobo.services.files.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserDTOToUserPublicDTOMapper userDTOToUserPublicDTOMapper;

    public UserController(UserService userService, UserDTOToUserPublicDTOMapper userDTOToUserPublicDTOMapper) {
        this.userService = userService;
        this.userDTOToUserPublicDTOMapper = userDTOToUserPublicDTOMapper;
    }

    @GetMapping
    public List<UserPublicDTO> getAllUsers() {
        return this.userService.getAllUsers();
    }

    // we need "/id/ before the actual id, because otherwise
    // /{id} and /{username} lead to ambiguity
    @GetMapping("/id/{id}")
    public UserPublicDTO getUserById(@PathVariable UUID id) {
        return this.userService.getUserDTOById(id);
    }

    @GetMapping("/{username}")
    public UserPublicDTO getUserByUsername(@PathVariable String username) {
        return this.userService.getUserByUsername(username);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserPublicDTO registerUser(@ModelAttribute @Valid UserRegisterDTO registerDTO) {
        UserDTO userDTO = this.userService.registerUser(registerDTO);
        return this.userDTOToUserPublicDTOMapper.mapUserDTOToUserPublicDTO(userDTO);
    }

    @PutMapping("/{id}")
    public UserPublicDTO updateUser(@PathVariable UUID id, @ModelAttribute @Valid UserUpdateDTO userUpdateDTO) {
        return this.userService.updateUserById(id, userUpdateDTO);
    }

    @DeleteMapping("/{id}")
    public UserPublicDTO deleteUser(@PathVariable UUID id) {
        return this.userService.deleteUserById(id);
    }
}
