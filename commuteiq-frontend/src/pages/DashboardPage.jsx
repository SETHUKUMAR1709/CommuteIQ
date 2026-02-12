import { useState, useEffect } from 'react';
import { getDashboardAnalytics } from '../api/apiService';

const statCards = [
    { key: 'totalEmployees', label: 'Total Employees', icon: '👥', color: 'indigo' },
    { key: 'activeRidePlans', label: 'Active Plans', icon: '🗺️', color: 'emerald' },
    { key: 'pendingRequests', label: 'Pending Requests', icon: '⏳', color: 'amber' },
    { key: 'safetyEvents', label: 'Safety Events', icon: '🛡️', color: 'red' },
];

const colorMap = {
    indigo: { gradient: 'from-indigo-500 to-purple-500', bg: 'bg-indigo-500/10', text: 'text-indigo-400', border: 'border-indigo-500/30' },
    emerald: { gradient: 'from-emerald-500 to-teal-400', bg: 'bg-emerald-500/10', text: 'text-emerald-400', border: 'border-emerald-500/30' },
    amber: { gradient: 'from-amber-500 to-yellow-400', bg: 'bg-amber-500/10', text: 'text-amber-400', border: 'border-amber-500/30' },
    red: { gradient: 'from-red-500 to-rose-400', bg: 'bg-red-500/10', text: 'text-red-400', border: 'border-red-500/30' },
};

export default function DashboardPage() {
    const [stats, setStats] = useState({});
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadStats();
    }, []);

    const loadStats = async () => {
        try {
            const res = await getDashboardAnalytics();
            setStats(res.data || res || {});
        } catch {
            // Use demo data if analytics API isn't ready
            setStats({ totalEmployees: 0, activeRidePlans: 0, pendingRequests: 0, safetyEvents: 0, totalCost: 0, avgOccupancy: 0 });
        } finally {
            setLoading(false);
        }
    };

    if (loading) return (
        <div className="flex items-center justify-center h-64 text-gray-500">
            <div className="w-6 h-6 border-2 border-white/[0.08] border-t-indigo-500 rounded-full animate-spin mr-3" />
            Loading dashboard...
        </div>
    );

    return (
        <div>
            <div className="mb-7">
                <h2 className="text-2xl font-bold text-gray-100">Dashboard</h2>
                <p className="text-gray-400 text-sm mt-1">Transportation analytics overview</p>
            </div>

            {/* Stat Cards */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5 mb-7">
                {statCards.map(card => {
                    const c = colorMap[card.color];
                    return (
                        <div key={card.key} className="bg-[rgba(22,22,40,0.85)] backdrop-blur-xl border border-white/[0.08] rounded-2xl p-5 relative overflow-hidden hover:-translate-y-1 hover:shadow-xl transition-all duration-200 group">
                            <div className={`absolute top-0 left-0 right-0 h-[3px] bg-gradient-to-r ${c.gradient}`} />
                            <div className="text-2xl mb-3">{card.icon}</div>
                            <div className="text-3xl font-extrabold text-gray-100 mb-1">{stats[card.key] ?? 0}</div>
                            <div className="text-xs text-gray-500 uppercase tracking-wider font-medium">{card.label}</div>
                        </div>
                    );
                })}
            </div>

            {/* Additional Stats */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                <div className="bg-[rgba(22,22,40,0.85)] backdrop-blur-xl border border-white/[0.08] rounded-2xl p-6">
                    <h3 className="text-sm font-semibold text-gray-300 mb-4">Cost Overview</h3>
                    <div className="space-y-3">
                        <div className="flex justify-between items-center">
                            <span className="text-sm text-gray-400">Total Cost</span>
                            <span className="text-lg font-bold text-emerald-400">₹{(stats.totalCost || 0).toLocaleString()}</span>
                        </div>
                        <div className="flex justify-between items-center">
                            <span className="text-sm text-gray-400">Fuel Cost</span>
                            <span className="text-sm font-semibold text-gray-300">₹{(stats.fuelCost || 0).toLocaleString()}</span>
                        </div>
                        <div className="flex justify-between items-center">
                            <span className="text-sm text-gray-400">Driver Cost</span>
                            <span className="text-sm font-semibold text-gray-300">₹{(stats.driverCost || 0).toLocaleString()}</span>
                        </div>
                    </div>
                </div>

                <div className="bg-[rgba(22,22,40,0.85)] backdrop-blur-xl border border-white/[0.08] rounded-2xl p-6">
                    <h3 className="text-sm font-semibold text-gray-300 mb-4">Performance</h3>
                    <div className="space-y-3">
                        <div className="flex justify-between items-center">
                            <span className="text-sm text-gray-400">Avg Occupancy</span>
                            <span className="text-lg font-bold text-indigo-400">{(stats.avgOccupancy || 0).toFixed(1)}%</span>
                        </div>
                        <div className="w-full h-2 bg-white/5 rounded-full overflow-hidden">
                            <div className="h-full bg-gradient-to-r from-indigo-500 to-purple-500 rounded-full transition-all" style={{ width: `${Math.min(stats.avgOccupancy || 0, 100)}%` }} />
                        </div>
                        <div className="flex justify-between items-center mt-2">
                            <span className="text-sm text-gray-400">Completed Rides</span>
                            <span className="text-sm font-semibold text-gray-300">{stats.completedRides || 0}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
