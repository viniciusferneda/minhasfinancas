package com.vferneda.minhasfinancas.service.impl;

import com.vferneda.minhasfinancas.exceptions.AutenticacaoException;
import com.vferneda.minhasfinancas.exceptions.RegraNegocioException;
import com.vferneda.minhasfinancas.model.entity.Usuario;
import com.vferneda.minhasfinancas.repository.UsuarioRepository;
import com.vferneda.minhasfinancas.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private UsuarioRepository repository;

    public UsuarioServiceImpl(UsuarioRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        final Optional<Usuario> usuario = repository.findByEmail(email);
        if (!usuario.isPresent()) {
            throw new AutenticacaoException("Usuário não encontrado!");
        }
        if (!usuario.get().getSenha().equals(senha)) {
            throw new AutenticacaoException("Senha inválida!");
        }
        return usuario.get();
    }

    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        validarEmail(usuario.getEmail());
        return repository.save(usuario);
    }

    @Override
    public void validarEmail(String email) {
        boolean existe = this.repository.existsByEmail(email);
        if (existe) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail.");
        }
    }

    @Override
    public Optional<Usuario> obterPorId(Long id) {
        return repository.findById(id);
    }
}
