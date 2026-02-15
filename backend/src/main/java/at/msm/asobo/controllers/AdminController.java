package at.msm.asobo.controllers;

import at.msm.asobo.dto.comment.UserCommentWithEventTitleDTO;
import at.msm.asobo.dto.filter.MediumFilterDTO;
import at.msm.asobo.dto.filter.UserCommentFilterDTO;
import at.msm.asobo.dto.filter.UserFilterDTO;
import at.msm.asobo.dto.medium.MediumWithEventTitleDTO;
import at.msm.asobo.dto.user.UserAdminSummaryDTO;
import at.msm.asobo.dto.user.UserFullDTO;
import at.msm.asobo.services.AdminService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
public class AdminController {
  private final AdminService adminService;

  public AdminController(AdminService adminService) {
    this.adminService = adminService;
  }

  @GetMapping("/users/paginated")
  public Page<UserAdminSummaryDTO> getAllUsersPaginated(
      @RequestParam(required = false) String firstName,
      @RequestParam(required = false) String surname,
      @RequestParam(required = false) String location,
      @RequestParam(required = false) String country,
      @RequestParam(required = false) Boolean isActive,
      @RequestParam(required = false) Set<Long> roleIds,
      @PageableDefault(sort = "surname", direction = Sort.Direction.ASC) Pageable pageable) {
    UserFilterDTO filterDTO =
        new UserFilterDTO(firstName, surname, location, country, isActive, roleIds);

    return this.adminService.getAllUsersPaginated(filterDTO, pageable);
  }

  @GetMapping("/users")
  public List<UserFullDTO> getAllUsers(
      @RequestParam(required = false) String firstName,
      @RequestParam(required = false) String surname,
      @RequestParam(required = false) String location,
      @RequestParam(required = false) String country,
      @RequestParam(required = false) Boolean isActive,
      @RequestParam(required = false) Set<Long> roleIds) {
    UserFilterDTO filterDTO =
        new UserFilterDTO(firstName, surname, location, country, isActive, roleIds);

    return this.adminService.getAllUsers(filterDTO);
  }

  // TODO?: On expand get full user details for ONE user
  /*@GetMapping("/users/{id}/full")
  public UserFullDTO getUserWithDetails(@PathVariable UUID id);*/

  @GetMapping("/comments")
  public Page<UserCommentWithEventTitleDTO> getAllUserCommentsWithEventTitle(
      @RequestParam(required = false) UUID authorId,
      @RequestParam(required = false) UUID eventId,
      @RequestParam(required = false) LocalDateTime date,
      @RequestParam(required = false) LocalDateTime dateFrom,
      @RequestParam(required = false) LocalDateTime dateTo,
      @PageableDefault(sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
    UserCommentFilterDTO filterDTO =
        new UserCommentFilterDTO(authorId, eventId, date, dateFrom, dateTo);

    return this.adminService.getAllUserCommentsWithEventTitle(filterDTO, pageable);
  }

  @GetMapping("/media")
  public Page<MediumWithEventTitleDTO> getAllMediaWithEventTitle(
      @RequestParam(required = false) UUID creatorId,
      @RequestParam(required = false) UUID eventId,
      @RequestParam(required = false) LocalDateTime date,
      @RequestParam(required = false) LocalDateTime dateFrom,
      @RequestParam(required = false) LocalDateTime dateTo,
      @PageableDefault(sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
    MediumFilterDTO filterDTO = new MediumFilterDTO(creatorId, eventId, date, dateFrom, dateTo);

    return this.adminService.getAllMediaWithEventTitle(filterDTO, pageable);
  }

  /*@GetMapping
  public List<UserAdminDTO> getAllUsers() {
      //return this.userService.getAllUsers();
  }*/

  // @GetMapping("/{id}")
  /*public UserAdminDTO getUserById(@PathVariable UUID id) {
      User foundUser = this.userService.getUserById(id);
      return new UserAdminDTO(foundUser);
  }*/

  /*@GetMapping("/{username}")
  /*public UserAdminDTO getUserByUsername(@PathVariable String username) {
      User foundUser = this.userService.getUserByUsername(username);
      return new UserAdminDTO(foundUser);
  }*/

  /*@PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserAdminDTO createUser(@RequestBody @Valid User user) {
      User savedUser = this.userService.createUser(user);
      return new UserAdminDTO(savedUser);
  }*/

  // TODO create UserAdminService that returns UserAdminDTOs
  //    @PutMapping("/{id}")
  //    public UserAdminDTO updateUser(@PathVariable UUID id, @RequestBody @Valid UserUpdateDTO
  // userUpdateDTO) {
  //        User updatedUser = this.userService.updateUserById(id, userUpdateDTO);
  //        return new UserAdminDTO(updatedUser);
  //    }
}
