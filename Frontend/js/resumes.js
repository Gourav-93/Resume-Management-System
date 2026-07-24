// resumes.js
document.addEventListener('DOMContentLoaded', async () => {
    // Auth check
    if (!API.getToken()) {
        window.location.href = 'index.html';
        return;
    }

    const searchInput = document.getElementById('searchInput');
    const searchType = document.getElementById('searchType');
    
    // Debounce timer
    let timeout = null;

    searchInput.addEventListener('input', () => {
        clearTimeout(timeout);
        timeout = setTimeout(() => {
            performSearch(searchInput.value, searchType.value);
        }, 500); // 500ms debounce
    });

    searchType.addEventListener('change', () => {
        performSearch(searchInput.value, searchType.value);
    });

    // Initial load
    try {
        await performSearch('', 'name');
    } catch (error) {
        UI.showError('Failed to load resumes.');
    }
});

async function performSearch(query, type) {
    const grid = document.getElementById('resumesGrid');
    const emptyState = document.getElementById('emptyState');
    
    // Show skeleton
    grid.innerHTML = `
        <div class="glass-card skeleton" style="height: 250px;"></div>
        <div class="glass-card skeleton" style="height: 250px;"></div>
        <div class="glass-card skeleton" style="height: 250px;"></div>
        <div class="glass-card skeleton" style="height: 250px;"></div>
    `;
    grid.style.display = 'grid';
    emptyState.style.display = 'none';

    try {
        let resumes = [];
        
        if (!query.trim()) {
            resumes = await API.get('/resumes/my-resumes');
        } else {
            if (type === 'name') {
                resumes = await API.get(`/resumes/search?name=${encodeURIComponent(query)}`);
            } else if (type === 'skills') {
                resumes = await API.get(`/resumes/search/skills?skills=${encodeURIComponent(query)}`);
            }
        }

        renderResumes(resumes);
    } catch (error) {
        UI.showError('Search failed: ' + error.message);
        grid.innerHTML = '';
        emptyState.style.display = 'block';
    }
}

function renderResumes(resumes) {
    const grid = document.getElementById('resumesGrid');
    const emptyState = document.getElementById('emptyState');

    if (resumes.length === 0) {
        grid.style.display = 'none';
        emptyState.style.display = 'block';
        return;
    }

    grid.style.display = 'grid';
    emptyState.style.display = 'none';

    grid.innerHTML = resumes.map(resume => `
        <div class="glass-card resume-card">
            <div class="resume-card-header">
                <div class="resume-avatar">
                    ${resume.name.charAt(0).toUpperCase()}
                </div>
                <span class="badge" style="background: rgba(16, 185, 129, 0.1); color: var(--success-color);">
                    <i class="fa-regular fa-file-pdf"></i> PDF
                </span>
            </div>
            
            <div style="margin-top: var(--spacing-sm);">
                <h3 style="font-size: 1.25rem; font-weight: 600;">${resume.name}</h3>
                <div style="color: var(--text-secondary); font-size: 0.875rem; margin-top: var(--spacing-xs);">
                    <div><i class="fa-regular fa-envelope"></i> ${resume.email}</div>
                    <div><i class="fa-solid fa-phone"></i> ${resume.phone}</div>
                </div>
            </div>
            
            <div class="skills-container">
                ${resume.skills.split(',').map(skill => `<span class="badge">${skill.trim()}</span>`).join('')}
            </div>

            <div style="margin-top: auto; padding-top: var(--spacing-md); border-top: 1px solid var(--border-color); display: flex; justify-content: space-between; align-items: center;">
                <span style="font-size: 0.75rem; color: var(--text-secondary);">
                    <i class="fa-regular fa-calendar"></i> ${new Date(resume.uploadedAt).toLocaleDateString()}
                </span>
                <a href="resume-details.html?id=${resume.id}" class="btn btn-secondary" style="padding: 0.5rem 1rem;">
                    View Details
                </a>
            </div>
        </div>
    `).join('');
}
