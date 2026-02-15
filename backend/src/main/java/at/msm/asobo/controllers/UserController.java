package at.msm.asobo.controllers;

import at.msm.asobo.dto.auth.LoginResponseDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.dto.user.UserUpdateDTO;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.UserService;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  // we need "/id/ before the actual id, because otherwise
  // /{id} and /{username} lead to ambiguity
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'USER')")
  @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public UserPublicDTO getUserById(@PathVariable UUID id) {
    return this.userService.getUserDTOById(id);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'USER')")
  @GetMapping("/{username}")
  public UserPublicDTO getUserByUsername(@PathVariable String username) {

    return this.userService.getUserByUsername(username);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'USER')")
  @PatchMapping("/{id}")
  public LoginResponseDTO updateUser(
      @PathVariable UUID id,
      @RequestBody @Valid UserUpdateDTO userUpdateDTO,
      @AuthenticationPrincipal UserPrincipal loggedInUser) {

    return this.userService.updateUserById(id, loggedInUser, userUpdateDTO);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'USER')")
  @PatchMapping("/{id}/profile-picture")
  public LoginResponseDTO updateProfilePicture(
      @PathVariable UUID id,
      @RequestParam("profilePicture") MultipartFile profilePicture,
      @AuthenticationPrincipal UserPrincipal loggedInUser) {

    UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
    userUpdateDTO.setProfilePicture(profilePicture);

    return this.userService.updateUserById(id, loggedInUser, userUpdateDTO);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'USER')")
  @DeleteMapping("/{userId}")
  public UserPublicDTO deleteUser(
      @PathVariable UUID userId, @AuthenticationPrincipal UserPrincipal loggedInUser) {
    return this.userService.deleteUserById(userId, loggedInUser);
  }

  @GetMapping("/countries")
  public List<String> getAllCountryCodes() {
    return Arrays.asList(Locale.getISOCountries());
  }
}
