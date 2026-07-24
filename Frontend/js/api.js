// api.js
class API {
    static getToken() {
        return localStorage.getItem('token');
    }

    static async request(endpoint, options = {}) {
        const url = `${CONFIG.API_BASE_URL}${endpoint}`;
        
        // Default headers
        const headers = new Headers();
        
        // Add auth token if exists
        const token = this.getToken();
        if (token) {
            headers.append('Authorization', `Bearer ${token}`);
        }

        // If body is NOT FormData, set content-type to JSON
        if (options.body && !(options.body instanceof FormData)) {
            headers.append('Content-Type', 'application/json');
            options.body = JSON.stringify(options.body);
        }

        // Merge headers
        if (options.headers) {
            Object.keys(options.headers).forEach(key => {
                headers.append(key, options.headers[key]);
            });
        }

        const config = {
            ...options,
            headers
        };

        try {
            const response = await fetch(url, config);

            // Handle unauthorized
            if (response.status === 401 || response.status === 403) {
                // Ignore for login/register endpoints
                if (!endpoint.includes('/auth/')) {
                    localStorage.removeItem('token');
                    window.location.href = 'index.html';
                    return null;
                }
            }

            // Handle empty response (like DELETE)
            if (response.status === 204) {
                return true;
            }

            // Check if response is JSON or Text based on content-type
            const contentType = response.headers.get("content-type");
            let data;
            
            if (contentType && contentType.indexOf("application/json") !== -1) {
                data = await response.json();
            } else if (contentType && contentType.indexOf("application/pdf") !== -1) {
                return response.blob();
            } else {
                data = await response.text();
            }

            if (!response.ok) {
                throw new Error(data.message || typeof data === 'string' ? data : 'API Request Failed');
            }

            return data;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }

    static async get(endpoint) {
        return this.request(endpoint, { method: 'GET' });
    }

    static async post(endpoint, data) {
        return this.request(endpoint, {
            method: 'POST',
            body: data
        });
    }

    static async put(endpoint, data) {
        return this.request(endpoint, {
            method: 'PUT',
            body: data
        });
    }

    static async delete(endpoint) {
        return this.request(endpoint, { method: 'DELETE' });
    }
}
