import React, { useState, useEffect, useMemo } from 'react';
import './Unidades.css';
import { Search, Plus, Edit, Eye, Ban, ChevronLeft, ChevronRight, X } from 'lucide-react';

interface UnidadeData {
  id: number;
  numero: string;
  bloco: string;
  proprietarioId: number | null;
  inquilinoId: number | null;
  status: 'ADIMPLENTE' | 'INADIMPLENTE' | 'EM_NEGOCIACAO' | 'INATIVO';
  saldoDevedor: number;
}

interface UsuarioData {
  id: number;
  nome: string;
}

const PAGE_SIZE = 10;

export const Unidades: React.FC = () => {
  const [unidades, setUnidades] = useState<UnidadeData[]>([]);
  const [usuarios, setUsuarios] = useState<UsuarioData[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [modalMode, setModalMode] = useState<'create' | 'edit' | null>(null);
  const [editingUnidade, setEditingUnidade] = useState<UnidadeData | null>(null);
  const [viewingUnidade, setViewingUnidade] = useState<UnidadeData | null>(null);
  const [inativandoUnidade, setInativandoUnidade] = useState<UnidadeData | null>(null);
  const [inativarError, setInativarError] = useState<string | null>(null);
  const [inativaIds, setInativaIds] = useState<number[]>([]);
  const [submitting, setSubmitting] = useState(false);

  const [filterBloco, setFilterBloco] = useState('');
  const [filterStatus, setFilterStatus] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);

  const [formData, setFormData] = useState({
    numero: '',
    bloco: 'A',
    proprietarioId: '',
    status: 'ADIMPLENTE',
    saldoDevedor: '0.00',
  });

  useEffect(() => {
    Promise.all([
      fetch('/unidades').then(r => { if (!r.ok) throw new Error('Erro ao buscar unidades'); return r.json(); }),
      fetch('/usuarios').then(r => { if (!r.ok) throw new Error('Erro ao buscar usuários'); return r.json(); }),
    ])
      .then(([fetchedUnidades, fetchedUsuarios]) => {
        setUnidades(fetchedUnidades);
        setUsuarios(fetchedUsuarios);
      })
      .catch(err => setError((err as Error).message))
      .finally(() => setLoading(false));
  }, []);

  const usuarioMap = useMemo(() => {
    const map: Record<number, string> = {};
    usuarios.forEach(u => { map[u.id] = u.nome; });
    return map;
  }, [usuarios]);

  const blocos = useMemo(() => [...new Set(unidades.map(u => u.bloco))].sort(), [unidades]);

  const filtered = useMemo(() => {
    return unidades.filter(u => {
      if (filterBloco && u.bloco !== filterBloco) return false;
      if (filterStatus && u.status !== filterStatus) return false;
      if (searchQuery) {
        const q = searchQuery.toLowerCase();
        const nome = u.proprietarioId != null ? (usuarioMap[u.proprietarioId] ?? '') : '';
        if (!u.numero.toLowerCase().includes(q) && !nome.toLowerCase().includes(q)) return false;
      }
      return true;
    });
  }, [unidades, filterBloco, filterStatus, searchQuery, usuarioMap]);

  const totalPages = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
  const paginated = filtered.slice((currentPage - 1) * PAGE_SIZE, currentPage * PAGE_SIZE);

  const formatarAndar = (numero: string) => `${numero.charAt(0)}º`;

  const formatCurrency = (value: number) =>
    value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });

  const renderStatusBadge = (status: string) => {
    switch (status) {
      case 'ADIMPLENTE': return <span className="badge badge-success">Em dia</span>;
      case 'INADIMPLENTE': return <span className="badge badge-danger">Inadimplente</span>;
      case 'EM_NEGOCIACAO': return <span className="badge badge-warning">Pendente</span>;
      default: return null;
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleFilterChange = (setter: React.Dispatch<React.SetStateAction<string>>) =>
    (e: React.ChangeEvent<HTMLSelectElement | HTMLInputElement>) => {
      setter(e.target.value);
      setCurrentPage(1);
    };

  const emptyForm = { numero: '', bloco: 'A', proprietarioId: '', status: 'ADIMPLENTE', saldoDevedor: '0.00' };

  const openCreate = () => {
    setFormData(emptyForm);
    setEditingUnidade(null);
    setModalMode('create');
  };

  const openEdit = (unidade: UnidadeData) => {
    setFormData({
      numero: unidade.numero,
      bloco: unidade.bloco,
      proprietarioId: String(unidade.proprietarioId ?? ''),
      status: unidade.status,
      saldoDevedor: String(unidade.saldoDevedor),
    });
    setEditingUnidade(unidade);
    setModalMode('edit');
  };

  const closeModal = () => {
    setModalMode(null);
    setEditingUnidade(null);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    const payload = {
      numero: formData.numero,
      bloco: formData.bloco,
      proprietarioId: Number(formData.proprietarioId),
      inquilinoId: editingUnidade?.inquilinoId ?? null,
      status: formData.status,
      saldoDevedor: parseFloat(formData.saldoDevedor) || 0,
    };
    try {
      if (modalMode === 'edit' && editingUnidade) {
        const res = await fetch(`/unidades/${editingUnidade.id}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload),
        });
        if (!res.ok) throw new Error('Erro ao atualizar unidade');
        const updated: UnidadeData = await res.json();
        setUnidades(prev => prev.map(u => u.id === updated.id ? updated : u));
      } else {
        const res = await fetch('/unidades', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload),
        });
        if (!res.ok) throw new Error('Erro ao criar unidade');
        const created: UnidadeData = await res.json();
        setUnidades(prev => [...prev, created]);
      }
      closeModal();
    } catch (err) {
      alert((err as Error).message);
    } finally {
      setSubmitting(false);
    }
  };

  const confirmInativar = async () => {
    if (!inativandoUnidade) return;
    setSubmitting(true);
    try {
      const res = await fetch(`/unidades/${inativandoUnidade.id}`, { method: 'DELETE' });
      if (!res.ok) {
        const body = await res.json().catch(() => null);
        const msg = body?.message ?? 'Erro ao inativar unidade';
        setInativarError(msg);
        return;
      }
      setInativaIds(prev => [...prev, inativandoUnidade.id]);
      setInativandoUnidade(null);
      setInativarError(null);
    } catch {
      setInativarError('Erro ao inativar unidade');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="page-content">
      <div className="page-header">
        <div>
          <h1>Gestão de Unidades</h1>
          <p>
            {loading
              ? 'Carregando...'
              : `${unidades.length} unidades cadastradas${blocos.length > 0 ? ` · Blocos ${blocos.join(', ')}` : ''}`}
          </p>
        </div>
        <button className="btn-primary" onClick={openCreate}>
          <Plus size={18} /> Nova Unidade
        </button>
      </div>

      <div className="filters-bar">
        <select className="filter-select" value={filterBloco} onChange={handleFilterChange(setFilterBloco)}>
          <option value="">Todos os Blocos</option>
          {blocos.map(b => <option key={b} value={b}>Bloco {b}</option>)}
        </select>
        <select className="filter-select" value={filterStatus} onChange={handleFilterChange(setFilterStatus)}>
          <option value="">Todos os Status</option>
          <option value="ADIMPLENTE">Em dia</option>
          <option value="INADIMPLENTE">Inadimplente</option>
          <option value="EM_NEGOCIACAO">Pendente</option>
        </select>
        <div className="search-input-wrapper">
          <Search size={16} />
          <input
            type="text"
            placeholder="Buscar por unidade ou titular"
            className="filter-input"
            value={searchQuery}
            onChange={handleFilterChange(setSearchQuery)}
          />
        </div>
      </div>

      <div className="table-container">
        {error ? (
          <div style={{ padding: '32px', textAlign: 'center', color: 'var(--danger)' }}>{error}</div>
        ) : loading ? (
          <div style={{ padding: '32px', textAlign: 'center', color: 'var(--gray-500)' }}>Carregando unidades...</div>
        ) : (
          <table className="data-table">
            <thead>
              <tr>
                <th>UNIDADE</th>
                <th>TITULAR</th>
                <th>SALDO DEVEDOR</th>
                <th>BLOCO/ANDAR</th>
                <th>STATUS</th>
                <th style={{ textAlign: 'right' }}>AÇÕES</th>
              </tr>
            </thead>
            <tbody>
              {paginated.length === 0 ? (
                <tr>
                  <td colSpan={6} style={{ textAlign: 'center', color: 'var(--gray-400)', padding: '32px' }}>
                    Nenhuma unidade encontrada
                  </td>
                </tr>
              ) : paginated.map((unidade) => {
                const inativa = inativaIds.includes(unidade.id) || unidade.status === 'INATIVO';
                return (
                <tr key={unidade.id} style={{ opacity: inativa ? 0.45 : 1 }}>
                  <td style={{ fontWeight: 600 }}>{unidade.numero}</td>
                  <td>
                    {unidade.proprietarioId != null
                      ? (usuarioMap[unidade.proprietarioId] ?? `Usuário #${unidade.proprietarioId}`)
                      : '—'}
                  </td>
                  <td>{formatCurrency(unidade.saldoDevedor)}</td>
                  <td>Bloco {unidade.bloco} - {formatarAndar(unidade.numero)}</td>
                  <td>
                    {inativa
                      ? <span className="badge badge-inactive">Inativo</span>
                      : renderStatusBadge(unidade.status)}
                  </td>
                  <td className="actions-cell">
                    <button className="action-icon" title="Editar" disabled={inativa} onClick={() => openEdit(unidade)}><Edit size={16} /></button>
                    <button className="action-icon" title="Visualizar" onClick={() => setViewingUnidade(unidade)}><Eye size={16} /></button>
                    <button className="action-icon" title="Inativar" disabled={inativa} onClick={() => setInativandoUnidade(unidade)}>
                      <Ban size={16} />
                    </button>
                  </td>
                </tr>
                );
              })}
            </tbody>
          </table>
        )}

        {!loading && !error && (
          <div className="pagination">
            <span>
              Mostrando {paginated.length} de {filtered.length} unidades
              {filtered.length !== unidades.length ? ` (${unidades.length} total)` : ''}
            </span>
            <div className="pagination-controls">
              <button
                className="page-btn"
                disabled={currentPage === 1}
                onClick={() => setCurrentPage(p => p - 1)}
              >
                <ChevronLeft size={16} /> Anterior
              </button>
              {Array.from({ length: Math.min(totalPages, 5) }, (_, i) => i + 1).map(p => (
                <button
                  key={p}
                  className={`page-number ${p === currentPage ? 'active' : ''}`}
                  onClick={() => setCurrentPage(p)}
                >
                  {p}
                </button>
              ))}
              <button
                className="page-btn"
                disabled={currentPage === totalPages}
                onClick={() => setCurrentPage(p => p + 1)}
              >
                Próxima <ChevronRight size={16} />
              </button>
            </div>
          </div>
        )}
      </div>

      {modalMode !== null && (
        <div className="unidade-modal-overlay">
          <div className="unidade-modal-card">
            <div className="unidade-modal-header">
              <h2>{modalMode === 'edit' ? 'Editar Unidade' : 'Adicionar Unidade'}</h2>
              <button className="btn-close" onClick={closeModal}>
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="unidade-modal-form">
              <div className="form-row">
                <div className="form-group">
                  <label>UNIDADE</label>
                  <input
                    type="text"
                    name="numero"
                    placeholder="Ex: 304"
                    value={formData.numero}
                    onChange={handleInputChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>BLOCO</label>
                  <select name="bloco" value={formData.bloco} onChange={handleInputChange}>
                    <option value="A">Bloco A</option>
                    <option value="B">Bloco B</option>
                    <option value="C">Bloco C</option>
                    <option value="D">Bloco D</option>
                  </select>
                </div>
              </div>

              <div className="form-group">
                <label>PROPRIETÁRIO</label>
                <select name="proprietarioId" value={formData.proprietarioId} onChange={handleInputChange} required>
                  <option value="">Selecione o proprietário</option>
                  {usuarios.map(u => (
                    <option key={u.id} value={u.id}>{u.nome}</option>
                  ))}
                </select>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>STATUS</label>
                  <select name="status" value={formData.status} onChange={handleInputChange}>
                    <option value="ADIMPLENTE">Em dia</option>
                    <option value="INADIMPLENTE">Inadimplente</option>
                    <option value="EM_NEGOCIACAO">Pendente</option>
                  </select>
                </div>
                <div className="form-group">
                  <label>SALDO DEVEDOR</label>
                  <input
                    type="number"
                    name="saldoDevedor"
                    step="0.01"
                    min="0"
                    placeholder="0.00"
                    value={formData.saldoDevedor}
                    onChange={handleInputChange}
                    required
                  />
                </div>
              </div>

              <div className="unidade-modal-footer">
                <button type="button" className="btn-cancelar" onClick={closeModal}>
                  Cancelar
                </button>
                <button type="submit" className="btn-salvar" disabled={submitting}>
                  {submitting ? 'Salvando...' : modalMode === 'edit' ? 'Salvar Alterações' : 'Salvar Unidade'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {viewingUnidade && (
        <div className="unidade-modal-overlay">
          <div className="unidade-modal-card">
            <div className="unidade-modal-header">
              <h2>Detalhes da Unidade</h2>
              <button className="btn-close" onClick={() => setViewingUnidade(null)}>
                <X size={20} />
              </button>
            </div>

            <div className="detalhe-grid">
              <div className="detalhe-item">
                <span className="detalhe-label">UNIDADE</span>
                <span className="detalhe-valor">{viewingUnidade.numero}</span>
              </div>
              <div className="detalhe-item">
                <span className="detalhe-label">BLOCO</span>
                <span className="detalhe-valor">Bloco {viewingUnidade.bloco}</span>
              </div>
              <div className="detalhe-item">
                <span className="detalhe-label">ANDAR</span>
                <span className="detalhe-valor">{viewingUnidade.numero.charAt(0)}º andar</span>
              </div>
              <div className="detalhe-item">
                <span className="detalhe-label">STATUS</span>
                <span className="detalhe-valor">{renderStatusBadge(viewingUnidade.status)}</span>
              </div>
              <div className="detalhe-item detalhe-full">
                <span className="detalhe-label">PROPRIETÁRIO</span>
                <span className="detalhe-valor">
                  {viewingUnidade.proprietarioId != null
                    ? (usuarioMap[viewingUnidade.proprietarioId] ?? `Usuário #${viewingUnidade.proprietarioId}`)
                    : '—'}
                </span>
              </div>
              <div className="detalhe-item detalhe-full">
                <span className="detalhe-label">INQUILINO</span>
                <span className="detalhe-valor">
                  {viewingUnidade.inquilinoId != null
                    ? (usuarioMap[viewingUnidade.inquilinoId] ?? `Usuário #${viewingUnidade.inquilinoId}`)
                    : '—'}
                </span>
              </div>
              <div className="detalhe-item detalhe-full">
                <span className="detalhe-label">SALDO DEVEDOR</span>
                <span className="detalhe-valor" style={{ color: viewingUnidade.saldoDevedor > 0 ? '#dc2626' : '#16a34a', fontWeight: 600 }}>
                  {formatCurrency(viewingUnidade.saldoDevedor)}
                </span>
              </div>
            </div>

            <div className="unidade-modal-footer">
              <button type="button" className="btn-cancelar" onClick={() => setViewingUnidade(null)}>
                Fechar
              </button>
              <button
                type="button"
                className="btn-salvar"
                disabled={inativaIds.includes(viewingUnidade.id) || viewingUnidade.status === 'INATIVO'}
                onClick={() => { setViewingUnidade(null); openEdit(viewingUnidade); }}
              >
                <Edit size={15} style={{ marginRight: 6 }} /> Editar
              </button>
            </div>
          </div>
        </div>
      )}

      {inativandoUnidade && (
        <div className="unidade-modal-overlay">
          <div className="unidade-modal-card" style={{ maxWidth: '420px' }}>
            <div className="unidade-modal-header">
              <h2>Inativar Unidade</h2>
              <button className="btn-close" onClick={() => { setInativandoUnidade(null); setInativarError(null); }}>
                <X size={20} />
              </button>
            </div>
            <div className="inativar-modal-body">
              <p>
                Tem certeza que deseja inativar a unidade <strong>{inativandoUnidade.numero}</strong> — Bloco <strong>{inativandoUnidade.bloco}</strong>?
              </p>
              <p className="inativar-modal-hint">
                A unidade será removida do sistema e nenhuma operação poderá ser realizada sobre ela.
              </p>
              {inativarError && (
                <p style={{ color: '#dc2626', fontSize: '13px', marginTop: '8px', fontWeight: 500 }}>
                  {inativarError}
                </p>
              )}
            </div>
            <div className="unidade-modal-footer">
              <button type="button" className="btn-cancelar" onClick={() => { setInativandoUnidade(null); setInativarError(null); }}>
                Cancelar
              </button>
              <button type="button" className="btn-inativar" disabled={submitting} onClick={confirmInativar}>
                {submitting ? 'Inativando...' : 'Inativar Unidade'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
