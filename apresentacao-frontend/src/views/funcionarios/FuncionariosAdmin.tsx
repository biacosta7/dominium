import { useEffect, useState } from 'react';
import { Plus, Search, Pencil } from 'lucide-react';
import type {
  CadastrarFuncionarioRequest,
  Funcionario,
  TipoVinculo,
} from './types/funcionario';
import { funcionarioService } from './services/funcionarioService';
import './Funcionarios.css';

type FiltroVinculo = 'TODOS' | TipoVinculo;

const VINCULO_LABEL: Record<TipoVinculo, string> = {
  CLT: 'CLT',
  TERCEIRIZADO: 'Terceirizado',
  EVENTUAL: 'Eventual',
};

const STATUS_LABEL: Record<string, string> = {
  ATIVO: 'Ativo',
  INATIVO: 'Inativo',
  BLOQUEADO: 'Bloqueado',
};

function formatarData(d: string | null | undefined) {
  if (!d) return 'Indeterminado';
  const [ano, mes, dia] = d.split('-');
  return `${dia}/${mes}/${ano}`;
}

const formVazio = (): CadastrarFuncionarioRequest => ({
  nome: '',
  cpf: '',
  email: '',
  telefone: '',
  tipoVinculo: 'CLT',
  contratoInicio: '',
  contratoFim: '',
  valorMensal: null,
});

export default function FuncionariosAdmin() {
  const [funcionarios, setFuncionarios] = useState<Funcionario[]>([]);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState('');
  const [busca, setBusca] = useState('');
  const [filtroVinculo, setFiltroVinculo] = useState<FiltroVinculo>('TODOS');

  const [modalAberto, setModalAberto] = useState(false);
  const [editando, setEditando] = useState<Funcionario | null>(null);
  const [form, setForm] = useState<CadastrarFuncionarioRequest>(formVazio());
  const [integrarDespesa, setIntegrarDespesa] = useState(false);
  const [salvando, setSalvando] = useState(false);
  const [erroModal, setErroModal] = useState('');

  async function carregar() {
    setLoading(true);
    setErro('');
    try {
      const dados = await funcionarioService.listar();
      setFuncionarios(dados);
    } catch (e: any) {
      setErro(e.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    carregar();
  }, []);

  function abrirCriar() {
    setEditando(null);
    setForm(formVazio());
    setIntegrarDespesa(false);
    setErroModal('');
    setModalAberto(true);
  }

  function abrirEditar(f: Funcionario) {
    setEditando(f);
    setForm({
      nome: f.nome,
      cpf: f.cpf,
      email: f.email ?? '',
      telefone: f.telefone ?? '',
      tipoVinculo: f.tipoVinculo,
      contratoInicio: f.contratoInicio,
      contratoFim: f.contratoFim,
      valorMensal: f.valorMensal,
    });
    setIntegrarDespesa(!!f.valorMensal);
    setErroModal('');
    setModalAberto(true);
  }

  function fecharModal() {
    setModalAberto(false);
    setEditando(null);
    setErroModal('');
  }

  function setField<K extends keyof CadastrarFuncionarioRequest>(
    key: K,
    value: CadastrarFuncionarioRequest[K]
  ) {
    setForm((prev) => ({ ...prev, [key]: value }));
  }

  function dataValida(d: string): boolean {
    if (!d) return false;
    const ano = Number(d.split('-')[0]);
    return ano >= 1900 && ano <= 2100;
  }

  async function handleSalvar() {
    if (!form.nome.trim()) { setErroModal('Nome é obrigatório'); return; }
    if (!form.contratoInicio) { setErroModal('Início do contrato é obrigatório'); return; }
    if (!dataValida(form.contratoInicio)) { setErroModal('Data de início inválida'); return; }
    if (!form.contratoFim) { setErroModal('Fim do contrato é obrigatório'); return; }
    if (!dataValida(form.contratoFim)) { setErroModal('Data de fim inválida'); return; }
    if (form.contratoFim < form.contratoInicio) { setErroModal('A data de fim deve ser após o início'); return; }

    setSalvando(true);
    setErroModal('');
    try {
      const payload = { ...form, valorMensal: integrarDespesa ? form.valorMensal : null };
      if (editando) {
        await funcionarioService.editar(editando.id, payload);
      } else {
        await funcionarioService.cadastrar(payload);
      }
      await carregar();
      fecharModal();
    } catch (e: any) {
      setErroModal(e.message);
    } finally {
      setSalvando(false);
    }
  }

  const filtrados = funcionarios.filter((f) => {
    const matchBusca = f.nome.toLowerCase().includes(busca.toLowerCase());
    const matchVinculo = filtroVinculo === 'TODOS' || f.tipoVinculo === filtroVinculo;
    return matchBusca && matchVinculo;
  });

  return (
    <div className="func-page">
      <div className="func-header">
        <div>
          <h1>Funcionários e Prestadores</h1>
          <p className="func-header-sub">Gestão de contratos e controle de acessos</p>
        </div>
        <button className="func-novo-btn" onClick={abrirCriar}>
          <Plus size={15} /> Novo Colaborador
        </button>
      </div>

      {erro && <p className="func-erro">{erro}</p>}

      <div className="func-toolbar">
        <div className="func-filter-tabs">
          {(['TODOS', 'CLT', 'TERCEIRIZADO', 'EVENTUAL'] as FiltroVinculo[]).map((v) => (
            <button
              key={v}
              className={`func-tab ${filtroVinculo === v ? 'active' : ''}`}
              onClick={() => setFiltroVinculo(v)}
            >
              {v === 'TODOS' ? 'Todos os Vínculos' : VINCULO_LABEL[v as TipoVinculo]}
            </button>
          ))}
        </div>
        <div className="func-search">
          <Search size={14} color="#94a3b8" />
          <input
            placeholder="Buscar por nome..."
            value={busca}
            onChange={(e) => setBusca(e.target.value)}
          />
        </div>
      </div>

      {loading ? (
        <p className="func-loading">Carregando...</p>
      ) : (
        <div className="func-table-wrap">
          <table className="func-table">
            <thead>
              <tr>
                <th>Nome</th>
                <th>Vínculo</th>
                <th>Início do Contrato</th>
                <th>Vencimento do Contrato</th>
                <th>Status</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {filtrados.length === 0 ? (
                <tr>
                  <td colSpan={6}>
                    <p className="func-empty">Nenhum colaborador encontrado.</p>
                  </td>
                </tr>
              ) : (
                filtrados.map((f) => (
                  <tr key={f.id}>
                    <td className="func-nome">{f.nome}</td>
                    <td>
                      <span className={`badge-vinculo badge-${f.tipoVinculo.toLowerCase()}`}>
                        {VINCULO_LABEL[f.tipoVinculo]}
                      </span>
                    </td>
                    <td>{formatarData(f.contratoInicio)}</td>
                    <td>{formatarData(f.contratoFim)}</td>
                    <td>
                      <span className={`badge-status badge-${f.status.toLowerCase()}`}>
                        {STATUS_LABEL[f.status] ?? f.status}
                      </span>
                    </td>
                    <td>
                      <div className="func-acoes">
                        <button className="func-btn-icon" title="Editar" onClick={() => abrirEditar(f)}>
                          <Pencil size={13} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}

      {modalAberto && (
        <div className="func-modal-overlay" onClick={fecharModal}>
          <div className="func-modal" onClick={(e) => e.stopPropagation()}>
            <div className="func-modal-header">
              <h2>{editando ? 'Editar Colaborador' : 'Cadastrar Colaborador'}</h2>
              <button className="func-modal-close" onClick={fecharModal}>×</button>
            </div>

            <div className="func-modal-body">
              <div className="func-field-row">
                <div>
                  <label className="func-field-label">Nome / Empresa</label>
                  <input
                    className="func-field-input"
                    placeholder="Nome completo"
                    value={form.nome}
                    onChange={(e) => setField('nome', e.target.value)}
                  />
                </div>
                <div>
                  <label className="func-field-label">Vínculo</label>
                  <select
                    className="func-field-input"
                    value={form.tipoVinculo}
                    onChange={(e) => setField('tipoVinculo', e.target.value as TipoVinculo)}
                  >
                    <option value="CLT">CLT</option>
                    <option value="TERCEIRIZADO">Terceirizado (PJ)</option>
                    <option value="EVENTUAL">Eventual</option>
                  </select>
                </div>
              </div>

              <div className="func-field-row">
                <div>
                  <label className="func-field-label">Início do Contrato</label>
                  <input
                    type="date"
                    className="func-field-input"
                    min="1900-01-01"
                    max="2100-12-31"
                    value={form.contratoInicio}
                    onChange={(e) => setField('contratoInicio', e.target.value)}
                  />
                </div>
                <div>
                  <label className="func-field-label">Fim (ou Vencimento OS)</label>
                  <input
                    type="date"
                    className="func-field-input"
                    min="1900-01-01"
                    max="2100-12-31"
                    value={form.contratoFim}
                    onChange={(e) => setField('contratoFim', e.target.value)}
                  />
                </div>
              </div>

              <label className="func-checkbox-row">
                <input
                  type="checkbox"
                  checked={integrarDespesa}
                  onChange={(e) => setIntegrarDespesa(e.target.checked)}
                />
                Integrar pagamento como despesa mensal fixa (US12)
              </label>

              {integrarDespesa && (
                <div>
                  <label className="func-field-label">Valor Mensal</label>
                  <div className="func-valor-field">
                    <span>R$</span>
                    <input
                      type="number"
                      min="0"
                      step="0.01"
                      placeholder="0,00"
                      value={form.valorMensal ?? ''}
                      onChange={(e) =>
                        setField('valorMensal', e.target.value ? Number(e.target.value) : null)
                      }
                    />
                  </div>
                </div>
              )}

              {erroModal && <p className="func-field-erro">{erroModal}</p>}
            </div>

            <div className="func-modal-footer">
              <button className="func-btn-cancel" onClick={fecharModal} disabled={salvando}>
                Cancelar
              </button>
              <button className="func-btn-save" onClick={handleSalvar} disabled={salvando}>
                {salvando ? 'Salvando...' : 'Salvar Colaborador'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
