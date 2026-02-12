import { useState, useEffect } from 'react';
import { getEmployees, createEmployee, deleteEmployee } from '../api/apiService';

export default function EmployeesPage() {
    const [employees, setEmployees] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showModal, setShowModal] = useState(false);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [form, setForm] = useState({ name: '', gender: 'Male', department: '', homeLatitude: 0, homeLongitude: 0, officeLocation: '' });

    useEffect(() => { loadEmployees(); }, [page]);

    const loadEmployees = async () => {
        setLoading(true);
        try {
            const res = await getEmployees(page);
            const data = res.data || res;
            setEmployees(data.content || data || []);
            setTotalPages(data.totalPages || 1);
        } catch { setEmployees([]); }
        finally { setLoading(false); }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await createEmployee(form);
            setShowModal(false);
            setForm({ name: '', gender: 'Male', department: '', homeLatitude: 0, homeLongitude: 0, officeLocation: '' });
            loadEmployees();
        } catch (err) { alert(err.message); }
    };

    const handleDelete = async (id) => {
        if (!confirm('Are you sure?')) return;
        try { await deleteEmployee(id); loadEmployees(); }
        catch (err) { alert(err.message); }
    };

    return (
        <div>
            <div className="flex items-center justify-between mb-7">
                <div>
                    <h2 className="text-2xl font-bold text-gray-100">Employees</h2>
                    <p className="text-gray-400 text-sm mt-1">Manage employee records</p>
                </div>
                <button onClick={() => setShowModal(true)} className="px-5 py-2.5 bg-gradient-to-r from-indigo-500 to-purple-500 text-white text-sm font-semibold rounded-lg shadow-lg shadow-indigo-500/30 hover:shadow-indigo-500/50 hover:-translate-y-0.5 transition-all cursor-pointer">
                    + Add Employee
                </button>
            </div>

            {/* Table */}
            <div className="bg-[rgba(22,22,40,0.85)] backdrop-blur-xl border border-white/[0.08] rounded-2xl overflow-hidden">
                {loading ? (
                    <div className="flex items-center justify-center h-40 text-gray-500">
                        <div className="w-5 h-5 border-2 border-white/[0.08] border-t-indigo-500 rounded-full animate-spin mr-3" /> Loading...
                    </div>
                ) : employees.length === 0 ? (
                    <div className="text-center py-16 text-gray-500">
                        <div className="text-4xl mb-3 opacity-50">👥</div>
                        <p>No employees found</p>
                    </div>
                ) : (
                    <>
                        <div className="overflow-x-auto">
                            <table className="w-full">
                                <thead>
                                    <tr>
                                        <th className="text-left px-6 py-3.5 text-[0.7rem] font-semibold uppercase tracking-wider text-gray-500 border-b border-white/[0.08] bg-white/[0.02]">Name</th>
                                        <th className="text-left px-6 py-3.5 text-[0.7rem] font-semibold uppercase tracking-wider text-gray-500 border-b border-white/[0.08] bg-white/[0.02]">Gender</th>
                                        <th className="text-left px-6 py-3.5 text-[0.7rem] font-semibold uppercase tracking-wider text-gray-500 border-b border-white/[0.08] bg-white/[0.02]">Department</th>
                                        <th className="text-left px-6 py-3.5 text-[0.7rem] font-semibold uppercase tracking-wider text-gray-500 border-b border-white/[0.08] bg-white/[0.02]">Office</th>
                                        <th className="text-left px-6 py-3.5 text-[0.7rem] font-semibold uppercase tracking-wider text-gray-500 border-b border-white/[0.08] bg-white/[0.02]">Status</th>
                                        <th className="text-left px-6 py-3.5 text-[0.7rem] font-semibold uppercase tracking-wider text-gray-500 border-b border-white/[0.08] bg-white/[0.02]">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {employees.map(emp => (
                                        <tr key={emp.id} className="hover:bg-white/[0.03] transition-colors">
                                            <td className="px-6 py-3.5 text-sm text-gray-300 font-medium">{emp.name}</td>
                                            <td className="px-6 py-3.5 text-sm text-gray-400">{emp.gender}</td>
                                            <td className="px-6 py-3.5 text-sm text-gray-400">{emp.department}</td>
                                            <td className="px-6 py-3.5 text-sm text-gray-400">{emp.officeLocation}</td>
                                            <td className="px-6 py-3.5">
                                                <span className={`inline-flex px-2.5 py-1 rounded-full text-[0.65rem] font-semibold uppercase ${emp.active !== false ? 'bg-emerald-500/15 text-emerald-400' : 'bg-red-500/15 text-red-400'}`}>
                                                    {emp.active !== false ? 'Active' : 'Inactive'}
                                                </span>
                                            </td>
                                            <td className="px-6 py-3.5">
                                                <button onClick={() => handleDelete(emp.id)} className="px-3 py-1.5 text-xs text-red-400 bg-red-500/10 border border-red-500/20 rounded-lg hover:bg-red-500/20 transition-colors cursor-pointer">
                                                    Delete
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                        {/* Pagination */}
                        <div className="flex items-center justify-center gap-2 p-4 border-t border-white/[0.04]">
                            <button disabled={page === 0} onClick={() => setPage(p => p - 1)} className="px-4 py-1.5 border border-white/[0.08] rounded-lg text-xs text-gray-400 hover:bg-white/[0.06] disabled:opacity-30 disabled:cursor-not-allowed transition-colors cursor-pointer">Prev</button>
                            <span className="text-xs text-gray-500">{page + 1} / {totalPages || 1}</span>
                            <button disabled={page >= totalPages - 1} onClick={() => setPage(p => p + 1)} className="px-4 py-1.5 border border-white/[0.08] rounded-lg text-xs text-gray-400 hover:bg-white/[0.06] disabled:opacity-30 disabled:cursor-not-allowed transition-colors cursor-pointer">Next</button>
                        </div>
                    </>
                )}
            </div>

            {/* Add Modal */}
            {showModal && (
                <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-[1000] animate-[fadeIn_0.2s_ease]">
                    <div className="bg-[#161628] border border-white/[0.08] rounded-2xl w-[90%] max-w-lg animate-[slideUp_0.3s_ease]">
                        <div className="flex items-center justify-between px-6 py-5 border-b border-white/[0.08]">
                            <h3 className="text-lg font-semibold text-gray-100">Add Employee</h3>
                            <button onClick={() => setShowModal(false)} className="text-gray-500 text-xl hover:text-gray-300 cursor-pointer bg-transparent border-none">×</button>
                        </div>
                        <form onSubmit={handleSubmit} className="p-6">
                            <div className="grid grid-cols-2 gap-4 mb-4">
                                <div>
                                    <label className="block text-xs font-semibold text-gray-400 uppercase tracking-wider mb-1.5">Name</label>
                                    <input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} required className="w-full px-3 py-2.5 bg-white/5 border border-white/[0.08] rounded-lg text-gray-100 text-sm focus:outline-none focus:border-indigo-500 transition-colors" />
                                </div>
                                <div>
                                    <label className="block text-xs font-semibold text-gray-400 uppercase tracking-wider mb-1.5">Gender</label>
                                    <select value={form.gender} onChange={e => setForm({ ...form, gender: e.target.value })} className="w-full px-3 py-2.5 bg-white/5 border border-white/[0.08] rounded-lg text-gray-100 text-sm focus:outline-none focus:border-indigo-500 appearance-none">
                                        <option value="Male" className="bg-[#161628]">Male</option>
                                        <option value="Female" className="bg-[#161628]">Female</option>
                                    </select>
                                </div>
                            </div>
                            <div className="grid grid-cols-2 gap-4 mb-4">
                                <div>
                                    <label className="block text-xs font-semibold text-gray-400 uppercase tracking-wider mb-1.5">Department</label>
                                    <input value={form.department} onChange={e => setForm({ ...form, department: e.target.value })} required className="w-full px-3 py-2.5 bg-white/5 border border-white/[0.08] rounded-lg text-gray-100 text-sm focus:outline-none focus:border-indigo-500 transition-colors" />
                                </div>
                                <div>
                                    <label className="block text-xs font-semibold text-gray-400 uppercase tracking-wider mb-1.5">Office Location</label>
                                    <input value={form.officeLocation} onChange={e => setForm({ ...form, officeLocation: e.target.value })} required className="w-full px-3 py-2.5 bg-white/5 border border-white/[0.08] rounded-lg text-gray-100 text-sm focus:outline-none focus:border-indigo-500 transition-colors" />
                                </div>
                            </div>
                            <div className="grid grid-cols-2 gap-4 mb-4">
                                <div>
                                    <label className="block text-xs font-semibold text-gray-400 uppercase tracking-wider mb-1.5">Latitude</label>
                                    <input type="number" step="any" value={form.homeLatitude} onChange={e => setForm({ ...form, homeLatitude: parseFloat(e.target.value) })} required className="w-full px-3 py-2.5 bg-white/5 border border-white/[0.08] rounded-lg text-gray-100 text-sm focus:outline-none focus:border-indigo-500 transition-colors" />
                                </div>
                                <div>
                                    <label className="block text-xs font-semibold text-gray-400 uppercase tracking-wider mb-1.5">Longitude</label>
                                    <input type="number" step="any" value={form.homeLongitude} onChange={e => setForm({ ...form, homeLongitude: parseFloat(e.target.value) })} required className="w-full px-3 py-2.5 bg-white/5 border border-white/[0.08] rounded-lg text-gray-100 text-sm focus:outline-none focus:border-indigo-500 transition-colors" />
                                </div>
                            </div>
                            <div className="flex justify-end gap-3 mt-6">
                                <button type="button" onClick={() => setShowModal(false)} className="px-4 py-2.5 bg-white/5 text-gray-400 rounded-lg text-sm border border-white/[0.08] hover:bg-white/10 transition-colors cursor-pointer">Cancel</button>
                                <button type="submit" className="px-5 py-2.5 bg-gradient-to-r from-indigo-500 to-purple-500 text-white text-sm font-semibold rounded-lg shadow-lg shadow-indigo-500/30 cursor-pointer">Add Employee</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}
