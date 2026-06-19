import { useEffect, useState } from 'react';
import type { Pauta } from './types/pauta';
import type { Assembleia } from './types/assembleia';
import { pautaService } from './services/pautaService';
import { assembleiaService } from './services/assembleiaService';
import PautaCardMorador from './components/PautaCardMorador';
import './Assembleias.css';

// TODO: substituir pelos dados reais da sessão autenticada
const USUARIO_ID = 1;
const UNIDADE_ID = 1;

function formatarDataHora(dataHora: string) {
  const data = new Date(dataHora);
  const dia = String(data.getDate()).padStart(2, '0');
  const mes = String(data.getMonth() + 1).padStart(2, '0');
  const ano = data.getFullYear();
  const hora = String(data.getHours()).padStart(2, '0');
  const minuto = String(data.getMinutes()).padStart(2, '0');
  return `${dia}/${mes}/${ano} · ${hora}h${minuto !== '00' ? minuto : ''}`;
}

const TIPO_LABEL: Record<string, string> = {
  ORDINARIA: 'Ordinária',
  EXTRAORDINARIA: 'Extraordinária',
};

export default function AssembleiasMorador() {
  const [pautas, setPautas] = useState<Pauta[]>([]);
  const [assembleias, setAssembleias] = useState<Assembleia[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([pautaService.listar(), assembleiaService.listar()])
      .then(([dadosPautas, dadosAssembleias]) => {
        setPautas(dadosPautas);
        setAssembleias(dadosAssembleias);
      })
      .finally(() => setLoading(false));
  }, []);

  const abertas = pautas.filter((p) => p.status === 'ABERTA');

  const agora = new Date();
  const proximaAssembleia = assembleias
    .filter((a) => a.status === 'AGENDADA' && new Date(a.dataHora) >= agora)
    .sort((a, b) => new Date(a.dataHora).getTime() - new Date(b.dataHora).getTime())[0];

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

          <section>
            <p className="section-label">PRÓXIMA ASSEMBLEIA</p>
            {proximaAssembleia ? (
              <div className="pauta-card">
                <div className="pauta-card-header">
                  <div>
                    <h3 className="pauta-nome">{proximaAssembleia.titulo}</h3>
                    <p className="pauta-meta">
                      {TIPO_LABEL[proximaAssembleia.tipo]} · {formatarDataHora(proximaAssembleia.dataHora)} · {proximaAssembleia.local}
                    </p>
                  </div>
                  <span className="badge badge-agendada">Agendada</span>
                </div>

                {proximaAssembleia.pauta.length > 0 && (
                  <div className="assembleia-pautas-lista">
                    <p className="section-label" style={{ marginBottom: 6 }}>PAUTAS</p>
                    <ol>
                      {proximaAssembleia.pauta.map((item, idx) => (
                        <li key={idx}>{item}</li>
                      ))}
                    </ol>
                  </div>
                )}
              </div>
            ) : (
              <div className="card-box-empty">
                <p>Nenhuma assembleia agendada no momento.</p>
              </div>
            )}
          </section>
        </>
      )}
    </div>
  );
}
