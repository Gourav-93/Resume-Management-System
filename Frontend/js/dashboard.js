// dashboard.js
document.addEventListener('DOMContentLoaded', async () => {
    // Auth check
    if (!API.getToken()) {
        window.location.href = 'index.html';
        return;
    }

    try {
        await loadDashboardData();
    } catch (error) {
        UI.showError('Failed to load dashboard data.');
    }
});

async function loadDashboardData() {
    const resumes = await API.get('/resumes/my-resumes');
    
    // Update stats
    document.getElementById('totalResumesCount').textContent = resumes.length;

    // Sort by uploadedAt (newest first)
    resumes.sort((a, b) => new Date(b.uploadedAt) - new Date(a.uploadedAt));
    
    // Display recent 3 resumes
    const recentResumes = resumes.slice(0, 3);
    const grid = document.getElementById('recentResumesGrid');
    
    if (recentResumes.length === 0) {
        grid.innerHTML = `
            <div style="grid-column: 1 / -1; text-align: center; padding: var(--spacing-xl); background: var(--surface-color); border-radius: var(--radius-lg); border: 1px dashed var(--border-color);">
                <p style="color: var(--text-secondary); margin-bottom: var(--spacing-sm);">No resumes uploaded yet.</p>
                <a href="upload.html" class="btn btn-primary" style="font-size: 0.875rem; padding: 0.5rem 1rem;">Upload First Resume</a>
            </div>
        `;
        return;
    }

    grid.innerHTML = recentResumes.map(resume => `
        <div class="glass-card bento-item">
            <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: var(--spacing-md);">
                <div>
                    <h3 style="font-size: 1.125rem; font-weight: 600;">${resume.name}</h3>
                    <p style="color: var(--text-secondary); font-size: 0.875rem;">${resume.email}</p>
                </div>
                <div style="width: 40px; height: 40px; border-radius: 50%; background: var(--gradient-primary); display: flex; align-items: center; justify-content: center; color: white; font-weight: bold;">
                    ${resume.name.charAt(0).toUpperCase()}
                </div>
            </div>
            
            <div style="display: flex; gap: var(--spacing-xs); flex-wrap: wrap; margin-bottom: var(--spacing-md);">
                ${resume.skills.split(',').slice(0, 3).map(skill => `<span class="badge">${skill.trim()}</span>`).join('')}
                ${resume.skills.split(',').length > 3 ? `<span class="badge">...</span>` : ''}
            </div>

            <div style="margin-top: auto; padding-top: var(--spacing-md); border-top: 1px solid var(--border-color); display: flex; justify-content: space-between; align-items: center;">
                <span style="font-size: 0.75rem; color: var(--text-secondary);">
                    <i class="fa-regular fa-calendar"></i> ${new Date(resume.uploadedAt).toLocaleDateString()}
                </span>
                <a href="resume-details.html?id=${resume.id}" class="btn btn-secondary" style="padding: 0.25rem 0.75rem; font-size: 0.875rem;">
                    View
                </a>
            </div>
        </div>
    `).join('');
}
