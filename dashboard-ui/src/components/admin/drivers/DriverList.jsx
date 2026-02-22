import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronRight } from 'lucide-react';

const FEEDBACK_API = 'http://localhost:8080/api/feedback';

const DriverList = () => {
    const [dbDriverRatings, setDbDriverRatings] = useState([]);
    const navigate = useNavigate();

    const fetchDbDriverRatings = async () => {
        try {
            const res = await fetch(`${ FEEDBACK_API }/drivers/ratings`);
            if (res.ok) {
                const data = await res.json();
                setDbDriverRatings(Array.isArray(data) ? data : []);
            }
        } catch (error) {
            console.error("Failed to fetch DB driver ratings", error);
        }
    };

    useEffect(() => {
        fetchDbDriverRatings();
    }, []);

    const getRatingColor = (rating) => {
        if (rating >= 4.0) return '#10b981';
        if (rating >= 3.0) return '#f59e0b';
        return '#ef4444';
    };

    return (
        <div className="card animate-fade-in">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
                <h3 style={{ fontSize: '1.25rem', fontWeight: 600 }}>Driver Historical Ratings (Database)</h3>
            </div>

            {dbDriverRatings.length === 0 ? (
                <p style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '2rem' }}>No historical data available</p>
            ) : (
                <div className="table-container">
                    <table>
                        <thead>
                            <tr>
                                <th>Driver ID</th>
                                <th>Average Rating</th>
                                <th>Progress</th>
                                <th style={{ textAlign: 'right' }}>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            {dbDriverRatings.map((r, i) => (
                                <tr key={i}>
                                    <td style={{ fontWeight: 600 }}>{r.driverId}</td>
                                    <td>
                                        <span style={{
                                            fontWeight: 700,
                                            color: getRatingColor(r.averageRating),
                                            background: `${ getRatingColor(r.averageRating) }15`,
                                            padding: '2px 8px',
                                            borderRadius: '4px'
                                        }}>
                                            {(r.averageRating || 0).toFixed(2)}
                                        </span>
                                    </td>
                                    <td style={{ width: '40%' }}>
                                        <div className="progress-bar-container">
                                            <div
                                                className="progress-bar-fill"
                                                style={{
                                                    width: `${ ((r.averageRating || 0) / 5) * 100 }%`,
                                                    background: getRatingColor(r.averageRating)
                                                }}
                                            ></div>
                                        </div>
                                    </td>
                                    <td style={{ textAlign: 'right' }}>
                                        <button
                                            className="text-button"
                                            onClick={() => navigate(`/admin/details/DRIVER/${ r.driverId }`)}
                                        >
                                            View Details
                                            <ChevronRight size={16} />
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
};

export default DriverList;
