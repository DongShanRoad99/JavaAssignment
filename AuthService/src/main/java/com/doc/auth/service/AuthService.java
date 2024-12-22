package com.doc.auth.service;

import com.doc.auth.model.AuthPayload;
import com.doc.auth.model.User;
import com.doc.auth.model.input.LoginInput;
import com.doc.auth.model.input.RegisterInput;

public interface AuthService {
    User getCurrentUser();
    AuthPayload login(LoginInput input);
    AuthPayload register(RegisterInput input);
    Boolean logout();
} 