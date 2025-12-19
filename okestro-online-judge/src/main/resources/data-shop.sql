-- 상점 아이템 초기 데이터 (MariaDB용)
-- 실행: application.yml에서 spring.sql.init.mode: always 설정 또는 직접 실행
-- 중복 방지: ON DUPLICATE KEY UPDATE 사용 (type, item_value 기준)
-- 주의: shop_items 테이블에 UNIQUE KEY(type, item_value) 제약조건 필요

-- ============================================
-- 아바타 (DiceBear 스타일)
-- ============================================
INSERT INTO shop_items (type, name, description, price, item_value, preview_url, rarity, is_active, sort_order, created_at, updated_at)
VALUES
('AVATAR', 'Adventurer', '탐험가 스타일의 친근한 아바타', 100, 'adventurer', NULL, 'COMMON', 1, 1, NOW(), NOW()),
('AVATAR', 'Adventurer Neutral', '중성적인 탐험가 아바타', 100, 'adventurer-neutral', NULL, 'COMMON', 1, 2, NOW(), NOW()),
('AVATAR', 'Avataaars', '다양한 커스터마이징이 가능한 아바타', 150, 'avataaars', NULL, 'RARE', 1, 3, NOW(), NOW()),
('AVATAR', 'Big Ears', '큰 귀가 특징인 귀여운 아바타', 150, 'big-ears', NULL, 'RARE', 1, 4, NOW(), NOW()),
('AVATAR', 'Big Smile', '밝은 미소의 아바타', 150, 'big-smile', NULL, 'RARE', 1, 5, NOW(), NOW()),
('AVATAR', 'Bottts', '로봇 스타일 아바타', 200, 'bottts', NULL, 'EPIC', 1, 6, NOW(), NOW()),
('AVATAR', 'Croodles', '손그림 스타일 아바타', 200, 'croodles', NULL, 'EPIC', 1, 7, NOW(), NOW()),
('AVATAR', 'Fun Emoji', '재미있는 이모지 아바타', 150, 'fun-emoji', NULL, 'RARE', 1, 8, NOW(), NOW()),
('AVATAR', 'Icons', '아이콘 스타일 아바타', 100, 'icons', NULL, 'COMMON', 1, 9, NOW(), NOW()),
('AVATAR', 'Identicon', '고유 패턴 아바타', 100, 'identicon', NULL, 'COMMON', 1, 10, NOW(), NOW()),
('AVATAR', 'Lorelei', '우아한 스타일 아바타', 250, 'lorelei', NULL, 'EPIC', 1, 11, NOW(), NOW()),
('AVATAR', 'Micah', '미니멀 스타일 아바타', 200, 'micah', NULL, 'EPIC', 1, 12, NOW(), NOW()),
('AVATAR', 'Miniavs', '미니 아바타', 150, 'miniavs', NULL, 'RARE', 1, 13, NOW(), NOW()),
('AVATAR', 'Notionists', '노션 스타일 아바타', 200, 'notionists', NULL, 'EPIC', 1, 14, NOW(), NOW()),
('AVATAR', 'Open Peeps', '오픈 피플 스타일', 200, 'open-peeps', NULL, 'EPIC', 1, 15, NOW(), NOW()),
('AVATAR', 'Personas', '페르소나 스타일', 250, 'personas', NULL, 'EPIC', 1, 16, NOW(), NOW()),
('AVATAR', 'Pixel Art', '픽셀 아트 스타일', 300, 'pixel-art', NULL, 'LEGENDARY', 1, 17, NOW(), NOW()),
('AVATAR', 'Pixel Art Neutral', '중성 픽셀 아트', 300, 'pixel-art-neutral', NULL, 'LEGENDARY', 1, 18, NOW(), NOW()),
('AVATAR', 'Rings', '링 패턴 아바타', 100, 'rings', NULL, 'COMMON', 1, 19, NOW(), NOW()),
('AVATAR', 'Shapes', '도형 아바타', 100, 'shapes', NULL, 'COMMON', 1, 20, NOW(), NOW()),
('AVATAR', 'Thumbs', '엄지척 아바타', 150, 'thumbs', NULL, 'RARE', 1, 21, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    price = VALUES(price),
    preview_url = VALUES(preview_url),
    rarity = VALUES(rarity),
    is_active = VALUES(is_active),
    sort_order = VALUES(sort_order),
    updated_at = NOW();

-- ============================================
-- 프리렌 아바타 (장송의 프리렌)
-- 이미지 파일 위치: public/images/avatars/frieren/
-- ============================================
INSERT INTO shop_items (type, name, description, price, item_value, preview_url, rarity, is_active, sort_order, created_at, updated_at)
VALUES
('AVATAR', '프리렌 - 무표정', '장송의 프리렌: 평소의 심드렁한 무표정', 300, 'frieren-neutral', '/images/avatars/frieren/neutral.png', 'EPIC', 1, 22, NOW(), NOW()),
('AVATAR', '프리렌 - 씨익', '장송의 프리렌: 묘하게 자신만만한 미소', 350, 'frieren-smile', '/images/avatars/frieren/smile.png', 'LEGENDARY', 1, 23, NOW(), NOW()),
('AVATAR', '프리렌 - 기본', '장송의 프리렌: 천년을 살아온 엘프 마법사', 300, 'frieren-default', '/images/avatars/frieren/surprised.png', 'EPIC', 1, 24, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    price = VALUES(price),
    preview_url = VALUES(preview_url),
    rarity = VALUES(rarity),
    is_active = VALUES(is_active),
    sort_order = VALUES(sort_order),
    updated_at = NOW();

-- ============================================
-- 리제로 아바타 (Re:Zero)
-- 이미지 파일 위치: public/images/avatars/rezero/
-- ============================================
INSERT INTO shop_items (type, name, description, price, item_value, preview_url, rarity, is_active, sort_order, created_at, updated_at)
VALUES
('AVATAR', '렘', 'Re:Zero: 푸른 머리의 쌍둥이 메이드', 300, 'rezero-rem', '/images/avatars/rezero/rem.png', 'EPIC', 1, 25, NOW(), NOW()),
('AVATAR', '람', 'Re:Zero: 분홍 머리의 쌍둥이 메이드', 300, 'rezero-ram', '/images/avatars/rezero/ram.png', 'EPIC', 1, 26, NOW(), NOW()),
('AVATAR', '에밀리아', 'Re:Zero: 은발의 하프엘프 왕선후보', 350, 'rezero-emilia', '/images/avatars/rezero/emilia.png', 'LEGENDARY', 1, 27, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    price = VALUES(price),
    preview_url = VALUES(preview_url),
    rarity = VALUES(rarity),
    is_active = VALUES(is_active),
    sort_order = VALUES(sort_order),
    updated_at = NOW();

-- ============================================
-- 귀멸의 칼날 아바타 (Demon Slayer)
-- 이미지 파일 위치: public/images/avatars/kimetsu/
-- ============================================
INSERT INTO shop_items (type, name, description, price, item_value, preview_url, rarity, is_active, sort_order, created_at, updated_at)
VALUES
('AVATAR', '탄지로', '귀멸의 칼날: 물의 호흡을 사용하는 주인공', 300, 'kimetsu-tanjiro', '/images/avatars/kimetsu/tanjiro.png', 'EPIC', 1, 28, NOW(), NOW()),
('AVATAR', '네즈코', '귀멸의 칼날: 탄지로의 여동생, 혈귀', 350, 'kimetsu-nezuko', '/images/avatars/kimetsu/nezuko.png', 'LEGENDARY', 1, 29, NOW(), NOW()),
('AVATAR', '젠이츠', '귀멸의 칼날: 번개의 호흡, 겁쟁이 검사', 300, 'kimetsu-zenitsu', '/images/avatars/kimetsu/zenitsu.png', 'EPIC', 1, 30, NOW(), NOW()),
('AVATAR', '이노스케', '귀멸의 칼날: 짐승의 호흡, 멧돼지 가면', 300, 'kimetsu-inosuke', '/images/avatars/kimetsu/inosuke.png', 'EPIC', 1, 31, NOW(), NOW()),
('AVATAR', '기유', '귀멸의 칼날: 물의 호흡 주(柱)', 350, 'kimetsu-giyu', '/images/avatars/kimetsu/giyu.png', 'LEGENDARY', 1, 32, NOW(), NOW()),
('AVATAR', '시노부', '귀멸의 칼날: 벌레의 호흡 주(柱)', 350, 'kimetsu-shinobu', '/images/avatars/kimetsu/shinobu.png', 'LEGENDARY', 1, 33, NOW(), NOW()),
('AVATAR', '렌고쿠', '귀멸의 칼날: 염의 호흡 주(柱)', 400, 'kimetsu-rengoku', '/images/avatars/kimetsu/rengoku.png', 'LEGENDARY', 1, 34, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    price = VALUES(price),
    preview_url = VALUES(preview_url),
    rarity = VALUES(rarity),
    is_active = VALUES(is_active),
    sort_order = VALUES(sort_order),
    updated_at = NOW();

-- ============================================
-- 원피스 아바타 (One Piece)
-- 이미지 파일 위치: public/images/avatars/onepiece/
-- ============================================
INSERT INTO shop_items (type, name, description, price, item_value, preview_url, rarity, is_active, sort_order, created_at, updated_at)
VALUES
('AVATAR', '루피', '원피스: 밀짚모자 해적단 선장, 고무고무 열매', 400, 'onepiece-luffy', '/images/avatars/onepiece/luffy.png', 'LEGENDARY', 1, 35, NOW(), NOW()),
('AVATAR', '조로', '원피스: 밀짚모자 해적단 검사, 삼도류', 350, 'onepiece-zoro', '/images/avatars/onepiece/zoro.png', 'EPIC', 1, 36, NOW(), NOW()),
('AVATAR', '나미', '원피스: 밀짚모자 해적단 항해사', 300, 'onepiece-nami', '/images/avatars/onepiece/nami.png', 'EPIC', 1, 37, NOW(), NOW()),
('AVATAR', '상디', '원피스: 밀짚모자 해적단 요리사, 검은다리', 300, 'onepiece-sanji', '/images/avatars/onepiece/sanji.png', 'EPIC', 1, 38, NOW(), NOW()),
('AVATAR', '쵸파', '원피스: 밀짚모자 해적단 선의, 인간인간 열매', 300, 'onepiece-chopper', '/images/avatars/onepiece/chopper.png', 'EPIC', 1, 39, NOW(), NOW()),
('AVATAR', '로빈', '원피스: 밀짚모자 해적단 고고학자, 꽃꽃 열매', 300, 'onepiece-robin', '/images/avatars/onepiece/robin.png', 'EPIC', 1, 40, NOW(), NOW()),
('AVATAR', '에이스', '원피스: 불주먹 에이스, 메라메라 열매', 400, 'onepiece-ace', '/images/avatars/onepiece/ace.png', 'LEGENDARY', 1, 41, NOW(), NOW()),
('AVATAR', '샹크스', '원피스: 빨간 머리 사황, 루피의 은인', 500, 'onepiece-shanks', '/images/avatars/onepiece/shanks.png', 'LEGENDARY', 1, 42, NOW(), NOW()),
('AVATAR', '로', '원피스: 트라팔가 로, 수술수술 열매', 350, 'onepiece-law', '/images/avatars/onepiece/law.png', 'EPIC', 1, 43, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    price = VALUES(price),
    preview_url = VALUES(preview_url),
    rarity = VALUES(rarity),
    is_active = VALUES(is_active),
    sort_order = VALUES(sort_order),
    updated_at = NOW();

-- ============================================
-- 체인소맨 아바타 (Chainsaw Man)
-- 이미지 파일 위치: public/images/avatars/chainsawman/
-- ============================================
INSERT INTO shop_items (type, name, description, price, item_value, preview_url, rarity, is_active, sort_order, created_at, updated_at)
VALUES
('AVATAR', '덴지', '체인소맨: 체인소의 악마와 계약한 주인공', 350, 'chainsawman-denji', '/images/avatars/chainsawman/denji.png', 'EPIC', 1, 44, NOW(), NOW()),
('AVATAR', '파워', '체인소맨: 혈액의 마인, 천진난만한 성격', 350, 'chainsawman-power', '/images/avatars/chainsawman/power.png', 'EPIC', 1, 45, NOW(), NOW()),
('AVATAR', '마키마', '체인소맨: 공안 대마 특이 4과 리더, 지배의 악마', 500, 'chainsawman-makima', '/images/avatars/chainsawman/makima.png', 'LEGENDARY', 1, 46, NOW(), NOW()),
('AVATAR', '아키', '체인소맨: 공안 소속 데빌 헌터, 침착한 성격', 300, 'chainsawman-aki', '/images/avatars/chainsawman/aki.png', 'EPIC', 1, 47, NOW(), NOW()),
('AVATAR', '레제', '체인소맨: 폭탄의 악마, 덴지의 첫사랑', 400, 'chainsawman-reze', '/images/avatars/chainsawman/reze.png', 'LEGENDARY', 1, 48, NOW(), NOW()),
('AVATAR', '히메노', '체인소맨: 공안 소속 데빌 헌터, 유령의 악마', 300, 'chainsawman-himeno', '/images/avatars/chainsawman/himeno.png', 'EPIC', 1, 49, NOW(), NOW()),
('AVATAR', '나유타', '체인소맨: 지배의 악마 환생체, 덴지와 동거', 400, 'chainsawman-nayuta', '/images/avatars/chainsawman/nayuta.png', 'LEGENDARY', 1, 50, NOW(), NOW()),
('AVATAR', '코베니', '체인소맨: 공안 소속 신입 데빌 헌터', 300, 'chainsawman-kobeni', '/images/avatars/chainsawman/kobeni.png', 'EPIC', 1, 51, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    price = VALUES(price),
    preview_url = VALUES(preview_url),
    rarity = VALUES(rarity),
    is_active = VALUES(is_active),
    sort_order = VALUES(sort_order),
    updated_at = NOW();

-- ============================================
-- 나루토 아바타 (Naruto)
-- 이미지 파일 위치: public/images/avatars/naruto/
-- ============================================
INSERT INTO shop_items (type, name, description, price, item_value, preview_url, rarity, is_active, sort_order, created_at, updated_at)
VALUES
('AVATAR', '나루토', '나루토: 나선환을 사용하는 7대 호카게', 400, 'naruto-naruto', '/images/avatars/naruto/naruto.png', 'LEGENDARY', 1, 52, NOW(), NOW()),
('AVATAR', '사스케', '나루토: 우치하 일족의 마지막 생존자, 사륜안', 400, 'naruto-sasuke', '/images/avatars/naruto/sasuke.png', 'LEGENDARY', 1, 53, NOW(), NOW()),
('AVATAR', '사쿠라', '나루토: 의료닌자, 츠나데의 제자', 300, 'naruto-sakura', '/images/avatars/naruto/sakura.png', 'EPIC', 1, 54, NOW(), NOW()),
('AVATAR', '카카시', '나루토: 사륜안의 카카시, 7반 담당 상급닌자', 400, 'naruto-kakashi', '/images/avatars/naruto/kakashi.png', 'LEGENDARY', 1, 55, NOW(), NOW()),
('AVATAR', '이타치', '나루토: 우치ha 일족의 천재, 만화경 사륜안', 500, 'naruto-itachi', '/images/avatars/naruto/itachi.png', 'LEGENDARY', 1, 56, NOW(), NOW()),
('AVATAR', '가아라', '나루토: 모래마을의 5대 카제, 수호학의 인주력', 350, 'naruto-gaara', '/images/avatars/naruto/gaara.png', 'EPIC', 1, 57, NOW(), NOW()),
('AVATAR', '히나타', '나루토: 휴우가 일족, 백안의 소유자', 300, 'naruto-hinata', '/images/avatars/naruto/hinata.png', 'EPIC', 1, 58, NOW(), NOW()),
('AVATAR', '미나토', '나루토: 4대 호카게, 나루토의 아버지', 500, 'naruto-minato', '/images/avatars/naruto/minato.png', 'LEGENDARY', 1, 59, NOW(), NOW()),
('AVATAR', '지라이야', '나루토: 전설의 삼닌, 나루토의 스승', 400, 'naruto-jiraiya', '/images/avatars/naruto/jiraiya.png', 'LEGENDARY', 1, 60, NOW(), NOW()),
('AVATAR', '오로치마루', '나루토: 전설의 삼닌, 뱀을 사용하는 닌자', 400, 'naruto-orochimaru', '/images/avatars/naruto/orochimaru.png', 'LEGENDARY', 1, 61, NOW(), NOW()),
('AVATAR', '록 리', '나루토: 체술 전문 닌자, 가이의 제자', 300, 'naruto-rocklee', '/images/avatars/naruto/rocklee.png', 'EPIC', 1, 62, NOW(), NOW()),
('AVATAR', '네지', '나루토: 휴우가 분가, 백안의 천재', 350, 'naruto-neji', '/images/avatars/naruto/neji.png', 'EPIC', 1, 63, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    price = VALUES(price),
    preview_url = VALUES(preview_url),
    rarity = VALUES(rarity),
    is_active = VALUES(is_active),
    sort_order = VALUES(sort_order),
    updated_at = NOW();

-- ============================================
-- 프로필 테두리 (고유 색상)
-- ============================================
INSERT INTO shop_items (type, name, description, price, item_value, preview_url, rarity, is_active, sort_order, created_at, updated_at)
VALUES
('PROFILE_BORDER', '브론즈 테두리', '브론즈 색상의 기본 테두리', 50, '#CD7F32', NULL, 'COMMON', 1, 101, NOW(), NOW()),
('PROFILE_BORDER', '실버 테두리', '은빛으로 빛나는 테두리', 100, '#A8A8A8', NULL, 'RARE', 1, 102, NOW(), NOW()),
('PROFILE_BORDER', '골드 테두리', '황금빛 프리미엄 테두리', 200, '#DAA520', NULL, 'EPIC', 1, 103, NOW(), NOW()),
('PROFILE_BORDER', '플래티넘 테두리', '백금 광택의 테두리', 300, '#E5E4E2', NULL, 'EPIC', 1, 104, NOW(), NOW()),
('PROFILE_BORDER', '다이아몬드 테두리', '다이아몬드처럼 빛나는 테두리', 500, '#B9F2FF', NULL, 'LEGENDARY', 1, 105, NOW(), NOW()),
('PROFILE_BORDER', '루비 테두리', '붉은 루비 테두리', 500, '#E0115F', NULL, 'LEGENDARY', 1, 106, NOW(), NOW()),
('PROFILE_BORDER', '에메랄드 테두리', '초록빛 에메랄드 테두리', 400, '#50C878', NULL, 'LEGENDARY', 1, 107, NOW(), NOW()),
('PROFILE_BORDER', '사파이어 테두리', '푸른 사파이어 테두리', 400, '#0F52BA', NULL, 'LEGENDARY', 1, 108, NOW(), NOW()),
('PROFILE_BORDER', '레인보우 테두리', '무지개빛 특별 테두리', 1000, 'rainbow', NULL, 'LEGENDARY', 1, 109, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    price = VALUES(price),
    preview_url = VALUES(preview_url),
    rarity = VALUES(rarity),
    is_active = VALUES(is_active),
    sort_order = VALUES(sort_order),
    updated_at = NOW();

-- ============================================
-- 뱃지 (이모지 아이콘)
-- ============================================
INSERT INTO shop_items (type, name, description, price, item_value, preview_url, rarity, is_active, sort_order, created_at, updated_at)
VALUES
('BADGE', '새싹', '코딩 여정의 시작', 30, 'sprout', '🌱', 'COMMON', 1, 201, NOW(), NOW()),
('BADGE', '도구함', '문제를 척척 해결하는 사람', 50, 'toolbox', '🧰', 'COMMON', 1, 202, NOW(), NOW()),
('BADGE', '노트북', '코드의 달인', 100, 'laptop', '💻', 'RARE', 1, 203, NOW(), NOW()),
('BADGE', '두뇌', '알고리즘을 정복한 자', 200, 'brain', '🧠', 'EPIC', 1, 204, NOW(), NOW()),
('BADGE', '번개', '빠른 문제 풀이의 달인', 150, 'lightning', '⚡', 'RARE', 1, 205, NOW(), NOW()),
('BADGE', '과녁', '한 번에 정답을 맞추는 사람', 200, 'bullseye', '🎯', 'EPIC', 1, 206, NOW(), NOW()),
('BADGE', '별', '모든 분야에 능통한 사람', 300, 'star', '🌟', 'EPIC', 1, 207, NOW(), NOW()),
('BADGE', '왕관', '코딩계의 전설', 500, 'crown', '👑', 'LEGENDARY', 1, 208, NOW(), NOW()),
('BADGE', '불꽃', '연속 출석의 대가', 150, 'flame', '🔥', 'RARE', 1, 209, NOW(), NOW()),
('BADGE', '달', '밤에 더 빛나는 개발자', 100, 'moon', '🌙', 'RARE', 1, 210, NOW(), NOW()),
('BADGE', '커피', '커피와 함께하는 코딩', 80, 'coffee', '☕', 'COMMON', 1, 211, NOW(), NOW()),
('BADGE', '벌레', '버그를 잡는 사냥꾼', 120, 'bug', '🐛', 'RARE', 1, 212, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    price = VALUES(price),
    preview_url = VALUES(preview_url),
    rarity = VALUES(rarity),
    is_active = VALUES(is_active),
    sort_order = VALUES(sort_order),
    updated_at = NOW();

-- ============================================
-- 칭호 (고유한 칭호들)
-- ============================================
INSERT INTO shop_items (type, name, description, price, item_value, preview_url, rarity, is_active, sort_order, created_at, updated_at)
VALUES
('TITLE', '코딩 입문자', '이제 막 시작한 개발자', 50, '코딩 입문자', NULL, 'COMMON', 1, 301, NOW(), NOW()),
('TITLE', '성장하는 개발자', '꾸준히 발전하는 사람', 100, '성장하는 개발자', NULL, 'COMMON', 1, 302, NOW(), NOW()),
('TITLE', '숙련된 코더', '경험 많은 개발자', 200, '숙련된 코더', NULL, 'RARE', 1, 303, NOW(), NOW()),
('TITLE', '기술 리더', '팀을 이끄는 기술 리더', 300, '기술 리더', NULL, 'EPIC', 1, 304, NOW(), NOW()),
('TITLE', '시스템 설계자', '시스템을 설계하는 사람', 400, '시스템 설계자', NULL, 'EPIC', 1, 305, NOW(), NOW()),
('TITLE', '전설의 개발자', '개발계의 전설', 500, '전설의 개발자', NULL, 'LEGENDARY', 1, 306, NOW(), NOW()),
('TITLE', '알고리즘 정복자', '알고리즘의 달인', 300, '알고리즘 정복자', NULL, 'EPIC', 1, 307, NOW(), NOW()),
('TITLE', '속도의 마법사', '빠르고 정확한 코더', 250, '속도의 마법사', NULL, 'EPIC', 1, 308, NOW(), NOW()),
('TITLE', '버그 사냥꾼', '버그를 처치하는 자', 200, '버그 사냥꾼', NULL, 'RARE', 1, 309, NOW(), NOW()),
('TITLE', '만능 개발자', '프론트와 백엔드 모두 정복', 400, '만능 개발자', NULL, 'EPIC', 1, 310, NOW(), NOW()),
('TITLE', '오픈소스 영웅', '오픈소스에 기여하는 개발자', 300, '오픈소스 영웅', NULL, 'EPIC', 1, 311, NOW(), NOW()),
('TITLE', '초월한 자', '10배의 생산성을 가진 개발자', 1000, '초월한 자', NULL, 'LEGENDARY', 1, 312, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    price = VALUES(price),
    preview_url = VALUES(preview_url),
    rarity = VALUES(rarity),
    is_active = VALUES(is_active),
    sort_order = VALUES(sort_order),
    updated_at = NOW();

-- ============================================
-- 닉네임 색상 (페인트) - 고유한 색상
-- ============================================
INSERT INTO shop_items (type, name, description, price, item_value, preview_url, rarity, is_active, sort_order, created_at, updated_at)
VALUES
('NAME_COLOR', '체리 레드', '열정적인 체리색 닉네임', 80, '#DC143C', NULL, 'COMMON', 1, 401, NOW(), NOW()),
('NAME_COLOR', '선셋 오렌지', '따뜻한 석양색 닉네임', 80, '#FF8C00', NULL, 'COMMON', 1, 402, NOW(), NOW()),
('NAME_COLOR', '레몬 옐로우', '밝은 레몬색 닉네임', 80, '#FFF44F', NULL, 'COMMON', 1, 403, NOW(), NOW()),
('NAME_COLOR', '민트 그린', '상쾌한 민트색 닉네임', 80, '#98FF98', NULL, 'COMMON', 1, 404, NOW(), NOW()),
('NAME_COLOR', '터콰이즈', '시원한 청록색 닉네임', 100, '#40E0D0', NULL, 'RARE', 1, 405, NOW(), NOW()),
('NAME_COLOR', '스카이 블루', '맑은 하늘색 닉네임', 100, '#87CEEB', NULL, 'RARE', 1, 406, NOW(), NOW()),
('NAME_COLOR', '라벤더', '고귀한 라벤더색 닉네임', 120, '#E6E6FA', NULL, 'RARE', 1, 407, NOW(), NOW()),
('NAME_COLOR', '핫 핑크', '화려한 핑크색 닉네임', 100, '#FF69B4', NULL, 'RARE', 1, 408, NOW(), NOW()),
('NAME_COLOR', '선샤인 골드', '빛나는 금빛 닉네임', 200, '#FFD700', NULL, 'EPIC', 1, 409, NOW(), NOW()),
('NAME_COLOR', '문라이트 실버', '달빛 은빛 닉네임', 150, '#C0C0C0', NULL, 'EPIC', 1, 410, NOW(), NOW()),
('NAME_COLOR', '네온 라임', '눈부신 네온 초록', 200, '#32CD32', NULL, 'EPIC', 1, 411, NOW(), NOW()),
('NAME_COLOR', '일렉트릭 퍼플', '전기같은 보라색', 200, '#BF00FF', NULL, 'EPIC', 1, 412, NOW(), NOW()),
('NAME_COLOR', '레인보우', '무지개빛 그라데이션 닉네임', 500, 'rainbow', NULL, 'LEGENDARY', 1, 413, NOW(), NOW()),
('NAME_COLOR', '홀로그램', '홀로그램 효과 닉네임', 500, 'hologram', NULL, 'LEGENDARY', 1, 414, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    price = VALUES(price),
    preview_url = VALUES(preview_url),
    rarity = VALUES(rarity),
    is_active = VALUES(is_active),
    sort_order = VALUES(sort_order),
    updated_at = NOW();

-- ============================================
-- 카드 배경 (그라데이션 및 패턴)
-- ============================================
INSERT INTO shop_items (type, name, description, price, item_value, preview_url, rarity, is_active, sort_order, created_at, updated_at)
VALUES
-- 단색 배경
('CARD_BACKGROUND', '미드나잇 블랙', '깊고 어두운 밤하늘 같은 배경', 50, 'linear-gradient(135deg, #1a1a2e 0%, #16213e 100%)', NULL, 'COMMON', 1, 501, NOW(), NOW()),
('CARD_BACKGROUND', '오션 블루', '깊은 바다처럼 푸른 배경', 50, 'linear-gradient(135deg, #0c3483 0%, #a2b6df 100%)', NULL, 'COMMON', 1, 502, NOW(), NOW()),
('CARD_BACKGROUND', '포레스트 그린', '울창한 숲의 초록빛 배경', 50, 'linear-gradient(135deg, #134e5e 0%, #71b280 100%)', NULL, 'COMMON', 1, 503, NOW(), NOW()),
('CARD_BACKGROUND', '선셋 오렌지', '노을 지는 하늘빛 배경', 50, 'linear-gradient(135deg, #f12711 0%, #f5af19 100%)', NULL, 'COMMON', 1, 504, NOW(), NOW()),

-- 파스텔 그라데이션
('CARD_BACKGROUND', '코튼 캔디', '솜사탕처럼 달콤한 파스텔 배경', 100, 'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)', NULL, 'RARE', 1, 505, NOW(), NOW()),
('CARD_BACKGROUND', '라벤더 드림', '라벤더 꽃밭 같은 몽환적인 배경', 100, 'linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%)', NULL, 'RARE', 1, 506, NOW(), NOW()),
('CARD_BACKGROUND', '민트 브리즈', '상쾌한 민트빛 그라데이션', 100, 'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)', NULL, 'RARE', 1, 507, NOW(), NOW()),
('CARD_BACKGROUND', '피치 블러썸', '복숭아꽃 같은 따뜻한 배경', 100, 'linear-gradient(135deg, #ffecd2 0%, #fcb69f 50%, #ee9ca7 100%)', NULL, 'RARE', 1, 508, NOW(), NOW()),

-- 프리미엄 그라데이션
('CARD_BACKGROUND', '오로라 보레알리스', '북극의 오로라처럼 신비로운 배경', 200, 'linear-gradient(135deg, #00c9ff 0%, #92fe9d 50%, #00c9ff 100%)', NULL, 'EPIC', 1, 509, NOW(), NOW()),
('CARD_BACKGROUND', '갤럭시 퍼플', '우주 은하수 같은 보라빛 배경', 200, 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', NULL, 'EPIC', 1, 510, NOW(), NOW()),
('CARD_BACKGROUND', '네온 시티', '사이버펑크 느낌의 네온 배경', 250, 'linear-gradient(135deg, #f953c6 0%, #b91d73 50%, #00d4ff 100%)', NULL, 'EPIC', 1, 511, NOW(), NOW()),
('CARD_BACKGROUND', '골든 선라이즈', '황금빛 일출 그라데이션', 200, 'linear-gradient(135deg, #f7971e 0%, #ffd200 100%)', NULL, 'EPIC', 1, 512, NOW(), NOW()),
('CARD_BACKGROUND', '로즈 골드', '우아한 로즈골드 배경', 200, 'linear-gradient(135deg, #b76e79 0%, #e8c8c8 100%)', NULL, 'EPIC', 1, 513, NOW(), NOW()),

-- 레전더리 애니메이션 배경 (특수 효과)
('CARD_BACKGROUND', '레인보우 웨이브', '무지개빛이 흐르는 배경', 500, 'linear-gradient(270deg, #ff0000, #ff7f00, #ffff00, #00ff00, #0000ff, #4b0082, #9400d3)', NULL, 'LEGENDARY', 1, 514, NOW(), NOW()),
('CARD_BACKGROUND', '홀로그래픽', '홀로그램처럼 빛나는 배경', 500, 'linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 25%, #f5f7fa 50%, #c3cfe2 75%, #f5f7fa 100%)', NULL, 'LEGENDARY', 1, 515, NOW(), NOW()),
('CARD_BACKGROUND', '다이아몬드 더스트', '다이아몬드 가루가 뿌려진 배경', 600, 'linear-gradient(135deg, #e8e8e8 0%, #ffffff 25%, #e8e8e8 50%, #ffffff 75%, #e8e8e8 100%)', NULL, 'LEGENDARY', 1, 516, NOW(), NOW()),
('CARD_BACKGROUND', '피닉스 플레임', '불사조의 불꽃 같은 배경', 600, 'linear-gradient(135deg, #f12711 0%, #f5af19 25%, #f12711 50%, #f5af19 75%, #f12711 100%)', NULL, 'LEGENDARY', 1, 517, NOW(), NOW()),
('CARD_BACKGROUND', '드래곤 스케일', '용의 비늘처럼 신비로운 배경', 800, 'linear-gradient(135deg, #1a2a6c 0%, #b21f1f 50%, #fdbb2d 100%)', NULL, 'LEGENDARY', 1, 518, NOW(), NOW()),

-- 애니메이션 테마 카드 배경 (LEGENDARY) - 실제 이미지
('CARD_BACKGROUND', '장송의 프리렌', '프리렌과 함께하는 마법의 여정', 1000, '/images/backgrounds/frieren-banner.jpg', NULL, 'LEGENDARY', 1, 519, NOW(), NOW()),
('CARD_BACKGROUND', '귀멸의 칼날', '탄지로와 동료들의 귀살대 여정', 1000, '/images/backgrounds/demon-slayer-banner.jpg', NULL, 'LEGENDARY', 1, 520, NOW(), NOW()),
('CARD_BACKGROUND', 'Re:Zero', '렘과 람, 쌍둥이 메이드의 세계', 1000, '/images/backgrounds/rezero-banner.jpg', NULL, 'LEGENDARY', 1, 521, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    price = VALUES(price),
    preview_url = VALUES(preview_url),
    rarity = VALUES(rarity),
    is_active = VALUES(is_active),
    sort_order = VALUES(sort_order),
    updated_at = NOW();

-- ============================================
-- 닉네임 변경권 (소모성 아이템)
-- ============================================
INSERT INTO shop_items (type, name, description, price, item_value, preview_url, rarity, is_active, sort_order, created_at, updated_at)
VALUES
('NICKNAME_CHANGE', '닉네임 변경권', '닉네임을 변경할 수 있는 티켓 (1회 사용)', 300, 'nickname_change_ticket', NULL, 'RARE', 1, 601, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    price = VALUES(price),
    preview_url = VALUES(preview_url),
    rarity = VALUES(rarity),
    is_active = VALUES(is_active),
    sort_order = VALUES(sort_order),
    updated_at = NOW();
