import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronRight, Info } from 'lucide-react';

const FEEDBACK_API = 'http://localhost:8080/api/feedback';

const FeedbackCenter = () => {
    const [feedbackType, setFeedbackType] = useState('DRIVER');
    const [feedbackList, setFeedbackList] = useState([]);
    const [averages, setAverages] = useState([]);
    const navigate = useNavigate();

    const fetchFeedbacks = async (type) => {
        try {
            const res = await fetch(`${ FEEDBACK_API }/admin/feedbacks/${ type }`);
            if (res.ok) {
                const data = await res.json();
                setFeedbackList(Array.isArray(data) ? data : []);
            }
        } catch (error) {
            console.error("Failed to fetch feedbacks", error);
        }
    };

    const fetchAverages = async (type) => {
        try {
            const res = await fetch(`${ FEEDBACK_API }/admin/averages/${ type }`);
            if (res.ok) {
                const data = await res.json();
                setAverages(Array.isArray(data) ? data : [data]);
            }
        } catch (error) {
            console.error("Failed to fetch averages", error);
        }
    };

    useEffect(() => {
        fetchFeedbacks(feedbackType);
        fetchAverages(feedbackType);
    }, [feedbackType]);

    const getRatingColor = (rating) => {
        if (rating >= 4.0) return '#10b981';
        if (rating >= 3.0) return '#f59e0b';
        return '#ef4444';
    };

    return (
        <div className="animate-fade-in">
            <div className="card" style={{ marginBottom: '1.5rem' }}>
                <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
                    <span style={{ fontWeight: 600 }}>Filter By Type:</span>
                    {['DRIVER', 'TRIP', 'MARSHAL', 'APP'].map(t => (
                        <button
                            key={t}
                            className={`pill-button ${ feedbackType === t ? 'active' : '' }`}
                            onClick={() => setFeedbackType(t)}
                        >
                            {t}
                        </button>
                    ))}
                </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '1.5rem' }}>
                <div className="card">
                    <h3 style={{ fontSize: '1.125rem', fontWeight: 600, marginBottom: '1rem' }}>{feedbackType} Summary</h3>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                        {feedbackType === 'APP' ? (
                            <div className="glass-panel" style={{ padding: '1.5rem', textAlign: 'center' }}>
                                <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>Overall App Rating</p>
                                <p style={{ fontSize: '2rem', fontWeight: 700, color: getRatingColor(averages[0]?.average) }}>
                                    {(averages[0]?.average || 0).toFixed(2)}
                                </p>
                            </div>
                        ) : (
                            averages.slice(0, 5).map((a, i) => (
                                <div key={i} className="list-item" onClick={() => navigate(`/admin/details/${ feedbackType }/${ a.id }`)} style={{ cursor: 'pointer' }}>
                                    <div style={{ fontWeight: 600 }}>{a.id}</div>
                                    <div style={{ color: getRatingColor(a.average), fontWeight: 700 }}>
                                        {(a.average || 0).toFixed(2)}
                                    </div>
                                </div>
                            ))
                        )}
                        {feedbackType !== 'APP' && averages.length > 5 && (
                            <p style={{ textAlign: 'center', fontSize: '0.875rem', color: 'var(--text-muted)' }}>
                                Showing top 5 {feedbackType.toLowerCase()}s
                            </p>
                        )}
                    </div>
                </div>

                <div className="card">
                    <h3 style={{ fontSize: '1.125rem', fontWeight: 600, marginBottom: '1rem' }}>Recent {feedbackType} Feedback</h3>
                    <div className="feedback-scroll">
                        {feedbackList.length === 0 ? (
                            <p style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '2rem' }}>No feedback entries</p>
                        ) : (
                            feedbackList.map((f, i) => (
                                <div key={i} className="feedback-card">
                                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                                        <span style={{ fontWeight: 600 }}>{f.driverId || f.tripId || f.marshalId || f.userId}</span>
                                        <span className="rating-tag" style={{ background: `${ getRatingColor(f.rating) }15`, color: getRatingColor(f.rating) }}>
                                            {f.rating} ★
                                        </span>
                                    </div>
                                    <p style={{ fontSize: '0.925rem', color: 'var(--text-main)', fontStyle: 'italic' }}>"{f.comment}"</p>
                                    <div style={{ marginTop: '0.75rem', fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                                        {new Date(f.createdAt).toLocaleString()}
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

export default FeedbackCenter;
