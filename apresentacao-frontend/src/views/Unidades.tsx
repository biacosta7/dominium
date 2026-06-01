import React, { useState } from 'react';
import './Unidades.css';
import { Search, Plus, Edit, Eye, Pause, ChevronLeft, ChevronRight } from 'lucide-react';

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

  const formatarAndar = (numero: string) => `${numero.charAt(0)}º`;

  const renderStatusBadge = (status: string) => {
    switch (status) {
      case 'ADIMPLENTE': return <span className="badge badge-success">Em dia</span>;
      case 'INADIMPLENTE': return <span className="badge badge-danger">Inadimplente</span>;
      case 'EM_NEGOCIACAO': return <span className="badge badge-warning">Pendente</span>;
      default: return null;
    }
  };

  return (
    <div className="page-content">
      <div className="page-header">
        <div>
          <h1>Gestão de Unidades</h1>
          <p>120 unidades cadastradas · Blocos A, B, C e D</p>
        </div>
        <button className="btn-primary">
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
      </div>
    </div>
  );
};