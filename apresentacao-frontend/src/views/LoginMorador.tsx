import React, { useState } from 'react';
import './Login.css';
import { ArrowRight } from 'lucide-react';

interface LoginMoradorProps {
  onLoginSuccess: (role: 'morador' | 'admin', userEmail: string) => void;
  onNavigate: (view: 'login-morador' | 'login-admin' | 'cadastro') => void;
}

export const LoginMorador: React.FC<LoginMoradorProps> = ({ onLoginSuccess, onNavigate }) => {
  const [email, setEmail] = useState('ana.lima@email.com');
  const [senha, setSenha] = useState('senha123');
  const [error, setError] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (email === 'ana.lima@email.com' && senha === 'senha123') {
      onLoginSuccess('morador', email);
    } else if (email === 'sindico@residencial.com' && senha === 'admin123') {
      // Allow seamless routing even if they used the wrong screen but correct admin details
      onLoginSuccess('admin', email);
    } else {
      setError('Credenciais inválidas. Use ana.lima@email.com / senha123');
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        {/* Left Information Panel */}
        <div className="login-info-side">
          <div className="logo-container">
            <div className="logo-badge">D</div>
            <span className="logo-text">Dominium</span>
          </div>

          <div className="info-content">
            <h2>Seu condomínio na palma da mão.</h2>
            <p>
              Reserve áreas comuns, acompanhe taxas, participe das assembleias e muito mais — tudo em um só lugar.
            </p>
          </div>

          <div className="condo-box">
            <div className="label">Condomínio</div>
            <h3>Residencial Parque Verde</h3>
            <p>Blocos A-D · 120 unidades</p>
          </div>
        </div>

        {/* Right Form Panel */}
        <div className="login-form-side">
          <div className="form-header">
            <h2>Bem-vindo(a)!</h2>
            <p>Acesse o portal do morador com seu e-mail e senha.</p>
          </div>

          <form onSubmit={handleSubmit} style={{ margin: '30px 0 0 0' }}>
            {error && (
              <div style={{ color: 'var(--danger)', fontSize: '13px', marginBottom: '16px', fontWeight: 500 }}>
                {error}
              </div>
            )}

            <div className="form-group">
              <label htmlFor="email">E-mail</label>
              <div className="input-wrapper">
                <input
                  type="email"
                  id="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="exemplo@email.com"
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="senha">Senha</label>
              <div className="input-wrapper">
                <input
                  type="password"
                  id="senha"
                  value={senha}
                  onChange={(e) => setSenha(e.target.value)}
                  placeholder="Sua senha"
                  required
                />
              </div>
            </div>

            <div className="form-links">
              <span className="forgot-password" style={{ cursor: 'pointer' }}>
                Esqueci minha senha
              </span>
            </div>

            <button type="submit" className="submit-btn">
              Entrar <ArrowRight size={18} />
            </button>
          </form>

          <div className="form-footer">
            <div>
              Problemas com acesso? Contate o síndico.
            </div>
            <div style={{ marginTop: '12px', display: 'flex', flexDirection: 'column', gap: '6px' }}>
              <button 
                type="button" 
                className="switch-panel-btn" 
                onClick={() => onNavigate('login-admin')}
              >
                Acessar Painel Administrativo (Síndico)
              </button>
              <button 
                type="button" 
                className="switch-panel-btn"
                style={{ color: 'var(--gray-600)' }}
                onClick={() => onNavigate('cadastro')}
              >
                Não tem uma conta? <strong>Cadastre-se</strong>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
