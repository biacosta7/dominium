import React, { useEffect, useState } from 'react';
import { Search, Plus, Edit2, Trash2, Eye, X, Users, Loader2, AlertCircle } from 'lucide-react';
import { moradorService } from './services/moradorService';
import type { Vinculo, Unidade } from './types/morador';
import './MoradoresAdmin.css';

export default function MoradoresAdmin() {
  const [vinculos, setVinculos] = useState<Vinculo[]>([]);
  const [units, setUnits] = useState<Unidade[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Filters state
  const [searchTerm, setSearchTerm] = useState('');
  const [typeFilter, setTypeFilter] = useState<'ALL' | 'TITULAR' | 'DEPENDENTE'>('ALL');

  // Modals state
  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showViewModal, setShowViewModal] = useState(false);
  const [selectedVinculo, setSelectedVinculo] = useState<Vinculo | null>(null);

  // Form states
  const [formName, setFormName] = useState('');
  const [formUnit, setFormUnit] = useState(''); // text input to match "Ex: 304"
  const [formType, setFormType] = useState<'TITULAR' | 'DEPENDENTE'>('TITULAR');
  const [formPhone, setFormPhone] = useState('');
  const [formEmail, setFormEmail] = useState('');
  const [formCpf, setFormCpf] = useState('');
  const [formError, setFormError] = useState<string | null>(null);
  const [formSubmitting, setFormSubmitting] = useState(false);

  // Load all data
  async function loadData() {
    setLoading(true);
    setError(null);
    try {
      const fetchedUnits = await moradorService.fetchUnits();
      setUnits(fetchedUnits);

      // Fetch all resident vínculos concurrently across units
      const fetchedVinculosPromises = fetchedUnits.map((u) =>
        moradorService.fetchMoradoresDaUnidade(u.id).catch(() => [])
      );
      const lists = await Promise.all(fetchedVinculosPromises);
      const allVinculos = lists.flat();
      setVinculos(allVinculos);
    } catch (err: any) {
      setError(err.message || 'Erro ao carregar dados dos moradores.');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadData();
  }, []);

  // Map unitId to unit object
  function getUnitForVinculo(unidadeId: number): Unidade | undefined {
    return units.find((u) => u.id === unidadeId);
  }

  // Handle open add modal
  const handleOpenAddModal = () => {
    setFormName('');
    setFormUnit('');
    setFormType('TITULAR');
    setFormPhone('');
    setFormEmail('');
    setFormCpf('');
    setFormError(null);
    setShowAddModal(true);
  };

  // Handle open edit modal
  const handleOpenEditModal = (vinculo: Vinculo) => {
    const unit = getUnitForVinculo(vinculo.unidadeId);
    setSelectedVinculo(vinculo);
    setFormName(vinculo.usuario.nome);
    setFormUnit(unit ? `${unit.numero}${unit.bloco ? ' - ' + unit.bloco : ''}` : '');
    setFormType(vinculo.tipo);
    setFormPhone(vinculo.usuario.telefone || '');
    setFormEmail(vinculo.usuario.email);
    setFormCpf(vinculo.usuario.cpf || '');
    setFormError(null);
    setShowEditModal(true);
  };

  // Handle open view modal
  const handleOpenViewModal = (vinculo: Vinculo) => {
    setSelectedVinculo(vinculo);
    setShowViewModal(true);
  };

  // Create Resident
  const handleAddSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormError(null);
    setFormSubmitting(true);

    try {
      const unitId = Number(formUnit);
      if (!unitId) {
        throw new Error('Selecione uma unidade.');
      }

      await moradorService.addMorador(unitId, {
        nome: formName,
        email: formEmail,
        telefone: formPhone,
        cpf: formCpf,
        tipoViculo: formType,
      });

      setShowAddModal(false);
      loadData();
    } catch (err: any) {
      setFormError(err.message || 'Erro ao salvar morador.');
    } finally {
      setFormSubmitting(false);
    }
  };

  // Update Resident
  const handleEditSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedVinculo) return;
    setFormError(null);
    setFormSubmitting(true);

    try {
      await moradorService.updateMorador(selectedVinculo.id, selectedVinculo.usuario.id, {
        nome: formName,
        email: formEmail,
        telefone: formPhone,
        cpf: formCpf,
        tipoViculo: formType,
      });

      setShowEditModal(false);
      setSelectedVinculo(null);
      loadData();
    } catch (err: any) {
      setFormError(err.message || 'Erro ao atualizar morador.');
    } finally {
      setFormSubmitting(false);
    }
  };

  // Delete Resident
  const handleDelete = async (vinculoId: number, name: string) => {
    if (window.confirm(`Tem certeza que deseja remover o vínculo de ${name}?`)) {
      try {
        await moradorService.removeMorador(vinculoId);
        loadData();
      } catch (err: any) {
        alert(err.message || 'Erro ao remover morador.');
      }
    }
  };

  // Filter logic
  const filteredVinculos = vinculos.filter((vinculo) => {
    const unit = getUnitForVinculo(vinculo.unidadeId);
    const unitStr = unit ? `${unit.numero} ${unit.bloco || ''}`.toLowerCase() : '';
    const nameStr = vinculo.usuario.nome.toLowerCase();
    const matchesSearch =
      nameStr.includes(searchTerm.toLowerCase()) || unitStr.includes(searchTerm.toLowerCase());
    const matchesType = typeFilter === 'ALL' || vinculo.tipo === typeFilter;
    return matchesSearch && matchesType;
  });

  return (
    <div className="moradores-container animate-fade-in">
      {/* Title section */}
      <div className="moradores-header">
        <div>
          <h1>Gestão de Moradores</h1>
          <p className="moradores-sub">
            {vinculos.length} moradores em {units.length} unidades cadastradas
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

      {/* Main card panel */}
      <div className="moradores-card">
        {/* Filters and search */}
        <div className="filters-bar">
          <div className="filter-select-wrapper">
            <select
              value={typeFilter}
              onChange={(e) => setTypeFilter(e.target.value as any)}
              className="filter-select"
            >
              <option value="ALL">Todos os Tipos</option>
              <option value="TITULAR">Titular</option>
              <option value="DEPENDENTE">Dependente</option>
            </select>
          </div>

          <div className="search-input-wrapper">
            <Search size={18} />
            <input
              type="text"
              placeholder="Buscar por nome ou unidade..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="search-input"
            />
          </div>
        </div>

        {/* Loading state */}
        {loading ? (
          <div className="loading-spinner-container">
            <Loader2 size={40} className="spinner" />
            <p>Carregando moradores...</p>
          </div>
        ) : filteredVinculos.length === 0 ? (
          <div className="empty-state-container">
            <Users size={48} className="empty-icon" />
            <h3>Nenhum morador encontrado</h3>
            <p>Tente ajustar os filtros ou adicione um novo morador.</p>
          </div>
        ) : (
          /* Table list */
          <div className="table-responsive">
            <table className="moradores-table">
              <thead>
                <tr>
                  <th>NOME</th>
                  <th>UNIDADE</th>
                  <th>TIPO</th>
                  <th>TELEFONE</th>
                  <th>STATUS</th>
                  <th>AÇÕES</th>
                </tr>
              </thead>
              <tbody>
                {filteredVinculos.map((vinculo) => {
                  const unit = getUnitForVinculo(vinculo.unidadeId);
                  const isUnitInadimplente = unit?.status === 'INADIMPLENTE';
                  return (
                    <tr key={vinculo.id}>
                      <td className="font-bold text-slate-800">{vinculo.usuario.nome}</td>
                      <td>
                        {unit ? `${unit.numero}${unit.bloco ? ' - ' + unit.bloco : ''}` : '—'}
                      </td>
                      <td>
                        <span
                          className={`badge-tipo ${vinculo.tipo === 'TITULAR' ? 'badge-titular' : 'badge-dependente'
                            }`}
                        >
                          {vinculo.tipo === 'TITULAR' ? 'Titular' : 'Dependente'}
                        </span>
                      </td>
                      <td>{vinculo.usuario.telefone || '—'}</td>
                      <td>
                        {vinculo.status === 'INATIVO' ? (
                          <span className="badge-status badge-status-inativo">Inativo</span>
                        ) : isUnitInadimplente ? (
                          <span className="badge-status badge-status-inadimplente">
                            Inadimplente
                          </span>
                        ) : (
                          <span className="badge-status badge-status-ativo">Ativo</span>
                        )}
                      </td>
                      <td>
                        <div className="action-buttons-group">
                          <button
                            className="action-btn key-btn"
                            title="Visualizar Informações"
                            onClick={() => handleOpenViewModal(vinculo)}
                          >
                            <Eye size={14} />
                          </button>
                          <button
                            className="action-btn edit-btn"
                            title="Editar Morador"
                            onClick={() => handleOpenEditModal(vinculo)}
                          >
                            <Edit2 size={14} />
                          </button>
                          <button
                            className="action-btn delete-btn"
                            title="Remover Morador"
                            onClick={() => handleDelete(vinculo.id, vinculo.usuario.nome)}
                          >
                            <Trash2 size={14} />
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* ========================================================
          ADD MORADOR MODAL
          ======================================================== */}
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
                  <label>UNIDADE</label>
                  <select
                    value={formUnit}
                    onChange={(e) => setFormUnit(e.target.value)}
                    required
                  >
                    <option value="">Selecione...</option>
                    {units.map((u) => (
                      <option key={u.id} value={u.id}>
                        {u.numero}{u.bloco ? ` - Bloco ${u.bloco}` : ''}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="form-group-half">
                  <label>TIPO</label>
                  <select
                    value={formType}
                    onChange={(e) => setFormType(e.target.value as any)}
                    required
                  >
                    <option value="TITULAR">Titular</option>
                    <option value="DEPENDENTE">Dependente</option>
                  </select>
                </div>
              </div>

              <div className="form-row">
                <div className="form-group-half">
                  <label>TELEFONE</label>
                  <input
                    type="text"
                    placeholder="(11) 9 0000-0000"
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

      {/* ========================================================
          EDIT MORADOR MODAL
          ======================================================== */}
      {showEditModal && (
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
                  <label>UNIDADE</label>
                  <input
                    type="text"
                    placeholder="Ex: 304"
                    value={formUnit}
                    disabled // Disable unit changes in edit since backend only allows updating vínculo type
                    className="disabled-input"
                  />
                </div>
                <div className="form-group-half">
                  <label>TIPO</label>
                  <select
                    value={formType}
                    onChange={(e) => setFormType(e.target.value as any)}
                    required
                  >
                    <option value="TITULAR">Titular</option>
                    <option value="DEPENDENTE">Dependente</option>
                  </select>
                </div>
              </div>

              <div className="form-row">
                <div className="form-group-half">
                  <label>TELEFONE</label>
                  <input
                    type="text"
                    placeholder="(11) 9 0000-0000"
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
                  {formSubmitting ? 'Salvando...' : 'Salvar Morador'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* ========================================================
          VIEW MORADOR MODAL
          ======================================================== */}
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
                    {(() => {
                      const unit = getUnitForVinculo(selectedVinculo.unidadeId);
                      return unit ? `${unit.numero}${unit.bloco ? ' - Bloco ' + unit.bloco : ''}` : '—';
                    })()}
                  </div>
                </div>
                <div className="detail-item" style={{ flex: 1 }}>
                  <span className="detail-label" style={{ fontSize: '11px', fontWeight: 700, color: 'var(--gray-400)', textTransform: 'uppercase', letterSpacing: '0.5px' }}>Tipo</span>
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
