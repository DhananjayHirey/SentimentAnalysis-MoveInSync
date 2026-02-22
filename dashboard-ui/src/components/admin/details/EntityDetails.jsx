import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { User, MapPin, Smartphone, Info, ChevronRight, Activity } from 'lucide-react';

const ADMIN_API = 'http://localhost:8081/api/feedback/admin';

const EntityDetails = () => {
    const { type, id } = useParams();
    const navigate = useNavigate();
    const [entityDetails, setEntityDetails] = useState(null);
    const [loading, setLoading] = useState(true);
    const [filter, setFilter] = useState('ALL');

    const fetchEntityDetails = async (type, id) => {
        try {
            setLoading(true);
            const res = await fetch(`${ ADMIN_API }/details/${ type }/${ id }`);
            if (res.ok) {
                const data = await res.json();
                setEntityDetails(data);
            }
        } catch (error) {
            console.error("Failed to fetch entity details", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (type && id) {
            fetchEntityDetails(type, id);
        }
    }, [type, id]);

    const getRatingColor = (rating) => {
        if (rating >= 4.0) return '#10b981';
        if (rating >= 3.0) return '#f59e0b';
        return '#ef4444';
    };

    const getSentimentLabel = (rating) => {
        if (rating >= 4) return 'POSITIVE';
        if (rating === 3) return 'NEUTRAL';
        return 'NEGATIVE';
    };

    const filteredFeedbacks = entityDetails?.feedbacks?.filter(f => {
        if (filter === 'ALL') return true;
        return f.sentimentLabel === filter;
    }) || [];

    if (loading) return (
        <div className="card animate-pulse" style={{ textAlign: 'center', padding: '4rem' }}>
            <Activity className="animate-spin" style={{ margin: '0 auto 1rem' }} />
            <p>Loading details for {type} {id}...</p>
        </div>
    );

    if (!entityDetails) return (
        <div className="card" style={{ textAlign: 'center', padding: '4rem' }}>
            <Info size={48} color="var(--text-muted)" style={{ margin: '0 auto 1rem' }} />
            <p>No details found for {type} {id}</p>
            <button className="pill-button" onClick={() => navigate(-1)} style={{ marginTop: '1rem' }}>Go Back</button>
        </div>
    );

    return (
        <div className="animate-fade-in">
            <div style={{ marginBottom: '1.5rem' }}>
                <button onClick={() => navigate(-1)} className="text-button" style={{ marginBottom: '1rem' }}>
                    ← Back
                </button>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
                    <h2 style={{ fontSize: '1.5rem', fontWeight: 700 }}>{type} Details: {id}</h2>
                    <div className="glass-panel" style={{ padding: '0.5rem', display: 'flex', gap: '0.5rem' }}>
                        {['ALL', 'POSITIVE', 'NEUTRAL', 'NEGATIVE'].map(f => (
                            <button
                                key={f}
                                className={`pill-button ${ filter === f ? 'active' : '' }`}
                                onClick={() => setFilter(f)}
                                style={{ fontSize: '0.75rem', padding: '4px 12px' }}
                            >
                                {f}
                            </button>
                        ))}
                    </div>
                </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '1.5rem' }}>
                <div className="card">
                    <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
                        <div style={{
                            width: '80px',
                            height: '80px',
                            background: 'var(--primary-light)',
                            borderRadius: '50%',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            margin: '0 auto 1rem'
                        }}>
                            {type === 'DRIVER' ? <User size={40} color="var(--primary)" /> :
                                type === 'TRIP' ? <MapPin size={40} color="var(--primary)" /> :
                                    <Smartphone size={40} color="var(--primary)" />}
                        </div>
                        <h3 style={{ fontSize: '1.25rem', fontWeight: 600 }}>{id}</h3>
                        <p style={{ color: 'var(--text-muted)' }}>{type} Profile</p>
                    </div>

                    <div className="glass-panel" style={{ padding: '1.5rem', textAlign: 'center' }}>
                        <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '0.5rem' }}>Overall Rating</p>
                        <p style={{ fontSize: '2.5rem', fontWeight: 800, color: getRatingColor(entityDetails.overallRating) }}>
                            {(entityDetails.overallRating || 0).toFixed(2)}
                        </p>
                        <div style={{ display: 'flex', justifyContent: 'center', gap: '4px', marginTop: '0.5rem' }}>
                            {[1, 2, 3, 4, 5].map(star => (
                                <span key={star} style={{ color: star <= Math.round(entityDetails.overallRating) ? '#f59e0b' : '#e2e8f0' }}>★</span>
                            ))}
                        </div>
                    </div>

                    <div style={{ marginTop: '1.5rem', display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '0.5rem' }}>
                        <div className="sentiment-stat positive">
                            <span>Pos</span>
                            <strong>{entityDetails.positiveCount || 0}</strong>
                        </div>
                        <div className="sentiment-stat neutral">
                            <span>Neu</span>
                            <strong>{entityDetails.neutralCount || 0}</strong>
                        </div>
                        <div className="sentiment-stat negative">
                            <span>Neg</span>
                            <strong>{entityDetails.negativeCount || 0}</strong>
                        </div>
                    </div>
                </div>

                <div className="card">
                    <h3 style={{ fontSize: '1.125rem', fontWeight: 600, marginBottom: '1.5rem' }}>
                        {filter !== 'ALL' ? `${ filter } ` : ''}Feedback History ({filteredFeedbacks.length})
                    </h3>
                    <div className="feedback-list">
                        {filteredFeedbacks.length === 0 ? (
                            <p style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '2rem' }}>No {filter.toLowerCase()} feedback found</p>
                        ) : (
                            filteredFeedbacks.map((f, i) => (
                                <div key={i} className="feedback-item" style={{
                                    padding: '1rem',
                                    borderBottom: '1px solid var(--border)',
                                    display: 'flex',
                                    gap: '1rem'
                                }}>
                                    <div style={{
                                        minWidth: '40px',
                                        height: '40px',
                                        borderRadius: '8px',
                                        background: `${ getRatingColor(f.rating) }15`,
                                        color: getRatingColor(f.rating),
                                        display: 'flex',
                                        alignItems: 'center',
                                        justifyContent: 'center',
                                        fontWeight: 700
                                    }}>
                                        {f.rating}
                                    </div>
                                    <div style={{ flex: 1 }}>
                                        <p style={{ fontWeight: 500, marginBottom: '0.25rem' }}>"{f.comment}"</p>
                                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                            <span style={{
                                                fontSize: '0.65rem',
                                                padding: '2px 6px',
                                                borderRadius: '3px',
                                                background: f.sentimentLabel === 'POSITIVE' ? '#10b98120' : f.sentimentLabel === 'NEUTRAL' ? '#f59e0b20' : '#ef444420',
                                                color: f.sentimentLabel === 'POSITIVE' ? '#10b981' : f.sentimentLabel === 'NEUTRAL' ? '#f59e0b' : '#ef4444',
                                                fontWeight: 600
                                            }}>
                                                {f.sentimentLabel}
                                            </span>
                                            <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                                                {new Date(f.createdAt).toLocaleString()}
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            ))
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default EntityDetails;
