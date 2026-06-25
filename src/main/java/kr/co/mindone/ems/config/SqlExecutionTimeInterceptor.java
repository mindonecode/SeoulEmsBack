package kr.co.mindone.ems.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * packageName    : kr.co.mindone.ems.config
 * fileName       : SqlExecutionTimeInterceptor
 * description    : 모든 MyBatis SQL의 실행시간을 statement ID 와 함께 로깅한다.
 *                  대시보드 API(selectPumpStatus, selectPumpPrdctOnOffStatus,
 *                  selectWtlvData, selectLatestTagData 등)에서 어느 쿼리가
 *                  느린지 핀포인트로 찾기 위한 진단용 인터셉터.
 *                  mybatis-spring-boot-starter 가 Interceptor 빈을 자동 등록한다.
 * ===========================================================
 * 임계값(ms)은 application.properties 의 sql.slow-query-threshold-ms 로 조정.
 * 기본 1000ms 이상이면 WARN, 그 미만은 INFO 로 기록.
 */
@Slf4j
@Component
// SQL 실행시간 로깅 진단용 인터셉터. 기본 비활성(중지) 상태이며,
// 다시 켜려면 application.properties 에 sql.execution-time.enabled=true 추가.
@ConditionalOnProperty(prefix = "sql.execution-time", name = "enabled", havingValue = "true", matchIfMissing = false)
@Intercepts({
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "update",
                args = {MappedStatement.class, Object.class})
})
public class SqlExecutionTimeInterceptor implements Interceptor {

    @Value("${sql.slow-query-threshold-ms:1000}")
    private long slowThresholdMs;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        long start = System.currentTimeMillis();
        try {
            return invocation.proceed();
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            String statementId = ms.getId();
            if (elapsed >= slowThresholdMs) {
                log.warn("[SQL-TIME][SLOW] {}ms - {}", elapsed, statementId);
            } else {
                log.info("[SQL-TIME] {}ms - {}", elapsed, statementId);
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // no-op
    }
}
