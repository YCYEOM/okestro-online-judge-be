-- DDL (Data Definition Language)

-- 기존 테이블 삭제 (FK 제약조건 순서 고려)
DROP TABLE IF EXISTS comment_like;
DROP TABLE IF EXISTS solution_like;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS solution;
DROP TABLE IF EXISTS board;
DROP TABLE IF EXISTS user_problem_status;
DROP TABLE IF EXISTS submission;
DROP TABLE IF EXISTS testcase;
DROP TABLE IF EXISTS problem_tag_map;
DROP TABLE IF EXISTS problem_tag;
DROP TABLE IF EXISTS problem_statistics;
DROP TABLE IF EXISTS user_statistics;
DROP TABLE IF EXISTS problem;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS organization;
DROP TABLE IF EXISTS tier;

-- 1. Tier (티어 & 난이도 통합)
CREATE TABLE tier (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '티어 ID',
                      group_name VARCHAR(255) NOT NULL COMMENT '티어/난이도 그룹 명',
                      level INT NOT NULL COMMENT '티어/난이도 레벨',
                      power_score INT COMMENT '티어 점수 (내부 계산용)',
                      min_score INT COMMENT '해당 티어 진입 최소 점수',
                      max_score INT COMMENT '해당 티어 최대 점수',
                      problem_score INT COMMENT '해당 난이도 문제 해결 시 획득 점수'
) COMMENT '티어 및 문제 난이도 정보';

-- 2. Organization (조직)
CREATE TABLE organization (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '조직 ID',
                              parent_id BIGINT COMMENT '상위 조직 ID',
                              name VARCHAR(255) NOT NULL COMMENT '조직 이름',
                              depth INT NOT NULL COMMENT '조직 계층 깊이',
                              created_at DATETIME COMMENT '생성 일시',
                              updated_at DATETIME COMMENT '수정 일시',
                              FOREIGN KEY (parent_id) REFERENCES organization(id)
) COMMENT '조직 정보';

-- 3. User (사용자)
CREATE TABLE users (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '사용자 ID',
                      tier_id BIGINT COMMENT '티어 ID',
                      organization_id BIGINT COMMENT '조직 ID',
                      username VARCHAR(255) NOT NULL UNIQUE COMMENT '사용자명',
                      password_hash VARCHAR(255) NOT NULL COMMENT '비밀번호 해시',
                      email VARCHAR(255) NOT NULL UNIQUE COMMENT '이메일',
                      role VARCHAR(50) NOT NULL COMMENT '사용자 권한',
                      created_at DATETIME COMMENT '생성 일시',
                      updated_at DATETIME COMMENT '수정 일시',
                      FOREIGN KEY (tier_id) REFERENCES tier(id),
                      FOREIGN KEY (organization_id) REFERENCES organization(id)
) COMMENT '사용자 정보';

-- 4. User Statistics (사용자 통계)
CREATE TABLE user_statistics (
                                 user_id BIGINT PRIMARY KEY COMMENT '사용자 ID',
                                 solved_count BIGINT NOT NULL DEFAULT 0 COMMENT '해결한 문제 수',
                                 failed_count BIGINT NOT NULL DEFAULT 0 COMMENT '실패한 문제 수',
                                 ranking_point BIGINT NOT NULL DEFAULT 0 COMMENT '랭킹 포인트',
                                 FOREIGN KEY (user_id) REFERENCES users(id)
) COMMENT '사용자 통계';

-- 5. Problem (문제)
CREATE TABLE problem (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '문제 ID',
                         title VARCHAR(255) NOT NULL COMMENT '문제 제목',
                         content_path VARCHAR(255) NOT NULL COMMENT '문제 내용 파일 경로',
                         input_desc TEXT COMMENT '입력 설명',
                         output_desc TEXT COMMENT '출력 설명',
                         tier_id BIGINT NOT NULL COMMENT '난이도(티어) ID',
                         time_limit_ms INT NOT NULL COMMENT '시간 제한(ms)',
                         memory_limit_kb INT NOT NULL COMMENT '메모리 제한(KB)',
                         created_at DATETIME COMMENT '생성 일시',
                         updated_at DATETIME COMMENT '수정 일시',
                         FOREIGN KEY (tier_id) REFERENCES tier(id)
) COMMENT '문제 정보';

-- 6. Problem Statistics (문제 통계)
CREATE TABLE problem_statistics (
                                    problem_id BIGINT PRIMARY KEY COMMENT '문제 ID',
                                    solved_count BIGINT NOT NULL DEFAULT 0 COMMENT '해결된 횟수',
                                    try_count BIGINT NOT NULL DEFAULT 0 COMMENT '시도 횟수',
                                    acceptance_rate DOUBLE NOT NULL DEFAULT 0.0 COMMENT '정답률',
                                    FOREIGN KEY (problem_id) REFERENCES problem(id)
) COMMENT '문제 통계';

-- 7. Problem Tag (문제 태그)
CREATE TABLE problem_tag (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '태그 ID',
                             name VARCHAR(255) NOT NULL UNIQUE COMMENT '태그 이름'
) COMMENT '문제 태그';

-- 8. Problem Tag Map (문제-태그 매핑)
CREATE TABLE problem_tag_map (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '매핑 ID',
                                 problem_id BIGINT NOT NULL COMMENT '문제 ID',
                                 tag_id BIGINT NOT NULL COMMENT '태그 ID',
                                 FOREIGN KEY (problem_id) REFERENCES problem(id),
                                 FOREIGN KEY (tag_id) REFERENCES problem_tag(id)
) COMMENT '문제-태그 매핑';

-- 9. TestCase (테스트케이스)
CREATE TABLE testcase (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '테스트케이스 ID',
                          problem_id BIGINT NOT NULL COMMENT '문제 ID',
                          input TEXT NOT NULL COMMENT '입력 데이터',
                          output TEXT NOT NULL COMMENT '출력 데이터',
                          is_sample BOOLEAN NOT NULL COMMENT '예제 여부',
                          FOREIGN KEY (problem_id) REFERENCES problem(id)
) COMMENT '테스트케이스';

-- 10. Submission (제출)
CREATE TABLE submission (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '제출 ID',
                            user_id BIGINT NOT NULL COMMENT '사용자 ID',
                            problem_id BIGINT NOT NULL COMMENT '문제 ID',
                            language VARCHAR(50) NOT NULL COMMENT '언어',
                            source_code TEXT NOT NULL COMMENT '소스 코드',
                            result VARCHAR(50) NOT NULL COMMENT '채점 결과',
                            exec_time_ms INT COMMENT '실행 시간(ms)',
                            memory_kb INT COMMENT '메모리 사용량(KB)',
                            created_at DATETIME COMMENT '생성 일시',
                            updated_at DATETIME COMMENT '수정 일시',
                            FOREIGN KEY (user_id) REFERENCES users(id),
                            FOREIGN KEY (problem_id) REFERENCES problem(id)
) COMMENT '제출 정보';

-- 11. User Problem Status (사용자 문제 풀이 상태)
CREATE TABLE user_problem_status (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '상태 ID',
                                     user_id BIGINT NOT NULL COMMENT '사용자 ID',
                                     problem_id BIGINT NOT NULL COMMENT '문제 ID',
                                     status VARCHAR(50) NOT NULL COMMENT '상태',
                                     last_solved DATETIME COMMENT '마지막 해결 일시',
                                     FOREIGN KEY (user_id) REFERENCES users(id),
                                     FOREIGN KEY (problem_id) REFERENCES problem(id)
) COMMENT '사용자 문제 풀이 상태';

-- 12. Solution (정답 공유)
CREATE TABLE solution (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '솔루션 ID',
                          user_id BIGINT NOT NULL COMMENT '작성자 ID',
                          problem_id BIGINT NOT NULL COMMENT '관련 문제 ID',
                          submission_id BIGINT NOT NULL UNIQUE COMMENT '관련 제출 ID',
                          language VARCHAR(50) NOT NULL COMMENT '언어',
                          code TEXT NOT NULL COMMENT '소스 코드',
                          description TEXT COMMENT '설명',
                          visibility BOOLEAN NOT NULL DEFAULT TRUE COMMENT '공개 여부',
                          created_at DATETIME COMMENT '생성 일시',
                          updated_at DATETIME COMMENT '수정 일시',
                                     FOREIGN KEY (user_id) REFERENCES users(id),
                          FOREIGN KEY (problem_id) REFERENCES problem(id),
                          FOREIGN KEY (submission_id) REFERENCES submission(id)
) COMMENT '정답 코드 공유';

-- 13. Comment (댓글)
CREATE TABLE comment (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '댓글 ID',
                         solution_id BIGINT NOT NULL COMMENT '솔루션 ID',
                         user_id BIGINT NOT NULL COMMENT '작성자 ID',
                         content TEXT NOT NULL COMMENT '내용',
                         created_at DATETIME COMMENT '생성 일시',
                         updated_at DATETIME COMMENT '수정 일시',
                         FOREIGN KEY (solution_id) REFERENCES solution(id),
                         FOREIGN KEY (user_id) REFERENCES users(id)
) COMMENT '댓글';

-- 14. Solution Like (솔루션 좋아요)
CREATE TABLE solution_like (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '좋아요 ID',
                               solution_id BIGINT NOT NULL COMMENT '솔루션 ID',
                               user_id BIGINT NOT NULL COMMENT '사용자 ID',
                               created_at DATETIME COMMENT '생성 일시',
                               FOREIGN KEY (solution_id) REFERENCES solution(id),
                               FOREIGN KEY (user_id) REFERENCES users(id),
                               UNIQUE KEY uk_solution_user (solution_id, user_id)
) COMMENT '솔루션 좋아요';

-- 15. Comment Like (댓글 좋아요)
CREATE TABLE comment_like (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '좋아요 ID',
                              comment_id BIGINT NOT NULL COMMENT '댓글 ID',
                              user_id BIGINT NOT NULL COMMENT '사용자 ID',
                              created_at DATETIME COMMENT '생성 일시',
                              FOREIGN KEY (comment_id) REFERENCES comment(id),
                              FOREIGN KEY (user_id) REFERENCES users(id),
                              UNIQUE KEY uk_comment_user (comment_id, user_id)
) COMMENT '댓글 좋아요';


-- DML (Data Manipulation Language) - 기초 데이터 삽입

INSERT INTO tier (group_name, level, power_score, min_score, max_score, problem_score) VALUES
-- Bronze (1~5점)
('Bronze', 5, 0, 0, 59, 1),
('Bronze', 4, 60, 60, 119, 2),
('Bronze', 3, 120, 120, 179, 3),
('Bronze', 2, 180, 180, 239, 4),
('Bronze', 1, 240, 240, 299, 5),

-- Silver (6~10점)
('Silver', 5, 300, 300, 399, 6),
('Silver', 4, 400, 400, 499, 7),
('Silver', 3, 500, 500, 599, 8),
('Silver', 2, 600, 600, 699, 9),
('Silver', 1, 700, 700, 799, 10),

-- Gold (11~15점)
('Gold', 5, 800, 800, 949, 11),
('Gold', 4, 950, 950, 1099, 12),
('Gold', 3, 1100, 1100, 1249, 13),
('Gold', 2, 1250, 1250, 1399, 14),
('Gold', 1, 1400, 1400, 1549, 15),

-- Platinum (16~20점)
('Platinum', 5, 1550, 1550, 1749, 16),
('Platinum', 4, 1750, 1750, 1949, 17),
('Platinum', 3, 1950, 1950, 2149, 18),
('Platinum', 2, 2150, 2150, 2349, 19),
('Platinum', 1, 2350, 2350, 2549, 20),

-- Diamond (21~25점)
('Diamond', 5, 2550, 2550, 2799, 21),
('Diamond', 4, 2800, 2800, 3049, 22),
('Diamond', 3, 3050, 3050, 3299, 23),
('Diamond', 2, 3300, 3300, 3549, 24),
('Diamond', 1, 3550, 3550, 3799, 25),

-- Ruby (26~30점)
('Ruby', 5, 3800, 3800, 4099, 26),
('Ruby', 4, 4100, 4100, 4399, 27),
('Ruby', 3, 4400, 4400, 4699, 28),
('Ruby', 2, 4700, 4700, 4999, 29),
('Ruby', 1, 5000, 5000, 2147483647, 30);
