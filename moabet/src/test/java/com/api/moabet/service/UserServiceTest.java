package com.api.moabet.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.api.moabet.dto.users.UserRequestDTO;
import com.api.moabet.dto.users.UserResponseDTO;
import com.api.moabet.exception.BusinessException;
import com.api.moabet.exception.ResourceNotFoundException;
import com.api.moabet.models.User;
import com.api.moabet.models.Wallet;
import com.api.moabet.repository.UserRepository;
import com.api.moabet.repository.WalletRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Lucas Silva");
        user.setEmail("lucas@email.com");
        user.setPassword("senhaCriptografada");
        user.setCpf("123.456.789-00");
        user.setPhone("(11) 99999-9999");

        wallet = new Wallet();
        wallet.setId(10L);
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.ZERO);
    }

    // --- TESTES DO CREATE USER ---

    @Test
    void deveCriarUsuarioECarteiraComSucesso() {
        UserRequestDTO request = new UserRequestDTO("Lucas Silva", "lucas@email.com", "123456", "123.456.789-00",
                "(11) 99999-9999");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("senhaCriptografada");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO response = userService.createUser(request);

        assertNotNull(response);
        assertEquals("Lucas Silva", response.name());
        assertEquals(BigDecimal.ZERO, response.balance());

        verify(passwordEncoder, times(1)).encode("123456");
        verify(userRepository, times(1)).save(any(User.class));
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaExistir() {
        UserRequestDTO request = new UserRequestDTO("Lucas Silva", "lucas@email.com", "123456", "123.456.789-00",
                "(11) 99999-9999");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.createUser(request);
        });

        assertEquals("Email already exists", exception.getMessage());

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    // --- TESTES DO GET USER BY ID (AJUSTADOS PELA NOVA ORDEM) ---

    @Test
    void deveBuscarUsuarioPorIdComSucesso() {
        // ARRANGE - Ajustado para refletir a nova ordem de execução estrutural
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));

        // ACT
        UserResponseDTO response = userService.getUserById(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals("Lucas Silva", response.name());
        assertEquals(BigDecimal.ZERO, response.balance());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoForEncontrado() {
        // ARRANGE - Usuário não existe no banco
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(1L);
        });

        assertEquals("User not found", exception.getMessage());

        // VERIFY - Como o usuário não existe, a execução para ali e NUNCA pesquisa a
        // carteira
        verify(walletRepository, never()).findByUserId(anyLong());
    }

    @Test
    void deveLancarExcecaoQuandoCarteiraNaoForEncontradaMasUsuarioExiste() {
        // ARRANGE - Usuário existe, mas por alguma inconsistência a carteira sumiu
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(1L);
        });

        assertEquals("Wallet not found", exception.getMessage());
    }

    // --- TESTES DO GET ALL USERS ---

    @Test
    void deveRetornarListaDeUsuariosComSucesso() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));

        List<UserResponseDTO> response = userService.getAllUsers();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Lucas Silva", response.get(0).name());
    }
    // --- TESTES DO DELETE ---

    @Test
    void deveDeletarUsuarioComSucesso() {
        // ARRANGE
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        // ACT
        userService.deleteUser(1L);

        // ASSERT & VERIFY
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarUsuarioInexistente() {
        // ARRANGE
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(999L);
        });

        // VERIFY: Como o usuário não existe, o deleteById nunca deve ser chamado
        verify(userRepository, never()).deleteById(anyLong());
    }
}
