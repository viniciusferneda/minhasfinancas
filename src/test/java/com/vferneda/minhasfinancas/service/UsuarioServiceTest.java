package com.vferneda.minhasfinancas.service;

import com.vferneda.minhasfinancas.exceptions.AutenticacaoException;
import com.vferneda.minhasfinancas.exceptions.RegraNegocioException;
import com.vferneda.minhasfinancas.model.entity.Usuario;
import com.vferneda.minhasfinancas.repository.UsuarioRepository;
import com.vferneda.minhasfinancas.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    private UsuarioService service;

    @MockBean
    private UsuarioRepository repository;

    @BeforeEach
    public void setUp() {
        service = new UsuarioServiceImpl(repository);
    }

    @Test
    public void deveAutenticarUmUsuarioComSucesso() {
        //cenário
        String email = "email@email.com";
        String senha = "senha";

        final Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

        Assertions.assertDoesNotThrow(() -> {
            //ação
            final Usuario result = service.autenticar(email, senha);

            //verificação
            Assertions.assertNotNull(result);
        });
    }

    @Test
    public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado(){
        //cenário
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        //ação
        final AutenticacaoException autenticacaoException = Assertions.assertThrows(AutenticacaoException.class, () -> {
            service.autenticar("email@email.com", "senha");
        });

        Assertions.assertEquals("Usuário não encontrado!", autenticacaoException.getMessage());
    }

    @Test
    public void deveLancarErroQuandoSenhaNaoBater(){
        //cenário
        final String senha = "senha";
        final Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

        //ação
        final AutenticacaoException autenticacaoException = Assertions.assertThrows(AutenticacaoException.class, () -> {
            service.autenticar("email@email.com", "123");
        });

        Assertions.assertEquals("Senha inválida!", autenticacaoException.getMessage());
    }

    @Test
    public void deveValidarEmail() {
        //cenário
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

        //ação
        Assertions.assertDoesNotThrow(() -> {
            service.validarEmail("email@email.com");
        });
    }

    @Test
    public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
        // cenário
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

        //ação
        Assertions.assertThrows(RegraNegocioException.class, () -> {
            service.validarEmail("email@email.com");
        });
    }
}
