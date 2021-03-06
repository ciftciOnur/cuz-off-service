package com.cuzoffservice.interfaces.controller;

import com.cuzoffservice.application.service.UserService;
import com.cuzoffservice.application.service.impl.JwtUserDetailsService;
import com.cuzoffservice.infrastructure.config.EmailNotFoundException;
import com.cuzoffservice.infrastructure.config.JwtTokenUtil;
import com.cuzoffservice.infrastructure.config.PasswordNotCorrectException;
import com.cuzoffservice.interfaces.dto.CreateUserRequestDto;
import com.cuzoffservice.interfaces.dto.CreateUserResponseDto;
import com.cuzoffservice.interfaces.dto.LoginRequestDto;
import com.cuzoffservice.interfaces.dto.LoginResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
@CrossOrigin
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequestDto authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new LoginResponseDto(token));
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody CreateUserRequestDto registerRequest) throws Exception {
        CreateUserResponseDto createUserResponseDTO = userService.createUser(registerRequest);
        return new ResponseEntity<CreateUserResponseDto>(createUserResponseDTO, HttpStatus.CREATED);
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new EmailNotFoundException("USER_DISABLED");
        } catch (BadCredentialsException e) {
            throw new PasswordNotCorrectException("INVALID_CREDENTIALS");
        }
    }
}