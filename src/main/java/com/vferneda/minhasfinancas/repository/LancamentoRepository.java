package com.vferneda.minhasfinancas.repository;

import com.vferneda.minhasfinancas.model.entity.Lancamento;
import com.vferneda.minhasfinancas.model.enums.TipoLancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    @Query(value = "select sum(lan.valor) " + //
            " from Lancamento lan " + //
            "   join lan.usuario usu " + //
            " where usu.id = :idUsuario " + //
            "   and lan.tipo = :tipo" + //
            " group by usu")
    BigDecimal obterSaldoPorTipoLancamentoEUsuario(@Param("idUsuario") Long idUsuario, @Param("tipo") TipoLancamento tipo);
}
