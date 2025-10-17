(() => {
    const $ = (sel) => document.querySelector(sel);

    // ====== 설정 ======
    // 기본 아바타(플레이스홀더) 경로: 백엔드 플래그 없이 삭제를 구현하기 위해 실제로 업로드할 파일 소스
    const DEFAULT_PLACEHOLDER_SRC =
        $('#profileThumb')?.getAttribute('src') ||
        '/images/default-user-profile.png';

    // ====== DOM ======
    const profileForm = $('#profileForm');
    const passwordForm = $('#passwordForm');
    const saveBtn = $('#saveBtn');
    const pwBtn = $('#pwBtn');

    const csrfEl = $('#csrfField');

    // summary(상단)
    const summaryName = $('#summaryName');
    const summaryNick = $('#summaryNick');
    const summaryEmail = $('#summaryEmail');
    const summaryAddr = $('#summaryAddr');
    const profilePreview = $('#profilePreview');     // 상단 요약 아바타 (저장 성공 후에만 갱신)

    // profile fields
    const nickEl = $('#nickName');
    const roadEl = $('#roadAddress');
    const detailEl = $('#detailAddress');
    const latEl = $('#latitude');
    const lngEl = $('#longitude');

    // image controls
    const fileEl = $('#profileImage');
    const profileThumb = $('#profileThumb');         // 폼 내부 썸네일
    const removeImageBtn = $('#removeImageBtn');

    // password fields
    const curPwEl = $('#currentPassword');
    const newPwEl = $('#newPassword');
    const newPw2El = $('#newPasswordConfirm');

    // error labels
    const roadAddressError = $('#road_address_error');
    const detailAddressError = $('#detail_address_error');
    const latErr = $('#latErr');
    const lngErr = $('#lngErr');
    const curPwError = $('#current_password_error');
    const pwError = $('#password_error');
    const pwConfirmError = $('#password_confirm_error');

    // ====== 상태 ======
    // 삭제 모드: true면 저장 시 기본 이미지 파일을 실제로 업로드 (백엔드 플래그 불필요)
    let deleteMode = false;
    let lastObjectUrl = null; // 미리보기 URL 정리용

    // ====== 유틸 ======
    const withTimeout = (ms, p) =>
        Promise.race([new Promise((_, r) => setTimeout(() => r(new Error('요청 시간이 초과되었습니다.')), ms)), p]);

    function appendCsrf(fd) {
        if (csrfEl && csrfEl.name && csrfEl.value && !fd.has(csrfEl.name)) {
            fd.append(csrfEl.name, csrfEl.value);
        }
        const metaParam = document.querySelector('meta[name="_csrf_parameter"]')?.content;
        const metaToken = document.querySelector('meta[name="_csrf"]')?.content;
        if (metaParam && metaToken && !fd.has(metaParam)) fd.append(metaParam, metaToken);
    }

    async function parseError(res, fallback) {
        let msg = `${fallback} (${res.status})`;
        try {
            const ct = res.headers.get('content-type') || '';
            if (ct.includes('application/json')) {
                const j = await res.json();
                if (j?.message) msg += ` - ${j.message}`;
            } else {
                const t = await res.text();
                if (t) msg += ` - ${t}`;
            }
        } catch {}
        throw new Error(msg);
    }

    const isValidLat = (v) => { const n = Number(v); return !Number.isNaN(n) && n >= -90 && n <= 90; };
    const isValidLng = (v) => { const n = Number(v); return !Number.isNaN(n) && n >= -180 && n <= 180; };
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/;

    // 플레이스홀더 이미지를 File 객체로 변환 (삭제모드에서 사용)
    async function makeDefaultImageFile() {
        const res = await fetch(DEFAULT_PLACEHOLDER_SRC, { cache: 'no-store' });
        if (!res.ok) throw new Error('기본 이미지 로드 실패');
        const blob = await res.blob();
        const type = blob.type || 'image/png';
        const ext = (type.split('/')[1] || 'png').toLowerCase();
        return new File([blob], `default-profile.${ext}`, { type });
    }

    // ====== 초기 로드 /api/me ======
    async function loadMe() {
        const res = await withTimeout(15000, fetch('/api/me', {
            credentials: 'include',
            headers: { 'Accept': 'application/json' }
        }));
        if (!res.ok) await parseError(res, '내 정보 불러오기 실패');
        const me = await res.json();

        // summary
        summaryName.textContent = me.name || '-';
        summaryNick.textContent = me.nickName || '-';
        summaryEmail.textContent = me.email || '-';
        summaryAddr.textContent = me.roadAddress
            ? (me.detailAddress ? `${me.roadAddress} ${me.detailAddress}` : me.roadAddress)
            : '-';

        // form
        nickEl.value = me.nickName ?? '';
        roadEl.value = me.roadAddress ?? '';
        detailEl.value = me.detailAddress ?? '';

        if (me.latitude != null) latEl.value = String(me.latitude);
        if (me.longitude != null) lngEl.value = String(me.longitude);
        if ((!latEl.value || !lngEl.value) && me.coordinates) {
            const { x, y } = me.coordinates; // x=lng, y=lat
            if (typeof y === 'number') latEl.value = y.toFixed(6);
            if (typeof x === 'number') lngEl.value = x.toFixed(6);
        }

        // avatar: 저장된 값 기준으로 두 곳을 셋업 (썸네일은 시작점 동일)
        const avatar = me.profileUrl || DEFAULT_PLACEHOLDER_SRC;
        if (profilePreview) profilePreview.src = avatar;
        if (profileThumb) profileThumb.src = avatar;

        // 초기 상태에서 삭제모드는 꺼짐
        deleteMode = false;
    }

    // ====== 검증 ======
    function guardAddressOnBlur() {
        const roadOk = !!roadEl.value.trim();
        const detailOk = !!detailEl.value.trim();
        roadAddressError.style.display = roadOk ? 'none' : 'block';
        detailAddressError.style.display = detailOk ? 'none' : 'block';
        return roadOk && detailOk;
    }
    function guardCoords() {
        let ok = true;
        const lat = latEl.value.trim();
        const lng = lngEl.value.trim();
        if (lat && !isValidLat(lat)) { latErr.style.display = 'block'; ok = false; } else latErr.style.display = 'none';
        if (lng && !isValidLng(lng)) { lngErr.style.display = 'block'; ok = false; } else lngErr.style.display = 'none';
        return ok;
    }
    function validatePasswordFields() {
        let ok = true;
        if (!curPwEl.value.trim()) { curPwError.style.display='block'; ok = false; } else curPwError.style.display='none';
        const pw = newPwEl.value.trim();
        if (!pw || !passwordRegex.test(pw)) { pwError.style.display='block'; ok = false; } else pwError.style.display='none';
        if (pw !== newPw2El.value.trim()) { pwConfirmError.style.display='block'; ok = false; } else pwConfirmError.style.display='none';
        return ok;
    }

    // ====== PATCH /api/me ======
    async function patchProfile() {
        if (!guardAddressOnBlur() || !guardCoords()) return;

        const fd = new FormData(profileForm);

        // 좌표 빈값 제거
        const lat = latEl.value.trim();
        const lng = lngEl.value.trim();
        if (!lat) fd.delete('latitude');
        if (!lng) fd.delete('longitude');

        // 이미지 처리 (백엔드 플래그 없이)
        const pickedFile = fileEl?.files?.[0];
        if (deleteMode) {
            // 삭제 의도 → 기본 이미지 파일을 업로드로 대체
            const defaultFile = await makeDefaultImageFile();
            fd.set('profileImage', defaultFile);
        } else if (pickedFile) {
            // 새 파일 업로드
            const max = 5 * 1024 * 1024;
            if (pickedFile.size > max) { alert('이미지 용량이 5MB를 초과합니다.'); return; }
            if (!pickedFile.type.startsWith('image/')) { alert('이미지 파일만 업로드할 수 있습니다.'); return; }
            fd.set('profileImage', pickedFile);
        } else {
            // 이미지 변경 없음 → 서버는 기존 유지
            fd.delete('profileImage');
        }

        appendCsrf(fd);

        const res = await withTimeout(20000, fetch('/api/me', {
            method: 'PATCH',
            body: fd,
            credentials: 'include',
            headers: { 'Accept': 'application/json' }
        }));
        if (!res.ok) await parseError(res, '프로필 수정 실패');

        alert('프로필이 저장되었습니다.');
        // 성공 후 내정보로 이동 (캐시 무효화 파라미터 추가)
        location.href = '/users/me?ts=' + Date.now();
    }

    // ====== PATCH /api/me/password ======
    async function patchPassword() {
        if (!validatePasswordFields()) return;

        const fd = new FormData();
        fd.append('currentPassword', curPwEl.value.trim());
        fd.append('newPassword', newPwEl.value.trim());
        fd.append('newPasswordConfirm', newPw2El.value.trim());
        appendCsrf(fd);

        const res = await withTimeout(15000, fetch('/api/me/password', {
            method: 'PATCH',
            body: fd,
            credentials: 'include',
            headers: { 'Accept': 'application/json' }
        }));
        if (!res.ok) await parseError(res, '비밀번호 변경 실패');

        alert('비밀번호가 변경되었습니다.');
        location.href = '/users/me?ts=' + Date.now();
    }

    // ====== 이벤트 ======
    // 프로필 저장
    profileForm?.addEventListener('submit', async (e) => {
        e.preventDefault();
        saveBtn.disabled = true;
        try { await patchProfile(); }
        catch (err) { console.error(err); alert(err?.message || '프로필 저장 중 오류가 발생했습니다.'); }
        finally { saveBtn.disabled = false; }
    });

    // 비밀번호 저장
    passwordForm?.addEventListener('submit', async (e) => {
        e.preventDefault();
        pwBtn.disabled = true;
        try { await patchPassword(); }
        catch (err) { console.error(err); alert(err?.message || '비밀번호 변경 중 오류가 발생했습니다.'); }
        finally { pwBtn.disabled = false; }
    });

    // 주소/좌표 blur 검증
    roadEl?.addEventListener('blur', guardAddressOnBlur);
    detailEl?.addEventListener('blur', guardAddressOnBlur);
    latEl?.addEventListener('blur', guardCoords);
    lngEl?.addEventListener('blur', guardCoords);

    // 파일 선택 → 아래 썸네일만 변경 (상단 요약은 저장 성공 후 갱신)
    fileEl?.addEventListener('change', () => {
        const f = fileEl.files?.[0];
        if (!f) return;

        const max = 5 * 1024 * 1024;
        if (f.size > max) { alert('이미지 용량이 5MB를 초과합니다.'); fileEl.value = ''; return; }
        if (!f.type.startsWith('image/')) { alert('이미지 파일만 업로드할 수 있습니다.'); fileEl.value = ''; return; }

        deleteMode = false; // 새 파일 고르면 삭제모드 해제

        if (lastObjectUrl) URL.revokeObjectURL(lastObjectUrl);
        const url = URL.createObjectURL(f);
        lastObjectUrl = url;

        if (profileThumb) profileThumb.src = url; // 미리보기만
    });

    // 이미지 삭제 → 썸네일만 기본으로, 저장 시 기본 이미지 업로드
    removeImageBtn?.addEventListener('click', () => {
        if (fileEl) fileEl.value = '';
        if (lastObjectUrl) { URL.revokeObjectURL(lastObjectUrl); lastObjectUrl = null; }
        deleteMode = true;
        if (profileThumb) profileThumb.src = DEFAULT_PLACEHOLDER_SRC;
    });

    // ====== 시작 ======
    (async function init() {
        try { await loadMe(); }
        catch (e) { console.error(e); alert('내 정보를 불러오지 못했습니다. 다시 시도해주세요.'); }
    })();
})();