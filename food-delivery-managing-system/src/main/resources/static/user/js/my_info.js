(() => {
    const $ = (id) => document.getElementById(id);

    const endpoint = "/api/me";
    const fallbackAvatar = "https://dummyimage.com/96x96/eeeeee/888888.png&text=User";

    const fmtDate = (iso) => {
        if (!iso) return "-";
        const d = new Date(iso);
        return `${d.getFullYear()}.${String(d.getMonth()+1).padStart(2,"0")}.${String(d.getDate()).padStart(2,"0")}`;
    };

    const setText = (id, text, removeSkeleton = true) => {
        const el = $(id);
        if (!el) return;
        el.textContent = text ?? "-";
        if (removeSkeleton) el.classList.remove("skeleton");
    };

    const fetchJSON = async (url) => {
        const res = await fetch(url, { credentials: "same-origin" });
        if (!res.ok) throw new Error(`${res.status} ${res.statusText} at ${url}`);
        return res.json();
    };

    async function loadMe() {
        try {
            const me = await fetchJSON(endpoint);
            // 서버 스키마: userId, email, name, nickName, roadAddress, detailAddress, latitude, longitude, profileUrl, createdAt?
            setText("name", me.name || "이름 없음");
            setText("nickName", me.nickName ? `@${me.nickName}` : "");
            setText("email", me.email);
            setText("joinedAt", me.createdAt ? fmtDate(me.createdAt) : "-");

            const address = [me.roadAddress, me.detailAddress].filter(Boolean).join(" ");
            setText("address", address || "-");

            const avatar = $("avatar");
            if (avatar) {
                avatar.src = me.profileUrl || fallbackAvatar;
                avatar.onerror = () => (avatar.src = fallbackAvatar);
            }

            if (typeof me.latitude === "number" && typeof me.longitude === "number") {
                $("coordsRow")?.removeAttribute("hidden");
                $("latChip").textContent = `위도 ${Number(me.latitude).toFixed(6)}`;
                $("lngChip").textContent = `경도 ${Number(me.longitude).toFixed(6)}`;
            } else {
                $("coordsRow")?.setAttribute("hidden", "true");
            }
        } catch (e) {
            console.error("me error", e);
            setText("name", "불러오기 실패");
            setText("email", "-");
            setText("joinedAt", "-");
            setText("address", "-");
            $("coordsRow")?.setAttribute("hidden", "true");
        }
    }

    // 초기 로드
    loadMe();
})();
