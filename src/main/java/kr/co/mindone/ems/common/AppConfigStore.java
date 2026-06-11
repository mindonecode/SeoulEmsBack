package kr.co.mindone.ems.common;
/**
 * packageName    : kr.co.mindone.ems.common
 * fileName       : AppConfigStore
 * description    : TB_CONFIG 런타임 설정 캐시.
 *                  @PostConstruct 최초 로딩 + 60초마다 재로딩하여 앱 재시작 없이 무중단 반영.
 *                  조회 실패/키 부재/파싱 실패 시 호출부가 넘긴 fallback 으로 안전 동작.
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026-06-05        claude        최초 생성
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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

    /** 60초마다 TB_CONFIG 재로딩 → 무중단 반영. */
    @Scheduled(fixedDelay = 60_000)
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
