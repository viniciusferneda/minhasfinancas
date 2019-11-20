package com.vferneda.minhasfinancas.service;

import com.vferneda.minhasfinancas.exceptions.AutenticacaoException;
import com.vferneda.minhasfinancas.exceptions.RegraNegocioException;
import com.vferneda.minhasfinancas.model.entity.Usuario;
import com.vferneda.minhasfinancas.repository.UsuarioRepository;
import com.vferneda.minhasfinancas.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @SpyBean
    private UsuarioServiceImpl service;

    @MockBean
    private UsuarioRepository repository;

    @Test
    public void deveSalvarUmUsuario() {
        //cenário
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        Usuario usuario = Usuario.builder().id(1l).nome("nome").email("email@email.com").senha("senha").build();
        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

        Assertions.assertDoesNotThrow(() -> {
            //ação
            final Usuario usuarioSalvo = service.salvarUsuario(new Usuario());

            //verificação
            Assertions.assertNotNull(usuarioSalvo);
            Assertions.assertEquals(usuario.getId(), 1);
            Assertions.assertEquals(usuario.getNome(), "nome");
            Assertions.assertEquals(usuario.getEmail(), "email@email.com");
            Assertions.assertEquals(usuario.getSenha(), "senha");
        });
    }

    @Test
    public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
        //cenário
        final String email = "email@email.com";
        final Usuario usuario = Usuario.builder().email(email).build();
        Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);

        //ação
        Assertions.assertThrows(RegraNegocioException.class, () -> {
            service.salvarUsuario(usuario);
        });

        //verificação
        Mockito.verify(repository, Mockito.never()).save(usuario);
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
    public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
        //cenário
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        //ação
        final AutenticacaoException autenticacaoException = Assertions.assertThrows(AutenticacaoException.class, () -> {
            service.autenticar("email@email.com", "senha");
        });

        Assertions.assertEquals("Usuário não encontrado!", autenticacaoException.getMessage());
    }

    @Test
    public void deveLancarErroQuandoSenhaNaoBater() {
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
