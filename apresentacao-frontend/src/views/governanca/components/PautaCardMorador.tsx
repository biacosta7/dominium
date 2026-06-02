import { useEffect, useState } from 'react';
import type { Pauta } from '../types/pauta';
import type { OpcaoVoto, ResumoVotos } from '../types/voto';
import { votoService } from '../services/votoService';

interface Props {
  pauta: Pauta;
  usuarioId: number;
  unidadeId: number;
}

export default function PautaCardMorador({ pauta, usuarioId, unidadeId }: Props) {
  const [opcao, setOpcao] = useState<OpcaoVoto | null>(null);
  const [votado, setVotado] = useState(false);
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState('');
  const [resumo, setResumo] = useState<ResumoVotos | null>(null);

  useEffect(() => {
    votoService.listarPorPauta(pauta.id).then((votos) => {
      setResumo(votoService.resumir(votos));
      const jaVotou = votos.some((v) => v.unidadeId === unidadeId);
      setVotado(jaVotou);
    });
  }, [pauta.id, unidadeId]);

  async function handleVotar() {
    if (!opcao) return;
    setLoading(true);
    setErro('');
    try {
      await votoService.votar({ pautaId: pauta.id, usuarioId, unidadeId, opcao });
      setVotado(true);
      // atualiza resumo
      const votos = await votoService.listarPorPauta(pauta.id);
      setResumo(votoService.resumir(votos));
    } catch (e: any) {
      setErro(e.message);
    } finally {
      setLoading(false);
    }
  }

  const total = resumo?.total ?? 0;
  const pct = (n: number) => total > 0 ? Math.round((n / total) * 100) : 0;

  if (votado) {
    return (
      <div className="pauta-card votado-card">
        <div className="votado-success">
          <span className="votado-icon">🗳️</span>
          <h3>Voto registrado!</h3>
          <p>Seu voto foi computado com sucesso. Obrigado pela participação!</p>
          <span className="badge badge-aberta">● Votação concluída</span>
        </div>

        {resumo && (
          <div className="resultado-parcial-morador">
            <h4>Resultado parcial — {pauta.titulo}</h4>
            <BarraResultado label="✅ Sim" votos={resumo.favor} pct={pct(resumo.favor)} cor="favor" />
            <BarraResultado label="❌ Não" votos={resumo.contra} pct={pct(resumo.contra)} cor="contra" />
            <BarraResultado label="⭕ Abstenção" votos={resumo.abstencao} pct={pct(resumo.abstencao)} cor="abstencao" />
            <p className="parcial-aviso">Resultado parcial, não definitivo</p>
          </div>
        )}
      </div>
    );
  }

  return (
    <div className="pauta-card votacao-aberta">
      <div className="pauta-card-top">
        <span className="badge-votacao-aberta">VOTAÇÃO ABERTA</span>
      </div>

      <h3 className="pauta-nome">{pauta.titulo}</h3>
      {pauta.descricao && <p className="pauta-desc">{pauta.descricao}</p>}

      {resumo && (
        <p className="pauta-meta">{resumo.total} de ? unidades já votaram · Sua unidade ainda não votou</p>
      )}

      <div className="opcoes-voto">
        <OpcaoItem
          ativa={opcao === 'FAVOR'}
          onClick={() => setOpcao('FAVOR')}
          icone="✅"
          titulo="Sim — Aprovar"
          subtitulo="Votar a favor da proposta"
        />
        <OpcaoItem
          ativa={opcao === 'CONTRA'}
          onClick={() => setOpcao('CONTRA')}
          icone="❌"
          titulo="Não — Rejeitar"
          subtitulo="Votar contra a proposta"
        />
        <OpcaoItem
          ativa={opcao === 'ABSTENCAO'}
          onClick={() => setOpcao('ABSTENCAO')}
          icone="⭕"
          titulo="Abstenção"
          subtitulo="Não se posicionar"
        />
      </div>

      {erro && <p className="field-erro">{erro}</p>}

      <button
        className="btn-primario btn-votar"
        onClick={handleVotar}
        disabled={!opcao || loading}
      >
        {loading ? 'Registrando...' : 'Confirmar meu voto →'}
      </button>
    </div>
  );
}

function OpcaoItem({
  ativa, onClick, icone, titulo, subtitulo,
}: {
  ativa: boolean; onClick: () => void; icone: string; titulo: string; subtitulo: string;
}) {
  return (
    <div className={`opcao-voto ${ativa ? 'opcao-ativa' : ''}`} onClick={onClick}>
      <span className="opcao-icone">{icone}</span>
      <div>
        <p className="opcao-titulo">{titulo}</p>
        <p className="opcao-sub">{subtitulo}</p>
      </div>
    </div>
  );
}

function BarraResultado({ label, votos, pct, cor }: {
  label: string; votos: number; pct: number; cor: string;
}) {
  return (
    <div className="barra-resultado">
      <div className="barra-label">
        <span>{label} ({votos} votos)</span>
        <span className={`resultado-pct ${cor}`}>{pct}%</span>
      </div>
      <div className="barra-bg">
        <div className={`barra-fill ${cor}`} style={{ width: `${pct}%` }} />
      </div>
    </div>
  );
}