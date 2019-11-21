package com.vferneda.minhasfinancas.api.controller;

import com.vferneda.minhasfinancas.api.dto.AtualizarStatusDTO;
import com.vferneda.minhasfinancas.api.dto.LancamentoDTO;
import com.vferneda.minhasfinancas.exceptions.RegraNegocioException;
import com.vferneda.minhasfinancas.model.entity.Lancamento;
import com.vferneda.minhasfinancas.model.entity.Usuario;
import com.vferneda.minhasfinancas.model.enums.StatusLancamento;
import com.vferneda.minhasfinancas.model.enums.TipoLancamento;
import com.vferneda.minhasfinancas.service.LancamentoService;
import com.vferneda.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {

    private final LancamentoService service;
    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity buscar(@RequestParam(value = "descricao", required = false) String descricao,
                                 @RequestParam(value = "mes", required = false) Integer mes,
                                 @RequestParam(value = "ano", required = false) Integer ano,
                                 @RequestParam("usuario") Long idUsuario) {
        final Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);
        final Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
        if (!usuario.isPresent()) {
            return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado!");
        } else {
            lancamentoFiltro.setUsuario(usuario.get());
        }
        List<Lancamento> lLancamentos = service.buscar(lancamentoFiltro);
        return ResponseEntity.ok(lLancamentos);
    }

    @PostMapping
    public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
        try {
            final Lancamento lancamento = converter(dto);
            final Lancamento lancamentoSalvo = service.salvar(lancamento);
            return new ResponseEntity(lancamentoSalvo, HttpStatus.CREATED);
        } catch (RegraNegocioException exc) {
            return ResponseEntity.badRequest().body(exc.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable Long id, @RequestBody LancamentoDTO dto) {
        return this.service.obterPorId(id).map(entity -> {
            try {
                Lancamento lancamento = converter(dto);
                lancamento.setId(entity.getId());
                service.atualizar(lancamento);
                return ResponseEntity.ok(lancamento);
            } catch (RegraNegocioException exc) {
                return ResponseEntity.badRequest().body(exc.getMessage());
            }
        }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado!", HttpStatus.BAD_REQUEST));
    }

    @PutMapping("{id}/atualiza-status")
    public ResponseEntity atualizarStatus(@PathVariable Long id, @RequestBody AtualizarStatusDTO dto) {
        return this.service.obterPorId(id).map(entity -> {
            try {
                final StatusLancamento statusLancamento = StatusLancamento.valueOf(dto.getStatus());
                if (statusLancamento == null) {
                    return ResponseEntity.badRequest().body("Não foi possível atualizar o status do Lançamento. Envie um status válido!");
                }
                entity.setStatus(statusLancamento);
                service.atualizar(entity);
                return ResponseEntity.ok(entity);
            } catch (RegraNegocioException exc) {
                return ResponseEntity.badRequest().body(exc.getMessage());
            }
        }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado!", HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("{id}")
    public ResponseEntity atualizar(@PathVariable Long id) {
        return this.service.obterPorId(id).map(entity -> {
            service.deletar(entity);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado!", HttpStatus.BAD_REQUEST));
    }

    private Lancamento converter(LancamentoDTO dto) {
        final Lancamento lancamento = new Lancamento();

        lancamento.setId(dto.getId());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setValor(dto.getValor());

        final Usuario usuario = usuarioService.obterPorId(dto.getUsuario()).//
                orElseThrow(() -> new RegraNegocioException("Usuário não encontrado!"));

        lancamento.setUsuario(usuario);

        if (dto.getTipo() != null) {
            lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
        }

        if (dto.getStatus() != null) {
            lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
        }

        return lancamento;
    }

}
