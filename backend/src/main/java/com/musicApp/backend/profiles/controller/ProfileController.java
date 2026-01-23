package com.musicApp.backend.profiles.controller;

import java.net.Authenticator;

import org.hibernate.property.access.internal.PropertyAccessFieldImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import com.musicApp.backend.profiles.dto.ProfileRequestBody;

import jakarta.validation.constraints.Email;

import com.musicApp.backend.features.authentication.dto.AuthenticationRequestBody;
import com.musicApp.backend.features.authentication.dto.AuthenticationResponseBody;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import com.musicApp.backend.features.authentication.service.AuthenticationService;
import com.musicApp.backend.features.authentication.utils.EmailService;
import com.musicApp.backend.features.authentication.repository.AuthenticationUserRepository;



@CrossOrigin(origins = "http://127.0.0.1:5173") // adjust if frontend uses a different port
@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {
  private final AuthenticationService authenticationService;
  private final EmailService emailService;
  private final AuthenticationUserRepository authenticationUserRepository;


@Autowired
public ProfileController(AuthenticationUserRepository authenticationUserRepository, 
                          AuthenticationService authenticationService,
                            EmailService emailService){
                              this.authenticationService = authenticationService;
                              this.authenticationUserRepository = authenticationUserRepository;
                              this.emailService = emailService;
                            }


  @GetMapping
  public AuthenticationUser getProfile(@RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser){
    return authenticationService.getUser(authenticationUser.getEmail());

  }

   @PutMapping("/fname")
   public void updateName(@RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser, @RequestParam String name){
     AuthenticationUser user = authenticationService.getUser(authenticationUser.getEmail());
     user.setName(name);
     authenticationUserRepository.save(user);
   }

  @PutMapping("/lname")
   public void updateLastName(@RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser, @RequestParam String lname){
     AuthenticationUser user = authenticationService.getUser(authenticationUser.getEmail());
    user.setLastName(lname);;
    authenticationUserRepository.save(user);
   }
  
  @PutMapping("/bio")
  public void updateBio(@RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser, @RequestParam String bio){
     AuthenticationUser user = authenticationService.getUser(authenticationUser.getEmail());
    user.setBio(bio);
    authenticationUserRepository.save(user);
  }

  @PutMapping("/color")
  public void updateColor(@RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser, @RequestParam int color){
     AuthenticationUser user = authenticationService.getUser(authenticationUser.getEmail());
    user.setColor(color);
    authenticationUserRepository.save(user);
  } 
}
