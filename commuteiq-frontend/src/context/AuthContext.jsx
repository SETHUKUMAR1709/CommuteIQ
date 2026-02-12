import { createContext, useContext, useState, useEffect } from 'react';
import { login as apiLogin, register as apiRegister } from '../api/apiService';

const AuthContext = createContext(null);

export const useAuth = () => useContext(AuthContext);

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const stored = localStorage.getItem('user');
        if (stored) {
            try { setUser(JSON.parse(stored)); } catch { /* noop */ }
        }
        setLoading(false);
    }, []);

    const loginUser = async (credentials) => {
        const res = await apiLogin(credentials);
        const data = res.data || res;
        localStorage.setItem('token', data.token);
        const userInfo = { username: credentials.username, role: data.role || 'ADMIN' };
        localStorage.setItem('user', JSON.stringify(userInfo));
        setUser(userInfo);
        return data;
    };

    const registerUser = async (data) => {
        const res = await apiRegister(data);
        return res;
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, loading, loginUser, registerUser, logout }}>
            {children}
        </AuthContext.Provider>
    );
}
