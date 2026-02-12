import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function LoginPage() {
    const [isRegister, setIsRegister] = useState(false);
    const [formData, setFormData] = useState({ username: '', password: '', email: '', role: 'ADMIN' });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const { loginUser, registerUser } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            if (isRegister) {
                await registerUser(formData);
                setIsRegister(false);
                setError('');
                alert('Registration successful! Please login.');
            } else {
                await loginUser({ username: formData.username, password: formData.password });
                navigate('/');
            }
        } catch (err) {
            setError(err.message || 'Something went wrong');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-[#0f0f1a] relative overflow-hidden">
            {/* Background effects */}
            <div className="absolute w-[600px] h-[600px] bg-[radial-gradient(circle,rgba(99,102,241,0.15),transparent_70%)] -top-24 -right-24" />
            <div className="absolute w-[400px] h-[400px] bg-[radial-gradient(circle,rgba(139,92,246,0.1),transparent_70%)] -bottom-12 -left-12" />

            <div className="w-full max-w-md px-6 relative z-10">
                {/* Brand */}
                <div className="text-center mb-9">
                    <div className="text-4xl w-[72px] h-[72px] bg-gradient-to-br from-indigo-500 to-purple-500 rounded-2xl flex items-center justify-center mx-auto mb-4 shadow-lg shadow-indigo-500/30">🚌</div>
                    <h1 className="text-3xl font-extrabold bg-gradient-to-r from-indigo-400 to-purple-400 bg-clip-text text-transparent">CommuteIQ</h1>
                    <p className="text-gray-500 text-sm mt-1">Corporate Transportation Optimizer</p>
                </div>

                {/* Card */}
                <div className="bg-[rgba(22,22,40,0.85)] backdrop-blur-xl border border-white/[0.08] rounded-2xl p-8">
                    <h2 className="text-lg font-semibold text-gray-100 mb-6">{isRegister ? 'Create Account' : 'Welcome Back'}</h2>

                    {error && (
                        <div className="bg-red-500/10 border border-red-500/20 rounded-lg px-4 py-3 text-red-400 text-sm mb-4">{error}</div>
                    )}

                    <form onSubmit={handleSubmit}>
                        <div className="mb-4">
                            <label className="block text-xs font-semibold text-gray-400 uppercase tracking-wider mb-1.5">Username</label>
                            <input
                                name="username"
                                value={formData.username}
                                onChange={handleChange}
                                required
                                className="w-full px-4 py-3 bg-white/5 border border-white/[0.08] rounded-lg text-gray-100 text-sm focus:outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/20 transition-all placeholder:text-gray-600"
                                placeholder="Enter username"
                            />
                        </div>

                        {isRegister && (
                            <div className="mb-4">
                                <label className="block text-xs font-semibold text-gray-400 uppercase tracking-wider mb-1.5">Email</label>
                                <input
                                    name="email"
                                    type="email"
                                    value={formData.email}
                                    onChange={handleChange}
                                    required
                                    className="w-full px-4 py-3 bg-white/5 border border-white/[0.08] rounded-lg text-gray-100 text-sm focus:outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/20 transition-all placeholder:text-gray-600"
                                    placeholder="Enter email"
                                />
                            </div>
                        )}

                        <div className="mb-4">
                            <label className="block text-xs font-semibold text-gray-400 uppercase tracking-wider mb-1.5">Password</label>
                            <input
                                name="password"
                                type="password"
                                value={formData.password}
                                onChange={handleChange}
                                required
                                className="w-full px-4 py-3 bg-white/5 border border-white/[0.08] rounded-lg text-gray-100 text-sm focus:outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/20 transition-all placeholder:text-gray-600"
                                placeholder="Enter password"
                            />
                        </div>

                        {isRegister && (
                            <div className="mb-4">
                                <label className="block text-xs font-semibold text-gray-400 uppercase tracking-wider mb-1.5">Role</label>
                                <select
                                    name="role"
                                    value={formData.role}
                                    onChange={handleChange}
                                    className="w-full px-4 py-3 bg-white/5 border border-white/[0.08] rounded-lg text-gray-100 text-sm focus:outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/20 transition-all appearance-none"
                                >
                                    <option value="ADMIN" className="bg-[#161628]">Admin</option>
                                    <option value="EMPLOYEE" className="bg-[#161628]">Employee</option>
                                    <option value="DRIVER" className="bg-[#161628]">Driver</option>
                                </select>
                            </div>
                        )}

                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full py-3.5 bg-gradient-to-r from-indigo-500 to-purple-500 text-white font-semibold rounded-lg shadow-lg shadow-indigo-500/30 hover:shadow-indigo-500/50 hover:-translate-y-0.5 transition-all disabled:opacity-50 disabled:cursor-not-allowed mt-2 cursor-pointer"
                        >
                            {loading ? '⟳ Processing...' : isRegister ? 'Create Account' : 'Sign In'}
                        </button>
                    </form>

                    <p className="text-center mt-5 text-sm text-gray-500">
                        {isRegister ? 'Already have an account? ' : "Don't have an account? "}
                        <button
                            onClick={() => { setIsRegister(!isRegister); setError(''); }}
                            className="text-indigo-400 font-semibold hover:underline bg-transparent border-none cursor-pointer"
                        >
                            {isRegister ? 'Sign In' : 'Sign Up'}
                        </button>
                    </p>
                </div>
            </div>
        </div>
    );
}
