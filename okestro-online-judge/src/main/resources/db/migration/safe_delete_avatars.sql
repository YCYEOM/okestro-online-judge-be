-- Foreign Key 제약 조건을 고려한 안전한 아바타 삭제 스크립트
-- user_inventory에서 참조를 먼저 제거한 후 shop_items를 삭제합니다

-- ============================================
-- 1. 삭제 전 영향 받는 사용자 확인
-- ============================================

-- 중복된 아바타를 보유/장착한 사용자 확인
SELECT
    ui.user_id,
    ui.shop_item_id,
    si.name,
    si.item_value,
    ui.is_equipped,
    ui.purchased_at
FROM user_inventory ui
JOIN shop_items si ON ui.shop_item_id = si.id
WHERE si.type = 'AVATAR'
  AND si.name IN (
    SELECT name
    FROM shop_items
    WHERE type = 'AVATAR'
    GROUP BY name
    HAVING COUNT(*) > 1
  )
ORDER BY si.name, ui.user_id;


-- ============================================
-- 2. 특정 아바타 안전 삭제 (예시)
-- ============================================

-- 방법 1: 특정 ID의 아바타 삭제
-- 1단계: user_inventory에서 먼저 삭제
-- DELETE FROM user_inventory WHERE shop_item_id = 100;
-- 2단계: shop_items에서 삭제
-- DELETE FROM shop_items WHERE id = 100;

-- 방법 2: 특정 item_value의 아바타 삭제
-- 1단계: 해당 shop_item_id 찾기
-- SELECT id FROM shop_items WHERE item_value = 'onepiece-luffy';
-- 2단계: user_inventory에서 먼저 삭제
-- DELETE FROM user_inventory WHERE shop_item_id IN (SELECT id FROM shop_items WHERE item_value = 'onepiece-luffy');
-- 3단계: shop_items에서 삭제
-- DELETE FROM shop_items WHERE item_value = 'onepiece-luffy';


-- ============================================
-- 3. 중복 아바타 자동 정리 (안전 버전)
-- ============================================

-- 중복된 아바타 중 오래된 것들의 ID 확인
SELECT s1.id, s1.name, s1.item_value, s1.created_at
FROM shop_items s1
WHERE s1.type = 'AVATAR'
  AND EXISTS (
      SELECT 1
      FROM shop_items s2
      WHERE s2.type = 'AVATAR'
        AND s2.name = s1.name
        AND s2.id > s1.id  -- 더 최근 것이 있는 경우
  )
ORDER BY s1.name;

-- 위에서 확인한 중복 아바타들을 안전하게 삭제
-- 1단계: user_inventory에서 참조 제거
DELETE FROM user_inventory
WHERE shop_item_id IN (
    SELECT id FROM (
        SELECT s1.id
        FROM shop_items s1
        WHERE s1.type = 'AVATAR'
          AND EXISTS (
              SELECT 1
              FROM shop_items s2
              WHERE s2.type = 'AVATAR'
                AND s2.name = s1.name
                AND s2.id > s1.id
          )
    ) AS old_items
);

-- 2단계: shop_items에서 중복 삭제
DELETE FROM shop_items
WHERE id IN (
    SELECT id FROM (
        SELECT s1.id
        FROM shop_items s1
        WHERE s1.type = 'AVATAR'
          AND EXISTS (
              SELECT 1
              FROM shop_items s2
              WHERE s2.type = 'AVATAR'
                AND s2.name = s1.name
                AND s2.id > s1.id
          )
    ) AS old_items
);


-- ============================================
-- 4. 특정 시리즈 전체 안전 삭제
-- ============================================

-- 원피스 캐릭터 전체 삭제 (2단계)
-- 1단계: user_inventory에서 참조 제거
-- DELETE FROM user_inventory
-- WHERE shop_item_id IN (
--     SELECT id FROM shop_items
--     WHERE type = 'AVATAR' AND item_value LIKE 'onepiece-%'
-- );
-- 2단계: shop_items에서 삭제
-- DELETE FROM shop_items WHERE type = 'AVATAR' AND item_value LIKE 'onepiece-%';

-- 체인소맨 캐릭터 전체 삭제
-- DELETE FROM user_inventory WHERE shop_item_id IN (SELECT id FROM shop_items WHERE type = 'AVATAR' AND item_value LIKE 'chainsawman-%');
-- DELETE FROM shop_items WHERE type = 'AVATAR' AND item_value LIKE 'chainsawman-%';

-- 나루토 캐릭터 전체 삭제
-- DELETE FROM user_inventory WHERE shop_item_id IN (SELECT id FROM shop_items WHERE type = 'AVATAR' AND item_value LIKE 'naruto-%');
-- DELETE FROM shop_items WHERE type = 'AVATAR' AND item_value LIKE 'naruto-%';


-- ============================================
-- 5. DiceBear 스타일 아바타만 안전 삭제
-- ============================================

-- 1단계: 삭제 대상 확인
SELECT id, name, item_value, price, rarity
FROM shop_items
WHERE type = 'AVATAR'
  AND (preview_url IS NULL OR preview_url = '')
ORDER BY sort_order;

-- 2단계: user_inventory에서 참조 제거
-- DELETE FROM user_inventory
-- WHERE shop_item_id IN (
--     SELECT id FROM shop_items
--     WHERE type = 'AVATAR'
--       AND (preview_url IS NULL OR preview_url = '')
-- );

-- 3단계: shop_items에서 삭제
-- DELETE FROM shop_items
-- WHERE type = 'AVATAR'
--   AND (preview_url IS NULL OR preview_url = '');


-- ============================================
-- 6. 삭제 후 확인
-- ============================================

-- 남아있는 아바타 목록
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

-- 중복 확인
SELECT name, COUNT(*) as count
FROM shop_items
WHERE type = 'AVATAR'
GROUP BY name
HAVING COUNT(*) > 1;


-- ============================================
-- 7. sort_order 재정렬
-- ============================================

SET @new_order = 0;
UPDATE shop_items
SET sort_order = (@new_order := @new_order + 1)
WHERE type = 'AVATAR'
ORDER BY sort_order;


-- ============================================
-- 8. 특정 사용자의 인벤토리 확인
-- ============================================

-- 특정 사용자(예: user_id=1)의 아바타 확인
-- SELECT
--     ui.id,
--     ui.shop_item_id,
--     si.name,
--     si.item_value,
--     ui.is_equipped,
--     ui.purchased_at
-- FROM user_inventory ui
-- JOIN shop_items si ON ui.shop_item_id = si.id
-- WHERE ui.user_id = 1
--   AND si.type = 'AVATAR'
-- ORDER BY ui.purchased_at DESC;
