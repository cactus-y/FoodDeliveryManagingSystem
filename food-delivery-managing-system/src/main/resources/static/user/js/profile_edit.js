(() => {
    const $ = (id) => document.getElementById(id);

    // ------------ 내 정보 로드 ------------
    let initialMe = null;

    async function loadMe() {
        try {
            const res = await fetch('/api/me', { credentials: 'include' });
            if (!res.ok) throw new Error(`${res.status} ${res.statusText}`);
            const me = await res.json();

            initialMe = {
                name: me.name ?? '',
                email: me.email ?? '',
                nickName: me.nickName ?? '',
                roadAddress: me.roadAddress ?? '',
                detailAddress: me.detailAddress ?? '',
                latitude: typeof me.latitude === 'number' ? me.latitude : '',
                longitude: typeof me.longitude === 'number' ? me.longitude : '',
                profileImageUrl: me.profileImageUrl ?? ''
            };
        } catch (e) {
            console.warn('GET /api/me 실패:', e);
            initialMe = {
                name: '', email: '', nickName: '',
                roadAddress: '', detailAddress: '',
                latitude: '', longitude: '', profileImageUrl: ''
            };
        } finally {
            fillForm(initialMe);
        }
    }

    function fillForm(me) {
        if ($('name')) $('name').value = me.name;
        if ($('email')) $('email').value = me.email;
        if ($('nick_name')) $('nick_name').value = me.nickName;
        if ($('road_address')) $('road_address').value = me.roadAddress;
        if ($('detail_address')) $('detail_address').value = me.detailAddress;
        if ($('latitude') != null) $('latitude').value = me.latitude ?? '';
        if ($('longitude') != null) $('longitude').value = me.longitude ?? '';

        const preview = $('profilePreview');
        if (preview) {
            preview.src = me.profileImageUrl || 'https://dummyimage.com/128x128/ffffff/6b7280.png&text=Profile';
            preview.onerror = () => {
                preview.src = 'https://dummyimage.com/128x128/ffffff/6b7280.png&text=Profile';
            };
        }
    }

    // ------------ 이미지 미리보기 ------------
    function initProfilePreview() {
        const fileInput = $('profile');
        const preview = $('profilePreview');
        if (!fileInput || !preview) return;

        fileInput.addEventListener('change', (e) => {
            const file = e.target.files?.[0];
            if (!file) return;
            const reader = new FileReader();
            reader.onload = (evt) => { preview.src = evt.target.result; };
            reader.readAsDataURL(file);
        });
    }

    // ------------ 프로필 제출 (실제 PATCH) ------------
    function enhanceProfileForm() {
        const form = $('profileForm');
        if (!form) return;

        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            // 간단 검증
            const name = $('name'), nick = $('nick_name'), road = $('road_address'), detail = $('detail_address');
            const nameErr = $('name_error'), nickErr = $('nickname_error'), roadErr = $('road_address_error'), detailErr = $('detail_address_error');

            let valid = true;
            if (!name.value.trim()) { nameErr.style.display = 'block'; valid = false; } else nameErr.style.display = 'none';
            if (!nick.value.trim()) { nickErr.style.display = 'block'; valid = false; } else nickErr.style.display = 'none';
            if (!road.value.trim()) { roadErr.style.display = 'block'; valid = false; } else roadErr.style.display = 'none';
            if (!detail.value.trim()) { detailErr.style.display = 'block'; valid = false; } else detailErr.style.display = 'none';
            if (!valid) return;

            const fd = new FormData(form);
            // 파일 필드는 FormData에 자동 포함되지만, name을 맞춰두는 차원에서 한 번 더 세팅해도 무방
            if ($('profile')?.files?.[0]) fd.set('profileImage', $('profile').files[0]);

            try {
                const res = await fetch('/api/me', {
                    method: 'PATCH',
                    body: fd,
                    credentials: 'include'
                    // Content-Type 자동 설정(FormData) — 직접 지정하지 말 것
                });

                if (!res.ok) {
                    const text = await res.text().catch(() => '');
                    throw new Error(`프로필 수정 실패 (${res.status}): ${text || res.statusText}`);
                }

                alert('프로필이 저장되었습니다.');
                // 저장 후 내 정보 페이지로 이동 (캐시 무효화 쿼리 추가 권장)
                window.location.href = '/users/me?ts=' + Date.now();
            } catch (err) {
                console.error(err);
                alert(err.message || '프로필 저장 중 오류가 발생했습니다.');
            }
        });

        // 초기화 버튼 → 처음 로드 상태로 복원
        $('resetBtn')?.addEventListener('click', () => { if (initialMe) fillForm(initialMe); });
    }

    // ------------ 비밀번호 변경 (실제 PATCH) ------------
    function enhancePasswordForm() {
        const form = $('passwordForm');
        if (!form) return;

        const npw = $('new_password'), npc = $('new_password_confirm'), npcErr = $('new_password_confirm_error');
        const match = () => {
            if (!npw.value || !npc.value) { npcErr.style.display = 'none'; return; }
            npcErr.style.display = (npw.value === npc.value) ? 'none' : 'block';
        };
        npw?.addEventListener('input', match);
        npc?.addEventListener('input', match);

        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            const cur = $('current_password'), curErr = $('current_password_error'), npwErr = $('new_password_error');
            let valid = true;
            if (!cur.value.trim()) { curErr.style.display = 'block'; valid = false; } else curErr.style.display = 'none';
            if (!npw.value.trim()) { npwErr.style.display = 'block'; valid = false; } else npwErr.style.display = 'none';
            if (!npc.value.trim() || npw.value !== npc.value) { npcErr.style.display = 'block'; valid = false; }
            if (!valid) return;

            // URL-encoded 로 전송 (@ModelAttribute 바인딩용)
            const body = new URLSearchParams();
            body.set('currentPassword', cur.value);
            body.set('newPassword', npw.value);
            body.set('passwordConfirm', npc.value);

            try {
                const res = await fetch('/api/me/password', {
                    method: 'PATCH',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
                    body,
                    credentials: 'include'
                });

                if (!res.ok) {
                    const text = await res.text().catch(() => '');
                    throw new Error(`비밀번호 변경 실패 (${res.status}): ${text || res.statusText}`);
                }

                alert('비밀번호가 변경되었습니다. 다시 로그인해야 할 수 있습니다.');
                form.reset();
            } catch (err) {
                console.error(err);
                alert(err.message || '비밀번호 변경 중 오류가 발생했습니다.');
            }
        });
    }

    // HTML의 구글 스크립트가 로드되면 이 함수가 호출됨
    window.initAutocomplete = function initAutocomplete() {
        const input = document.getElementById('road_address');
        if (!input || !window.google?.maps?.places) return;

        const options = {
            componentRestrictions: { country: 'KR' },
            fields: ['formatted_address', 'geometry', 'name', 'place_id'],
            strictBounds: false
        };
        const autocomplete = new google.maps.places.Autocomplete(input, options);

        autocomplete.addListener('place_changed', () => {
            const place = autocomplete.getPlace();
            if (!place || !place.geometry) return;

            const lat = place.geometry.location.lat();
            const lng = place.geometry.location.lng();
            $('road_address').value = place.formatted_address || place.name || '';
            if ($('latitude')) $('latitude').value = Number(lat.toFixed(6));
            if ($('longitude')) $('longitude').value = Number(lng.toFixed(6));
            $('detail_address')?.focus();
            const pid = document.getElementById('place_id');
            if (pid) pid.value = place.place_id || '';
        });
    };

    // ------------ 초기화 ------------
    document.addEventListener('DOMContentLoaded', () => {
        loadMe();
        initProfilePreview();
        enhanceProfileForm();
        enhancePasswordForm();
    });
})();
