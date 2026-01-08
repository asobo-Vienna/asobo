package at.msm.asobo.controllers;

import at.msm.asobo.dto.auth.LoginResponseDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.dto.user.UserUpdateDTO;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;


@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'USER')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // we need "/id/ before the actual id, because otherwise
    // /{id} and /{username} lead to ambiguity
    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserPublicDTO getUserById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return this.userService.getUserDTOById(id);
    }

    @GetMapping("/{username}")
    public UserPublicDTO getUserByUsername(
            @PathVariable String username,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        return this.userService.getUserByUsername(username);
    }

    @PatchMapping("/{id}")
    public LoginResponseDTO updateUser(
            @PathVariable UUID id,
            @RequestBody @Valid UserUpdateDTO userUpdateDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        UUID loggedInUserId = userPrincipal.getUserId();
        return this.userService.updateUserById(id, loggedInUserId, userUpdateDTO);
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
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public UserPublicDTO deleteUser(@PathVariable UUID id) {
        return this.userService.deleteUserById(id);
    }
}
