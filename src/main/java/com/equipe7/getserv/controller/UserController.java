package com.equipe7.getserv.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.equipe7.getserv.controller.form.RoleToUserForm;
import com.equipe7.getserv.model.RoleEntity;
import com.equipe7.getserv.model.UserEntity;
import com.equipe7.getserv.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("")
public class UserController {
	
	private final UserService userService;
	
	public UserController(UserService userService) {
		super();
		this.userService = userService;
	}

	@GetMapping("/get/users")
	public ResponseEntity<List<UserEntity>> getUsers(){
		return ResponseEntity.ok().body(userService.getUsers());
	}

	@PostMapping("/post/user")
	public ResponseEntity<UserEntity> postUser(UserEntity user){
		return ResponseEntity.created(null).body(userService.createUser(user));
	}

	@PostMapping("/post/role")
	public ResponseEntity<RoleEntity> postUser(RoleEntity role){
		return ResponseEntity.created(null).body(userService.createRole(role));
	}

	@PostMapping("/post/addrole")
	public ResponseEntity<?> postToUser(RoleToUserForm form){
		userService.addRoleToUser(form.getUsername(), form.getRoleName());
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/token/refresh")
	public void refresh(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
			try {
				String refresh_token = authorizationHeader.substring("Basic ".length());
				Algorithm algorithm = Algorithm.HMAC256("easteregg".getBytes());
				JWTVerifier varifier = JWT.require(algorithm).build();
				DecodedJWT decodedJWT = varifier.verify(refresh_token);
				String username = decodedJWT.getSubject();
				UserEntity user = userService.getUser(username);

				String access_token = JWT.create()
						.withSubject(user.getUsername())
						.withExpiresAt(new Date(System.currentTimeMillis() + 90 * 60 * 1000))
						.withIssuer(request.getRequestURL().toString())
						.withClaim("roles", user.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toList()))
						.sign(algorithm);
				
				Map<String, String> tokens = new HashMap<>();
				tokens.put("access_token", "Basic " + access_token);
				tokens.put("refresh_token", "Basic " + refresh_token);
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), tokens);
			} catch (Exception exception) {
				response.setHeader("error", exception.getMessage());
				response.setStatus(HttpStatus.FORBIDDEN.value());

				Map<String, String> error = new HashMap<>();
				error.put("error_message", exception.getMessage());
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), error);
			}
		} else {
			throw new RuntimeException("Refresh token is missing"); 
		}
	}
	
}
