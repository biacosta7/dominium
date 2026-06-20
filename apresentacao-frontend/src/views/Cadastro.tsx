import React, { useState } from 'react';
import './Login.css';
import { ArrowRight, CheckCircle } from 'lucide-react';
import { moradorService } from './moradores/services/moradorService';

interface CadastroProps {
  onNavigate: (view: 'login-morador' | 'login-admin' | 'cadastro') => void;
}

export const Cadastro: React.FC<CadastroProps> = ({ onNavigate }) => {
  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [cpf, setCpf] = useState('');
  const [bloco, setBloco] = useState('A');
  const [apto, setApto] = useState('');
  const [tipo, setTipo] = useState('Morador');
  const [senha, setSenha] = useState('');
  const [confirmarSenha, setConfirmarSenha] = useState('');
  const [isSuccess, setIsSuccess] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (senha !== confirmarSenha) {
      setError('As senhas não coincidem.');
      return;
    }
    
    setLoading(true);
    setError('');

    try {
      const units = await moradorService.fetchUnits();
      const matchedUnit = units.find(
        (u) =>
          u.bloco.trim().toUpperCase() === bloco.trim().toUpperCase() &&
          u.numero.trim() === apto.trim()
      );

      if (!matchedUnit) {
        throw new Error(`Unidade não encontrada: Bloco ${bloco} - Apto ${apto}. Verifique os dados ou fale com o síndico.`);
      }

      const tipoVinculo = 'TITULAR';

      await moradorService.addMorador(
        matchedUnit.id,
        {
          nome,
          email,
          telefone: '',
          cpf,
          tipoViculo: tipoVinculo,
          senha,
        },
        null
      );

      setIsSuccess(true);
    } catch (err: any) {
      setError(err.message || 'Erro ao processar o cadastro.');
    } finally {
      setLoading(false);
    }
  };

  if (isSuccess) {
    return (
      <div className="login-page">
        <div className="admin-card" style={{ textAlign: 'center', gap: '20px' }}>
          <div style={{ color: 'var(--success)', display: 'flex', justifyContent: 'center', marginBottom: '10px' }}>
            <CheckCircle size={60} />
          </div>
          <h2 style={{ fontFamily: 'var(--font-title)', fontSize: '24px' }}>Cadastro Realizado!</h2>
          <p style={{ color: 'var(--gray-500)', fontSize: '14px', lineHeight: '1.6' }}>
            Sua conta para a unidade <strong>Bloco {bloco} - Apto {apto}</strong> foi criada com sucesso e está aguardando homologação do síndico.
          </p>
          <button 
            onClick={() => onNavigate('login-morador')} 
            className="submit-btn" 
            style={{ marginTop: '10px' }}
          >
            Voltar para o Login
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="login-page">
      <div className="login-card" style={{ minHeight: '680px' }}>
        {/* Left Side */}
        <div className="login-info-side" style={{ background: 'linear-gradient(135deg, #0d9488 0%, #1d4ed8 100%)' }}>
          <div className="logo-container">
            <div className="logo-badge">D</div>
            <span className="logo-text">Dominium</span>
          </div>

          <div className="info-content">
            <h2>Crie sua conta no portal.</h2>
            <p>
              Cadastre-se para ter acesso a reservas de churrasqueiras, salão de festas, visualizar suas cotas condominiais e interagir com a gestão de forma 100% digital.
            </p>
          </div>

          <div className="condo-box">
            <div className="label">Informações</div>
            <h3>Homologação Rápida</h3>
            <p>Seus dados serão enviados ao síndico para liberação do acesso.</p>
          </div>
        </div>

        {/* Right Side - Form */}
        <div className="login-form-side" style={{ overflowY: 'auto' }}>
          <div className="form-header">
            <h2>Cadastre-se</h2>
            <p>Preencha os campos abaixo para solicitar acesso.</p>
          </div>

          <form onSubmit={handleSubmit} style={{ margin: '24px 0 0 0', display: 'flex', flexDirection: 'column', gap: '14px' }}>
            {error && (
              <div style={{ color: 'var(--danger)', fontSize: '13px', fontWeight: 500 }}>
                {error}
              </div>
            )}

            <div className="form-group" style={{ marginBottom: 0 }}>
              <label htmlFor="cad-nome">Nome Completo</label>
              <div className="input-wrapper">
                <input
                  type="text"
                  id="cad-nome"
                  value={nome}
                  onChange={(e) => setNome(e.target.value)}
                  placeholder="Seu nome completo"
                  required
                />
              </div>
            </div>

            <div className="form-group" style={{ marginBottom: 0 }}>
              <label htmlFor="cad-email">E-mail</label>
              <div className="input-wrapper">
                <input
                  type="email"
                  id="cad-email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="seuemail@email.com"
                  required
                />
              </div>
            </div>

            <div className="form-group" style={{ marginBottom: 0 }}>
              <label htmlFor="cad-cpf">CPF</label>
              <div className="input-wrapper">
                <input
                  type="text"
                  id="cad-cpf"
                  value={cpf}
                  onChange={(e) => setCpf(e.target.value)}
                  placeholder="000.000.000-00"
                  required
                />
              </div>
            </div>

            <div style={{ display: 'flex', gap: '12px' }}>
              <div className="form-group" style={{ flex: 1, marginBottom: 0 }}>
                <label htmlFor="cad-bloco">Bloco</label>
                <div className="input-wrapper">
                  <select 
                    id="cad-bloco" 
                    value={bloco} 
                    onChange={(e) => setBloco(e.target.value)}
                    style={{
                      width: '100%',
                      padding: '12px 16px',
                      border: '1px solid var(--gray-200)',
                      borderRadius: 'var(--radius-md)',
                      outline: 'none',
                      backgroundColor: 'white'
                    }}
                  >
                    <option value="A">Bloco A</option>
                    <option value="B">Bloco B</option>
                    <option value="C">Bloco C</option>
                    <option value="D">Bloco D</option>
                  </select>
                </div>
              </div>

              <div className="form-group" style={{ flex: 1, marginBottom: 0 }}>
                <label htmlFor="cad-apto">Apartamento</label>
                <div className="input-wrapper">
                  <input
                    type="text"
                    id="cad-apto"
                    value={apto}
                    onChange={(e) => setApto(e.target.value)}
                    placeholder="Ex: 102"
                    required
                  />
                </div>
              </div>
            </div>

            <div className="form-group" style={{ marginBottom: 0 }}>
              <label htmlFor="cad-tipo">Vínculo</label>
              <div className="input-wrapper">
                <select 
                  id="cad-tipo" 
                  value={tipo} 
                  onChange={(e) => setTipo(e.target.value)}
                  style={{
                    width: '100%',
                    padding: '12px 16px',
                    border: '1px solid var(--gray-200)',
                    borderRadius: 'var(--radius-md)',
                    outline: 'none',
                    backgroundColor: 'white'
                  }}
                >
                  <option value="Morador">Morador Residente</option>
                  <option value="Proprietario">Proprietário Não Residente</option>
                  <option value="Inquilino">Inquilino</option>
                </select>
              </div>
            </div>

            <div style={{ display: 'flex', gap: '12px' }}>
              <div className="form-group" style={{ flex: 1, marginBottom: 0 }}>
                <label htmlFor="cad-senha">Senha</label>
                <div className="input-wrapper">
                  <input
                    type="password"
                    id="cad-senha"
                    value={senha}
                    onChange={(e) => setSenha(e.target.value)}
                    placeholder="Min 6 dígitos"
                    required
                    minLength={6}
                  />
                </div>
              </div>

              <div className="form-group" style={{ flex: 1, marginBottom: 0 }}>
                <label htmlFor="cad-confirmar">Confirmar</label>
                <div className="input-wrapper">
                  <input
                    type="password"
                    id="cad-confirmar"
                    value={confirmarSenha}
                    onChange={(e) => setConfirmarSenha(e.target.value)}
                    placeholder="Confirme a senha"
                    required
                  />
                </div>
              </div>
            </div>

            <button type="submit" className="submit-btn" style={{ marginTop: '10px' }} disabled={loading}>
              {loading ? 'Solicitando...' : 'Solicitar Cadastro'} <ArrowRight size={18} />
            </button>
          </form>

          <div className="form-footer" style={{ marginTop: '16px' }}>
            Já tem uma conta?{' '}
            <button 
              type="button" 
              className="switch-panel-btn" 
              onClick={() => onNavigate('login-morador')}
            >
              Voltar ao Login
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
