import {h} from 'preact';
import {useEffect, useState} from 'preact/hooks';
import AuthService from '../services/AuthService';
import interwebLogo from '/logo.svg';
import {Button} from "primereact/button";
import {InputText} from "primereact/inputtext";
import {Card} from "primereact/card";

export default function LoginForm({onLogin, toast}) {
    const [email, setEmail] = useState('');
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const urlParams = new URLSearchParams(window.location.search);
        const token = urlParams.get('token');

        if (token) {
            setLoading(true);
            AuthService.exchangeToken(token)
                .then(() => {
                    onLogin();
                    toast.current.show({
                        severity: 'success',
                        summary: 'Success',
                        detail: 'Successfully logged in'
                    });
                })
                .catch(err => {
                    toast.current.show({
                        severity: 'error',
                        summary: 'Error',
                        detail: err.response?.data || 'Invalid or expired login token'
                    });
                })
                .finally(() => {
                    setLoading(false);
                    // Remove the token from the URL to prevent reusing it
                    window.history.replaceState({}, document.title, window.location.pathname);
                });
        }
    }, []);

    const handleLogin = async () => {
        if (!email) {
            toast.current.show({severity: 'error', summary: 'Error', detail: 'Email is required'});
            return;
        }

        setLoading(true);
        try {
            const message = await AuthService.sendLink(email);
            toast.current.show({
                severity: 'success',
                summary: 'Success',
                detail: message
            });
        } catch (err) {
            toast.current.show({
                severity: 'error',
                summary: 'An Error Occurred',
                detail: err.response?.data || 'Failed to login'
            });
        } finally {
            setLoading(false);
        }
    };

    return (
        <Card>
            <div className="text-center mb-5">
                <img src={interwebLogo} class="logo" alt="Interweb" height={50} className="mb-3"/>
                <div className="text-900 text-3xl font-medium mb-3">Interweb Api Keys</div>
            </div>

            <div>
                <div className="flex flex-column gap-2 mb-4">
                    <label htmlFor="email">Email</label>
                    <InputText id="email" type="email" placeholder="Email address" aria-describedby="email-help" autoComplete="email" required="true"
                               value={email} onChange={(e) => setEmail(e.target.value)}/>
                    <small id="email-help">We will email you the login link.</small>
                </div>

                <Button label="Send Login Link" icon="pi pi-send" className="w-full"
                        loading={loading} onClick={handleLogin}/>
            </div>
        </Card>
    );
}

