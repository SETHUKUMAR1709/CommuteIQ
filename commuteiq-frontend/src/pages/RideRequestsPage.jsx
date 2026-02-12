import { useState, useEffect } from 'react';
import { getRideRequests, createRideRequest, cancelRideRequest } from '../api/apiService';

const statusBadge = {
    PENDING: 'bg-amber-500/15 text-amber-400',
    PLANNED: 'bg-purple-500/15 text-purple-400',
    COMPLETED: 'bg-emerald-500/15 text-emerald-400',
    CANCELLED: 'bg-red-500/15 text-red-400',
};

export default function RideRequestsPage() {
    const [requests, setRequests] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showModal, setShowModal] = useState(false);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [form, setForm] = useState({ employeeId: '', requestDate: '' });

    useEffect(() => { loadRequests(); }, [page]);

    const loadRequests = async () => {
        setLoading(true);
        try {
            const res = await getRideRequests(page);
            const data = res.data || res;
            setRequests(data.content || data || []);
            setTotalPages(data.totalPages || 1);
        } catch { setRequests([]); }
        finally { setLoading(false); }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await createRideRequest({ ...form, employeeId: parseInt(form.employeeId) });
            setShowModal(false);
            setForm({ employeeId: '', requestDate: '' });
            loadRequests();
        } catch (err) { alert(err.message); }
    };

    const handleCancel = async (id) => {
        if (!confirm('Cancel this ride request?')) return;
        try { await cancelRideRequest(id); loadRequests(); }
        catch (err) { alert(err.message); }
    };

    return (
        <div>
            <div className="flex items-center justify-between mb-7">
                <div>
                    <h2 className="text-2xl font-bold text-gray-100">Ride Requests</h2>
                    <p className="text-gray-400 text-sm mt-1">Manage ride requests from employees</p>
                </div>
                <button onClick={() => setShowModal(true)} className="px-5 py-2.5 bg-gradient-to-r from-indigo-500 to-purple-500 text-white text-sm font-semibold rounded-lg shadow-lg shadow-indigo-500/30 hover:shadow-indigo-500/50 hover:-translate-y-0.5 transition-all cursor-pointer">
                    + New Request
                </button>
            </div>

            <div className="bg-[rgba(22,22,40,0.85)] backdrop-blur-xl border border-white/[0.08] rounded-2xl overflow-hidden">
                {loading ? (
                    <div className="flex items-center justify-center h-40 text-gray-500">
                        <div className="w-5 h-5 border-2 border-white/[0.08] border-t-indigo-500 rounded-full animate-spin mr-3" /> Loading...
                    </div>
                ) : requests.length === 0 ? (
                    <div className="text-center py-16 text-gray-500">
                        <div className="text-4xl mb-3 opacity-50">🚗</div>
                        <p>No ride requests found</p>
                    </div>
                ) : (
                    <>
                        <div className="overflow-x-auto">
                            <table className="w-full">
                                <thead>
                                    <tr>
                                        {['ID', 'Employee', 'Date', 'Status', 'Created', 'Actions'].map(h => (
                                            <th key={h} className="text-left px-6 py-3.5 text-[0.7rem] font-semibold uppercase tracking-wider text-gray-500 border-b border-white/[0.08] bg-white/[0.02]">{h}</th>
                                        ))}
                                    </tr>
                                </thead>
                                <tbody>
                                    {requests.map(req => (
                                        <tr key={req.id} className="hover:bg-white/[0.03] transition-colors">
                                            <td className="px-6 py-3.5 text-sm text-gray-300 font-mono">#{req.id}</td>
                                            <td className="px-6 py-3.5 text-sm text-gray-300">{req.employeeName || `Emp #${req.employeeId}`}</td>
                                            <td className="px-6 py-3.5 text-sm text-gray-400">{req.requestDate}</td>
                                            <td className="px-6 py-3.5">
                                                <span className={`inline-flex px-2.5 py-1 rounded-full text-[0.65rem] font-semibold uppercase ${statusBadge[req.status] || 'bg-gray-500/15 text-gray-400'}`}>
                                                    {req.status}
                                                </span>
                                            </td>
                                            <td className="px-6 py-3.5 text-sm text-gray-500">{req.createdAt ? new Date(req.createdAt).toLocaleDateString() : '-'}</td>
                                            <td className="px-6 py-3.5">
                                                {req.status === 'PENDING' && (
                                                    <button onClick={() => handleCancel(req.id)} className="px-3 py-1.5 text-xs text-red-400 bg-red-500/10 border border-red-500/20 rounded-lg hover:bg-red-500/20 transition-colors cursor-pointer">
                                                        Cancel
                                                    </button>
                                                )}
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

            {showModal && (
                <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-[1000]">
                    <div className="bg-[#161628] border border-white/[0.08] rounded-2xl w-[90%] max-w-md">
                        <div className="flex items-center justify-between px-6 py-5 border-b border-white/[0.08]">
                            <h3 className="text-lg font-semibold text-gray-100">New Ride Request</h3>
                            <button onClick={() => setShowModal(false)} className="text-gray-500 text-xl hover:text-gray-300 cursor-pointer bg-transparent border-none">×</button>
                        </div>
                        <form onSubmit={handleSubmit} className="p-6 space-y-4">
                            <div>
                                <label className="block text-xs font-semibold text-gray-400 uppercase tracking-wider mb-1.5">Employee ID</label>
                                <input type="number" value={form.employeeId} onChange={e => setForm({ ...form, employeeId: e.target.value })} required className="w-full px-3 py-2.5 bg-white/5 border border-white/[0.08] rounded-lg text-gray-100 text-sm focus:outline-none focus:border-indigo-500" />
                            </div>
                            <div>
                                <label className="block text-xs font-semibold text-gray-400 uppercase tracking-wider mb-1.5">Request Date</label>
                                <input type="date" value={form.requestDate} onChange={e => setForm({ ...form, requestDate: e.target.value })} required className="w-full px-3 py-2.5 bg-white/5 border border-white/[0.08] rounded-lg text-gray-100 text-sm focus:outline-none focus:border-indigo-500" />
                            </div>
                            <div className="flex justify-end gap-3 pt-2">
                                <button type="button" onClick={() => setShowModal(false)} className="px-4 py-2.5 bg-white/5 text-gray-400 rounded-lg text-sm border border-white/[0.08] hover:bg-white/10 transition-colors cursor-pointer">Cancel</button>
                                <button type="submit" className="px-5 py-2.5 bg-gradient-to-r from-indigo-500 to-purple-500 text-white text-sm font-semibold rounded-lg shadow-lg shadow-indigo-500/30 cursor-pointer">Submit</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}
