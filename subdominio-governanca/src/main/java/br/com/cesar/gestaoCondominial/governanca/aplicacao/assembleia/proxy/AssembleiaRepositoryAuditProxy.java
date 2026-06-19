package br.com.cesar.gestaoCondominial.governanca.aplicacao.assembleia.proxy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.Assembleia;
import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.AssembleiaId;
import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.repository.AssembleiaRepository;

@Primary
@Service
public class AssembleiaRepositoryAuditProxy implements AssembleiaRepository {

    private static final Logger log = LoggerFactory.getLogger(AssembleiaRepositoryAuditProxy.class);

    private final AssembleiaRepository delegate;

    public AssembleiaRepositoryAuditProxy(
            @Qualifier("assembleiaRepositoryJdbc") AssembleiaRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public Assembleia save(Assembleia assembleia) {
        boolean isNova = assembleia.getId().getValor() == null;
        String operacao = isNova ? "CRIAR" : "ATUALIZAR";

        log.info("[AUDITORIA-ASSEMBLEIA] {} | título='{}' | status={} | síndico={} | em={}",
                operacao,
                assembleia.getTitulo(),
                assembleia.getStatus(),
                assembleia.getSindicoId(),
                LocalDateTime.now());

        Assembleia resultado = delegate.save(assembleia);

        log.info("[AUDITORIA-ASSEMBLEIA] {} concluído | id={} | em={}",
                operacao,
                resultado.getId().getValor(),
                LocalDateTime.now());

        return resultado;
    }

    @Override
    public Optional<Assembleia> findById(AssembleiaId id) {
        log.debug("[AUDITORIA-ASSEMBLEIA] BUSCAR | id={}", id.getValor());
        Optional<Assembleia> resultado = delegate.findById(id);
        resultado.ifPresentOrElse(
                a -> log.debug("[AUDITORIA-ASSEMBLEIA] BUSCAR encontrada | id={} | título='{}'",
                        id.getValor(), a.getTitulo()),
                () -> log.warn("[AUDITORIA-ASSEMBLEIA] BUSCAR não encontrada | id={}", id.getValor())
        );
        return resultado;
    }

    @Override
    public List<Assembleia> findAll() {
        log.debug("[AUDITORIA-ASSEMBLEIA] LISTAR | em={}", LocalDateTime.now());
        List<Assembleia> resultado = delegate.findAll();
        log.debug("[AUDITORIA-ASSEMBLEIA] LISTAR retornou {} assembleias", resultado.size());
        return resultado;
    }
}
