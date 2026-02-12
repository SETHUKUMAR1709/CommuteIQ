import { useState, useEffect } from 'react';
import { getRidePlans, generateRidePlans, updateRidePlanStatus } from '../api/apiService';

const statusBadge = {
    SCHEDULED: 'bg-indigo-500/15 text-indigo-400',
    IN_PROGRESS: 'bg-blue-500/15 text-blue-400',
    COMPLETED: 'bg-emerald-500/15 text-emerald-400',
    CANCELLED: 'bg-red-500/15 text-red-400',
};

export default function RidePlansPage() {
    const [plans, setPlans] = useState([]);
    const [loading, setLoading] = useState(true);
    const [generating, setGenerating] = useState(false);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [genDate, setGenDate] = useState('');
    const [showGenModal, setShowGenModal] = useState(false);

    useEffect(() => { loadPlans(); }, [page]);

    const loadPlans = async () => {
        setLoading(true);
        try {
            const res = await getRidePlans(page);
            const data = res.data || res;
            setPlans(data.content || data || []);
            setTotalPages(data.totalPages || 1);
        } catch { setPlans([]); }
        finally { setLoading(false); }
    };

    const handleGenerate = async () => {
        if (!genDate) return alert('Please pick a date');
        setGenerating(true);
        try {
            await generateRidePlans(genDate);
            setShowGenModal(false);
            setGenDate('');
            loadPlans();
        } catch (err) { alert(err.message); }
        finally { setGenerating(false); }
    };

    const handleStatusUpdate = async (id, status) => {
        try { await updateRidePlanStatus(id, status); loadPlans(); }
        catch (err) { alert(err.message); }
    };

    return (
        <div>
            <div className="flex items-center justify-between mb-7">
                <div>
                    <h2 className="text-2xl font-bold text-gray-100">Ride Plans</h2>
                    <p className="text-gray-400 text-sm mt-1">View and manage optimized ride plans</p>
                </div>
                <button onClick={() => setShowGenModal(true)} className="px-5 py-2.5 bg-gradient-to-r from-emerald-500 to-teal-400 text-white text-sm font-semibold rounded-lg shadow-lg shadow-emerald-500/30 hover:shadow-emerald-500/50 hover:-translate-y-0.5 transition-all cursor-pointer">
                    ⚡ Generate Plans
                </button>
            </div>

            <div className="bg-[rgba(22,22,40,0.85)] backdrop-blur-xl border border-white/[0.08] rounded-2xl overflow-hidden">
                {loading ? (
                    <div className="flex items-center justify-center h-40 text-gray-500">
                        <div className="w-5 h-5 border-2 border-white/[0.08] border-t-indigo-500 rounded-full animate-spin mr-3" /> Loading...
                    </div>
                ) : plans.length === 0 ? (
                    <div className="text-center py-16 text-gray-500">
                        <div className="text-4xl mb-3 opacity-50">🗺️</div>
                        <p>No ride plans yet. Generate plans to get started.</p>
                    </div>
                ) : (
                    <>
                        <div className="overflow-x-auto">
                            <table className="w-full">
                                <thead>
                                    <tr>
                                        {['ID', 'Date', 'Vehicle', 'Driver', 'Employees', 'Distance', 'Status', 'Actions'].map(h => (
                                            <th key={h} className="text-left px-6 py-3.5 text-[0.7rem] font-semibold uppercase tracking-wider text-gray-500 border-b border-white/[0.08] bg-white/[0.02]">{h}</th>
                                        ))}
                                    </tr>
                                </thead>
                                <tbody>
                                    {plans.map(plan => (
                                        <tr key={plan.id} className="hover:bg-white/[0.03] transition-colors">
                                            <td className="px-6 py-3.5 text-sm text-gray-300 font-mono">#{plan.id}</td>
                                            <td className="px-6 py-3.5 text-sm text-gray-300">{plan.date}</td>
                                            <td className="px-6 py-3.5 text-sm text-gray-400">{plan.vehiclePlate || `V#${plan.vehicleId}`}</td>
                                            <td className="px-6 py-3.5 text-sm text-gray-400">{plan.driverName || `D#${plan.driverId}`}</td>
                                            <td className="px-6 py-3.5 text-sm text-gray-400">{plan.employeeCount || plan.employees?.length || 0}</td>
                                            <td className="px-6 py-3.5 text-sm text-gray-400">{plan.estimatedDistance ? `${plan.estimatedDistance.toFixed(1)} km` : '-'}</td>
                                            <td className="px-6 py-3.5">
                                                <span className={`inline-flex px-2.5 py-1 rounded-full text-[0.65rem] font-semibold uppercase ${statusBadge[plan.status] || 'bg-gray-500/15 text-gray-400'}`}>
                                                    {plan.status?.replace('_', ' ')}
                                                </span>
                                            </td>
                                            <td className="px-6 py-3.5">
                                                <div className="flex gap-1.5">
                                                    {plan.status === 'SCHEDULED' && (
                                                        <button onClick={() => handleStatusUpdate(plan.id, 'IN_PROGRESS')} className="px-2.5 py-1 text-[0.65rem] text-blue-400 bg-blue-500/10 border border-blue-500/20 rounded-md hover:bg-blue-500/20 transition-colors cursor-pointer">Start</button>
                                                    )}
                                                    {plan.status === 'IN_PROGRESS' && (
                                                        <button onClick={() => handleStatusUpdate(plan.id, 'COMPLETED')} className="px-2.5 py-1 text-[0.65rem] text-emerald-400 bg-emerald-500/10 border border-emerald-500/20 rounded-md hover:bg-emerald-500/20 transition-colors cursor-pointer">Complete</button>
                                                    )}
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                        <div className="flex items-center justify-center gap-2 p-4 border-t border-white/[0.04]">
                            <button disabled={page === 0} onClick={() => setPage(p => p - 1)} className="px-4 py-1.5 border border-white/[0.08] rounded-lg text-xs text-gray-400 hover:bg-white/[0.06] disabled:opacity-30 disabled:cursor-not-allowed transition-colors cursor-pointer">Prev</button>
                            <span className="text-xs text-gray-500">{page + 1} / {totalPages || 1}</span>
                            <button disabled={page >= totalPages - 1} onClick={() => setPage(p => p + 1)} className="px-4 py-1.5 border border-white/[0.08] rounded-lg text-xs text-gray-400 hover:bg-white/[0.06] disabled:opacity-30 disabled:cursor-not-allowed transition-colors cursor-pointer">Next</button>
                        </div>
                    </>
                )}
            </div>

            {showGenModal && (
                <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-[1000]">
                    <div className="bg-[#161628] border border-white/[0.08] rounded-2xl w-[90%] max-w-sm">
                        <div className="flex items-center justify-between px-6 py-5 border-b border-white/[0.08]">
                            <h3 className="text-lg font-semibold text-gray-100">Generate Ride Plans</h3>
                            <button onClick={() => setShowGenModal(false)} className="text-gray-500 text-xl hover:text-gray-300 cursor-pointer bg-transparent border-none">×</button>
                        </div>
                        <div className="p-6">
                            <label className="block text-xs font-semibold text-gray-400 uppercase tracking-wider mb-1.5">Date</label>
                            <input type="date" value={genDate} onChange={e => setGenDate(e.target.value)} className="w-full px-3 py-2.5 bg-white/5 border border-white/[0.08] rounded-lg text-gray-100 text-sm focus:outline-none focus:border-indigo-500 mb-5" />
                            <div className="flex justify-end gap-3">
                                <button onClick={() => setShowGenModal(false)} className="px-4 py-2.5 bg-white/5 text-gray-400 rounded-lg text-sm border border-white/[0.08] hover:bg-white/10 transition-colors cursor-pointer">Cancel</button>
                                <button onClick={handleGenerate} disabled={generating} className="px-5 py-2.5 bg-gradient-to-r from-emerald-500 to-teal-400 text-white text-sm font-semibold rounded-lg shadow-lg disabled:opacity-50 cursor-pointer">
                                    {generating ? 'Generating...' : '⚡ Generate'}
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
