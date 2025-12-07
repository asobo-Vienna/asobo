package at.msm.asobo.controllers;

import at.msm.asobo.dto.auth.LoginResponseDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.dto.user.UserUpdateDTO;
import at.msm.asobo.exceptions.UserNotFoundException;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // we need "/id/ before the actual id, because otherwise
    // /{id} and /{username} lead to ambiguity
    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserPublicDTO> getUserById(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserPublicDTO user = this.userService.getUserDTOById(id);

        if (user == null) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }

        return ResponseEntity.ok(user);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserPublicDTO> getUserByUsername(
            @PathVariable String username,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        UserPublicDTO user = this.userService.getUserByUsername(username);

        if (user == null) {
            throw new UserNotFoundException("User with username " + username + " not found");
        }

        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LoginResponseDTO> updateUser(
            @PathVariable UUID id,
            @RequestBody @Valid UserUpdateDTO userUpdateDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        UUID loggedInUserId = userPrincipal.getUserId();
        LoginResponseDTO response = this.userService.updateUserById(id, loggedInUserId, userUpdateDTO);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/profile-picture")
    public LoginResponseDTO updateProfilePicture(
            @PathVariable UUID id,
            @RequestParam("profilePicture") MultipartFile profilePicture,
            @AuthenticationPrincipal UserPrincipal principal) {

        UUID loggedInUserId = principal.getUserId();

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setProfilePicture(profilePicture);

        return this.userService.updateUserById(id, loggedInUserId, userUpdateDTO);
    }

    @DeleteMapping("/{id}")
    public UserPublicDTO deleteUser(@PathVariable UUID id) {
        return this.userService.deleteUserById(id);
    }
}
