import { BrowserRouter, Routes, Route, Navigate, useNavigate } from 'react-router-dom'
import Dashboard from './components/Dashboard'
import FeedbackForm from './components/FeedbackForm'
import './App.css'

const NavigationSwitcher = () => {
  const navigate = useNavigate();
  return (
    <div style={{ position: 'fixed', bottom: '1rem', right: '1rem', zIndex: 1000, display: 'flex', gap: '0.5rem' }}>
      <button
        className="btn"
        style={{ background: 'var(--bg-card)', border: '1px solid var(--border)', color: 'white' }}
        onClick={() => navigate('/admin')}
      >
        Admin Dashboard
      </button>
      <button
        className="btn"
        style={{ background: 'var(--bg-card)', border: '1px solid var(--border)', color: 'white' }}
        onClick={() => navigate('/feedback')}
      >
        Employee Feedback
      </button>
    </div>
  );
};

function App() {
  return (
    <BrowserRouter>
      <div className="app-root">
        <NavigationSwitcher />

        <Routes>
          <Route path="/admin/*" element={<Dashboard />} />
          <Route path="/feedback" element={<FeedbackForm />} />
          <Route path="/" element={<Navigate to="/admin" replace />} />
        </Routes>
      </div>
    </BrowserRouter>
  )
}

export default App
