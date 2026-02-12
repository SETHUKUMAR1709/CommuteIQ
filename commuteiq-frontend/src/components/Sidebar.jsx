import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const navItems = [
    { path: '/', label: 'Dashboard', icon: '📊' },
    { path: '/employees', label: 'Employees', icon: '👥' },
    { path: '/ride-requests', label: 'Ride Requests', icon: '🚗' },
    { path: '/ride-plans', label: 'Ride Plans', icon: '🗺️' },
    { path: '/safety', label: 'Safety', icon: '🛡️' },
];

export default function Sidebar() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => { logout(); navigate('/login'); };

    return (
        <aside className="w-64 min-h-screen bg-[rgba(22,22,40,0.85)] backdrop-blur-xl border-r border-white/[0.08] flex flex-col fixed left-0 top-0 z-50">
            {/* Brand */}
            <div className="flex items-center gap-3 px-6 py-6 border-b border-white/[0.08]">
                <div className="text-2xl w-11 h-11 bg-gradient-to-br from-indigo-500 to-purple-500 rounded-xl flex items-center justify-center">🚌</div>
                <h1 className="text-xl font-bold bg-gradient-to-r from-indigo-400 to-purple-400 bg-clip-text text-transparent">CommuteIQ</h1>
            </div>

            {/* Nav */}
            <nav className="flex-1 p-3 flex flex-col gap-1">
                {navItems.map(item => (
                    <NavLink
                        key={item.path}
                        to={item.path}
                        end={item.path === '/'}
                        className={({ isActive }) =>
                            `flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-all duration-200
              ${isActive
                                ? 'bg-gradient-to-r from-indigo-500 to-purple-500 text-white shadow-lg shadow-indigo-500/30'
                                : 'text-gray-400 hover:bg-white/[0.06] hover:text-gray-200'}`
                        }
                    >
                        <span className="text-lg w-6 text-center">{item.icon}</span>
                        <span>{item.label}</span>
                    </NavLink>
                ))}
            </nav>

            {/* Footer */}
            <div className="p-4 border-t border-white/[0.08]">
                <div className="flex items-center gap-2.5 mb-3">
                    <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-indigo-500 to-purple-500 flex items-center justify-center text-white font-bold text-sm">
                        {user?.username?.charAt(0).toUpperCase() || 'U'}
                    </div>
                    <div className="flex flex-col">
                        <span className="text-sm font-semibold text-gray-200">{user?.username || 'User'}</span>
                        <span className="text-[0.65rem] text-gray-500 uppercase tracking-wider">{user?.role || 'ADMIN'}</span>
                    </div>
                </div>
                <button
                    onClick={handleLogout}
                    className="w-full py-2.5 border border-white/[0.08] rounded-lg bg-red-500/10 text-red-400 text-xs font-medium flex items-center justify-center gap-1.5 hover:bg-red-500/20 transition-colors cursor-pointer"
                >
                    ↪ Logout
                </button>
            </div>
        </aside>
    );
}
