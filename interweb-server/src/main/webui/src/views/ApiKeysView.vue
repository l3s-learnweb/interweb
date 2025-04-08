<template>
    <div class="api-keys-container">
        <div class="header-with-actions">
            <h1>API Keys</h1>
            <button @click="logout" class="logout-btn">Logout</button>
        </div>

        <div class="create-key-section">
            <h2>Create New API Key</h2>
            <div class="key-form">
                <input v-model="newKeyName" placeholder="Enter key name" required>
                <button @click="createKey" :disabled="loading || !newKeyName">
                    {{ loading ? 'Creating...' : 'Create Key' }}
                </button>
            </div>
            <div v-if="newKey" class="new-key-alert">
                <p><strong>New API Key Created:</strong></p>
                <p class="key-value">{{ newKey }}</p>
                <p class="key-warning">Copy this key now. You won't be able to see it again!</p>
            </div>
        </div>

        <div class="keys-list-section">
            <h2>Your API Keys</h2>
            <div v-if="loading" class="loading">Loading...</div>
            <div v-else-if="apiKeys.length === 0" class="no-keys">No API keys found</div>
            <table v-else class="keys-table">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Created</th>
                    <th>Last Used</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="key in apiKeys" :key="key.id">
                    <td>{{ key.name }}</td>
                    <td>{{ formatDate(key.created) }}</td>
                    <td>{{ key.lastUsed ? formatDate(key.lastUsed) : 'Never' }}</td>
                    <td>
                        <button
                            @click="confirmDelete(key)"
                            class="delete-btn"
                            title="Revoke API Key">
                            Delete
                        </button>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>

<script>
import ApiKeyService from '../services/ApiKeyService'
import AuthService from '../services/AuthService'

export default {
    data() {
        return {
            apiKeys: [],
            newKeyName: '',
            newKey: '',
            loading: false
        }
    },
    mounted() {
        this.fetchApiKeys()
    },
    methods: {
        fetchApiKeys() {
            this.loading = true
            ApiKeyService.getApiKeys()
                .then(response => {
                    this.apiKeys = response.data
                })
                .catch(error => {
                    console.error('Failed to fetch API keys:', error)
                    if (error.response?.status === 401) {
                        this.logout()
                    }
                })
                .finally(() => {
                    this.loading = false
                })
        },
        createKey() {
            if (!this.newKeyName) return

            this.loading = true
            ApiKeyService.createApiKey(this.newKeyName)
                .then(response => {
                    this.newKey = response.data.key
                    this.newKeyName = ''
                    this.fetchApiKeys()
                })
                .catch(error => {
                    console.error('Failed to create API key:', error)
                })
                .finally(() => {
                    this.loading = false
                })
        },
        formatDate(dateString) {
            return new Date(dateString).toLocaleString()
        },
        logout() {
            AuthService.logout()
            this.$router.push('/login')
        },
        confirmDelete(key) {
            if (confirm(`Are you sure you want to revoke the API key "${key.name}"? This action cannot be undone.`)) {
                this.deleteApiKey(key.id);
            }
        },
        deleteApiKey(keyId) {
            this.loading = true;
            ApiKeyService.deleteApiKey(keyId)
                .then(() => {
                    this.fetchApiKeys();
                })
                .catch(error => {
                    console.error('Failed to delete API key:', error);
                })
                .finally(() => {
                    this.loading = false;
                });
        },
    }
}
</script>

<style scoped>
.delete-btn {
  background: #dc3545;
  padding: 5px 10px;
  font-size: 0.9em;
}

.delete-btn:hover {
  background: #bd2130;
}
.api-keys-container {
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
}

.header-with-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.logout-btn {
    background: #ccc;
    color: #333;
}

.create-key-section, .keys-list-section {
    background: var(--color-background-soft);
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
}

.key-form {
    display: flex;
    gap: 10px;
    margin-bottom: 15px;
}

.key-form input {
    flex: 1;
    padding: 8px;
    border: 1px solid var(--color-border);
    border-radius: 4px;
}

button {
    padding: 8px 15px;
    background: hsla(160, 100%, 37%, 1);
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

button:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.new-key-alert {
    background: #f8f9fa;
    border-left: 4px solid hsla(160, 100%, 37%, 1);
    padding: 15px;
    margin-top: 15px;
}

.key-value {
    background: #eee;
    padding: 10px;
    font-family: monospace;
    word-break: break-all;
}

.key-warning {
    color: red;
    font-weight: bold;
}

.keys-table {
    width: 100%;
    border-collapse: collapse;
}

.keys-table th, .keys-table td {
    border: 1px solid var(--color-border);
    padding: 10px;
    text-align: left;
}

.no-keys {
    text-align: center;
    padding: 20px;
    color: #666;
}
</style>
