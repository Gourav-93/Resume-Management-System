// auth.js
document.addEventListener('DOMContentLoaded', () => {
    // Redirect if already logged in (on login/register pages)
    const currentPath = window.location.pathname;
    const isAuthPage = currentPath.endsWith('index.html') || currentPath.endsWith('register.html') || currentPath === '/';
    
    if (API.getToken() && isAuthPage) {
        window.location.href = 'dashboard.html';
    }

    // Handle Logout
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            localStorage.removeItem('token');
            window.location.href = 'index.html';
        });
    }

    // Handle Login
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;

            UI.showLoader();
            try {
                // Backend expects request params for login, so we use URL params
                const params = new URLSearchParams();
                params.append('email', email);
                params.append('password', password);
                
                const response = await API.post(`/auth/login?${params.toString()}`);
                
                // Assuming response is the token string
                if (response) {
                    localStorage.setItem('token', response);
                    UI.showSuccess('Login successful!');
                    setTimeout(() => window.location.href = 'dashboard.html', 1000);
                }
            } catch (error) {
                UI.showError('Invalid email or password. ' + error.message);
            } finally {
                UI.hideLoader();
            }
        });
    }

    // Handle Registration
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const name = document.getElementById('name').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;

            UI.showLoader();
            try {
                const userData = {
                    name,
                    email,
                    password
                };
                
                await API.post('/auth/register', userData);
                UI.showSuccess('Registration successful! Please login.');
                setTimeout(() => window.location.href = 'index.html', 1500);
            } catch (error) {
                UI.showError('Registration failed: ' + error.message);
            } finally {
                UI.hideLoader();
            }
        });
    }
});
