$(document).ready(function () {
    // HTML 문서를 로드할 때마다 실행합니다.
    getMessages();
})

// 메모를 불러와서 보여줍니다.
function getMessages() {
    // 1. 기존 메모 내용을 지웁니다.
    $('#cards-box').empty();
    // 2. 메모 목록을 불러와서 HTML로 붙입니다.
    $.ajax({
        type: 'GET',
        url: '/api/memos',
        success: function (response) {
            for (let i = 0; i < response.length; i++) {
                let message = response[i];
                let id = message['id'];
                let username = message['username'];
                let contents = message['contents'];
                let modifiedAt = message['modifiedAt'];
                addHTML(id, username, contents, modifiedAt);
            }
        }
    })
}

// 메모 하나를 HTML로 만들어서 body 태그 내 원하는 곳에 붙입니다.
function addHTML(id, username, contents, modifiedAt) {
    let tempHtml = `<div class="card">
        <div class="metadata">
            <div class="date">${modifiedAt}</div>
        </div>
        <div class="contents">
            <div id="${id}-contents" class="text">${contents}</div>
            <div id="${id}-editarea" class="edit" style="display:none;">
                <textarea id="${id}-textarea" class="te-edit" rows="3"></textarea>
            </div>
        </div>
        <div class="footer">
            <img id="${id}-edit" class="icon-start-edit" src="images/edit.png" onclick="editPost('${id}')">
            <img id="${id}-delete" class="icon-delete" src="images/delete.png" onclick="deleteOne('${id}')">
            <img id="${id}-submit" class="icon-end-edit" src="images/done.png" style="display:none;" onclick="submitEdit('${id}')">
        </div>
    </div>`;
    $('#cards-box').append(tempHtml);
}

// 검색 기능을 수행하는 함수 (애니메이션 적용)
function getMemosByKeyword() {
    let keyword = $('#search-keyword').val();

    if (keyword == "") {
        getMessages();
        return;
    }

    $('#cards-box').empty();

    $.ajax({
        type: 'GET',
        url: `/api/memos/contents?keyword=${keyword}`,
        success: function (response) {
            if (response.length == 0) {
                alert("해당 키워드가 포함된 메모가 없습니다.");
                getMessages();
                return;
            }

            for (let i = 0; i < response.length; i++) {
                let memo = response[i];
                addHTML(memo.id, memo.username, memo.contents, memo.modifiedAt);
            }

            // 검색 결과가 부드럽게 나타나도록 설정
            $('#cards-box').hide().fadeIn(500);
        }
    });
}

// script.js 내의 writePost 함수 수정
function writePost() {
    let contents = $('#contents').val();

    if (isValidContents(contents) == false) {
        return;
    }

    // [수정] 랜덤 생성 대신 고정된 이름 사용
    let username = "익명";
    let data = {'username': username, 'contents': contents};

    $.ajax({
        type: "POST",
        url: "/api/memos",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (response) {
            alert('메시지가 성공적으로 작성되었습니다.');
            $('#contents').val('');
            getMessages();
        }
    });
}

// 더 이상 쓰지 않는 genRandomName 함수는 삭제하셔도 됩니다.

// 메모를 수정합니다.
function submitEdit(id) {
    let username = $(`#${id}-username`).text().trim();
    let contents = $(`#${id}-textarea`).val().trim();

    if (isValidContents(contents) == false) {
        return;
    }

    let data = {'username': username, 'contents': contents};

    $.ajax({
        type: "PUT",
        url: `/api/memos/${id}`,
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (response) {
            alert('메시지 변경에 성공하였습니다.');
            getMessages(); // 새로고침 대신 목록 갱신
        }
    });
}

// 메모를 삭제합니다.
function deleteOne(id) {
    $.ajax({
        type: "DELETE",
        url: `/api/memos/${id}`,
        success: function (response) {
            alert('메시지 삭제에 성공하였습니다.');
            getMessages(); // 새로고침 대신 목록 갱신
        }
    })
}

// 유틸리티 함수들
function isValidContents(contents) {
    if (contents == '') {
        alert('내용을 입력해주세요');
        return false;
    }
    if (contents.trim().length > 140) {
        alert('공백 포함 140자 이하로 입력해주세요');
        return false;
    }
    return true;
}

function genRandomName(length) {
    let result = '';
    let characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let charactersLength = characters.length;
    for (let i = 0; i < length; i++) {
        let number = Math.random() * charactersLength;
        let index = Math.floor(number);
        result += characters.charAt(index);
    }
    return result;
}

// script.js 내의 함수 수정
function editPost(id) {
    // 1. 수정 창을 보여주는 함수 호출
    showEdits(id);

    // 2. 기존 글 내용을 가져와서 수정용 textarea에 미리 채워넣기
    let contents = $(`#${id}-contents`).text().trim();
    $(`#${id}-textarea`).val(contents);

    // 3. 포커스 자동 맞추기
    $(`#${id}-textarea`).focus();
}

function showEdits(id) {
    // 1. 기존 텍스트와 펜 아이콘은 숨기고
    $(`#${id}-contents`).hide();
    $(`#${id}-edit`).hide();

    // 2. 수정용 입력창과 체크(완료) 아이콘을 나타냅니다
    $(`#${id}-editarea`).slideDown(300);
    $(`#${id}-submit`).show();

    // 삭제 버튼은 취소 대용으로 계속 보여주거나, 필요에 따라 제어할 수 있습니다.
}