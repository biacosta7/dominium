import { useEffect, useState } from 'react';
import type { Pauta } from './types/pauta';
import { pautaService } from './services/pautaService';
import PautaCardAdmin from './components/PautaCardAdm';
import ModalCriarPauta from './components/ModalCriarPauta';
import './Assembleias.css';

// TODO: substituir pelo assembleiaId real vindo da sessão/contexto
const ASSEMBLEIA_ID_ATUAL = 1;

export default function AssembleiasAdmin() {
  const [pautas, setPautas] = useState<Pauta[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalAberto, setModalAberto] = useState(false);

  async function carregarPautas() {
    setLoading(true);
    try {
      const dados = await pautaService.listar();
      setPautas(dados);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    carregarPautas();
  }, []);

  const abertas = pautas.filter((p) => p.status === 'ABERTA');
  const encerradas = pautas.filter((p) => p.status === 'ENCERRADA');

  return (
    <div className="assembleias-page assembleias-page-admin">
      <div className="assembleias-header">
        <div>
          <h1>Assembleias e Votações</h1>
          <p className="assembleias-sub">Gerencie as pautas e votações do condomínio</p>
        </div>
        <button className="btn-primario" onClick={() => setModalAberto(true)}>
          + Nova Pauta
        </button>
      </div>

      {loading ? (
        <p className="loading-text">Carregando pautas...</p>
      ) : (
        <>
          {abertas.length > 0 && (
            <section>
              <p className="section-label">VOTAÇÕES ABERTAS</p>
              {abertas.map((p) => (
                <PautaCardAdmin key={p.id} pauta={p} onAtualizar={carregarPautas} />
              ))}
            </section>
          )}

          {encerradas.length > 0 && (
            <section>
              <p className="section-label">ENCERRADAS</p>
              {encerradas.map((p) => (
                <PautaCardAdmin key={p.id} pauta={p} onAtualizar={carregarPautas} />
              ))}
            </section>
          )}

          {pautas.length === 0 && (
            <div className="empty-state">
              <p>Nenhuma pauta cadastrada ainda.</p>
              <button className="btn-primario" onClick={() => setModalAberto(true)}>
                Criar primeira pauta
              </button>
            </div>
          )}
        </>
      )}

      {modalAberto && (
        <ModalCriarPauta
          assembleiaId={ASSEMBLEIA_ID_ATUAL}
          onClose={() => setModalAberto(false)}
          onCriada={carregarPautas}
        />
      )}
    </div>
  );
}