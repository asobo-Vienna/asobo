package at.msm.asobo.services;

import at.msm.asobo.dto.auth.LoginResponseDTO;
import at.msm.asobo.dto.auth.UserLoginDTO;
import at.msm.asobo.dto.auth.UserRegisterDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.entities.Role;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.registration.EmailAlreadyExistsException;
import at.msm.asobo.exceptions.registration.UsernameAlreadyExistsException;
import at.msm.asobo.mappers.UserDTOUserMapper;
import at.msm.asobo.security.JwtUtil;
import at.msm.asobo.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AuthService {
    @Value("${jwt.expiration-ms}")
    private long EXPIRATION_MS;
    @Value("${jwt.remember-me-expiration-ms}")
    private long REMEMBER_ME_EXPIRATION_MS;

    private final UserService userService;
    private final PasswordService passwordService;
    private final RoleService roleService;
    private final UserDTOUserMapper userDTOUserMapper;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserService userService, PasswordService passwordService, RoleService roleService, UserDTOUserMapper userDTOUserMapper, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.passwordService = passwordService;
        this.roleService = roleService;
        this.userDTOUserMapper = userDTOUserMapper;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public LoginResponseDTO registerUser(UserRegisterDTO userRegisterDTO) {
        this.validateUserRegistration(userRegisterDTO);

        User newUser = this.userDTOUserMapper.mapUserRegisterDTOToUser(userRegisterDTO);

        String hashedPassword = this.passwordService.hashPassword(userRegisterDTO.getPassword());
        newUser.setPassword(hashedPassword);

        Role userRole = this.roleService.getRoleByName("USER");
        newUser.setRoles(Set.of(userRole));

        newUser.setIsActive(true);

        User savedUser = this.userService.saveUser(newUser);

        UserPrincipal userPrincipal = new UserPrincipal(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = this.jwtUtil.generateToken(userPrincipal, EXPIRATION_MS);

        return new LoginResponseDTO(token, this.userDTOUserMapper.mapUserToUserPublicDTO(savedUser));
    }

    public LoginResponseDTO loginUser(UserLoginDTO userLoginDTO) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userLoginDTO.getIdentifier(),
                        userLoginDTO.getPassword()
                );

        Authentication authentication;
        try {
            authentication = this.authenticationManager.authenticate(authToken);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid identifier or password");
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        long expirationTime = EXPIRATION_MS;
        if (userLoginDTO.isRememberMe()) {
            expirationTime = REMEMBER_ME_EXPIRATION_MS; // 30 days;
        }

        String token = this.jwtUtil.generateToken(userPrincipal, expirationTime);

        User user = this.userService.getUserById(userPrincipal.getUserId());
        UserPublicDTO userPublicDTO = this.userDTOUserMapper.mapUserToUserPublicDTO(user);

        return new LoginResponseDTO(token, userPublicDTO);
    }

    private void validateUserRegistration(UserRegisterDTO userRegisterDTO) {
        this.validateEmailNotTaken(userRegisterDTO.getEmail());
        this.validateUsernameNotTaken(userRegisterDTO.getUsername());
    }

    private void validateEmailNotTaken(String email) {
        if (this.userService.isEmailAlreadyTaken(email)) {
            throw new EmailAlreadyExistsException(email);
        }
    }

    private void validateUsernameNotTaken(String username) {
        if (this.userService.isUsernameAlreadyTaken(username)) {
            throw new UsernameAlreadyExistsException(username);
        }
    }
}
