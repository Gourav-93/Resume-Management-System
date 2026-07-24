// upload.js
document.addEventListener('DOMContentLoaded', () => {
    // Auth check
    if (!API.getToken()) {
        window.location.href = 'index.html';
        return;
    }

    const dropArea = document.getElementById('dropArea');
    const fileInput = document.getElementById('file');
    const filePreview = document.getElementById('filePreview');
    const fileNameDisplay = document.getElementById('fileNameDisplay');
    const fileSizeDisplay = document.getElementById('fileSizeDisplay');
    const uploadForm = document.getElementById('uploadForm');

    // Drag and drop handlers
    ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
        dropArea.addEventListener(eventName, preventDefaults, false);
    });

    function preventDefaults(e) {
        e.preventDefault();
        e.stopPropagation();
    }

    ['dragenter', 'dragover'].forEach(eventName => {
        dropArea.addEventListener(eventName, () => dropArea.classList.add('dragover'), false);
    });

    ['dragleave', 'drop'].forEach(eventName => {
        dropArea.addEventListener(eventName, () => dropArea.classList.remove('dragover'), false);
    });

    dropArea.addEventListener('drop', (e) => {
        const dt = e.dataTransfer;
        const files = dt.files;
        handleFiles(files);
    });

    fileInput.addEventListener('change', function() {
        handleFiles(this.files);
    });

    function handleFiles(files) {
        if (files.length > 0) {
            const file = files[0];
            if (file.type !== 'application/pdf') {
                UI.showError('Only PDF files are allowed.');
                fileInput.value = ''; // Clear
                filePreview.style.display = 'none';
                return;
            }

            // Manually set files to input if from drop
            const dataTransfer = new DataTransfer();
            dataTransfer.items.add(file);
            fileInput.files = dataTransfer.files;

            fileNameDisplay.textContent = file.name;
            fileSizeDisplay.textContent = `(${(file.size / (1024 * 1024)).toFixed(2)} MB)`;
            filePreview.style.display = 'block';
        }
    }

    // Form submit
    uploadForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const file = fileInput.files[0];
        if (!file) {
            UI.showError('Please select a PDF file.');
            return;
        }

        const name = document.getElementById('name').value;
        const phone = document.getElementById('phone').value;
        const skills = document.getElementById('skills').value;

        const formData = new FormData();
        formData.append('name', name);
        formData.append('phone', phone);
        formData.append('skills', skills);
        formData.append('file', file);

        UI.showLoader();
        const submitBtn = document.getElementById('submitBtn');
        const originalBtnHtml = submitBtn.innerHTML;
        submitBtn.innerHTML = '<i class="fa-solid fa-circle-notch fa-spin"></i> Uploading...';
        submitBtn.disabled = true;

        try {
            await API.post('/resumes/upload', formData);
            UI.showSuccess('Resume uploaded successfully!');
            setTimeout(() => {
                window.location.href = 'resumes.html';
            }, 1500);
        } catch (error) {
            UI.showError('Upload failed: ' + error.message);
            submitBtn.innerHTML = originalBtnHtml;
            submitBtn.disabled = false;
        } finally {
            UI.hideLoader();
        }
    });
});
