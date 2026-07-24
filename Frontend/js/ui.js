// ui.js
class UI {
    static showLoader() {
        const loader = document.getElementById('globalLoader');
        if (loader) loader.classList.add('active');
    }

    static hideLoader() {
        const loader = document.getElementById('globalLoader');
        if (loader) loader.classList.remove('active');
    }

    static showToast(message, type = 'success') {
        const container = document.getElementById('toastContainer');
        if (!container) return;

        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        
        const icon = type === 'success' ? '<i class="fa-solid fa-circle-check" style="color: var(--success-color); font-size: 1.25rem;"></i>' 
                                      : '<i class="fa-solid fa-circle-exclamation" style="color: var(--error-color); font-size: 1.25rem;"></i>';
        
        toast.innerHTML = `
            ${icon}
            <div style="font-weight: 500;">${message}</div>
        `;

        container.appendChild(toast);

        // Trigger reflow for animation
        setTimeout(() => toast.classList.add('show'), 10);

        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => toast.remove(), 400); // Wait for transition
        }, 3000);
    }

    static showError(message) {
        this.showToast(message, 'error');
    }

    static showSuccess(message) {
        this.showToast(message, 'success');
    }

    static initTheme() {
        const savedTheme = localStorage.getItem('theme') || 'dark';
        document.documentElement.setAttribute('data-theme', savedTheme);

        const themeToggle = document.getElementById('themeToggle');
        if (themeToggle) {
            this.updateThemeIcon(savedTheme, themeToggle);
            themeToggle.addEventListener('click', () => {
                const currentTheme = document.documentElement.getAttribute('data-theme');
                const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
                document.documentElement.setAttribute('data-theme', newTheme);
                localStorage.setItem('theme', newTheme);
                this.updateThemeIcon(newTheme, themeToggle);
            });
        }
    }

    static updateThemeIcon(theme, btn) {
        if (theme === 'dark') {
            btn.innerHTML = '<i class="fa-solid fa-sun"></i>';
        } else {
            btn.innerHTML = '<i class="fa-solid fa-moon"></i>';
        }
    }
}

// Initialize theme on load
document.addEventListener('DOMContentLoaded', () => {
    UI.initTheme();
});
