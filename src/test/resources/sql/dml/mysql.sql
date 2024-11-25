-- ### Seed 'users' ###

-- # Aggregate - 8d031fe3-e445-4d51-8c70-ac3e3810da87

INSERT INTO users (id, username, password, display_name, role, locked)
VALUES ('8d031fe3-e445-4d51-8c70-ac3e3810da87', 'maxi123', '$2y$10$xHTfzAglrIZISiZf3vL8yeBP.1C9aBHlvof2kRoEF7YMV0YjdR75G', 'Maximilian Mustermann', 'ADMINISTRATOR', false);

-- # Aggregate - 6aa18950-81e0-4ac4-ad3a-37437db5c957

INSERT INTO users (id, username, password, display_name, role, locked)
VALUES ('6aa18950-81e0-4ac4-ad3a-37437db5c957', 'erika123', '$2y$10$xHTfzAglrIZISiZf3vL8yeBP.1C9aBHlvof2kRoEF7YMV0YjdR75G', 'Erika Musterfrau', 'USER', false);
