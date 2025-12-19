-- 특정 아바타를 직접 삭제하는 스크립트
-- 삭제하고 싶은 아바타의 주석을 제거하고 실행하세요

-- ============================================
-- 1. 이름으로 삭제
-- ============================================
-- 예시: '프리렌 - 무표정' 아바타 삭제
-- DELETE FROM shop_items WHERE type = 'AVATAR' AND name = '프리렌 - 무표정';

-- 예시: '루피' 아바타 삭제
-- DELETE FROM shop_items WHERE type = 'AVATAR' AND name = '루피';


-- ============================================
-- 2. item_value로 삭제 (더 정확함)
-- ============================================
-- 예시: frieren-neutral 삭제
-- DELETE FROM shop_items WHERE type = 'AVATAR' AND item_value = 'frieren-neutral';

-- 예시: onepiece-luffy 삭제
-- DELETE FROM shop_items WHERE type = 'AVATAR' AND item_value = 'onepiece-luffy';


-- ============================================
-- 3. ID로 삭제 (가장 정확함)
-- ============================================
-- 먼저 삭제할 아바타의 ID를 확인:
SELECT id, name, item_value, preview_url, price, rarity
FROM shop_items
WHERE type = 'AVATAR'
ORDER BY sort_order;

-- 확인한 ID로 삭제 (예: ID가 100인 아바타)
-- DELETE FROM shop_items WHERE id = 100;


-- ============================================
-- 4. 특정 시리즈 전체 삭제
-- ============================================
-- 원피스 캐릭터 전체 삭제
-- DELETE FROM shop_items WHERE type = 'AVATAR' AND item_value LIKE 'onepiece-%';

-- 체인소맨 캐릭터 전체 삭제
-- DELETE FROM shop_items WHERE type = 'AVATAR' AND item_value LIKE 'chainsawman-%';

-- 나루토 캐릭터 전체 삭제
-- DELETE FROM shop_items WHERE type = 'AVATAR' AND item_value LIKE 'naruto-%';

-- 프리렌 캐릭터 전체 삭제
-- DELETE FROM shop_items WHERE type = 'AVATAR' AND item_value LIKE 'frieren-%';

-- 귀멸의 칼날 캐릭터 전체 삭제
-- DELETE FROM shop_items WHERE type = 'AVATAR' AND item_value LIKE 'kimetsu-%';

-- Re:Zero 캐릭터 전체 삭제
-- DELETE FROM shop_items WHERE type = 'AVATAR' AND item_value LIKE 'rezero-%';


-- ============================================
-- 5. DiceBear 스타일 아바타만 삭제
-- ============================================
-- preview_url이 NULL인 것들 (DiceBear 아바타)
-- DELETE FROM shop_items
-- WHERE type = 'AVATAR'
--   AND (preview_url IS NULL OR preview_url = '');


-- ============================================
-- 6. 현재 아바타 목록 확인
-- ============================================
SELECT
    id,
    name,
    item_value,
    CASE
        WHEN preview_url IS NULL OR preview_url = '' THEN 'DiceBear'
        ELSE preview_url
    END as image_source,
    price,
    rarity,
    sort_order
FROM shop_items
WHERE type = 'AVATAR'
ORDER BY sort_order;


-- ============================================
-- 7. 중복 확인 (같은 이름)
-- ============================================
SELECT
    name,
    COUNT(*) as count,
    GROUP_CONCAT(id ORDER BY id) as ids
FROM shop_items
WHERE type = 'AVATAR'
GROUP BY name
HAVING COUNT(*) > 1;
