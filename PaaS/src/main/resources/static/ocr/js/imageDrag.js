const dropBox = document.getElementById('dropBox');
const fileInput = document.getElementById('fileInput');
const fileNameView = document.getElementById('fileNameView');
const notice = document.getElementById('notice');
let uploadedImage;

// 파일 선택 시
fileInput.addEventListener('change', function () {
    handleFileSelection();
});

// 드래그 앤 드롭 관련 이벤트
dropBox.ondragover = dropBox.ondragenter = function (e) {
    e.preventDefault();
    dropBox.classList.add('active');
};

dropBox.ondragleave = dropBox.ondragend = function () {
    dropBox.classList.remove('active');
};

dropBox.ondrop = function (e) {
    e.preventDefault();
    dropBox.classList.remove('active');

    const files = e.dataTransfer.files;

    // 파일 형식 체크
    const validFileTypes = ['image/jpeg', 'image/png', 'image/jpg'];
    const isValidFileType = Array.from(files).every(file => validFileTypes.includes(file.type));

    if (!isValidFileType) {
        alert('올바른 파일 형식이 아닙니다. JPG, JPEG, PNG 파일만 허용됩니다.');
        return;
    }

    // 파일 선택 input에 파일 설정
    fileInput.files = files;

    // 파일 선택 이벤트 핸들링
    fileInput.dispatchEvent(new Event('change'));
};

// 파일 업로드 버튼 클릭 시
document.getElementById('uploadBtn').addEventListener('click', function () {
    fileInput.click();
});

// 이미지 표시 함수
function showImage(file) {
    const imageElement = document.createElement('img');
    imageElement.src = URL.createObjectURL(file);
    imageElement.alt = file.name;
    uploadedImage = imageElement;
    dropBox.appendChild(uploadedImage);
}

// 파일 선택 이벤트 핸들링 함수
function handleFileSelection() {
    const files = fileInput.files;

    // 파일 형식 체크
    const validFileTypes = ['image/jpeg', 'image/png', 'image/jpg'];
    const isValidFileType = Array.from(files).every(file => validFileTypes.includes(file.type));

    if (!isValidFileType) {
        alert('올바른 파일 형식이 아닙니다. JPG, JPEG, PNG 파일만 허용됩니다.');
        return;
    }

    // 이미지 표시
    uploadedImage && uploadedImage.remove(); // 기존 이미지 삭제
    showImage(files[0]); // 첫 번째 이미지만 표시

    // 파일 정보 표시
    fileNameView.innerHTML = files[0].name;

    // 숨김 처리
    notice.style.display = 'none';
}
