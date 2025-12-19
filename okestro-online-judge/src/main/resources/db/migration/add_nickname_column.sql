-- ============================================
-- 닉네임 시스템 추가 마이그레이션
-- ============================================
-- 작성일: 2025-12-10
-- 설명: 사용자 테이블에 nickname 컬럼 추가 및 기존 사용자 데이터 마이그레이션

-- 1. nickname 컬럼 추가 (NULL 허용으로 먼저 추가)
ALTER TABLE users ADD COLUMN IF NOT EXISTS nickname VARCHAR(20);

-- 2. 기존 사용자들의 nickname을 username으로 초기화
UPDATE users
SET nickname = username
WHERE nickname IS NULL;

-- 3. nickname에 UNIQUE 제약조건 추가
ALTER TABLE users ADD CONSTRAINT IF NOT EXISTS uk_users_nickname UNIQUE (nickname);

-- 4. nickname을 NOT NULL로 변경
ALTER TABLE users MODIFY COLUMN nickname VARCHAR(20) NOT NULL;

-- 5. username의 UNIQUE 제약조건 제거 (이름은 중복 가능)
-- MariaDB에서는 제약조건 이름을 먼저 확인해야 함
-- ALTER TABLE users DROP INDEX uk_users_username IF EXISTS;

-- 완료 메시지
SELECT '닉네임 시스템 마이그레이션 완료' AS status;
