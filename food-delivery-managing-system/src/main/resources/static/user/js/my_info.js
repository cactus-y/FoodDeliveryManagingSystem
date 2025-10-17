(() => {
    const $ = (id) => document.getElementById(id);

    const endpoint = "/api/me";
    const fallbackAvatar = "https://dummyimage.com/96x96/eeeeee/888888.png&text=User";

    // robust date formatter
    const fmtDate = (v) => {
        if (v == null) return "-";

        // 숫자: epoch (sec/ms) 처리
        if (typeof v === "number") {
            const ms = v < 1e12 ? v * 1000 : v; // 10자리면 sec, 13자리면 ms로 가정
            const d = new Date(ms);
            return isNaN(d.getTime()) ? "-" : `${d.getFullYear()}.${String(d.getMonth()+1).padStart(2,"0")}.${String(d.getDate()).padStart(2,"0")}`;
        }

        // 문자열: 공백/빈문자 처리
        const s = String(v).trim();
        if (!s) return "-";

        // 'yyyy-MM-dd HH:mm:ss' 같이 'T' 없는 경우를 위한 보정 (로컬로 파싱됨)
        let toParse = s;
        if (/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/.test(s)) {
            toParse = s.replace(" ", "T"); // 간이 보정
        }

        const d = new Date(toParse);
        if (isNaN(d.getTime())) return "-";

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
            // 서버 스키마 유연 대응
            setText("name", me.name || "이름 없음");
            setText("nickName", me.nickName ? `@${me.nickName}` : "");
            setText("email", me.email);


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
            setText("address", "-");
            $("coordsRow")?.setAttribute("hidden", "true");
        }
    }

    // 초기 로드
    loadMe();
})();
