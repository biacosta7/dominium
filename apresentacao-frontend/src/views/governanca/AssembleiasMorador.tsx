import { useEffect, useState } from 'react';
import type { Pauta } from './types/pauta';
import { pautaService } from './services/pautaService';
import PautaCardMorador from './components/PautaCardMorador';
import './Assembleias.css';

// TODO: substituir pelos dados reais da sessão autenticada
const USUARIO_ID = 1;
const UNIDADE_ID = 1;

export default function AssembleiasMorador() {
  const [pautas, setPautas] = useState<Pauta[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    pautaService.listar().then(setPautas).finally(() => setLoading(false));
  }, []);

  const abertas = pautas.filter((p) => p.status === 'ABERTA');
  const encerradas = pautas.filter((p) => p.status === 'ENCERRADA');

  return (
    <div className="assembleias-page">
      <div className="assembleias-header">
        <div>
          <h1>Assembleias e Votações</h1>
          <p className="assembleias-sub">Participe das decisões do seu condomínio</p>
        </div>
      </div>

      {loading ? (
        <p className="loading-text">Carregando...</p>
      ) : (
        <>
          {abertas.length > 0 && (
            <section>
              <p className="section-label">VOTAÇÃO ABERTA</p>
              {abertas.map((p) => (
                <PautaCardMorador
                  key={p.id}
                  pauta={p}
                  usuarioId={USUARIO_ID}
                  unidadeId={UNIDADE_ID}
                />
              ))}
            </section>
          )}

          {abertas.length === 0 && (
            <div className="empty-state">
              <p>Nenhuma votação aberta no momento.</p>
            </div>
          )}

          {encerradas.length > 0 && (
            <section style={{ marginTop: '32px' }}>
              <p className="section-label" style={{ color: '#666' }}>VOTAÇÕES ENCERRADAS</p>
              {encerradas.map((p) => (
                <PautaCardMorador
                  key={p.id}
                  pauta={p}
                  usuarioId={USUARIO_ID}
                  unidadeId={UNIDADE_ID}
                />
              ))}
            </section>
          )}
        </>
      )}
    </div>
  );
}