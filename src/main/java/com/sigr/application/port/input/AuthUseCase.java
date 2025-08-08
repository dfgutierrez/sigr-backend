package com.sigr.application.port.input;

import com.sigr.application.dto.auth.LoginRequestDTO;
import com.sigr.application.dto.auth.LoginResponseDTO;
import com.sigr.application.dto.auth.RegisterRequestDTO;

public interface AuthUseCase {
    
    LoginResponseDTO login(LoginRequestDTO request);
    
    LoginResponseDTO register(RegisterRequestDTO request);
    
    void logout(String token);
}