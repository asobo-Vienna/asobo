package at.msm.asobo.controllers;

import at.msm.asobo.dto.auth.LoginResponseDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.dto.user.UserRegisterDTO;
import at.msm.asobo.dto.user.UserUpdateDTO;
import at.msm.asobo.mappers.LoginResponseDTOToUserPublicDTOMapper;
import at.msm.asobo.mappers.UserDTOToUserPublicDTOMapper;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserDTOToUserPublicDTOMapper userDTOToUserPublicDTOMapper;
    private final LoginResponseDTOToUserPublicDTOMapper loginResponseDTOToUserPublicDTOMapper;

    public UserController(
            UserService userService,
            UserDTOToUserPublicDTOMapper userDTOToUserPublicDTOMapper,
            LoginResponseDTOToUserPublicDTOMapper loginResponseDTOToUserPublicDTOMapper) {
        this.userService = userService;
        this.userDTOToUserPublicDTOMapper = userDTOToUserPublicDTOMapper;
        this.loginResponseDTOToUserPublicDTOMapper = loginResponseDTOToUserPublicDTOMapper;
    }

    @GetMapping
    public List<UserPublicDTO> getAllUsers(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to perform this action."); // TODO: maybe create UserNotAuthorizedException
        }

        return this.userService.getAllUsers();
    }

    // we need "/id/ before the actual id, because otherwise
    // /{id} and /{username} lead to ambiguity
    @GetMapping("/id/{id}")
    public UserPublicDTO getUserById(@PathVariable UUID id) {
        return this.userService.getUserDTOById(id);
    }

    @GetMapping("/{username}")
    public UserPublicDTO getUserByUsername(@PathVariable String username, Authentication authentication) {
        // Check if user is logged in
        String loggedInUsername = null;
        if (authentication != null && authentication.isAuthenticated()) {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            loggedInUsername = principal.getUsername();
            return this.userService.getUserByUsername(loggedInUsername);
        }

        // Optional: prevent accessing private profiles of others
        // Uncomment if you want only the logged-in user to access their profile
    /*
    if (loggedInUsername != null && !loggedInUsername.equals(username)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
    }
    */

        return this.userService.getUserByUsername(username);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserPublicDTO registerUser(@ModelAttribute @Valid UserRegisterDTO registerDTO) {
        LoginResponseDTO loginResponseDTO = this.userService.registerUser(registerDTO);
        return this.loginResponseDTOToUserPublicDTOMapper.mapLoginResponseDTOToUserPublicDTO(loginResponseDTO);
    }

    @PatchMapping("/{id}")
    public UserPublicDTO updateUser(@PathVariable UUID id, @RequestBody @Valid UserUpdateDTO userUpdateDTO, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        UUID loggedInUserId = UUID.fromString(userPrincipal.getUserId());

        return this.userService.updateUserById(id, loggedInUserId, userUpdateDTO);
    }

    @PatchMapping("/{id}/profile-picture")
    public UserPublicDTO updateProfilePicture(
            @PathVariable UUID id,
            @RequestParam("profilePicture") MultipartFile profilePicture,
            @AuthenticationPrincipal UserPrincipal principal) {

        UUID loggedInUserId = UUID.fromString(principal.getUserId());

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setProfilePicture(profilePicture);

        return this.userService.updateUserById(id, loggedInUserId, userUpdateDTO);
    }

    @DeleteMapping("/{id}")
    public UserPublicDTO deleteUser(@PathVariable UUID id) {
        return this.userService.deleteUserById(id);
    }
}
