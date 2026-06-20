import { useEffect, useRef, useState } from 'react';
import {
  Plus, X, Pencil, PowerOff, Download, FileText, Upload, AlertCircle, WifiOff,
} from 'lucide-react';
import { documentoService } from './services/documentoService';
import type { Documento } from './types/documento';
import { CATEGORIAS, type CategoriaDocumento } from './types/documento';
import './Documentos.css';

type TabFiltro = 'todas' | 'ativos';
type ModalTipo = 'criar' | 'editar' | null;

function formatarData(dateStr: string | null): string {
  if (!dateStr) return '—';
  const [ano, mes, dia] = dateStr.split('T')[0].split('-');
  return `${dia}/${mes}/${ano}`;
}

function calcularStatus(doc: Documento): 'ATIVO' | 'INATIVO' | 'ATENCAO' {
  if (doc.status === 'INATIVO') return 'INATIVO';
  if (doc.dataValidade) {
    const hoje = new Date();
    const validade = new Date(doc.dataValidade);
    const diffDias = Math.ceil((validade.getTime() - hoje.getTime()) / (1000 * 60 * 60 * 24));
    if (diffDias <= 30) return 'ATENCAO';
  }
  return 'ATIVO';
}

function StatusBadge({ doc }: { doc: Documento }) {
  const status = calcularStatus(doc);
  if (status === 'ATENCAO') {
    const dias = doc.dataValidade
      ? Math.ceil((new Date(doc.dataValidade).getTime() - Date.now()) / (1000 * 60 * 60 * 24))
      : 0;
    return (
      <span className="status-badge-doc status-atencao">
        <AlertCircle size={12} />
        Atenção{dias > 0 ? ` — Expira em ${dias} dias` : ' — Expirado'}
      </span>
    );
  }
  if (status === 'INATIVO') {
    return <span className="status-badge-doc status-inativo">Inativo</span>;
  }
  return <span className="status-badge-doc status-ativo">Ativo</span>;
}

export default function DocumentosAdmin() {
  const [documentos, setDocumentos] = useState<Documento[]>([]);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState('');
  const [modoMock, setModoMock] = useState(false);
  const [tab, setTab] = useState<TabFiltro>('ativos');
  const [modalTipo, setModalTipo] = useState<ModalTipo>(null);
  const [docEmEdicao, setDocEmEdicao] = useState<Documento | null>(null);

  // Form state
  const [formNome, setFormNome] = useState('');
  const [formCategoria, setFormCategoria] = useState<CategoriaDocumento>('ATA');
  const [formValidade, setFormValidade] = useState('');
  const [formArquivo, setFormArquivo] = useState<File | null>(null);
  const [formNotificar, setFormNotificar] = useState(true);
  const [formEnviando, setFormEnviando] = useState(false);
  const [formErro, setFormErro] = useState('');
  const [dragging, setDragging] = useState(false);

  const fileInputRef = useRef<HTMLInputElement>(null);

  async function carregar() {
    setLoading(true);
    setErro('');
    try {
      const dados = await documentoService.listar(true);
      setDocumentos(dados);
      setModoMock(documentoService.isMock);
    } catch (e: any) {
      setErro(e.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { carregar(); }, []);

  const documentosFiltrados = tab === 'ativos'
    ? documentos.filter(d => d.status === 'ATIVO')
    : documentos;

  function abrirCriar() {
    setDocEmEdicao(null);
    setFormNome('');
    setFormCategoria('ATA');
    setFormValidade('');
    setFormArquivo(null);
    setFormNotificar(true);
    setFormErro('');
    setModalTipo('criar');
  }

  function abrirEditar(doc: Documento) {
    setDocEmEdicao(doc);
    setFormNome(doc.nome);
    setFormCategoria((doc.categoria as CategoriaDocumento) || 'ATA');
    setFormValidade(doc.dataValidade?.split('T')[0] ?? '');
    setFormArquivo(null);
    setFormErro('');
    setModalTipo('editar');
  }

  function fecharModal() {
    setModalTipo(null);
    setDocEmEdicao(null);
    setFormArquivo(null);
    setFormErro('');
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setFormErro('');

    if (modalTipo === 'criar' && !formArquivo) {
      setFormErro('Selecione um arquivo para fazer upload.');
      return;
    }

    setFormEnviando(true);
    try {
      if (modalTipo === 'criar') {
        await documentoService.cadastrar(
          formNome,
          formCategoria,
          formValidade || null,
          formArquivo!
        );
      } else if (modalTipo === 'editar' && docEmEdicao && formArquivo) {
        await documentoService.atualizarArquivo(docEmEdicao.id, formArquivo);
      }
      fecharModal();
      await carregar();
    } catch (e: any) {
      setFormErro(e.message);
    } finally {
      setFormEnviando(false);
    }
  }

  async function handleInativar(doc: Documento) {
    if (!confirm(`Deseja inativar o documento "${doc.nome}"?`)) return;
    try {
      await documentoService.inativar(doc.id);
      await carregar();
    } catch (e: any) {
      setErro(e.message);
    }
  }

  function handleDrop(e: React.DragEvent) {
    e.preventDefault();
    setDragging(false);
    const file = e.dataTransfer.files[0];
    if (file) setFormArquivo(file);
  }

  return (
    <div className="documentos-page">
      <div className="documentos-header">
        <div>
          <h1>Documentos Oficiais</h1>
          <p>Repositório central de atas, contratos e alvarás</p>
        </div>
        <button className="btn-primary" onClick={abrirCriar}>
          <Plus size={15} /> Novo Documento
        </button>
      </div>

      {modoMock && (
        <div style={{
          display: 'flex', alignItems: 'center', gap: 8,
          background: '#fef3c7', color: '#92400e',
          border: '1px solid #fde68a', borderRadius: 'var(--radius-md)',
          padding: '9px 14px', fontSize: 13, marginBottom: 12,
        }}>
          <WifiOff size={14} />
          <span><strong>Modo Demonstração</strong> — backend offline. Os documentos são salvos localmente nesta sessão.</span>
        </div>
      )}

      {erro && <div className="doc-error">{erro}</div>}

      <div className="doc-tabs">
        <button
          className={`doc-tab ${tab === 'ativos' ? 'active' : ''}`}
          onClick={() => setTab('ativos')}
        >
          Somente Ativos
        </button>
        <button
          className={`doc-tab ${tab === 'todas' ? 'active' : ''}`}
          onClick={() => setTab('todas')}
        >
          Todas as Categorias
        </button>
      </div>

      <div className="doc-table-wrapper">
        {loading ? (
          <div className="doc-loading">Carregando documentos...</div>
        ) : documentosFiltrados.length === 0 ? (
          <div className="doc-empty">
            <FileText size={40} />
            <p>Nenhum documento encontrado.</p>
          </div>
        ) : (
          <table className="doc-table">
            <thead>
              <tr>
                <th>Título</th>
                <th>Categoria</th>
                <th>Atualização</th>
                <th>Validade</th>
                <th>Status</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {documentosFiltrados.map(doc => (
                <tr key={doc.id}>
                  <td>
                    <span className="doc-title">{doc.nome}</span>
                    {doc.nomeArquivoAtual && (
                      <div style={{ fontSize: 11, color: 'var(--gray-400)', marginTop: 2 }}>
                        {doc.nomeArquivoAtual}
                        {doc.versaoAtual && doc.versaoAtual > 1 && ` · v${doc.versaoAtual}`}
                      </div>
                    )}
                  </td>
                  <td>{CATEGORIAS[doc.categoria as CategoriaDocumento] ?? doc.categoria}</td>
                  <td>{formatarData(doc.dataCriacao)}</td>
                  <td>{formatarData(doc.dataValidade)}</td>
                  <td><StatusBadge doc={doc} /></td>
                  <td>
                    <div className="doc-actions">
                      <button
                        className="icon-btn"
                        title="Editar / Nova versão"
                        onClick={() => abrirEditar(doc)}
                        disabled={doc.status === 'INATIVO'}
                      >
                        <Pencil size={14} />
                      </button>
                      <a
                        className="icon-btn"
                        href={documentoService.downloadUrl(doc.id)}
                        title="Baixar"
                        target="_blank"
                        rel="noopener noreferrer"
                      >
                        <Download size={14} />
                      </a>
                      {doc.status === 'ATIVO' && (
                        <button
                          className="icon-btn danger"
                          title="Inativar"
                          onClick={() => handleInativar(doc)}
                        >
                          <PowerOff size={14} />
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Modal Criar / Editar */}
      {modalTipo && (
        <div className="modal-overlay" onClick={e => e.target === e.currentTarget && fecharModal()}>
          <div className="modal-card">
            <div className="modal-header">
              <h3>{modalTipo === 'criar' ? 'Novo Documento Oficial' : 'Editar Documento Oficial'}</h3>
              <button className="modal-close" onClick={fecharModal}><X size={18} /></button>
            </div>

            <form onSubmit={handleSubmit}>
              <div className="modal-body">
                {/* Nome */}
                <div className="form-field">
                  <label>Título do Documento</label>
                  <input
                    type="text"
                    placeholder="Ex: Ata da Reunião Extraordinária"
                    value={formNome}
                    onChange={e => setFormNome(e.target.value)}
                    required
                    disabled={modalTipo === 'editar'}
                  />
                </div>

                {/* Categoria + Validade */}
                <div className="form-row">
                  <div className="form-field">
                    <label>Categoria</label>
                    <select
                      value={formCategoria}
                      onChange={e => setFormCategoria(e.target.value as CategoriaDocumento)}
                      disabled={modalTipo === 'editar'}
                    >
                      {Object.entries(CATEGORIAS).map(([key, label]) => (
                        <option key={key} value={key}>{label}</option>
                      ))}
                    </select>
                  </div>
                  <div className="form-field">
                    <label>Prazo de Validade (Opcional)</label>
                    <input
                      type="date"
                      value={formValidade}
                      onChange={e => setFormValidade(e.target.value)}
                      disabled={modalTipo === 'editar'}
                    />
                  </div>
                </div>

                {/* Upload */}
                <div className="form-field">
                  <label>Arquivo (PDF, DOCX)</label>
                  <div
                    className={`upload-area ${dragging ? 'dragging' : ''} ${formArquivo ? 'upload-area-selected' : ''}`}
                    onClick={() => fileInputRef.current?.click()}
                    onDragOver={e => { e.preventDefault(); setDragging(true); }}
                    onDragLeave={() => setDragging(false)}
                    onDrop={handleDrop}
                  >
                    <Upload size={20} style={{ marginBottom: 6, display: 'block', margin: '0 auto 8px' }} />
                    {formArquivo
                      ? <span>{formArquivo.name}</span>
                      : <span>Clique para selecionar o arquivo ou arraste para cá</span>
                    }
                    <input
                      ref={fileInputRef}
                      type="file"
                      accept=".pdf,.docx,.doc"
                      onChange={e => setFormArquivo(e.target.files?.[0] ?? null)}
                    />
                  </div>
                </div>

                {/* Notificação */}
                {modalTipo === 'criar' && (
                  <label className="form-checkbox">
                    <input
                      type="checkbox"
                      checked={formNotificar}
                      onChange={e => setFormNotificar(e.target.checked)}
                    />
                    Gerar notificação ao sistema (USI!) antes do vencimento
                  </label>
                )}

                {formErro && <div className="doc-error" style={{ marginBottom: 0 }}>{formErro}</div>}
              </div>

              <div className="modal-footer">
                <button type="button" className="btn-secondary" onClick={fecharModal}>
                  Cancelar
                </button>
                <button type="submit" className="btn-submit" disabled={formEnviando}>
                  {formEnviando ? 'Enviando...' : <><Upload size={14} /> Fazer Upload</>}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
