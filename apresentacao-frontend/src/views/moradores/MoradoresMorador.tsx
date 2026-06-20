import React, { useEffect, useState } from 'react';
import { Search, Plus, Edit2, Trash2, Eye, X, Users, Loader2, AlertCircle } from 'lucide-react';
import { moradorService } from './services/moradorService';
import type { Vinculo } from './types/morador';
import './MoradoresAdmin.css';

interface MoradoresMoradorProps {
  unidadeId: number;
  requesterId: number;
  unidadeNumero: string;
  unidadeBloco?: string;
}

export default function MoradoresMorador({ unidadeId, requesterId, unidadeNumero, unidadeBloco }: MoradoresMoradorProps) {
  const [vinculos, setVinculos] = useState<Vinculo[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [searchTerm, setSearchTerm] = useState('');

  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showViewModal, setShowViewModal] = useState(false);
  const [selectedVinculo, setSelectedVinculo] = useState<Vinculo | null>(null);

  const [formName, setFormName] = useState('');
  const [formPhone, setFormPhone] = useState('');
  const [formEmail, setFormEmail] = useState('');
  const [formCpf, setFormCpf] = useState('');
  const [formError, setFormError] = useState<string | null>(null);
  const [formSubmitting, setFormSubmitting] = useState(false);

  async function loadData() {
    setLoading(true);
    setError(null);
    try {
      const data = await moradorService.fetchMoradoresDaUnidade(unidadeId);
      setVinculos(data.filter(v => v.status === 'ATIVO'));
    } catch (err: any) {
      setError(err.message || 'Erro ao carregar moradores da sua unidade.');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadData();
  }, [unidadeId]);

  const handleOpenAddModal = () => {
    setFormName('');
    setFormPhone('');
    setFormEmail('');
    setFormCpf('');
    setFormError(null);
    setShowAddModal(true);
  };

  const handleOpenEditModal = (vinculo: Vinculo) => {
    setSelectedVinculo(vinculo);
    setFormName(vinculo.usuario.nome);
    setFormPhone(vinculo.usuario.telefone || '');
    setFormEmail(vinculo.usuario.email);
    setFormCpf(vinculo.usuario.cpf || '');
    setFormError(null);
    setShowEditModal(true);
  };

  const handleOpenViewModal = (vinculo: Vinculo) => {
    setSelectedVinculo(vinculo);
    setShowViewModal(true);
  };

  const handleAddSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormError(null);
    setFormSubmitting(true);

    try {
      await moradorService.addMorador(unidadeId, {
        nome: formName,
        email: formEmail,
        telefone: formPhone,
        cpf: formCpf,
        tipoViculo: 'DEPENDENTE'
      }, requesterId);

      setShowAddModal(false);
      loadData();
    } catch (err: any) {
      setFormError(err.message || 'Erro ao adicionar dependente.');
    } finally {
      setFormSubmitting(false);
    }
  };

  const handleEditSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedVinculo) return;
    setFormError(null);
    setFormSubmitting(true);

    try {
      await moradorService.updateMorador(
        selectedVinculo.id,
        selectedVinculo.usuario.id,
        {
          nome: formName,
          email: formEmail,
          telefone: formPhone,
          cpf: formCpf,
          tipoViculo: selectedVinculo.tipo
        },
        requesterId
      );

      setShowEditModal(false);
      setSelectedVinculo(null);
      loadData();
    } catch (err: any) {
      setFormError(err.message || 'Erro ao atualizar dados.');
    } finally {
      setFormSubmitting(false);
    }
  };

  const handleDelete = async (vinculoId: number, type: 'TITULAR' | 'DEPENDENTE', name: string) => {
    if (type === 'TITULAR') {
      alert('Você não pode remover o titular da unidade.');
      return;
    }

    if (window.confirm(`Tem certeza que deseja remover o morador ${name} da sua unidade?`)) {
      try {
        await moradorService.removeMorador(vinculoId, requesterId);
        loadData();
      } catch (err: any) {
        alert(err.message || 'Erro ao remover morador.');
      }
    }
  };

  const filteredVinculos = vinculos.filter((v) => {
    const nameStr = v.usuario.nome.toLowerCase();
    const emailStr = v.usuario.email.toLowerCase();
    return nameStr.includes(searchTerm.toLowerCase()) || emailStr.includes(searchTerm.toLowerCase());
  });

  const displayUnitName = `${unidadeNumero}${unidadeBloco ? ' - Bloco ' + unidadeBloco : ''}`;

  return (
    <div className="moradores-container animate-fade-in">
      <div className="moradores-header">
        <div>
          <h1>Moradores da Unidade {displayUnitName}</h1>
          <p className="moradores-sub">
            Gerencie os dependentes e moradores vinculados ao seu apartamento
          </p>
        </div>
        <button className="add-morador-btn" onClick={handleOpenAddModal}>
          <Plus size={16} /> Adicionar Morador
        </button>
      </div>

      {error && (
        <div className="error-banner">
          <AlertCircle size={20} />
          <span>{error}</span>
          <button onClick={loadData} className="retry-btn">
            Tentar Novamente
          </button>
        </div>
      )}

      <div className="moradores-card">
        {/* Search */}
        <div className="filters-bar">
          <div className="search-input-wrapper">
            <Search size={18} />
            <input
              type="text"
              placeholder="Buscar por nome ou e-mail..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="search-input"
            />
          </div>
        </div>

        {loading ? (
          <div className="loading-spinner-container">
            <Loader2 size={40} className="spinner" />
            <p>Carregando moradores...</p>
          </div>
        ) : filteredVinculos.length === 0 ? (
          <div className="empty-state-container">
            <Users size={48} className="empty-icon" />
            <h3>Nenhum morador encontrado</h3>
            <p>Clique em "Adicionar Morador" para cadastrar dependentes.</p>
          </div>
        ) : (
          <div className="table-responsive">
            <table className="moradores-table">
              <thead>
                <tr>
                  <th>NOME</th>
                  <th>TIPO</th>
                  <th>TELEFONE</th>
                  <th>E-MAIL</th>
                  <th>AÇÕES</th>
                </tr>
              </thead>
              <tbody>
                {filteredVinculos.map((v) => (
                  <tr key={v.id}>
                    <td className="font-bold text-slate-800">{v.usuario.nome}</td>
                    <td>
                      <span className={`badge-tipo ${v.tipo === 'TITULAR' ? 'badge-titular' : 'badge-dependente'}`}>
                        {v.tipo === 'TITULAR' ? 'Titular' : 'Dependente'}
                      </span>
                    </td>
                    <td>{v.usuario.telefone || '—'}</td>
                    <td>{v.usuario.email}</td>
                    <td>
                      <div className="action-buttons-group">
                        <button
                          className="action-btn key-btn"
                          title="Visualizar Informações"
                          onClick={() => handleOpenViewModal(v)}
                        >
                          <Eye size={14} />
                        </button>
                        <button
                          className="action-btn edit-btn"
                          title="Editar Morador"
                          onClick={() => handleOpenEditModal(v)}
                        >
                          <Edit2 size={14} />
                        </button>
                        {v.tipo !== 'TITULAR' && (
                          <button
                            className="action-btn delete-btn"
                            title="Remover Morador"
                            onClick={() => handleDelete(v.id, v.tipo, v.usuario.nome)}
                          >
                            <Trash2 size={14} />
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {showAddModal && (
        <div className="modal-overlay">
          <div className="modal-card">
            <div className="modal-header">
              <h2>Adicionar Morador</h2>
              <button className="close-btn" onClick={() => setShowAddModal(false)}>
                <X size={20} />
              </button>
            </div>
            <form onSubmit={handleAddSubmit}>
              <div className="form-group-full">
                <label>NOME COMPLETO</label>
                <input
                  type="text"
                  placeholder="Nome do morador"
                  value={formName}
                  onChange={(e) => setFormName(e.target.value)}
                  required
                />
              </div>

              <div className="form-row">
                <div className="form-group-half">
                  <label>TELEFONE</label>
                  <input
                    type="text"
                    placeholder="(81) 9 9999-9999"
                    value={formPhone}
                    onChange={(e) => setFormPhone(e.target.value)}
                  />
                </div>
                <div className="form-group-half">
                  <label>CPF</label>
                  <input
                    type="text"
                    placeholder="000.000.000-00"
                    value={formCpf}
                    onChange={(e) => setFormCpf(e.target.value)}
                    required
                  />
                </div>
              </div>

              <div className="form-group-full">
                <label>E-MAIL</label>
                <input
                  type="email"
                  placeholder="email@exemplo.com"
                  value={formEmail}
                  onChange={(e) => setFormEmail(e.target.value)}
                  required
                />
              </div>

              {formError && (
                <div className="modal-error-message">
                  <AlertCircle size={16} />
                  <span>{formError}</span>
                </div>
              )}

              <div className="modal-footer">
                <button
                  type="button"
                  className="cancel-btn"
                  onClick={() => setShowAddModal(false)}
                >
                  Cancelar
                </button>
                <button type="submit" className="save-btn" disabled={formSubmitting}>
                  {formSubmitting ? 'Salvando...' : 'Salvar Morador'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showEditModal && selectedVinculo && (
        <div className="modal-overlay">
          <div className="modal-card">
            <div className="modal-header">
              <h2>Editar Morador</h2>
              <button className="close-btn" onClick={() => setShowEditModal(false)}>
                <X size={20} />
              </button>
            </div>
            <form onSubmit={handleEditSubmit}>
              <div className="form-group-full">
                <label>NOME COMPLETO</label>
                <input
                  type="text"
                  placeholder="Nome do morador"
                  value={formName}
                  onChange={(e) => setFormName(e.target.value)}
                  required
                />
              </div>

              <div className="form-row">
                <div className="form-group-half">
                  <label>TELEFONE</label>
                  <input
                    type="text"
                    placeholder="(81) 9 9999-9999"
                    value={formPhone}
                    onChange={(e) => setFormPhone(e.target.value)}
                  />
                </div>
                <div className="form-group-half">
                  <label>CPF</label>
                  <input
                    type="text"
                    placeholder="000.000.000-00"
                    value={formCpf}
                    onChange={(e) => setFormCpf(e.target.value)}
                    required
                  />
                </div>
              </div>

              <div className="form-group-full">
                <label>E-MAIL</label>
                <input
                  type="email"
                  placeholder="email@exemplo.com"
                  value={formEmail}
                  onChange={(e) => setFormEmail(e.target.value)}
                  required
                />
              </div>

              {formError && (
                <div className="modal-error-message">
                  <AlertCircle size={16} />
                  <span>{formError}</span>
                </div>
              )}

              <div className="modal-footer">
                <button
                  type="button"
                  className="cancel-btn"
                  onClick={() => setShowEditModal(false)}
                >
                  Cancelar
                </button>
                <button type="submit" className="save-btn" disabled={formSubmitting}>
                  {formSubmitting ? 'Salvando...' : 'Salvar Alterações'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showViewModal && selectedVinculo && (
        <div className="modal-overlay">
          <div className="modal-card">
            <div className="modal-header">
              <h2>Informações do Morador</h2>
              <button className="close-btn" onClick={() => { setShowViewModal(false); setSelectedVinculo(null); }}>
                <X size={20} />
              </button>
            </div>
            <div className="view-morador-details" style={{ padding: '24px', display: 'flex', flexDirection: 'column', gap: '20px' }}>
              <div className="detail-item">
                <span className="detail-label" style={{ fontSize: '11px', fontWeight: 700, color: 'var(--gray-400)', textTransform: 'uppercase', letterSpacing: '0.5px' }}>Nome Completo</span>
                <div className="detail-value" style={{ fontSize: '15px', fontWeight: 600, color: 'var(--gray-800)', marginTop: '4px' }}>{selectedVinculo.usuario.nome}</div>
              </div>

              <div style={{ display: 'flex', gap: '24px' }}>
                <div className="detail-item" style={{ flex: 1 }}>
                  <span className="detail-label" style={{ fontSize: '11px', fontWeight: 700, color: 'var(--gray-400)', textTransform: 'uppercase', letterSpacing: '0.5px' }}>Unidade</span>
                  <div className="detail-value" style={{ fontSize: '15px', fontWeight: 600, color: 'var(--gray-800)', marginTop: '4px' }}>
                    {displayUnitName}
                  </div>
                </div>
                <div className="detail-item" style={{ flex: 1 }}>
                  <span className="detail-label" style={{ fontSize: '11px', fontWeight: 700, color: 'var(--gray-400)', textTransform: 'uppercase', letterSpacing: '0.5px' }}>Tipo de Vínculo</span>
                  <div className="detail-value" style={{ marginTop: '4px' }}>
                    <span className={`badge-tipo ${selectedVinculo.tipo === 'TITULAR' ? 'badge-titular' : 'badge-dependente'}`}>
                      {selectedVinculo.tipo === 'TITULAR' ? 'Titular' : 'Dependente'}
                    </span>
                  </div>
                </div>
              </div>

              <div style={{ display: 'flex', gap: '24px' }}>
                <div className="detail-item" style={{ flex: 1 }}>
                  <span className="detail-label" style={{ fontSize: '11px', fontWeight: 700, color: 'var(--gray-400)', textTransform: 'uppercase', letterSpacing: '0.5px' }}>Telefone</span>
                  <div className="detail-value" style={{ fontSize: '15px', fontWeight: 600, color: 'var(--gray-800)', marginTop: '4px' }}>{selectedVinculo.usuario.telefone || '—'}</div>
                </div>
                <div className="detail-item" style={{ flex: 1 }}>
                  <span className="detail-label" style={{ fontSize: '11px', fontWeight: 700, color: 'var(--gray-400)', textTransform: 'uppercase', letterSpacing: '0.5px' }}>CPF</span>
                  <div className="detail-value" style={{ fontSize: '15px', fontWeight: 600, color: 'var(--gray-800)', marginTop: '4px' }}>{selectedVinculo.usuario.cpf || '—'}</div>
                </div>
              </div>

              <div className="detail-item">
                <span className="detail-label" style={{ fontSize: '11px', fontWeight: 700, color: 'var(--gray-400)', textTransform: 'uppercase', letterSpacing: '0.5px' }}>E-mail</span>
                <div className="detail-value" style={{ fontSize: '15px', fontWeight: 600, color: 'var(--gray-800)', marginTop: '4px' }}>{selectedVinculo.usuario.email}</div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
