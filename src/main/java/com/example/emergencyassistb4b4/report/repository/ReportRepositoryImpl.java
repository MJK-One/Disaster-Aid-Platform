package com.example.emergencyassistb4b4.report.repository;

import com.example.emergencyassistb4b4.report.domain.QReport;
import com.example.emergencyassistb4b4.report.domain.Report;
import com.example.emergencyassistb4b4.report.enums.ReportStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QReport r = QReport.report;

    @Override
    public Slice<Report> findNearby(double lat, double lng, double radiusKm, ReportStatus status, Pageable pageable) {
        //거리 계산 Haversine 공식 거리순 정렬
        NumberExpression<Double> distance = Expressions.numberTemplate(
                Double.class,
                "6371 * acos( cos( radians({0}) ) * cos( radians({1}.locationLat) ) * " +
                        "cos( radians({1}.locationLng) - radians({2}) ) + sin( radians({0}) ) * " +
                        "sin( radians({1}.locationLat) ) )",
                lat, r, lng
        );

        BooleanBuilder where = new BooleanBuilder()
                .and(distance.loe(radiusKm));
        if (status != null) {
            where.and(r.status.eq(status));
        }
        List<Report> content = queryFactory.selectFrom(r)
                .where(where)
                .orderBy(distance.asc(), r.createdAt.desc()) //거리순 ->최신순
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) content.remove(pageable.getPageSize());
        return new SliceImpl<>(content, pageable, hasNext);

    }

    @Override
    public Slice<Report> findByReporter(Long userId, ReportStatus status, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        BooleanBuilder where = new BooleanBuilder().and(r.reporter.id.eq(userId));

        if (status != null) where.and(r.status.eq(status));
        if (start != null) where.and(r.createdAt.goe(start));
        if (end != null) where.and(r.createdAt.loe(end));

        List<Report> content = queryFactory.selectFrom(r)
                .where(where)
                .orderBy(r.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        //true면 다음페이지 보여주기
        boolean hasNext = content.size() > pageable.getPageSize();
        //넘치는거 다시 제거
        if (hasNext) content.remove(pageable.getPageSize());
        return new SliceImpl<>(content, pageable, hasNext);
    }
}