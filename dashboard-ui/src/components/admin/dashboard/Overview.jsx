import React, { useState, useEffect, useMemo } from 'react';
import {
    Users,
    Activity,
    AlertTriangle,
    MessageSquare,
    User,
    MapPin,
    Smartphone,
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

const ANALYTICS_API = 'http://localhost:8081/api/analytics';
const CRITICAL_THRESHOLD = 2.5;
const POLL_INTERVAL = 30000; // 30 seconds

const Overview = () => {
    const [drivers, setDrivers] = useState([]);
    const [trips, setTrips] = useState([]);
    const [marshals, setMarshals] = useState([]);
    const [chartTab, setChartTab] = useState('DRIVER');
    const [summary, setSummary] = useState({
        totalDrivers: 0,
        totalTrips: 0,
        totalMarshals: 0,
        totalFeedbacks: 0,
        averageSystemSentiment: 0,
        activeAlerts: 0
    });

    const fetchData = async () => {
        try {
            const [driversRes, tripsRes, marshalsRes, summaryRes] = await Promise.all([
                fetch(`${ ANALYTICS_API }/drivers`),
                fetch(`${ ANALYTICS_API }/trips`),
                fetch(`${ ANALYTICS_API }/marshals`),
                fetch(`${ ANALYTICS_API }/summary`)
            ]);
            if (driversRes.ok) setDrivers(await driversRes.json());
            if (tripsRes.ok) setTrips(await tripsRes.json());
            if (marshalsRes.ok) setMarshals(await marshalsRes.json());
            if (summaryRes.ok) setSummary(await summaryRes.json());
        } catch (error) {
            console.error("Failed to fetch overview data", error);
        }
    };

    // Derive alerts from chart data — only non-recovered entities (score < threshold)
    const alerts = useMemo(() => {
        const criticalDrivers = drivers
            .filter(d => d.averageScore < CRITICAL_THRESHOLD)
            .map(d => ({ type: 'Driver', id: d.driverId, score: d.averageScore }));

        const criticalTrips = trips
            .filter(t => t.averageScore < CRITICAL_THRESHOLD)
            .map(t => ({ type: 'Trip', id: t.tripId, score: t.averageScore }));

        const criticalMarshals = marshals
            .filter(m => m.averageScore < CRITICAL_THRESHOLD)
            .map(m => ({ type: 'Marshal', id: m.marshalId, score: m.averageScore }));

        return [...criticalDrivers, ...criticalTrips, ...criticalMarshals]
            .sort((a, b) => a.score - b.score); // worst first
    }, [drivers, trips, marshals]);

    useEffect(() => {
        fetchData();
        
        // Poll analytics data every 30 seconds
        const interval = setInterval(fetchData, POLL_INTERVAL);

        return () => {
            clearInterval(interval);
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
                {/* <div className="card">
                    <div className="stat-header">
                        <span className="stat-label">System Sentiment</span>
                        <Activity size={20} color="#10b981" />
                    </div>
                    <div className="stat-value">{((summary.averageSystemSentiment / 5) * 100).toFixed(1)}%</div>
                    <div className="stat-trend positive">+2.4% from last hour</div>
                </div> */}
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
                        <span className="stat-label">Entities Tracked</span>
                        <Users size={20} color="#8b5cf6" />
                    </div>
                    <div className="stat-value">{summary.totalDrivers + (summary.totalTrips || 0) + (summary.totalMarshals || 0)}</div>
                    <div className="stat-trend">{summary.totalDrivers}D · {summary.totalTrips || 0}T · {summary.totalMarshals || 0}M</div>
                </div>
                <div className="card">
                    <div className="stat-header">
                        <span className="stat-label">Critical Alerts</span>
                        <AlertTriangle size={20} color="#ef4444" />
                    </div>
                    <div className="stat-value">{alerts.length}</div>
                    <div className="stat-trend negative">{alerts.length > 0 ? 'Requires attention' : 'All clear'}</div>
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
                            alerts.map((alert, i) => {
                                const icon = alert.type === 'Driver'
                                    ? <User size={18} color="#ef4444" />
                                    : alert.type === 'Trip'
                                        ? <MapPin size={18} color="#ef4444" />
                                        : <Smartphone size={18} color="#ef4444" />;
                                return (
                                    <div key={`${ alert.type }-${ alert.id }`} className="alert-item bounce-in">
                                        <div className="alert-icon">{icon}</div>
                                        <div className="alert-content">
                                            <div className="alert-title">
                                                {alert.type} #{alert.id}
                                            </div>
                                            <div className="alert-desc">
                                                Avg score: <strong style={{ color: '#ef4444' }}>{Number(alert.score).toFixed(2)}</strong>
                                                &nbsp;— Low sentiment detected
                                            </div>
                                        </div>
                                    </div>
                                );
                            })
                        )}
                    </div>
                </div>

                {/* Chart Section */}
                <div className="card">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
                        <h3 style={{ fontSize: '1.125rem', fontWeight: 600 }}>Sentiment Overview</h3>
                        <div style={{ display: 'flex', gap: '0.5rem' }}>
                            {['DRIVER', 'TRIP', 'MARSHAL'].map(t => (
                                <button
                                    key={t}
                                    className={`pill-button ${ chartTab === t ? 'active' : '' }`}
                                    onClick={() => setChartTab(t)}
                                    style={{ fontSize: '0.75rem', padding: '4px 12px' }}
                                >
                                    {t}
                                </button>
                            ))}
                        </div>
                    </div>
                    <div style={{ height: '300px', width: '100%', minWidth: 0 }}>
                        {(() => {
                            const data = chartTab === 'DRIVER' ? drivers : chartTab === 'TRIP' ? trips : marshals;
                            const idKey = chartTab === 'DRIVER' ? 'driverId' : chartTab === 'TRIP' ? 'tripId' : 'marshalId';
                            if (data.length === 0) return (
                                <div style={{ height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--text-muted)' }}>
                                    No {chartTab.toLowerCase()} data in database yet
                                </div>
                            );
                            return (
                                <ResponsiveContainer width="100%" height="100%">
                                    <BarChart data={data}>
                                        <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="var(--border)" />
                                        <XAxis
                                            dataKey={idKey}
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
                                            formatter={(value) => [Number(value).toFixed(2), "Avg Rating"]}
                                        />
                                        <Bar dataKey="averageScore" radius={[4, 4, 0, 0]}>
                                            {data.map((entry, index) => (
                                                <Cell key={`cell-${ index }`} fill={getSentimentColor(entry.averageScore)} />
                                            ))}
                                        </Bar>
                                    </BarChart>
                                </ResponsiveContainer>
                            );
                        })()}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Overview;
