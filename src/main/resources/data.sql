-- Insert Membership Tiers
INSERT INTO membership_tier (name) VALUES ('SILVER');
INSERT INTO membership_tier (name) VALUES ('GOLD');
INSERT INTO membership_tier (name) VALUES ('PLATINUM');

-- Insert Membership Plans with prices
INSERT INTO membership_plan (name, price) VALUES ('MONTHLY', 19.99);
INSERT INTO membership_plan (name, price) VALUES ('QUARTERLY', 49.99);
INSERT INTO membership_plan (name, price) VALUES ('YEARLY', 149.99);

-- Insert sample users
INSERT INTO app_users (name) VALUES ('Demo User 1');
INSERT INTO app_users (name) VALUES ('Demo User 2')