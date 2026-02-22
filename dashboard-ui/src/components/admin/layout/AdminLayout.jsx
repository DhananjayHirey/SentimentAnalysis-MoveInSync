import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import {
    Users,
    MessageSquare,
    Activity,
    AlertTriangle,
    LayoutDashboard,
    LogOut,
    Bell,
    User
} from 'lucide-react';

const AdminLayout = ({ children }) => {
    const navigate = useNavigate();

    return (
        <div className="admin-container">
            {/* Sidebar */}
            <aside className="sidebar">
                <div className="sidebar-header">
                    <div className="logo-icon">
                        <Activity size={24} color="white" />
                    </div>
                    <span className="logo-text">SentimentAI</span>
                </div>

                <nav className="sidebar-nav">
                    <NavLink
                        to="/admin/dashboard"
                        className={({ isActive }) => `nav-item ${ isActive ? 'active' : '' }`}
                    >
                        <LayoutDashboard size={20} />
                        <span>Dashboard</span>
                    </NavLink>
                    <NavLink
                        to="/admin/drivers"
                        className={({ isActive }) => `nav-item ${ isActive ? 'active' : '' }`}
                    >
                        <Users size={20} />
                        <span>Drivers</span>
                    </NavLink>
                    <NavLink
                        to="/admin/feedback"
                        className={({ isActive }) => `nav-item ${ isActive ? 'active' : '' }`}
                    >
                        <MessageSquare size={20} />
                        <span>Feedback</span>
                    </NavLink>
                </nav>

                <div className="sidebar-footer">
                    <button className="nav-item" onClick={() => navigate('/')} style={{ width: '100%', border: 'none', background: 'transparent', cursor: 'pointer' }}>
                        <LogOut size={20} />
                        <span>Logout</span>
                    </button>
                </div>
            </aside>

            {/* Main Content */}
            <main className="main-content">
                <header className="header">
                    <div className="header-search">
                        <h2 style={{ fontSize: '1.25rem', fontWeight: 600 }}>Admin Portal</h2>
                    </div>
                    <div className="header-actions">
                        <button className="icon-button">
                            <Bell size={20} />
                            <span className="notification-badge"></span>
                        </button>
                        <div className="user-profile">
                            <div className="avatar">
                                <User size={20} />
                            </div>
                            <div className="user-info">
                                <span className="user-name">Administrator</span>
                                <span className="user-role">Super Admin</span>
                            </div>
                        </div>
                    </div>
                </header>

                <div className="content-scrollable">
                    {children}
                </div>
            </main>
        </div>
    );
};

export default AdminLayout;
