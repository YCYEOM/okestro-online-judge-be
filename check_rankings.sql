-- 랭킹 데이터 확인 쿼리

-- 1. user_statistics 테이블 전체 데이터 확인
SELECT 
    us.user_id,
    u.username,
    us.solved_count,
    us.failed_count,
    us.ranking_point
FROM user_statistics us
JOIN users u ON us.user_id = u.id
ORDER BY us.ranking_point DESC, us.solved_count DESC;

-- 2. 사용자와 티어 정보 포함 확인
SELECT 
    us.user_id,
    u.username,
    u.email,
    t.group_name as tier_name,
    t.level as tier_level,
    us.solved_count,
    us.ranking_point,
    u.organization_name
FROM user_statistics us
JOIN users u ON us.user_id = u.id
LEFT JOIN tier t ON u.tier_id = t.id
ORDER BY us.ranking_point DESC, us.solved_count DESC;

-- 3. user_statistics가 있지만 랭킹 포인트가 0인 사용자
SELECT 
    us.user_id,
    u.username,
    us.solved_count,
    us.ranking_point
FROM user_statistics us
JOIN users u ON us.user_id = u.id
WHERE us.ranking_point = 0;

-- 4. user_statistics 테이블 총 개수
SELECT COUNT(*) as total_stats_count FROM user_statistics;

-- 5. users 테이블 총 개수
SELECT COUNT(*) as total_users_count FROM users;

-- 6. tier가 NULL인 사용자 확인
SELECT 
    u.id,
    u.username,
    u.tier_id,
    u.email
FROM users u
WHERE u.tier_id IS NULL;

-- 7. 랭킹 쿼리와 동일한 조건으로 조회 (JPQL 쿼리 검증)
SELECT 
    us.user_id,
    u.username,
    t.group_name,
    us.solved_count,
    us.ranking_point,
    u.organization_name,
    u.profile_image
FROM user_statistics us
INNER JOIN users u ON us.user_id = u.id
INNER JOIN tier t ON u.tier_id = t.id
ORDER BY us.ranking_point DESC, us.solved_count DESC
LIMIT 10;


