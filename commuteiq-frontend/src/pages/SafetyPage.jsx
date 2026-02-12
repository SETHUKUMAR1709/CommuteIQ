import { useState, useEffect } from 'react';
import { getSafetyEvents, reportSafetyEvent } from '../api/apiService';

const typeBadge = {
    PANIC: 'bg-red-500/15 text-red-400',
    ROUTE_DEVIATION: 'bg-amber-500/15 text-amber-400',
    OVERSPEED: 'bg-orange-500/15 text-orange-400',
    UNSAFE_DROP: 'bg-pink-500/15 text-pink-400',
};

const typeIcons = {
    PANIC: '🚨',
    ROUTE_DEVIATION: '↪️',
    OVERSPEED: '⚡',
    UNSAFE_DROP: '⚠️',
};

export default function SafetyPage() {
    const [events, setEvents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showModal, setShowModal] = useState(false);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [form, setForm] = useState({ ridePlanId: '', type: 'PANIC', description: '' });

    useEffect(() => { loadEvents(); }, [page]);

    const loadEvents = async () => {
        setLoading(true);
        try {
            const res = await getSafetyEvents(page);
            const data = res.data || res;
            setEvents(data.content || data || []);
            setTotalPages(data.totalPages || 1);
        } catch { setEvents([]); }
        finally { setLoading(false); }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await reportSafetyEvent({ ...form, ridePlanId: parseInt(form.ridePlanId) });
            setShowModal(false);
            setForm({ ridePlanId: '', type: 'PANIC', description: '' });
            loadEvents();
        } catch (err) { alert(err.message); }
    };

    return (
        <div>
            <div className="flex items-center justify-between mb-7">
                <div>
                    <h2 className="text-2xl font-bold text-gray-100">Safety Events</h2>
                    <p className="text-gray-400 text-sm mt-1">Monitor and report safety incidents</p>
                </div>
                <button onClick={() => setShowModal(true)} className="px-5 py-2.5 bg-gradient-to-r from-red-500 to-rose-400 text-white text-sm font-semibold rounded-lg shadow-lg shadow-red-500/30 hover:shadow-red-500/50 hover:-translate-y-0.5 transition-all cursor-pointer">
                    🚨 Report Event
                </button>
            </div>

            {/* Event Type Summary */}
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-7">
                {Object.entries(typeIcons).map(([type, icon]) => {
                    const count = events.filter(e => e.type === type).length;
                    return (
                        <div key={type} className="bg-[rgba(22,22,40,0.85)] backdrop-blur-xl border border-white/[0.08] rounded-xl p-4 text-center">
                            <div className="text-xl mb-1">{icon}</div>
                            <div className="text-xl font-bold text-gray-200">{count}</div>
                            <div className="text-[0.65rem] text-gray-500 uppercase tracking-wider">{type.replace('_', ' ')}</div>
                        </div>
                    );
                })}
            </div>

            <div className="bg-[rgba(22,22,40,0.85)] backdrop-blur-xl border border-white/[0.08] rounded-2xl overflow-hidden">
                {loading ? (
                    <div className="flex items-center justify-center h-40 text-gray-500">
                        <div className="w-5 h-5 border-2 border-white/[0.08] border-t-indigo-500 rounded-full animate-spin mr-3" /> Loading...
                    </div>
                ) : events.length === 0 ? (
                    <div className="text-center py-16 text-gray-500">
                        <div className="text-4xl mb-3 opacity-50">🛡️</div>
                        <p>No safety events recorded</p>
                    </div>
                ) : (
                    <>
                        <div className="overflow-x-auto">
                            <table className="w-full">
                                <thead>
                                    <tr>
                                        {['ID', 'Type', 'Ride Plan', 'Description', 'Timestamp'].map(h => (
                                            <th key={h} className="text-left px-6 py-3.5 text-[0.7rem] font-semibold uppercase tracking-wider text-gray-500 border-b border-white/[0.08] bg-white/[0.02]">{h}</th>
                                        ))}
                                    </tr>
                                </thead>
                                <tbody>
                                    {events.map(evt => (
                                        <tr key={evt.id} className="hover:bg-white/[0.03] transition-colors">
                                            <td className="px-6 py-3.5 text-sm text-gray-300 font-mono">#{evt.id}</td>
                                            <td className="px-6 py-3.5">
                                                <span className={`inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-[0.65rem] font-semibold uppercase ${typeBadge[evt.type] || 'bg-gray-500/15 text-gray-400'}`}>
                                                    {typeIcons[evt.type]} {evt.type?.replace('_', ' ')}
                                                </span>
                                            </td>
                                            <td className="px-6 py-3.5 text-sm text-gray-400">Plan #{evt.ridePlanId}</td>
                                            <td className="px-6 py-3.5 text-sm text-gray-400 max-w-xs truncate">{evt.description || '-'}</td>
                                            <td className="px-6 py-3.5 text-sm text-gray-500">{evt.timestamp ? new Date(evt.timestamp).toLocaleString() : '-'}</td>
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
                            <h3 className="text-lg font-semibold text-gray-100">Report Safety Event</h3>
                            <button onClick={() => setShowModal(false)} className="text-gray-500 text-xl hover:text-gray-300 cursor-pointer bg-transparent border-none">×</button>
                        </div>
                        <form onSubmit={handleSubmit} className="p-6 space-y-4">
                            <div>
                                <label className="block text-xs font-semibold text-gray-400 uppercase tracking-wider mb-1.5">Ride Plan ID</label>
                                <input type="number" value={form.ridePlanId} onChange={e => setForm({ ...form, ridePlanId: e.target.value })} required className="w-full px-3 py-2.5 bg-white/5 border border-white/[0.08] rounded-lg text-gray-100 text-sm focus:outline-none focus:border-indigo-500" />
                            </div>
                            <div>
                                <label className="block text-xs font-semibold text-gray-400 uppercase tracking-wider mb-1.5">Event Type</label>
                                <select value={form.type} onChange={e => setForm({ ...form, type: e.target.value })} className="w-full px-3 py-2.5 bg-white/5 border border-white/[0.08] rounded-lg text-gray-100 text-sm focus:outline-none focus:border-indigo-500 appearance-none">
                                    <option value="PANIC" className="bg-[#161628]">🚨 Panic</option>
                                    <option value="ROUTE_DEVIATION" className="bg-[#161628]">↪️ Route Deviation</option>
                                    <option value="OVERSPEED" className="bg-[#161628]">⚡ Overspeed</option>
                                    <option value="UNSAFE_DROP" className="bg-[#161628]">⚠️ Unsafe Drop</option>
                                </select>
                            </div>
                            <div>
                                <label className="block text-xs font-semibold text-gray-400 uppercase tracking-wider mb-1.5">Description</label>
                                <textarea value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} rows={3} className="w-full px-3 py-2.5 bg-white/5 border border-white/[0.08] rounded-lg text-gray-100 text-sm focus:outline-none focus:border-indigo-500 resize-none" placeholder="Describe the incident..." />
                            </div>
                            <div className="flex justify-end gap-3 pt-2">
                                <button type="button" onClick={() => setShowModal(false)} className="px-4 py-2.5 bg-white/5 text-gray-400 rounded-lg text-sm border border-white/[0.08] hover:bg-white/10 transition-colors cursor-pointer">Cancel</button>
                                <button type="submit" className="px-5 py-2.5 bg-gradient-to-r from-red-500 to-rose-400 text-white text-sm font-semibold rounded-lg shadow-lg shadow-red-500/30 cursor-pointer">Report</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}
