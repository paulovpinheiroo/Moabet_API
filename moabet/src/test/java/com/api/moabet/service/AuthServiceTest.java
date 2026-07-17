package com.api.moabet.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.api.moabet.dto.users.login.UserLoginRequestDTO;
import com.api.moabet.dto.users.login.UserLoginResponseDTO;
import com.api.moabet.exception.BusinessException;
import com.api.moabet.exception.ResourceNotFoundException;
import com.api.moabet.models.User;
import com.api.moabet.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("usuario@email.com");
        user.setPassword("senhaCriptografadaNoBanco");
    }

    // --- CENÁRIO 1: SUCESSO NO LOGIN ---

    @Test
    void deveFazerLoginComSucesso() {
        // ARRANGE
        UserLoginRequestDTO requestDTO = new UserLoginRequestDTO("usuario@email.com", "senha123");
        String tokenGerado = "jwt-token-valido-mockado";

        when(userRepository.findByEmail(requestDTO.email())).thenReturn(Optional.of(user));
        // matches(senha_pura, senha_criptografada) -> retorna true se forem iguais
        when(passwordEncoder.matches("senha123", user.getPassword())).thenReturn(true);
        when(tokenService.generateToken(user)).thenReturn(tokenGerado);

        // ACT
        UserLoginResponseDTO response = authService.login(requestDTO);

        // ASSERT
        assertNotNull(response);
        assertEquals(tokenGerado, response.token()); // Certifique-se de que o campo no seu record se chama token()

        // VERIFY: Garante que todo o fluxo de segurança rodou uma vez
        verify(userRepository, times(1)).findByEmail(requestDTO.email());
        verify(passwordEncoder, times(1)).matches("senha123", user.getPassword());
        verify(tokenService, times(1)).generateToken(user);
    }

    // --- CENÁRIO 2: E-MAIL NÃO ENCONTRADO ---

    @Test
    void deveLancarExcecaoQuandoEmailNaoForEncontrado() {
        // ARRANGE
        UserLoginRequestDTO requestDTO = new UserLoginRequestDTO("errado@email.com", "senha123");

        when(userRepository.findByEmail(requestDTO.email())).thenReturn(Optional.empty());

        // ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            authService.login(requestDTO);
        });

        assertEquals("Email not found", exception.getMessage());

        // VERIFY: Se não achou o e-mail, o código deve parar e nunca checar a senha ou
        // gerar token
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(tokenService, never()).generateToken(any(User.class));
    }

    // --- CENÁRIO 3: SENHA INCORRETA ---

    @Test
    void deveLancarExcecaoQuandoSenhaForInvalida() {
        // ARRANGE
        UserLoginRequestDTO requestDTO = new UserLoginRequestDTO("usuario@email.com", "senhaErrada");

        when(userRepository.findByEmail(requestDTO.email())).thenReturn(Optional.of(user));
        // Força a comparação de senhas a retornar falso
        when(passwordEncoder.matches("senhaErrada", user.getPassword())).thenReturn(false);

        // ACT & ASSERT
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(requestDTO);
        });

        assertEquals("Invalid password", exception.getMessage());

        // VERIFY: Achou o e-mail, testou a senha, deu erro e NUNCA gerou o token
        verify(userRepository, times(1)).findByEmail(requestDTO.email());
        verify(passwordEncoder, times(1)).matches("senhaErrada", user.getPassword());
        verify(tokenService, never()).generateToken(any(User.class));
    }
}
