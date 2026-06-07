import { useState } from 'react';
import { LoginMorador } from './views/LoginMorador';
import { LoginAdmin } from './views/LoginAdmin';
import { Cadastro } from './views/Cadastro';
import { DashboardMorador } from './views/DashboardMorador';
import { DashboardAdmin } from './views/DashboardAdmin';
import './App.css';

type ViewState = 'login-morador' | 'login-admin' | 'cadastro' | 'dashboard-morador' | 'dashboard-admin';

function App() {
  const [view, setView] = useState<ViewState>(() => {
    const savedView = localStorage.getItem('dominium_view');
    const savedEmail = localStorage.getItem('dominium_userEmail');
    if (savedEmail && (savedView === 'dashboard-morador' || savedView === 'dashboard-admin')) {
      return savedView as ViewState;
    }
    return 'login-morador';
  });

  const [userEmail, setUserEmail] = useState(() => {
    return localStorage.getItem('dominium_userEmail') || '';
  });

  const handleLoginSuccess = (role: 'morador' | 'admin', email: string) => {
    setUserEmail(email);
    localStorage.setItem('dominium_userEmail', email);
    if (role === 'admin') {
      setView('dashboard-admin');
      localStorage.setItem('dominium_view', 'dashboard-admin');
    } else {
      setView('dashboard-morador');
      localStorage.setItem('dominium_view', 'dashboard-morador');
    }
  };

  const handleLogout = () => {
    setUserEmail('');
    setView('login-morador');
    localStorage.removeItem('dominium_userEmail');
    localStorage.removeItem('dominium_view');
  };

  const handleNavigate = (targetView: 'login-morador' | 'login-admin' | 'cadastro') => {
    setView(targetView);
  };

  return (
    <>
      {view === 'login-morador' && (
        <LoginMorador onLoginSuccess={handleLoginSuccess} onNavigate={handleNavigate} />
      )}
      {view === 'login-admin' && (
        <LoginAdmin onLoginSuccess={handleLoginSuccess} onNavigate={handleNavigate} />
      )}
      {view === 'cadastro' && (
        <Cadastro onNavigate={handleNavigate} />
      )}
      {view === 'dashboard-morador' && (
        <DashboardMorador userEmail={userEmail} onLogout={handleLogout} />
      )}
      {view === 'dashboard-admin' && (
        <DashboardAdmin userEmail={userEmail} onLogout={handleLogout} />
      )}
    </>
  );
}

export default App;
