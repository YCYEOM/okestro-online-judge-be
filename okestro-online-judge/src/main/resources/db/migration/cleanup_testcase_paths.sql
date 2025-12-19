-- Cleanup script for test_case table
-- This script sets input_path and output_path to NULL when the actual data is in DB columns
-- Run this manually if needed to clean up invalid MinIO path references

-- Update test cases where input_path exists but input column has data
UPDATE test_case
SET input_path = NULL
WHERE input_path IS NOT NULL
  AND input_path != ''
  AND input IS NOT NULL
  AND input != '';

-- Update test cases where output_path exists but output column has data
UPDATE test_case
SET output_path = NULL
WHERE output_path IS NOT NULL
  AND output_path != ''
  AND output IS NOT NULL
  AND output != '';

-- Show summary of affected rows
SELECT
    COUNT(*) as total_test_cases,
    COUNT(CASE WHEN input_path IS NOT NULL THEN 1 END) as has_input_path,
    COUNT(CASE WHEN output_path IS NOT NULL THEN 1 END) as has_output_path,
    COUNT(CASE WHEN input IS NOT NULL THEN 1 END) as has_input_data,
    COUNT(CASE WHEN output IS NOT NULL THEN 1 END) as has_output_data
FROM test_case;
