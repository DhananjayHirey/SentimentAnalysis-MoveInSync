import React, { useState } from 'react';
import { Truck, MapPin, Smartphone, ShieldCheck, Star, Send } from 'lucide-react';

const FeedbackForm = () => {
    // Feature flags - could be loaded from backend or config
    const [config] = useState({
        enableDriverFeedback: true,
        enableTripFeedback: true,
        enableAppFeedback: true,
        enableMarshalFeedback: true
    });

    const [formData, setFormData] = useState({
        entityType: 'DRIVER',
        entityId: '',
        rating: 5,
        comment: ''
    });

    const [status, setStatus] = useState({ type: '', message: '' });

    const feedbackTypes = [
        { id: 'DRIVER', label: 'Driver', icon: Truck, enabled: config.enableDriverFeedback },
        { id: 'TRIP', label: 'Trip', icon: MapPin, enabled: config.enableTripFeedback },
        { id: 'APP', label: 'Mobile App', icon: Smartphone, enabled: config.enableAppFeedback },
        { id: 'MARSHAL', label: 'Marshal', icon: ShieldCheck, enabled: config.enableMarshalFeedback }
    ].filter(t => t.enabled);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setStatus({ type: 'info', message: 'Submitting...' });

        try {
            const response = await fetch('http://localhost:8080/api/feedback', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formData)
            });

            if (response.ok) {
                setStatus({ type: 'success', message: 'Thank you for your feedback!' });
                setFormData({ ...formData, entityId: '', comment: '' });
            } else {
                throw new Error('Submission failed');
            }
        } catch (error) {
            setStatus({ type: 'danger', message: 'Failed to submit feedback. Please try again.' });
        }
    };

    return (
        <div className="card glass-panel" style={{ maxWidth: '500px', margin: '2rem auto', padding: '2rem' }}>
            <h2 style={{ marginBottom: '1.5rem', textAlign: 'center' }}>Employee Feedback</h2>

            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
                <div>
                    <label className="stat-label" style={{ marginBottom: '0.75rem', display: 'block' }}>What would you like to rate?</label>
                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem' }}>
                        {feedbackTypes.map(type => (
                            <button
                                key={type.id}
                                type="button"
                                className={`btn ${ formData.entityType === type.id ? 'btn-primary' : '' }`}
                                style={{
                                    display: 'flex',
                                    alignItems: 'center',
                                    gap: '0.5rem',
                                    justifyContent: 'center',
                                    background: formData.entityType === type.id ? '' : 'rgba(255,255,255,0.05)',
                                    border: formData.entityType === type.id ? 'none' : '1px solid var(--border)'
                                }}
                                onClick={() => setFormData({ ...formData, entityType: type.id })}
                            >
                                <type.icon size={16} /> {type.label}
                            </button>
                        ))}
                    </div>
                </div>

                <div>
                    <label className="stat-label" style={{ marginBottom: '0.5rem', display: 'block' }}>{formData.entityType} ID</label>
                    <input
                        type="text"
                        className="glass-panel"
                        style={{ width: '100%', padding: '0.75rem', color: 'white', background: 'rgba(0,0,0,0.2)' }}
                        placeholder={`Enter ${ formData.entityType.toLowerCase() } ID (e.g. D123)`}
                        value={formData.entityId}
                        onChange={(e) => setFormData({ ...formData, entityId: e.target.value })}
                        required
                    />
                </div>

                <div>
                    <label className="stat-label" style={{ marginBottom: '0.5rem', display: 'block' }}>Rating</label>
                    <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'center' }}>
                        {[1, 2, 3, 4, 5].map(star => (
                            <Star
                                key={star}
                                size={32}
                                fill={star <= formData.rating ? 'var(--warning)' : 'none'}
                                color={star <= formData.rating ? 'var(--warning)' : 'var(--text-muted)'}
                                style={{ cursor: 'pointer', transition: 'transform 0.1s' }}
                                onClick={() => setFormData({ ...formData, rating: star })}
                                onMouseEnter={(e) => e.target.style.transform = 'scale(1.2)'}
                                onMouseLeave={(e) => e.target.style.transform = 'scale(1)'}
                            />
                        ))}
                    </div>
                </div>

                <div>
                    <label className="stat-label" style={{ marginBottom: '0.5rem', display: 'block' }}>Comments</label>
                    <textarea
                        className="glass-panel"
                        style={{ width: '100%', padding: '0.75rem', color: 'white', background: 'rgba(0,0,0,0.2)', minHeight: '100px', resize: 'vertical' }}
                        placeholder="Tell us more about your experience..."
                        value={formData.comment}
                        onChange={(e) => setFormData({ ...formData, comment: e.target.value })}
                    />
                </div>

                <button type="submit" className="btn btn-primary" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem', padding: '1rem' }}>
                    <Send size={18} /> Submit Feedback
                </button>

                {status.message && (
                    <div className={`badge badge-${ status.type }`} style={{ padding: '0.75rem', textAlign: 'center', borderRadius: '0.5rem' }}>
                        {status.message}
                    </div>
                )}
            </form>
        </div>
    );
};

export default FeedbackForm;
