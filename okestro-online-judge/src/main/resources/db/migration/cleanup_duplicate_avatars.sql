-- 중복 아바타 정리 스크립트
-- 실행 전 반드시 백업 권장!

-- 1단계: 중복된 아바타 확인 (name 기준)
-- 같은 이름을 가진 아바타가 여러 개 있는지 확인
SELECT name, COUNT(*) as count
FROM shop_items
WHERE type = 'AVATAR'
GROUP BY name
HAVING COUNT(*) > 1
ORDER BY count DESC;

-- 2단계: 중복된 아바타 확인 (item_value 기준)
-- item_value는 UNIQUE이므로 중복이 없어야 하지만 확인
SELECT item_value, COUNT(*) as count
FROM shop_items
WHERE type = 'AVATAR'
GROUP BY item_value
HAVING COUNT(*) > 1;

-- 3단계: 중복된 아바타의 상세 정보 확인
SELECT id, name, item_value, preview_url, rarity, price, sort_order, created_at
FROM shop_items
WHERE type = 'AVATAR'
  AND name IN (
    SELECT name
    FROM shop_items
    WHERE type = 'AVATAR'
    GROUP BY name
    HAVING COUNT(*) > 1
  )
ORDER BY name, id;

-- 4단계: 각 중복 그룹에서 가장 최근에 생성된 것만 남기고 나머지 삭제
-- (주의: 실제 삭제 전 위의 SELECT로 확인 후 실행!)
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
                AND s2.id > s1.id  -- 더 최근 ID를 남기고 이전 것 삭제
          )
    ) AS duplicates
);

-- 5단계: 정리 후 아바타 목록 확인
SELECT id, name, item_value, preview_url, rarity, price, sort_order
FROM shop_items
WHERE type = 'AVATAR'
ORDER BY sort_order;

-- 6단계: sort_order 재정렬 (선택사항)
-- 중복 삭제 후 sort_order가 비연속적일 수 있으므로 재정렬
SET @new_order = 0;
UPDATE shop_items
SET sort_order = (@new_order := @new_order + 1)
WHERE type = 'AVATAR'
ORDER BY sort_order;
