package com.demo.dms.controller;

import com.demo.dms.entity.TicketDetails;
import com.demo.dms.entity.UserAccount;
import com.demo.dms.service.AppUserDetailsService;
import com.demo.dms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dms")
@CrossOrigin("*")
public class UserController {

    private final UserService userService;
    private final AppUserDetailsService appUserDetailsService;

    @Autowired
    public UserController(UserService userService, AppUserDetailsService appUserDetailsService) {
        this.userService = userService;
        this.appUserDetailsService = appUserDetailsService;
    }

    @GetMapping(value = "/users", produces = "application/json")
    public ResponseEntity<Page<UserAccount>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getUsers(pageable));
    }

}
