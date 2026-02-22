import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import AdminLayout from './admin/layout/AdminLayout';
import Overview from './admin/dashboard/Overview';
import DriverList from './admin/drivers/DriverList';
import FeedbackCenter from './admin/feedback/FeedbackCenter';
import EntityDetails from './admin/details/EntityDetails';

const Dashboard = () => {
  return (
    <AdminLayout>
      <Routes>
        <Route path="dashboard" element={<Overview />} />
        <Route path="drivers" element={<DriverList />} />
        <Route path="feedback" element={<FeedbackCenter />} />
        <Route path="details/:type/:id" element={<EntityDetails />} />
        <Route path="/" element={<Navigate to="dashboard" replace />} />
      </Routes>
    </AdminLayout>
  );
};

export default Dashboard;
