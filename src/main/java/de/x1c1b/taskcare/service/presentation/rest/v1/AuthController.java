package de.x1c1b.taskcare.service.presentation.rest.v1;

import de.x1c1b.taskcare.service.core.user.application.UserService;
import de.x1c1b.taskcare.service.core.user.application.query.FindUserByUsernameQuery;
import de.x1c1b.taskcare.service.core.user.domain.User;
import de.x1c1b.taskcare.service.infrastructure.security.spring.jwt.JwtTokenProvider;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.CredentialsDTO;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.TokenDTO;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.UserDTO;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.mapper.UserDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserDTOMapper userDTOMapper;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                          UserService userService, UserDTOMapper userDTOMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.userDTOMapper = userDTOMapper;
    }

    @PostMapping("/token")
    TokenDTO generateToken(@RequestBody CredentialsDTO dto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);

        return TokenDTO.builder()
                .type("Bearer")
                .token(jwt)
                .build();
    }

    @GetMapping("/user")
    UserDTO getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        FindUserByUsernameQuery query = new FindUserByUsernameQuery(userDetails.getUsername());
        User result = userService.query(query);
        return userDTOMapper.mapToDTO(result);
    }
}
