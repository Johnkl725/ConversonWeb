// Main application logic for PDF Converter Web

// State
let selectedFiles = [];
let uploadedFileIds = new Map(); // file -> fileId mapping
let currentJobId = null;
let stompClient = null;

// DOM Elements
const dropZone = document.getElementById('dropZone');
const fileInput = document.getElementById('fileInput');
const selectBtn = document.getElementById('selectBtn');
const fileCount = document.getElementById('fileCount');
const filesSection = document.getElementById('filesSection');
const filesTableBody = document.getElementById('filesTableBody');
const actionsSection = document.getElementById('actionsSection');
const convertBtn = document.getElementById('convertBtn');
const clearBtn = document.getElementById('clearBtn');
const progressSection = document.getElementById('progressSection');
const progressFill = document.getElementById('progressFill');
const progressText = document.getElementById('progressText');
const progressLog = document.getElementById('progressLog');
const resultsSection = document.getElementById('resultsSection');
const resultsSummary = document.getElementById('resultsSummary');
const resultsFiles = document.getElementById('resultsFiles');

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    setupEventListeners();
    connectWebSocket();
});

function setupEventListeners() {
    // File selection
    selectBtn.addEventListener('click', () => fileInput.click());
    fileInput.addEventListener('change', handleFileSelect);

    // Drag and drop
    dropZone.addEventListener('dragover', handleDragOver);
    dropZone.addEventListener('dragleave', handleDragLeave);
    dropZone.addEventListener('drop', handleDrop);

    // Actions
    convertBtn.addEventListener('click', startConversion);
    clearBtn.addEventListener('click', clearAll);
}

function handleDragOver(e) {
    e.preventDefault();
    dropZone.classList.add('drag-over');
}

function handleDragLeave(e) {
    e.preventDefault();
    dropZone.classList.remove('drag-over');
}

function handleDrop(e) {
    e.preventDefault();
    dropZone.classList.remove('drag-over');

    const files = Array.from(e.dataTransfer.files);
    addFiles(files);
}

function handleFileSelect(e) {
    const files = Array.from(e.target.files);
    addFiles(files);
    e.target.value = ''; // Reset input
}

function addFiles(files) {
    files.forEach(file => {
        if (!selectedFiles.find(f => f.name === file.name)) {
            selectedFiles.push(file);
        }
    });

    updateUI();
    uploadFiles(files);
}

function updateUI() {
    fileCount.textContent = `${selectedFiles.length} archivo${selectedFiles.length !== 1 ? 's' : ''} seleccionado${selectedFiles.length !== 1 ? 's' : ''}`;

    if (selectedFiles.length > 0) {
        filesSection.style.display = 'block';
        actionsSection.style.display = 'block';
        renderFilesTable();
    } else {
        filesSection.style.display = 'none';
        actionsSection.style.display = 'none';
    }
}

function renderFilesTable() {
    filesTableBody.innerHTML = '';

    selectedFiles.forEach((file, index) => {
        const row = document.createElement('tr');
        const fileId = uploadedFileIds.get(file);
        const status = fileId ? 'ready' : 'uploading';

        row.innerHTML = `
            <td>${file.name}</td>
            <td>${formatFileSize(file.size)}</td>
            <td>${getFileType(file.name)}</td>
            <td><span class="file-status status-${status}">${status === 'ready' ? 'Listo' : 'Subiendo...'}</span></td>
            <td><button class="btn-remove" onclick="removeFile(${index})">Eliminar</button></td>
        `;

        filesTableBody.appendChild(row);
    });
}

function removeFile(index) {
    const file = selectedFiles[index];
    uploadedFileIds.delete(file);
    selectedFiles.splice(index, 1);
    updateUI();
}

function clearAll() {
    selectedFiles = [];
    uploadedFileIds.clear();
    updateUI();
    hideProgress();
    hideResults();
}

async function uploadFiles(files) {
    const formData = new FormData();
    files.forEach(file => formData.append('files', file));

    try {
        const response = await fetch('/api/files/upload', {
            method: 'POST',
            body: formData
        });

        if (!response.ok) throw new Error('Upload failed');

        const data = await response.json();

        // Map uploaded files to their IDs
        data.uploadedFiles.forEach(uploadedFile => {
            const file = files.find(f => f.name === uploadedFile.originalName);
            if (file) {
                uploadedFileIds.set(file, uploadedFile.id);
            }
        });

        updateUI(); // Refresh to show "ready" status

    } catch (error) {
        console.error('Upload error:', error);
        alert('Error al subir archivos: ' + error.message);
    }
}

async function startConversion() {
    // Get selected conversion type
    const conversionType = document.querySelector('input[name="conversionType"]:checked').value;

    // Get file IDs
    const fileIds = Array.from(uploadedFileIds.values());

    if (fileIds.length === 0) {
        alert('Por favor espera a que se suban todos los archivos');
        return;
    }

    try {
        const response = await fetch('/api/conversion/start', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                fileIds: fileIds,
                conversionType: conversionType
            })
        });

        if (!response.ok) throw new Error('Conversion failed to start');

        const data = await response.json();
        currentJobId = data.jobId;

        showProgress();
        subscribeToJobProgress(currentJobId);

    } catch (error) {
        console.error('Conversion error:', error);
        alert('Error al iniciar conversión: ' + error.message);
    }
}

function showProgress() {
    progressSection.style.display = 'block';
    resultsSection.style.display = 'none';
    progressFill.style.width = '0%';
    progressText.textContent = '0%';
    progressLog.innerHTML = '<div>Iniciando conversión...</div>';
}

function hideProgress() {
    progressSection.style.display = 'none';
}

function hideResults() {
    resultsSection.style.display = 'none';
}

function updateProgress(current, total) {
    const percentage = Math.round((current / total) * 100);
    progressFill.style.width = percentage + '%';
    progressText.textContent = percentage + '%';
}

function addLog(message) {
    const logEntry = document.createElement('div');
    logEntry.textContent = `[${new Date().toLocaleTimeString()}] ${message}`;
    progressLog.appendChild(logEntry);
    progressLog.scrollTop = progressLog.scrollHeight;
}

async function showResults(jobId) {
    try {
        const response = await fetch(`/api/conversion/status/${jobId}`);
        const data = await response.json();

        resultsSection.style.display = 'block';
        resultsSummary.textContent = data.message || 'Conversión completada';

        // Show converted files
        resultsFiles.innerHTML = '';
        if (data.convertedFiles && data.convertedFiles.length > 0) {
            data.convertedFiles.forEach(fileName => {
                const fileCard = document.createElement('div');
                fileCard.className = 'result-file';
                fileCard.innerHTML = `
                    <div style="color: white; margin-bottom: 10px;">
                        <strong>📄 ${fileName}</strong>
                    </div>
                    <button class="download-btn" onclick="downloadFile('${jobId}', '${fileName}')">
                        ⬇️ Descargar PDF
                    </button>
                `;
                resultsFiles.appendChild(fileCard);
            });
        }

        // Show errors if any
        if (data.errors && data.errors.length > 0) {
            data.errors.forEach(error => addLog(`❌ ${error}`));
        }

    } catch (error) {
        console.error('Error fetching results:', error);
    }
}

function downloadFile(jobId, fileName) {
    const url = `/api/download/${jobId}/${fileName}`;
    window.open(url, '_blank');
}

// WebSocket Connection
function connectWebSocket() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, (frame) => {
        console.log('WebSocket connected');
    }, (error) => {
        console.error('WebSocket error:', error);
    });
}

function subscribeToJobProgress(jobId) {
    if (!stompClient) {
        console.error('WebSocket not connected');
        return;
    }

    // Subscribe to progress updates
    stompClient.subscribe(`/topic/progress/${jobId}`, (message) => {
        const data = JSON.parse(message.body);
        updateProgress(data.current, data.total);

        if (data.status === 'success') {
            addLog(`✅ ${data.fileName} - Convertido exitosamente`);
        } else if (data.status === 'failed') {
            addLog(`❌ ${data.fileName} - Error en conversión`);
        } else {
            addLog(`🔄 Convirtiendo: ${data.fileName}`);
        }
    });

    // Subscribe to completion
    stompClient.subscribe(`/topic/completion/${jobId}`, (message) => {
        const data = JSON.parse(message.body);
        addLog(`✨ ${data.message}`);

        setTimeout(() => {
            hideProgress();
            showResults(jobId);
        }, 1000);
    });
}

// Utility Functions
function formatFileSize(bytes) {
    if (bytes < 1024) return bytes + ' B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}

function getFileType(fileName) {
    const ext = fileName.split('.').pop().toUpperCase();
    const types = {
        'DOC': 'Word Document',
        'DOCX': 'Word Document',
        'JPG': 'Imagen JPEG',
        'JPEG': 'Imagen JPEG',
        'PNG': 'Imagen PNG',
        'BMP': 'Imagen BMP',
        'GIF': 'Imagen GIF',
        'TIFF': 'Imagen TIFF',
        'TIF': 'Imagen TIFF'
    };
    return types[ext] || ext;
}
