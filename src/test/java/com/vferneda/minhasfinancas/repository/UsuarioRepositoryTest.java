package com.vferneda.minhasfinancas.repository;

import com.vferneda.minhasfinancas.model.entity.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void deveVerificarAExistenciaDeUmaEmail() {
        // cenário
        final Usuario usuario = criarUsuario();
        entityManager.persist(usuario);

        // ação/execução
        boolean result = repository.existsByEmail("usuario@email.com");

        //verificação
        Assertions.assertTrue(result);
    }

    @Test
    public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComOEmail() {
        // ação/execução
        boolean result = repository.existsByEmail("usuario@email.com");

        //verificação
        Assertions.assertFalse(result);
    }

    @Test
    public void devePersistirUmUsuarioNaBaseDeDados() {
        //cenário
        final Usuario usuario = criarUsuario();

        //ação
        final Usuario usuarioSalvo = repository.save(usuario);

        Assertions.assertNotNull(usuarioSalvo.getId());
    }

    @Test
    public void deveBuscarUmUsuarioPorEmail() {
        //cenário
        final Usuario usuario = criarUsuario();
        entityManager.persist(usuario);

        //Verificação
        final Optional<Usuario> result = repository.findByEmail("usuario@email.com");

        Assertions.assertTrue(result.isPresent());
    }

    @Test
    public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExisteNaBase() {
        //Verificação
        final Optional<Usuario> result = repository.findByEmail("usuario@email.com");

        Assertions.assertFalse(result.isPresent());
    }

    private static Usuario criarUsuario() {
        return Usuario.builder().nome("usuario").email("usuario@email.com").senha("senha").build();
    }

}
