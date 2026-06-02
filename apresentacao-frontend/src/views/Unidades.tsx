import React, { useState } from 'react';
import './Unidades.css';
// Adicionado o 'X' na importação abaixo
import { Search, Plus, Edit, Eye, Pause, ChevronLeft, ChevronRight, X } from 'lucide-react';

interface UnidadeData {
  id: number;
  numero: string;
  bloco: string;
  titularNome: string;
  quantidadeMoradores: number;
  status: 'ADIMPLENTE' | 'INADIMPLENTE' | 'EM_NEGOCIACAO';
}

export const Unidades: React.FC = () => {
  const [unidades, setUnidades] = useState<UnidadeData[]>([
    { id: 1, numero: '101', bloco: 'A', titularNome: 'Roberto Alves', quantidadeMoradores: 3, status: 'ADIMPLENTE' },
    { id: 2, numero: '102', bloco: 'A', titularNome: 'Ana Lima', quantidadeMoradores: 2, status: 'ADIMPLENTE' },
    { id: 3, numero: '108', bloco: 'A', titularNome: 'Jorge Santos', quantidadeMoradores: 1, status: 'INADIMPLENTE' },
    { id: 4, numero: '215', bloco: 'B', titularNome: 'Carla Mendes', quantidadeMoradores: 2, status: 'ADIMPLENTE' },
    { id: 5, numero: '304', bloco: 'C', titularNome: 'Fernanda Costa', quantidadeMoradores: 4, status: 'ADIMPLENTE' },
    { id: 6, numero: '312', bloco: 'C', titularNome: 'Paulo Oliveira', quantidadeMoradores: 3, status: 'INADIMPLENTE' },
    { id: 7, numero: '421', bloco: 'D', titularNome: 'Rafael Lima', quantidadeMoradores: 2, status: 'EM_NEGOCIACAO' },
  ]);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState({
    numero: '',
    bloco: 'Bloco A',
    titular: '',
    email: '',
    telefone: '',
    status: 'ADIMPLENTE'
  });

  const formatarAndar = (numero: string) => `${numero.charAt(0)}º`;

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

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    console.log("Dados a serem enviados:", formData);
    setIsModalOpen(false);
  };

  return (
    <div className="page-content">
      <div className="page-header">
        <div>
          <h1>Gestão de Unidades</h1>
          <p>120 unidades cadastradas · Blocos A, B, C e D</p>
        </div>
        <button className="btn-primary" onClick={() => setIsModalOpen(true)}>
          <Plus size={18} /> Nova Unidade
        </button>
      </div>

      <div className="filters-bar">
        <select className="filter-select"><option>Todos os Blocos</option></select>
        <select className="filter-select"><option>Todos os Status</option></select>
        <div className="search-input-wrapper">
          <input type="text" placeholder="Buscar por unidade ou titular" className="filter-input" />
        </div>
      </div>

      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>UNIDADE</th>
              <th>TITULAR</th>
              <th>MORADORES</th>
              <th>BLOCO/ANDAR</th>
              <th>STATUS</th>
              <th style={{ textAlign: 'right' }}>AÇÕES</th>
            </tr>
          </thead>
          <tbody>
            {unidades.map((unidade) => (
              <tr key={unidade.id}>
                <td style={{ fontWeight: 600 }}>{unidade.numero}</td>
                <td>{unidade.titularNome}</td>
                <td>{unidade.quantidadeMoradores}</td>
                <td>Bloco {unidade.bloco} - {formatarAndar(unidade.numero)}</td>
                <td>{renderStatusBadge(unidade.status)}</td>
                <td className="actions-cell">
                  <button className="action-icon"><Edit size={16} /></button>
                  <button className="action-icon"><Eye size={16} /></button>
                  <button className="action-icon"><Pause size={16} /></button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        
        <div className="pagination">
          <span>Mostrando 7 de 120 unidades</span>
          <div className="pagination-controls">
            <button className="page-btn"><ChevronLeft size={16} /> Anterior</button>
            <button className="page-number active">1</button>
            <button className="page-number">2</button>
            <button className="page-number">3</button>
            <button className="page-btn">Próxima <ChevronRight size={16} /></button>
          </div>
        </div>
      </div> {/* <-- DIV FECHADA AQUI (encerra a table-container) */}

      {isModalOpen && (
        <div className="unidade-modal-overlay">
          <div className="unidade-modal-card">
            <div className="unidade-modal-header">
              <h2>Adicionar Unidade</h2>
              <button className="btn-close" onClick={() => setIsModalOpen(false)}>
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
                    <option value="Bloco A">Bloco A</option>
                    <option value="Bloco B">Bloco B</option>
                    <option value="Bloco C">Bloco C</option>
                    <option value="Bloco D">Bloco D</option>
                  </select>
                </div>
              </div>

              <div className="form-group">
                <label>TITULAR</label>
                <input 
                  type="text" 
                  name="titular"
                  placeholder="Nome completo" 
                  value={formData.titular}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-group">
                <label>E-MAIL</label>
                <input 
                  type="email" 
                  name="email"
                  placeholder="titular@email.com" 
                  value={formData.email}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>TELEFONE</label>
                  <input 
                    type="text" 
                    name="telefone"
                    placeholder="(11) 9 0000-0000" 
                    value={formData.telefone}
                    onChange={handleInputChange}
                  />
                </div>
                <div className="form-group">
                  <label>STATUS</label>
                  <select name="status" value={formData.status} onChange={handleInputChange}>
                    <option value="ADIMPLENTE">Em dia</option>
                    <option value="INADIMPLENTE">Inadimplente</option>
                    <option value="EM_NEGOCIACAO">Pendente</option>
                  </select>
                </div>
              </div>

              <div className="unidade-modal-footer">
                <button type="button" className="btn-cancelar" onClick={() => setIsModalOpen(false)}>
                  Cancelar
                </button>
                <button type="submit" className="btn-salvar">
                  Salvar Unidade
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};