package kr.co.mindone.ems.common;
/**
 * packageName    : kr.co.mindone.ems.common
 * fileName       : AppConfigStore
 * description    : TB_CONFIG 런타임 설정 캐시.
 *                  @PostConstruct 최초 로딩 + 제어 산출 사이클 진입 시 reload() 1회 호출로 갱신.
 *                  (별도 주기 폴링 없음 — 설정값을 실제 쓰는 제어로직이 돌 때만 DB 재조회)
 *                  사용자 설정 변경 직후에는 updateControlConfig 에서 reload() 로 즉시 반영.
 *                  조회 실패/키 부재/파싱 실패 시 호출부가 넘긴 fallback 으로 안전 동작.
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026-06-05        claude        최초 생성
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class AppConfigStore {

    @Autowired
    private CommonMapper commonMapper;

    /** CFG_KEY → CFG_VAL 캐시 (재로딩 시 통째로 교체). */
    private volatile Map<String, String> cache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        reload();
    }

    /**
     * TB_CONFIG 재조회 → 캐시 통째로 교체.
     * 앱 기동 시(@PostConstruct) 1회, 이후 제어 산출 사이클 진입 시
     * (DrvnConfig.setInsertPumpComn) 및 사용자 설정 변경 직후 호출된다.
     */
    public void reload() {
        try {
            List<HashMap<String, Object>> rows = commonMapper.selectAppConfig();
            Map<String, String> next = new ConcurrentHashMap<>();
            if (rows != null) {
                for (HashMap<String, Object> row : rows) {
                    Object k = row.get("CFG_KEY");
                    Object v = row.get("CFG_VAL");
                    if (k != null && v != null) {
                        next.put(String.valueOf(k), String.valueOf(v));
                    }
                }
            }
            cache = next;
            log.debug("[AppConfigStore] reloaded {} config entries: {}", next.size(), next.keySet());
        } catch (Exception e) {
            // DB 장애 등: 기존 캐시 유지, 호출부는 fallback 으로 안전 동작.
            log.warn("[AppConfigStore] reload failed, keeping previous cache ({} entries): {}",
                    cache.size(), e.getMessage());
        }
    }

    /**
     * double 설정값 조회. 키 부재/파싱 실패 시 fallback 반환(경고 로그).
     * @param key      설정 키 (예: "seoul.step.gn.target")
     * @param fallback DB 미적용/파싱 실패 시 사용할 기본값 (보통 @Value 주입 기본값)
     */
    public double getDouble(String key, double fallback) {
        String raw = cache.get(key);
        if (raw == null || raw.trim().isEmpty()) {
            return fallback;
        }
        try {
            return Double.parseDouble(raw.trim());
        } catch (NumberFormatException e) {
            log.warn("[AppConfigStore] key '{}' value '{}' 파싱 실패 → fallback {} 사용", key, raw, fallback);
            return fallback;
        }
    }
}
