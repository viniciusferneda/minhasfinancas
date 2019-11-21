package com.vferneda.minhasfinancas.repository;

import com.vferneda.minhasfinancas.model.entity.Lancamento;
import com.vferneda.minhasfinancas.model.enums.StatusLancamento;
import com.vferneda.minhasfinancas.model.enums.TipoLancamento;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LancamentoRepositoryTest {

    @Autowired
    private LancamentoRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void deveSalvarUmLancamento() {
        Lancamento lancamento = criarLancamento();
        lancamento = repository.save(lancamento);
        Assertions.assertNotNull(lancamento.getId());
    }

    @Test
    public void deveDeletarUmLancamento() {
        Lancamento lancamento = criarPersistirLancamento();

        lancamento = entityManager.find(Lancamento.class, lancamento.getId());

        repository.delete(lancamento);

        final Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
        Assertions.assertNull(lancamentoInexistente);
    }

    @Test
    public void deveAtualizarUmLancamento() {
        Lancamento lancamento = criarPersistirLancamento();

        lancamento.setAno(2018);
        lancamento.setDescricao("Teste de Atualização");
        lancamento.setStatus(StatusLancamento.CANCELADO);

        repository.save(lancamento);

        final Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());

        Assertions.assertEquals(lancamentoAtualizado.getAno(), 2018);
        Assertions.assertEquals(lancamentoAtualizado.getDescricao(), "Teste de Atualização");
        Assertions.assertEquals(lancamentoAtualizado.getStatus(), StatusLancamento.CANCELADO);
    }

    @Test
    public void deveBuscarLancamentoPorId() {
        Lancamento lancamento = criarPersistirLancamento();
        final Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());
        Assertions.assertTrue(lancamentoEncontrado.isPresent());
    }

    private Lancamento criarLancamento() {
        return Lancamento.builder()//
                .ano(2019)//
                .mes(1)//
                .descricao("Lançamento XPTO")//
                .valor(BigDecimal.valueOf(10))//
                .tipo(TipoLancamento.RECEITA)//
                .status(StatusLancamento.PENDENTE)//
                .dataCadastro(LocalDate.now()).build();
    }

    private Lancamento criarPersistirLancamento() {
        Lancamento lancamento = criarLancamento();
        entityManager.persist(lancamento);
        return lancamento;
    }
}
