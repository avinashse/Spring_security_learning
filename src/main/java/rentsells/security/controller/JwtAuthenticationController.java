package rentsells.security.controller;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import rentsells.security.component.JwtTokenUtil;
import rentsells.security.model.JwtResponse;
import rentsells.security.model.JwtValidation;


@RestController
public class JwtAuthenticationController {
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@RequestMapping(value = "/createToken", method = RequestMethod.GET)
	public ResponseEntity<?> createToken(@RequestParam("username") String username) throws Exception {
		final String token = jwtTokenUtil.generateToken(username);
		if(token == null) {
			return (ResponseEntity<?>) ResponseEntity.badRequest();
		}else {
			return ResponseEntity.ok(new JwtResponse(token));
		}

	}
	
	@PostMapping("/validateToken")
	public ResponseEntity<?> validateToken(@Valid @RequestBody JwtValidation jwtValidation) {
			return jwtTokenUtil.validateToken(jwtValidation);
	}
}
