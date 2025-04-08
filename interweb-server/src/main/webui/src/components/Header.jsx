import {h} from 'preact';
import {Button} from "primereact/button";
import {useEffect, useState} from "preact/hooks";
import AuthService from "../services/AuthService";

const Header = ({onLogout}) => {
    const [userInfo, setUserInfo] = useState(null);
    const [userUsage, setUserUsage] = useState(null);

    useEffect(() => {
        fetchUserInfo();
        fetchUserUsage();
    }, []);

    const fetchUserInfo = async () => {
        try {
            const user = await AuthService.userInfo();
            setUserInfo(user);
        } catch (error) {
            console.error('Failed to fetch user info:', error);
        }
    }

    const fetchUserUsage = async () => {
        try {
            const usage = await AuthService.userUsage();
            setUserUsage(usage);
        } catch (error) {
            console.error('Failed to fetch user usage:', error);
        }
    }

    return (
        <div className="flex justify-content-between align-items-center p-3">
            <div className="flex align-items-center">
                {userInfo && (
                    <>
                        <i className="pi pi-user mr-2" style={{fontSize: '1.5rem'}}></i>
                        <span>{userInfo.email}</span>
                    </>
                )}
                {userUsage && (
                    <>
                        <i className="pi pi-dollar ml-4 mr-2" style={{fontSize: '1.5rem'}}></i>
                        <span>{parseFloat(userUsage.monthly_budget_used).toFixed(2)} / {parseFloat(userUsage.monthly_budget).toFixed(2)}</span>
                    </>
                )}
            </div>
            <Button label="Logout" icon="pi pi-sign-out" className="p-button-text" onClick={onLogout}/>
        </div>
    );
};

export default Header;
