// resume-details.js
document.addEventListener('DOMContentLoaded', async () => {
    // Auth check
    if (!API.getToken()) {
        window.location.href = 'index.html';
        return;
    }

    const urlParams = new URLSearchParams(window.location.search);
    const resumeId = urlParams.get('id');

    if (!resumeId) {
        window.location.href = 'resumes.html';
        return;
    }

    try {
        await loadResumeDetails(resumeId);
    } catch (error) {
        UI.showError('Failed to load resume details.');
    }
});

async function loadResumeDetails(id) {
    const candidateInfoCard = document.getElementById('candidateInfoCard');
    const actionButtons = document.getElementById('actionButtons');
    const pdfPreviewContainer = document.getElementById('pdfPreviewContainer');

    try {
        // Fetch resume data
        const resume = await API.get(`/resumes/${id}`);

        // Update sidebar
        candidateInfoCard.innerHTML = `
            <div style="width: 120px; height: 120px; border-radius: 50%; background: var(--gradient-primary); margin: 0 auto; display: flex; align-items: center; justify-content: center; font-size: 3rem; color: white; font-weight: bold; box-shadow: 0 10px 25px -5px var(--shadow-color);">
                ${resume.name.charAt(0).toUpperCase()}
            </div>
            
            <div style="text-align: center; margin-top: var(--spacing-sm);">
                <h2 style="font-size: 1.5rem; margin-bottom: var(--spacing-xs);">${resume.name}</h2>
                <div style="color: var(--text-secondary); font-size: 0.875rem;">
                    <p style="margin-bottom: var(--spacing-xs);"><i class="fa-regular fa-envelope"></i> ${resume.email}</p>
                    <p><i class="fa-solid fa-phone"></i> ${resume.phone}</p>
                </div>
            </div>

            <div style="margin-top: var(--spacing-md); padding-top: var(--spacing-md); border-top: 1px solid var(--border-color);">
                <h4 style="font-size: 0.875rem; color: var(--text-secondary); margin-bottom: var(--spacing-sm);">Skills</h4>
                <div style="display: flex; flex-wrap: wrap; gap: var(--spacing-xs);">
                    ${resume.skills.split(',').map(skill => `<span class="badge">${skill.trim()}</span>`).join('')}
                </div>
            </div>
            
            <div style="margin-top: var(--spacing-md); padding-top: var(--spacing-md); border-top: 1px solid var(--border-color);">
                <h4 style="font-size: 0.875rem; color: var(--text-secondary); margin-bottom: var(--spacing-xs);">Upload Info</h4>
                <p style="font-size: 0.875rem;"><i class="fa-regular fa-file"></i> ${resume.fileName}</p>
                <p style="font-size: 0.875rem; color: var(--text-secondary); margin-top: var(--spacing-xs);"><i class="fa-regular fa-calendar"></i> ${new Date(resume.uploadedAt).toLocaleString()}</p>
            </div>
        `;

        // Load PDF Preview
        try {
            const pdfBlob = await API.get(`/resumes/preview/${id}`);
            const pdfUrl = URL.createObjectURL(pdfBlob);
            
            pdfPreviewContainer.innerHTML = `
                <iframe src="${pdfUrl}" width="100%" height="100%" style="border: none; border-radius: var(--radius-md);"></iframe>
            `;
        } catch (e) {
            pdfPreviewContainer.innerHTML = `
                <div style="text-align: center; color: var(--text-secondary);">
                    <i class="fa-solid fa-file-pdf" style="font-size: 3rem; margin-bottom: var(--spacing-sm); color: #ef4444;"></i>
                    <p>Preview not available.</p>
                </div>
            `;
        }

        // Setup actions
        actionButtons.style.display = 'flex';
        
        document.getElementById('downloadBtn').onclick = async () => {
            UI.showLoader();
            try {
                const pdfBlob = await API.get(`/resumes/download/${id}`);
                const url = window.URL.createObjectURL(pdfBlob);
                const a = document.createElement('a');
                a.href = url;
                a.download = resume.fileName;
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
                a.remove();
                UI.showSuccess('Download started');
            } catch (error) {
                UI.showError('Failed to download resume');
            } finally {
                UI.hideLoader();
            }
        };

        document.getElementById('deleteBtn').onclick = async () => {
            if (confirm('Are you sure you want to delete this resume? This action cannot be undone.')) {
                UI.showLoader();
                try {
                    await API.delete(`/resumes/${id}`);
                    UI.showSuccess('Resume deleted successfully');
                    setTimeout(() => {
                        window.location.href = 'resumes.html';
                    }, 1500);
                } catch (error) {
                    UI.showError('Failed to delete resume');
                } finally {
                    UI.hideLoader();
                }
            }
        };

    } catch (error) {
        UI.showError(error.message);
        candidateInfoCard.innerHTML = `<p style="color: var(--error-color); text-align: center;">Error loading candidate data.</p>`;
    }
}
