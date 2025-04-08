import {h} from 'preact';
import {useEffect, useRef, useState} from 'preact/hooks';
import {Toast} from "primereact/toast";

import AuthService from './services/AuthService';
import LoginForm from './components/LoginForm';
import Header from './components/Header';
import ApiKeysTable from './components/ApiKeysTable';

import 'primereact/resources/themes/lara-dark-blue/theme.css';
import 'primeflex/primeflex.css'
import 'primeicons/primeicons.css'

export function App() {
    const toast = useRef(null);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    useEffect(() => {
        setIsAuthenticated(AuthService.isAuthenticated());
    }, []);

    return (
        <>
            <Toast ref={toast}/>
            {!isAuthenticated && <LoginForm onLogin={() => setIsAuthenticated(true)} toast={toast}/>}
            {isAuthenticated && (
                <>
                    <Header onLogout={() => {
                        AuthService.logout();
                        setIsAuthenticated(false);
                    }}/>
                    <ApiKeysTable toast={toast}/>
                </>
            )}
            <div className="my-2">
                <a href="/q/docs/" className="mr-2">API Documentation</a>
            </div>
        </>
    );
}
