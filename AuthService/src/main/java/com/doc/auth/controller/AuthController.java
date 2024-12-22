package com.doc.auth.controller;

import com.doc.auth.model.User;
import com.doc.auth.model.AuthPayload;
import com.doc.auth.model.input.LoginInput;
import com.doc.auth.model.input.RegisterInput;
import com.doc.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class AuthController {
    
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 获取到自身代表的角色
     * @return User
     */
    @QueryMapping
    public User me() {
        return authService.getCurrentUser();
    }

    @MutationMapping
    public AuthPayload login(@Argument(name = "input") LoginInput input) {
        return authService.login(input);
    }

    @MutationMapping
    public AuthPayload register(@Argument(name = "input") RegisterInput input) {
        return authService.register(input);
    }

    @MutationMapping
    public Boolean logout() {
        return authService.logout();
    }
} 