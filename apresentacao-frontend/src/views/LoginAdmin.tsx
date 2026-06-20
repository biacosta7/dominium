import React, { useState } from 'react';
import './Login.css';
import { ArrowRight } from 'lucide-react';

interface LoginAdminProps {
  onLoginSuccess: (role: 'morador' | 'admin', userEmail: string) => void;
  onNavigate: (view: 'login-morador' | 'login-admin' | 'cadastro') => void;
}

export const LoginAdmin: React.FC<LoginAdminProps> = ({ onLoginSuccess, onNavigate }) => {
  const [email, setEmail] = useState('sindico@residencial.com');
  const [senha, setSenha] = useState('admin123');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const loginRes = await fetch('/usuarios/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, senha })
      });

      if (!loginRes.ok) {
        setError('Credenciais administrativas inválidas.');
        return;
      }

      const user = await loginRes.json();

      if (user.tipo === 'SINDICO') {
        onLoginSuccess('admin', email);
      } else {
        onLoginSuccess('morador', email);
      }
    } catch (err: any) {
      setError(err.message || 'Erro ao realizar login.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page" style={{ background: '#090d16' }}>
      <div className="admin-card">
        {/* Logo and Header */}
        <div className="admin-logo">
          <div className="logo-badge" style={{ backgroundColor: 'var(--primary)' }}>D</div>
          <h1>Dominium</h1>
          <span>Painel Administrativo</span>
        </div>

        <div className="form-header">
          <p>Entre com suas credenciais de síndico ou administrador para acessar o sistema.</p>
        </div>

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          {error && (
            <div style={{ color: 'var(--danger)', fontSize: '13px', textAlign: 'center', fontWeight: 500 }}>
              {error}
            </div>
          )}

          <div className="form-group" style={{ marginBottom: 0 }}>
            <label htmlFor="admin-email">E-mail</label>
            <div className="input-wrapper">
              <input
                type="email"
                id="admin-email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="sindico@condominio.com"
                required
              />
            </div>
          </div>

          <div className="form-group" style={{ marginBottom: 0 }}>
            <label htmlFor="admin-senha">Senha</label>
            <div className="input-wrapper">
              <input
                type="password"
                id="admin-senha"
                value={senha}
                onChange={(e) => setSenha(e.target.value)}
                placeholder="Senha de administrador"
                required
              />
            </div>
          </div>

          <div className="form-links" style={{ marginBottom: 0, justifyContent: 'flex-end' }}>
            <span className="forgot-password" style={{ cursor: 'pointer' }}>
              Esqueci minha senha
            </span>
          </div>

           <button type="submit" className="submit-btn" style={{ padding: '15px' }} disabled={loading}>
            {loading ? 'Entrando...' : 'Entrar no Painel'} <ArrowRight size={18} />
          </button>
        </form>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', alignItems: 'center' }}>
          <button 
            type="button" 
            className="switch-panel-btn"
            onClick={() => onNavigate('login-morador')}
          >
            Acessar Área do Morador
          </button>
          <button 
            type="button" 
            className="switch-panel-btn"
            style={{ color: 'var(--gray-600)' }}
            onClick={() => onNavigate('cadastro')}
          >
            Cadastrar nova conta
          </button>
        </div>

        <div className="admin-footer">
          Residencial Parque Verde · 120 unidades
        </div>
      </div>
    </div>
  );
};
