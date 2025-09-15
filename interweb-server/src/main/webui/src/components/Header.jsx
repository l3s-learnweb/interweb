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
            <div className="flex gap-4 align-items-center">
                {userInfo && (
                    <span title="User Identity" class="flex align-items-center gap-2">
                        <i className="pi pi-user" style={{fontSize: '1.5rem'}}></i>
                        <span>{userInfo.email}</span>
                    </span>
                )}
                {userUsage && (
                    <>
                        <span title="Monthly Usage" class="flex align-items-center gap-2">
                            <i className="pi pi-calendar" style={{fontSize: '1.5rem'}}></i>
                            <span>{parseFloat(userUsage.monthly_budget_used).toFixed(2)} / {parseFloat(userUsage.monthly_budget).toFixed(2)}</span>
                        </span>
                        <span title="All Time Usage" class="flex align-items-center gap-2">
                            <i className="pi pi-dollar" style={{fontSize: '1.5rem'}}></i>
                            <span>{parseFloat(userUsage.total_used).toFixed(2)}</span>
                        </span>
                    </>
                )}
            </div>
            <Button label="Logout" icon="pi pi-sign-out" className="p-button-text" onClick={onLogout}/>
        </div>
    );
};

export default Header;
