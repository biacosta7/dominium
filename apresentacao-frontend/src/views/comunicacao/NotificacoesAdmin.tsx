import { useEffect, useState } from 'react';
import { Bell, Plus, X, CheckCheck, Info } from 'lucide-react';
import { notificacaoService } from './services/notificacaoService';
import type { Notificacao } from './types/notificacao';
import { TIPOS_NOTIFICACAO } from './types/notificacao';
import './Notificacoes.css';
import './Documentos.css'; // reutiliza modal styles

const SINDICO_ID = 2;

function formatarDataHora(dateStr: string): string {
  const d = new Date(dateStr);
  const dia = String(d.getDate()).padStart(2, '0');
  const mes = String(d.getMonth() + 1).padStart(2, '0');
  const ano = d.getFullYear();
  const hora = String(d.getHours()).padStart(2, '0');
  const min = String(d.getMinutes()).padStart(2, '0');
  return `${dia}/${mes}/${ano} · ${hora}:${min}`;
}

interface NovoEnvioForm {
  tipo: string;
  titulo: string;
  conteudo: string;
  publico: string;
}

const FORM_INICIAL: NovoEnvioForm = {
  tipo: 'GERAL',
  titulo: '',
  conteudo: '',
  publico: 'TODOS',
};

export default function NotificacoesAdmin() {
  const [notificacoes, setNotificacoes] = useState<Notificacao[]>([]);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState('');
  const [modalAberto, setModalAberto] = useState(false);
  const [form, setForm] = useState<NovoEnvioForm>(FORM_INICIAL);
  const [formEnviando, setFormEnviando] = useState(false);
  const [formErro, setFormErro] = useState('');
  const [sucesso, setSucesso] = useState('');

  async function carregar() {
    setLoading(true);
    setErro('');
    try {
      const dados = await notificacaoService.listar();
      setNotificacoes(dados.sort((a, b) =>
        new Date(b.criadaEm).getTime() - new Date(a.criadaEm).getTime()
      ));
    } catch (e: any) {
      setErro(e.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { carregar(); }, []);

  async function handleMarcarLida(notif: Notificacao) {
    if (notif.lida) return;
    try {
      await notificacaoService.marcarComoLida(notif.id, SINDICO_ID);
      setNotificacoes(prev =>
        prev.map(n => n.id === notif.id ? { ...n, lida: true } : n)
      );
    } catch (e: any) {
      setErro(e.message);
    }
  }

  async function handleEnviar(e: React.FormEvent) {
    e.preventDefault();
    setFormErro('');
    if (!form.titulo.trim()) {
      setFormErro('Informe o título da notificação.');
      return;
    }
    setFormEnviando(true);
    try {
      // Nota: o backend ainda não expõe endpoint REST para envio manual.
      // Esta chamada retornará 404 até o endpoint ser implementado.
      const res = await fetch('/notificacoes/enviar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'X-Sindico-Id': String(SINDICO_ID) },
        body: JSON.stringify({
          mensagem: `[${form.titulo}] ${form.conteudo}`,
          tipo: form.tipo,
          publico: form.publico,
        }),
      });
      if (!res.ok) throw new Error('Endpoint de envio ainda não disponível no backend.');
      setSucesso('Notificação disparada com sucesso!');
      setModalAberto(false);
      setForm(FORM_INICIAL);
      await carregar();
    } catch (e: any) {
      // Fallback: simula localmente para fins de demonstração
      const novaSimulada: Notificacao = {
        id: Date.now(),
        usuarioId: SINDICO_ID,
        mensagem: `[${form.titulo}] ${form.conteudo}`,
        tipo: form.tipo,
        lida: false,
        criadaEm: new Date().toISOString(),
      };
      setNotificacoes(prev => [novaSimulada, ...prev]);
      setSucesso('Comunicado registrado (modo simulado — backend offline).');
      setModalAberto(false);
      setForm(FORM_INICIAL);
    } finally {
      setFormEnviando(false);
    }
  }

  const lidas = notificacoes.filter(n => n.lida).length;
  const total = notificacoes.length;

  return (
    <div className="notif-page">
      <div className="notif-header">
        <div>
          <h1>Notificações</h1>
          <p>Painel de comunicados do síndico e eventos do sistema</p>
        </div>
        <button className="btn-primary" onClick={() => { setModalAberto(true); setFormErro(''); setSucesso(''); }}>
          <Plus size={15} /> Novo Comunicado
        </button>
      </div>

      {erro && <div className="notif-error">{erro}</div>}
      {sucesso && (
        <div style={{ background: '#d1fae5', color: '#065f46', padding: '10px 14px', borderRadius: 'var(--radius-md)', fontSize: 13, marginBottom: 16 }}>
          {sucesso}
        </div>
      )}

      <div className="notif-section-bar">
        <h2>Registro de Disparos (Histórico Imutável)</h2>
        <span className="notif-filter-label">
          {total > 0 ? `${lidas}/${total} lidas` : 'Todos os Disparos'}
        </span>
      </div>

      <div className="notif-table-wrapper">
        {loading ? (
          <div className="notif-loading">Carregando notificações...</div>
        ) : notificacoes.length === 0 ? (
          <div className="notif-empty">
            <Bell size={40} />
            <p>Nenhuma notificação registrada.</p>
          </div>
        ) : (
          <table className="notif-table">
            <thead>
              <tr>
                <th>Título / Categoria</th>
                <th>Destinatário(s)</th>
                <th>Data do Disparo</th>
                <th>Status / Leitura</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {notificacoes.map(notif => (
                <tr key={notif.id}>
                  <td>
                    <div className="notif-titulo">
                      {notif.mensagem.length > 60
                        ? notif.mensagem.substring(0, 60) + '…'
                        : notif.mensagem}
                    </div>
                    <div className="notif-subtipo">
                      {TIPOS_NOTIFICACAO[notif.tipo] ?? notif.tipo}
                    </div>
                  </td>
                  <td style={{ color: 'var(--gray-600)', fontSize: 13 }}>
                    Usuário #{notif.usuarioId}
                  </td>
                  <td style={{ color: 'var(--gray-500)', fontSize: 12, whiteSpace: 'nowrap' }}>
                    {formatarDataHora(notif.criadaEm)}
                  </td>
                  <td>
                    {notif.lida ? (
                      <div className="notif-progress-wrap">
                        <div className="notif-progress-bar">
                          <div className="notif-progress-fill" style={{ width: '100%' }} />
                        </div>
                        <span className="notif-progress-label">Lida (Confirmada)</span>
                      </div>
                    ) : (
                      <span className="notif-lida-badge notif-nao-lida">
                        Entregue (Não lida)
                      </span>
                    )}
                  </td>
                  <td>
                    <div className="doc-actions">
                      {!notif.lida && (
                        <button
                          className="btn-mark-read"
                          title="Marcar como lida"
                          onClick={() => handleMarcarLida(notif)}
                        >
                          <CheckCheck size={13} /> Marcar lida
                        </button>
                      )}
                      <button className="icon-btn" title="Detalhes">
                        <Info size={14} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Modal Novo Comunicado */}
      {modalAberto && (
        <div className="modal-overlay" onClick={e => e.target === e.currentTarget && setModalAberto(false)}>
          <div className="modal-card">
            <div className="modal-header">
              <h3>Enviar Novo Comunicado</h3>
              <button className="modal-close" onClick={() => setModalAberto(false)}>
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleEnviar}>
              <div className="modal-body">
                {/* Regra informativa */}
                <div className="notif-regra-bar">
                  <strong>Regra (USI!):</strong> Toda notificação é registrada no sistema, não pode ser
                  excluída pelo morador e gera relatório de "leitura". Eventos como Multas, Taxas e
                  Assembleias disparam avisos automaticamente.
                </div>

                {/* Tipo + Público */}
                <div className="form-row">
                  <div className="form-field">
                    <label>Tipo de Mensagem</label>
                    <select
                      value={form.tipo}
                      onChange={e => setForm(f => ({ ...f, tipo: e.target.value }))}
                    >
                      {Object.entries(TIPOS_NOTIFICACAO).map(([k, v]) => (
                        <option key={k} value={k}>{v}</option>
                      ))}
                    </select>
                  </div>
                  <div className="form-field">
                    <label>Público / Destinatário</label>
                    <select
                      value={form.publico}
                      onChange={e => setForm(f => ({ ...f, publico: e.target.value }))}
                    >
                      <option value="TODOS">Todos os Moradores (Broadcast)</option>
                      <option value="SINDICO">Somente Síndico</option>
                    </select>
                  </div>
                </div>

                {/* Título */}
                <div className="form-field">
                  <label>Título da Notificação</label>
                  <input
                    type="text"
                    placeholder="Ex: Limpeza das caixas d'água neste sábado"
                    value={form.titulo}
                    onChange={e => setForm(f => ({ ...f, titulo: e.target.value }))}
                    required
                  />
                </div>

                {/* Conteúdo */}
                <div className="form-field">
                  <label>Conteúdo da Mensagem</label>
                  <textarea
                    rows={4}
                    placeholder="Digite a mensagem que aparecerá na tela inicial do morador..."
                    value={form.conteudo}
                    onChange={e => setForm(f => ({ ...f, conteudo: e.target.value }))}
                    style={{
                      width: '100%',
                      padding: '9px 12px',
                      border: '1px solid var(--gray-200)',
                      borderRadius: 'var(--radius-md)',
                      fontSize: 13,
                      resize: 'vertical',
                      boxSizing: 'border-box',
                      outline: 'none',
                      fontFamily: 'inherit',
                    }}
                  />
                </div>

                {formErro && (
                  <div className="notif-error" style={{ marginBottom: 0 }}>{formErro}</div>
                )}
              </div>

              <div className="modal-footer">
                <button type="button" className="btn-secondary" onClick={() => setModalAberto(false)}>
                  Cancelar
                </button>
                <button type="submit" className="btn-submit" disabled={formEnviando}>
                  {formEnviando ? 'Enviando...' : <><Bell size={14} /> Disparar Notificação</>}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
