import {h} from 'preact';
import {useEffect, useState} from 'preact/hooks';
import {DataTable} from 'primereact/datatable';
import {Column} from 'primereact/column';
import {Button} from 'primereact/button';
import {Dialog} from 'primereact/dialog';
import {InputText} from 'primereact/inputtext';
import {Card} from 'primereact/card';
import ApiKeyService from '../services/ApiKeysService.js';
import AuthService from '../services/AuthService';
import {IconField} from "primereact/iconfield";
import {InputIcon} from "primereact/inputicon";
import {confirmPopup, ConfirmPopup} from "primereact/confirmpopup";

export default function ApiKeysTable({toast}) {
    const [globalFilter, setGlobalFilter] = useState(null);
    const [apiKeys, setApiKeys] = useState([]);
    const [loading, setLoading] = useState(true);
    const [createDialog, setCreateDialog] = useState(false);
    const [usageDialog, setUsageDialog] = useState(false);
    const [currentUsage, setCurrentUsage] = useState(null);
    const [newKeyName, setNewKeyName] = useState('');
    const [newKeyUrl, setNewKeyUrl] = useState('');
    const [newKeyDesc, setNewKeyDesc] = useState('');

    useEffect(() => {
        if (!AuthService.isAuthenticated()) {
            return;
        }

        loadApiKeys();
    }, []);

    const loadApiKeys = async () => {
        try {
            setLoading(true);
            const response = await ApiKeyService.list();
            setApiKeys(response.data);
        } catch (err) {
            toast.current.show({
                severity: 'error',
                summary: 'Error',
                detail: 'Failed to load API keys'
            });
        } finally {
            setLoading(false);
        }
    };

    const createApiKey = async () => {
        if (!newKeyName) {
            toast.current.show({severity: 'error', summary: 'Error', detail: 'Name is required'});
            return;
        }

        try {
            await ApiKeyService.create({
                name: newKeyName,
                url: newKeyUrl,
                description: newKeyDesc
            });
            toast.current.show({severity: 'success', summary: 'Success', detail: 'API key created'});
            setCreateDialog(false);
            setNewKeyName('');
            setNewKeyUrl('');
            setNewKeyDesc('');
            loadApiKeys();
        } catch (err) {
            toast.current.show({
                severity: 'error',
                summary: 'Error',
                detail: 'Failed to create API key'
            });
        }
    };

    const deleteApiKey = async (id) => {
        try {
            await ApiKeyService.delete(id);
            toast.current.show({severity: 'success', summary: 'Success', detail: 'API key deleted'});
            loadApiKeys();
        } catch (err) {
            toast.current.show({
                severity: 'error',
                summary: 'Error',
                detail: 'Failed to delete API key'
            });
        }
    };

    const viewUsage = async (id) => {
        try {
            const response = await ApiKeyService.usage(id);
            setCurrentUsage(response.data);
            setUsageDialog(true);
        } catch (err) {
            toast.current.show({
                severity: 'error',
                summary: 'Error',
                detail: 'Failed to fetch usage information'
            });
        }
    };

    const header = (
        <div className="flex flex-wrap gap-2 align-items-center justify-content-between">
            <IconField iconPosition="left">
                <InputIcon className="pi pi-search"/>
                <InputText type="search" onInput={(e) => setGlobalFilter(e.target.value)} placeholder="Search..."/>
            </IconField>
            <Button label="Create New API Key" icon="pi pi-plus" onClick={() => setCreateDialog(true)}/>
        </div>
    );

    const dateTemplate = (rowData) => {
        return new Date(rowData.created * 1000).toLocaleString();
    };

    const apikeyTemplate = (rowData) => {
        const apiKey = rowData.apikey;
        const maskedKey = apiKey ? apiKey.substring(0, 4) + '*****' + apiKey.substring(apiKey.length - 4) : '';

        return (
            <div className="flex align-items-center">
                <span>{maskedKey}</span>
                <Button icon="pi pi-copy" className="p-button-text p-button-sm ml-2" onClick={(e) => {
                    e.stopPropagation();
                    navigator.clipboard.writeText(apiKey)
                        .then(() => toast.current.show({
                            severity: 'success',
                            summary: 'Copied!',
                            detail: 'API key copied to clipboard',
                            life: 3000
                        }))
                        .catch(() => toast.current.show({
                            severity: 'error',
                            summary: 'Error',
                            detail: 'Failed to copy to clipboard',
                            life: 3000
                        }));
                }} tooltip="Copy API Key" tooltipOptions={{position: 'top'}}/>
            </div>
        );
    };

    const confirmDelete = (event, id) => {
        confirmPopup({
            target: event.currentTarget,
            message: 'Do you want to delete this Api Key?',
            icon: 'pi pi-info-circle',
            defaultFocus: 'reject',
            acceptClassName: 'p-button-danger',
            accept: () => deleteApiKey(id),
            reject: () => {},
        });
    };

    const actionTemplate = (rowData) => {
        return (
            <div>
                <Button icon="pi pi-chart-bar" className="p-button-info mr-2" tooltip="View Usage" tooltipOptions={{position: 'top'}}
                        onClick={(e) => viewUsage(rowData.id)}/>
                <Button icon="pi pi-trash" className="p-button-danger" onClick={(e) => confirmDelete(e, rowData.id)}/>
            </div>
        )
    };

    return (
        <div>
            <ConfirmPopup/>
            <Card title="API Keys Management">
                <DataTable value={apiKeys} loading={loading} dataKey="id" paginator rows={20}
                           paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
                           currentPageReportTemplate="Showing {first} to {last} of {totalRecords} products" globalFilter={globalFilter} header={header}>
                    <Column field="name" header="Name"/>
                    <Column field="url" header="URL"/>
                    <Column field="description" header="Description"/>
                    <Column field="apikey" header="API Key" body={apikeyTemplate}/>
                    <Column field="created" header="Created" body={dateTemplate}/>
                    <Column body={actionTemplate} header="Actions"/>
                </DataTable>
            </Card>

            <Dialog header="Create New API Key" visible={createDialog} onHide={() => setCreateDialog(false)}
                    footer={
                        <div>
                            <Button label="Cancel" icon="pi pi-times" onClick={() => setCreateDialog(false)} className="p-button-text"/>
                            <Button label="Create" icon="pi pi-check" onClick={createApiKey}/>
                        </div>
                    }>
                <div className="p-field p-mb-3">
                    <label htmlFor="name">Name*</label>
                    <InputText
                        id="name"
                        value={newKeyName}
                        onChange={(e) => setNewKeyName(e.target.value)}
                        className="p-inputtext-lg"
                        style={{width: '100%'}}
                        required
                    />
                </div>
                <div className="p-field p-mb-3">
                    <label htmlFor="url">URL</label>
                    <InputText
                        id="url"
                        value={newKeyUrl}
                        onChange={(e) => setNewKeyUrl(e.target.value)}
                        className="p-inputtext-lg"
                        style={{width: '100%'}}
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="description">Description</label>
                    <InputText
                        id="description"
                        value={newKeyDesc}
                        onChange={(e) => setNewKeyDesc(e.target.value)}
                        className="p-inputtext-lg"
                        style={{width: '100%'}}
                    />
                </div>
            </Dialog>

            <Dialog header="API Key Usage" visible={usageDialog} onHide={() => setUsageDialog(false)} style={{width: '50vw'}}>
                {currentUsage && (
                    <div className="grid">
                        <div className="col-12 md:col-6 line-height-1">
                            <h3 className="mb-3">Chat Usage (All Time)</h3>
                            <p><strong>Total Requests:</strong> {currentUsage.chat.total_requests}</p>
                            <p><strong>Input Tokens:</strong> {currentUsage.chat.input_tokens}</p>
                            <p><strong>Output Tokens:</strong> {currentUsage.chat.output_tokens}</p>
                            <p><strong>Estimated Cost:</strong> ${currentUsage.chat.estimated_cost.toFixed(4)}</p>
                        </div>
                        <div className="col-12 md:col-6 line-height-1">
                            <h3 className="mb-3">Search Usage (All Time)</h3>
                            <p><strong>Total Requests:</strong> {currentUsage.search.total_requests}</p>
                            <p><strong>Estimated Cost:</strong> ${currentUsage.search.estimated_cost.toFixed(4)}</p>
                        </div>
                    </div>
                )}
            </Dialog>
        </div>
    );
}
