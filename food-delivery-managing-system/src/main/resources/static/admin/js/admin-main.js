function confirmLogout() {
    if (confirm("정말 로그아웃 하시겠습니까?")) {
        // Spring Security가 로그아웃 처리
        performLogout();
    }
}

function performLogout() {
    const logoutUrl = '/admin/logout'; // Spring Security에 설정한 로그아웃 URL

    // POST 요청을 보내기 위해 폼을 동적으로 생성
    // Spring Security의 CSRF 보호는 POST 요청 시 CSRF 토큰을 요구
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = logoutUrl;

    // CSRF 토큰을 hidden input으로 추가
    const csrfInput = document.createElement('input');
    csrfInput.type = 'hidden';
    csrfInput.name = csrfToken; // 토큰 값 자체가 name이 될 수 있음 (Thymeleaf 방식)
    csrfInput.value = csrfToken; // 토큰 값 자체가 value가 됨

    // Spring Security는 보통 _csrf 파라미터로 토큰 받음
    // 정확한 파라미터 이름(_csrf)을 사용하는 것이 일반적
    const csrfParameterName = '_csrf';
    csrfInput.name = csrfParameterName;
    csrfInput.value = csrfToken;

    form.appendChild(csrfInput);
    document.body.appendChild(form);

    alert("성공적으로 로그아웃되었습니다.");

    // 폼을 전송하여 로그아웃 처리 (세션 무효화) 후 SecurityConfig의 successUrl로 이동
    form.submit();
}

// CSRF 토큰 전역 변수 설정
const csrfToken = document.querySelector("meta[name='_csrf']")?.getAttribute("content");
const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.getAttribute("content");

const urlParams = new URLSearchParams(window.location.search);
if (urlParams.get('loginSuccess') === 'true') {
    alert('관리자 로그인에 성공했습니다.');
    // URL에서 파라미터 제거 (뒤로가기 시 alert 재출력 방지)
    window.history.replaceState({}, document.title, "/admin/main");
}
const PAGE_SIZE = 5;
let usersData = [];
let restaurantsData = [];
let currentUserPage = 1;
let currentRestaurantPage = 1;

// 사이드 바
function showUsers() {
    document.querySelectorAll('[id$="-section"]').forEach(el => el.style.display = 'none');
    document.getElementById('users-section').style.display = 'block';
    loadUsers();
}

function showRestaurants() {
    document.querySelectorAll('[id$="-section"]').forEach(el => el.style.display = 'none');
    document.getElementById('restaurants-section').style.display = 'block';
    loadRestaurants();
}

function showStatistics() {
    document.querySelectorAll('[id$="-section"]').forEach(el => el.style.display = 'none');
    document.getElementById('statistics-section').style.display = 'block';
    loadStatistics();
}

// 유저 로드
function loadUsers() {
    // console.log('유저 데이터 로드 중...');
    fetch('/api/admin/users')
        .then(res => {
            // console.log('응답 상태:', res.status);
            if (!res.ok) throw new Error('네트워크 응답 오류');
            return res.json();
        })
        .then(data => {
            // console.log('받은 데이터:', data);
            usersData = data;
            currentUserPage = 1;
            renderUsersPagination();
        })
        .catch(err => {
            // console.error('에러:', err);
            document.getElementById('users-tbody').innerHTML = '<tr><td colspan="5">데이터 로드 실패</td></tr>';
        });
}

function renderUsersPagination() {
    const start = (currentUserPage - 1) * PAGE_SIZE;
    const end = start + PAGE_SIZE;
    const pageData = usersData.slice(start, end);
    const totalPages = Math.ceil(usersData.length / PAGE_SIZE);

    renderUsersTable(pageData);
    renderUsersPaginationButtons(totalPages);
}

function renderUsersTable(users) {
    const tbody = document.getElementById('users-tbody');
    if (users.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6">데이터 없음</td></tr>';
        return;
    }
    tbody.innerHTML = users.map((user, idx) => `
            <tr>
                <td>${user.email}</td>
                <td style="position: relative; cursor: pointer;" class="restaurant-cell">
                    ${user.restaurantNames && user.restaurantNames.length > 0
        ? `<span class="restaurant-count">${user.restaurantNames.length}개 식당</span>
                           <div class="restaurant-tooltip">
                               ${user.restaurantNames.map(name => `<div>• ${name}</div>`).join('')}
                           </div>`
        : '-'}
                </td>
                <td>${formatDate(user.createdAt)}</td>
                <td>User Agent</td>
                <td>${user.userStatus}</td>
                <td>
                    <button class="btn btn-sm btn-primary" onclick="toggleUser('${encodeURIComponent(user.email)}')">
                        상태변경
                    </button>
                </td>
            </tr>
        `).join('');
}


function renderUsersPaginationButtons(totalPages) {
    const container = document.getElementById('users-pagination');
    let html = '<ul class="pagination justify-content-center">';

    if (currentUserPage > 1) {
        html += `<li class="page-item"><a class="page-link" onclick="userGotoPage(1)">처음</a></li>`;
        html += `<li class="page-item"><a class="page-link" onclick="userGotoPage(${currentUserPage - 1})">이전</a></li>`;
    }

    html += `<li class="page-item active"><span class="page-link">${currentUserPage}/${totalPages}</span></li>`;

    if (currentUserPage < totalPages) {
        html += `<li class="page-item"><a class="page-link" onclick="userGotoPage(${currentUserPage + 1})">다음</a></li>`;
        html += `<li class="page-item"><a class="page-link" onclick="userGotoPage(${totalPages})">마지막</a></li>`;
    }

    html += '</ul>';
    container.innerHTML = html;
}

function userGotoPage(page) {
    currentUserPage = page;
    renderUsersPagination();
}

// 유저 검색
document.addEventListener('DOMContentLoaded', () => {
    const userSearchInput = document.getElementById('users-search');
    if (userSearchInput) {
        userSearchInput.addEventListener('keyup', () => {
            const searchText = userSearchInput.value.toLowerCase();
            const filtered = usersData.filter(u =>
                u.email.toLowerCase().includes(searchText) ||
                u.name.toLowerCase().includes(searchText)
            );

            currentUserPage = 1;
            const start = 0;
            const end = PAGE_SIZE;
            const pageData = filtered.slice(start, end);
            const totalPages = Math.ceil(filtered.length / PAGE_SIZE);

            renderUsersTable(pageData);

            const container = document.getElementById('users-pagination');
            let html = '<ul class="pagination justify-content-center">';
            html += `<li class="page-item active"><span class="page-link">검색결과: ${filtered.length}개</span></li>`;
            html += '</ul>';
            container.innerHTML = html;
        });
    }
});

// 레스토랑 로드
function loadRestaurants() {
    // console.log('레스토랑 데이터 로드 중...');
    fetch('/api/admin/posts')
        .then(res => {
            // console.log('응답 상태:', res.status);
            if (!res.ok) throw new Error('네트워크 응답 오류');
            return res.json();
        })
        .then(data => {
            // console.log('받은 레스토랑 데이터:', data);
            restaurantsData = data;
            currentRestaurantPage = 1;
            renderRestaurantsPagination();
        })
        .catch(err => {
            // console.error('에러:', err);
            document.getElementById('restaurants-tbody').innerHTML = '<tr><td colspan="7">데이터 로드 실패</td></tr>';
        });
}

function renderRestaurantsPagination() {
    const start = (currentRestaurantPage - 1) * PAGE_SIZE;
    const end = start + PAGE_SIZE;
    const pageData = restaurantsData.slice(start, end);
    const totalPages = Math.ceil(restaurantsData.length / PAGE_SIZE);

    renderRestaurantsTable(pageData);
    renderRestaurantsPaginationButtons(totalPages);
}

function renderRestaurantsTable(restaurants) {
    const tbody = document.getElementById('restaurants-tbody');
    if (restaurants.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8">데이터 없음</td></tr>';
        return;
    }
    tbody.innerHTML = restaurants.map(r => `
            <tr>
                <td>${r.restaurantName}</td>
                <td>${r.email}</td>
                <td>${r.signatureMenu || '-'}</td>
                <td>${extractRegion(r.roadAddress)}</td>
                <td>${formatDate(r.createdAt)}</td>
                <td>${r.restaurantStatus}</td>
                <td>
                    <button class="btn btn-sm btn-primary" onclick="toggleRestaurant('${encodeURIComponent(r.email)}', '${encodeURIComponent(r.restaurantName)}')">
                        상태변경
                    </button>
                </td>
            </tr>
        `).join('');
}

function renderRestaurantsPaginationButtons(totalPages) {
    const container = document.getElementById('restaurants-pagination');
    let html = '<ul class="pagination justify-content-center">';

    if (currentRestaurantPage > 1) {
        html += `<li class="page-item"><a class="page-link" onclick="restaurantGotoPage(1)">처음</a></li>`;
        html += `<li class="page-item"><a class="page-link" onclick="restaurantGotoPage(${currentRestaurantPage - 1})">이전</a></li>`;
    }

    html += `<li class="page-item active"><span class="page-link">${currentRestaurantPage}/${totalPages}</span></li>`;

    if (currentRestaurantPage < totalPages) {
        html += `<li class="page-item"><a class="page-link" onclick="restaurantGotoPage(${currentRestaurantPage + 1})">다음</a></li>`;
        html += `<li class="page-item"><a class="page-link" onclick="restaurantGotoPage(${totalPages})">마지막</a></li>`;
    }

    html += '</ul>';
    container.innerHTML = html;
}

function restaurantGotoPage(page) {
    currentRestaurantPage = page;
    renderRestaurantsPagination();
}

// 레스토랑 검색
document.addEventListener('DOMContentLoaded', () => {
    const restaurantSearchInput = document.getElementById('restaurants-search');
    if (restaurantSearchInput) {
        restaurantSearchInput.addEventListener('keyup', () => {
            const searchText = restaurantSearchInput.value.toLowerCase();
            const filtered = restaurantsData.filter(r =>
                r.restaurantName.toLowerCase().includes(searchText) ||
                r.email.toLowerCase().includes(searchText)
            );

            currentRestaurantPage = 1;
            const start = 0;
            const end = PAGE_SIZE;
            const pageData = filtered.slice(start, end);
            const totalPages = Math.ceil(filtered.length / PAGE_SIZE);

            renderRestaurantsTable(pageData);

            const container = document.getElementById('restaurants-pagination');
            let html = '<ul class="pagination justify-content-center">';
            html += `<li class="page-item active"><span class="page-link">검색결과: ${filtered.length}개</span></li>`;
            html += '</ul>';
            container.innerHTML = html;
        });
    }
});

// 통계
function loadStatistics() {
    fetch('/api/admin/statistics')
        .then(res => res.json())
        .then(data => {
            // console.log('통계 데이터:', data);
            renderStatisticsTable(data);
            renderChart(data);
        })
        .catch(err => console.error('에러:', err));
}

function renderStatisticsTable(data) {
    const tbody = document.getElementById('statistics-tbody');
    tbody.innerHTML = Object.entries(data).map(([region, count]) => `
            <tr>
                <td>${region}</td>
                <td>${count}</td>
            </tr>
        `).join('');
}

function renderChart(data) {
    const ctx = document.getElementById('statistics-chart').getContext('2d');
    if (window.statisticsChart) {
        window.statisticsChart.destroy();
    }
    window.statisticsChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: Object.keys(data),
            datasets: [{
                data: Object.values(data),
                backgroundColor: ['#007bff', '#28a745', '#ffc107', '#dc3545', '#17a2b8', '#6f42c1', '#fd7e14']
            }]
        }
    });
}

// 상태 변경
function toggleUser(email) {
    fetch(`/api/admin/users/${email}/status`, {
        method: 'PUT',
        headers: {
            [csrfHeader]: csrfToken
        }
    })
        .then(res => {
            if (!res.ok) {
                // 403 Forbidden 에러 처리 (JSON 파싱 오류 방지)
                if (res.status === 403) {
                    alert('권한이 없거나 CSRF 토큰 오류입니다. (403)');
                } else {
                    alert('상태 변경 실패: ' + res.statusText);
                }
                throw new Error('API 응답 오류');
            }
            return res.json();
        })
        .then(result => {
            alert(result.message || '유저 상태 변경 성공');
            loadUsers();
        })
        .catch(err => {
            if (err.message !== 'API 응답 오류') {
                alert('요청 처리 중 오류 발생: ' + err.message);
            }
            console.error(err);
        });
}

function toggleRestaurant(email, name) {
    fetch(`/api/admin/restaurants/${email}/${name}/status`, {
        method: 'PUT',
        headers: {
            [csrfHeader]: csrfToken
        }
    })
        .then(res => {
            if (!res.ok) {
                if (res.status === 403) {
                    alert('권한이 없거나 CSRF 토큰 오류입니다. (403)');
                } else {
                    alert('상태 변경 실패: ' + res.statusText);
                }
                throw new Error('API 응답 오류');
            }
            return res.json();
        })
        .then(result => {
            alert(result.message || '레스토랑 상태 변경 성공');
            loadRestaurants();
        })
        .catch(err => {
            if (err.message !== 'API 응답 오류') {
                alert('요청 처리 중 오류 발생: ' + err.message);
            }
            console.error(err);
        });
}

function formatDate(dateStr) {
    if (!dateStr) return '-';
    return new Date(dateStr).toISOString().split('T')[0];
}

function extractRegion(roadAddress) {
    if (!roadAddress) return '-';

    // 맨 앞 "대한민국" 제거
    let cleanedAddress = roadAddress.replace(/대한민국\s*/i, '');
    const parts = cleanedAddress.split(/\s+/).filter(Boolean);

    // 주소 요소가 없으면 '-' 반환
    if (parts.length === 0) {
        return '-';
    }
    const firstPart = parts[0];
    let endIndex = 2;

    // 첫 번째 문자열이 '도'로 끝나는지 확인
    if (firstPart.endsWith('도')) {
        endIndex = 3;
    }
    const resultParts = parts.slice(0, endIndex);

    return resultParts.join(' ');
}


window.addEventListener('DOMContentLoaded', showUsers);