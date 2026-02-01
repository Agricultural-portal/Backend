-- Update all users with NULL or 0 wallet balance to default 10000
-- Run this script manually in MySQL or through your database client

USE farm_connect; -- Replace with your actual database name if different

-- Update users with NULL money field
UPDATE users 
SET money = 10000.00 
WHERE money IS NULL;

-- Update users with 0 balance (optional, if you want to give existing users with 0 the default)
UPDATE users 
SET money = 10000.00 
WHERE money = 0.00;

-- Verify the update
SELECT 
    id, 
    first_name, 
    last_name, 
    email, 
    role, 
    money 
FROM users 
ORDER BY id;
