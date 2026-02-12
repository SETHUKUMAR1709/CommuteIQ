package com.commuteiq.platform.service;

import com.commuteiq.platform.dto.request.AuthRequest;
import com.commuteiq.platform.dto.request.RegisterRequest;
import com.commuteiq.platform.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(AuthRequest request);
}
