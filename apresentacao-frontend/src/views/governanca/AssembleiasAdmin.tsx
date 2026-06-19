import { useEffect, useState } from 'react';
import { Plus } from 'lucide-react';
import type { Pauta } from './types/pauta';
import type { Assembleia } from './types/assembleia';
import { pautaService } from './services/pautaService';
import { assembleiaService } from './services/assembleiaService';
import PautaCardAdmin from './components/PautaCardAdm';
import ModalCriarPauta from './components/ModalCriarPauta';
import ModalAssembleia from './components/ModalAssembleia';
import ProximaAssembleiaCard from './components/ProximaAssembleiaCard';
import HistoricoAssembleias from './components/HistoricoAssembleias';
import './Assembleias.css';

export default function AssembleiasAdmin() {
  const [pautas, setPautas] = useState<Pauta[]>([]);
  const [assembleias, setAssembleias] = useState<Assembleia[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalPautaAberto, setModalPautaAberto] = useState(false);
  const [modalAssembleia, setModalAssembleia] = useState<'criar' | 'editar' | null>(null);
  const [processando, setProcessando] = useState(false);
  const [erro, setErro] = useState('');

  async function carregarPautas() {
    const dados = await pautaService.listar();
    setPautas(dados);
  }

  async function carregarAssembleias() {
    setErro('');
    const dados = await assembleiaService.listar();
    setAssembleias(dados);
  }

  async function carregarTudo() {
    setLoading(true);
    try {
      await Promise.all([carregarPautas(), carregarAssembleias()]);
    } finally {
      setLoading(false);
    }
  }

  async function recarregarAposSalvarAssembleia() {
    await Promise.all([carregarAssembleias(), carregarPautas()]);
  }

  useEffect(() => {
    carregarTudo();
  }, []);

  // GET /pautas só retorna pautas com status ABERTA (regra do backend), então
  // não há como listar pautas já encerradas aqui.
  const abertas = pautas.filter((p) => p.status === 'ABERTA');

  const agora = new Date();
  const proximaAssembleia = assembleias
    .filter((a) => a.status === 'AGENDADA' && new Date(a.dataHora) >= agora)
    .sort((a, b) => new Date(a.dataHora).getTime() - new Date(b.dataHora).getTime())[0];

  const assembleiasAgendadas = assembleias.filter((a) => a.status === 'AGENDADA');

  const historico = assembleias
    .filter((a) => a.id !== proximaAssembleia?.id)
    .sort((a, b) => new Date(b.dataHora).getTime() - new Date(a.dataHora).getTime());

  async function handleCancelar() {
    if (!proximaAssembleia) return;
    if (!confirm(`Cancelar a assembleia "${proximaAssembleia.titulo}"?`)) return;
    setProcessando(true);
    setErro('');
    try {
      await assembleiaService.cancelar(proximaAssembleia.id);
      await Promise.all([carregarAssembleias(), carregarPautas()]);
    } catch (e: any) {
      setErro(e.message);
    } finally {
      setProcessando(false);
    }
  }

  async function handleEncerrar() {
    if (!proximaAssembleia) return;
    if (!confirm(`Encerrar a assembleia "${proximaAssembleia.titulo}"?`)) return;
    setProcessando(true);
    setErro('');
    try {
      await assembleiaService.encerrar(proximaAssembleia.id);
      await Promise.all([carregarAssembleias(), carregarPautas()]);
    } catch (e: any) {
      setErro(e.message);
    } finally {
      setProcessando(false);
    }
  }

  return (
    <div className="assembleias-page assembleias-page-admin">
      <div className="assembleias-header">
        <div>
          <h1>Assembleias e Votações</h1>
          <p className="assembleias-sub">Gestão de pautas, votos e resultados</p>
        </div>
        <div className="assembleias-header-actions">
          {assembleiasAgendadas.length > 0 && (
            <button className="btn-secundario" onClick={() => setModalPautaAberto(true)}>
              Adicionar Pauta
            </button>
          )}
          <button className="criar-assembleia-btn" onClick={() => setModalAssembleia('criar')}>
            <Plus size={16} /> Criar Assembleia
          </button>
        </div>
      </div>

      {erro && <p className="field-erro" style={{ marginBottom: 12 }}>{erro}</p>}

      {loading ? (
        <p className="loading-text">Carregando...</p>
      ) : (
        <>
          <div className="assembleias-grid">
            {proximaAssembleia ? (
              <ProximaAssembleiaCard
                assembleia={proximaAssembleia}
                onEditar={() => setModalAssembleia('editar')}
                onCancelar={handleCancelar}
                onEncerrar={handleEncerrar}
                processando={processando}
              />
            ) : (
              <div className="card-box-empty">
                <p>Nenhuma assembleia agendada no momento.</p>
              </div>
            )}

            <div>
              {abertas.length > 0 ? (
                abertas.map((p) => (
                  <PautaCardAdmin key={p.id} pauta={p} onAtualizar={carregarPautas} />
                ))
              ) : (
                <div className="card-box-empty">
                  <p>Nenhuma votação em curso.</p>
                </div>
              )}
            </div>
          </div>

          <div className="historico-section">
            <h3>Histórico de Assembleias</h3>
            <HistoricoAssembleias assembleias={historico} />
          </div>
        </>
      )}

      {modalPautaAberto && (
        <ModalCriarPauta
          assembleias={assembleiasAgendadas}
          onClose={() => setModalPautaAberto(false)}
          onCriada={() => Promise.all([carregarPautas(), carregarAssembleias()])}
        />
      )}

      {modalAssembleia && (
        <ModalAssembleia
          assembleiaEmEdicao={modalAssembleia === 'editar' ? proximaAssembleia : null}
          onClose={() => setModalAssembleia(null)}
          onSalva={recarregarAposSalvarAssembleia}
        />
      )}
    </div>
  );
}
