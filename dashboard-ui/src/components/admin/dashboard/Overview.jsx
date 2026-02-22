import React, { useState, useEffect } from 'react';
import {
    Users,
    Activity,
    AlertTriangle,
    MessageSquare,
    ChevronRight,
    User,
    MapPin,
    Smartphone,
    Info
} from 'lucide-react';
import {
    BarChart,
    Bar,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip as RechartsTooltip,
    ResponsiveContainer,
    Cell
} from 'recharts';
import { Client } from '@stomp/stompjs';

const ANALYTICS_API = 'http://localhost:8081/api/analytics';

const Overview = () => {
    const [drivers, setDrivers] = useState([]);
    const [summary, setSummary] = useState({
        totalDrivers: 0,
        totalFeedbacks: 0,
        averageSystemSentiment: 0,
        activeAlerts: 0
    });
    const [alerts, setAlerts] = useState([]);

    const fetchData = async () => {
        try {
            const [driversRes, summaryRes] = await Promise.all([
                fetch(`${ ANALYTICS_API }/drivers`),
                fetch(`${ ANALYTICS_API }/summary`)
            ]);
            if (driversRes.ok) {
                const driversData = await driversRes.json();
                setDrivers(Array.isArray(driversData) ? driversData : []);
            }
            if (summaryRes.ok) {
                const summaryData = await summaryRes.json();
                setSummary(summaryData);
            }
        } catch (error) {
            console.error("Failed to fetch overview data", error);
        }
    };

    useEffect(() => {
        fetchData();
        const interval = setInterval(fetchData, 5000);

        const client = new Client({
            brokerURL: 'ws://localhost:8081/ws-alerts',
            onConnect: () => {
                client.subscribe('/topic/alerts', (message) => {
                    const alert = JSON.parse(message.body);
                    setAlerts(prev => [alert, ...prev].slice(0, 5));
                });
            },
            reconnectDelay: 5000,
        });

        client.activate();

        return () => {
            clearInterval(interval);
            client.deactivate();
        };
    }, []);

    const getSentimentColor = (score) => {
        if (score >= 4.0) return '#10b981'; // Green
        if (score >= 2.5) return '#f59e0b'; // Amber
        return '#ef4444'; // Red
    };

    return (
        <div className="animate-fade-in">
            {/* Stats Grid */}
            <div className="stats-grid">
                <div className="card">
                    <div className="stat-header">
                        <span className="stat-label">System Sentiment</span>
                        <Activity size={20} color="#10b981" />
                    </div>
                    <div className="stat-value">{((summary.averageSystemSentiment / 5) * 100).toFixed(1)}%</div>
                    <div className="stat-trend positive">+2.4% from last hour</div>
                </div>
                <div className="card">
                    <div className="stat-header">
                        <span className="stat-label">Total Feedbacks</span>
                        <MessageSquare size={20} color="#3b82f6" />
                    </div>
                    <div className="stat-value">{summary.totalFeedbacks}</div>
                    <div className="stat-trend">Live updates enabled</div>
                </div>
                <div className="card">
                    <div className="stat-header">
                        <span className="stat-label">Active Drivers</span>
                        <Users size={20} color="#8b5cf6" />
                    </div>
                    <div className="stat-value">{summary.totalDrivers}</div>
                    <div className="stat-trend">Current active sessions</div>
                </div>
                <div className="card">
                    <div className="stat-header">
                        <span className="stat-label">Critical Alerts</span>
                        <AlertTriangle size={20} color="#ef4444" />
                    </div>
                    <div className="stat-value">{summary.activeAlerts}</div>
                    <div className="stat-trend negative">Requires attention</div>
                </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '1.5rem', marginTop: '1.5rem' }}>
                {/* Alerts Section */}
                <div className="card">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                        <h3 style={{ fontSize: '1.125rem', fontWeight: 600 }}>Live Alerts</h3>
                        <span className="badge" style={{ background: '#fee2e2', color: '#ef4444' }}>Live</span>
                    </div>
                    <div className="alert-list">
                        {alerts.length === 0 ? (
                            <p style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '2rem' }}>No active alerts</p>
                        ) : (
                            alerts.map((alert, i) => (
                                <div key={i} className="alert-item bounce-in">
                                    <div className="alert-icon">
                                        <AlertTriangle size={18} color="#ef4444" />
                                    </div>
                                    <div className="alert-content">
                                        <div className="alert-title">Critical Sentiment: {alert.driverId}</div>
                                        <div className="alert-desc">{alert.message}</div>
                                    </div>
                                </div>
                            ))
                        )}
                    </div>
                </div>

                {/* Chart Section */}
                <div className="card">
                    <h3 style={{ fontSize: '1.125rem', fontWeight: 600, marginBottom: '1.5rem' }}>Driver Sentiment Overview</h3>
                    <div style={{ height: '300px', width: '100%' }}>
                        {drivers.length === 0 ? (
                            <div style={{ height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--text-muted)' }}>
                                No active driver data
                            </div>
                        ) : (
                            <ResponsiveContainer width="100%" height="100%">
                                <BarChart data={drivers}>
                                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="var(--border)" />
                                    <XAxis
                                        dataKey="driverId"
                                        axisLine={false}
                                        tickLine={false}
                                        tick={{ fill: 'var(--text-muted)', fontSize: 12 }}
                                    />
                                    <YAxis
                                        axisLine={false}
                                        tickLine={false}
                                        tick={{ fill: 'var(--text-muted)', fontSize: 12 }}
                                        domain={[0, 5]}
                                    />
                                    <RechartsTooltip
                                        cursor={{ fill: 'var(--glass)' }}
                                        contentStyle={{
                                            background: 'var(--bg-card)',
                                            border: '1px solid var(--border)',
                                            borderRadius: '8px',
                                            boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1)'
                                        }}
                                        formatter={(value) => [Number(value).toFixed(2), "Sentiment Rating"]}
                                    />
                                    <Bar dataKey="averageScore" radius={[4, 4, 0, 0]}>
                                        {drivers.map((entry, index) => (
                                            <Cell key={`cell-${ index }`} fill={getSentimentColor(entry.averageScore)} />
                                        ))}
                                    </Bar>
                                </BarChart>
                            </ResponsiveContainer>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Overview;
