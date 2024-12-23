-- ### Seed 'users' ###

-- # Aggregate - 8d031fe3-e445-4d51-8c70-ac3e3810da87

INSERT INTO users (id, username, password, display_name, role, identity_provider, locked)
VALUES ('8d031fe3-e445-4d51-8c70-ac3e3810da87', 'maxi123', '$2y$10$xHTfzAglrIZISiZf3vL8yeBP.1C9aBHlvof2kRoEF7YMV0YjdR75G', 'Maximilian Mustermann', 'ADMINISTRATOR', 'LOCAL', false);

-- # Aggregate - 6aa18950-81e0-4ac4-ad3a-37437db5c957

INSERT INTO users (id, username, password, display_name, role, identity_provider, locked)
VALUES ('6aa18950-81e0-4ac4-ad3a-37437db5c957', 'erika123', '$2y$10$xHTfzAglrIZISiZf3vL8yeBP.1C9aBHlvof2kRoEF7YMV0YjdR75G', 'Erika Musterfrau', 'USER', 'LOCAL', false);

-- ### Seed 'boards' ###

-- # Aggregate - 527c3e1e-6b2d-4887-a747-9dfb7cb3bb1e

INSERT INTO boards (id, name, description)
VALUES ('527c3e1e-6b2d-4887-a747-9dfb7cb3bb1e', 'Kanban #1', 'Kanban Board #1');

-- # Aggregate - 0b79570d-0544-4545-96a8-8eac2c012526

INSERT INTO boards (id, name, description)
VALUES ('0b79570d-0544-4545-96a8-8eac2c012526', 'Kanban #2', 'Kanban Board #2');

-- ### Seed 'members' ###

-- # Aggregate - 99bb36fb-0f5b-458a-8176-cc9d49faea3d

INSERT INTO members (id, user_id, board_id, role)
VALUES ('99bb36fb-0f5b-458a-8176-cc9d49faea3d', '8d031fe3-e445-4d51-8c70-ac3e3810da87', '527c3e1e-6b2d-4887-a747-9dfb7cb3bb1e', 'ADMINISTRATOR');

-- # Aggregate - 13d65e6a-daae-4fdb-83ab-4ff398cd2300

INSERT INTO members (id, user_id, board_id, role)
VALUES ('13d65e6a-daae-4fdb-83ab-4ff398cd2300', '6aa18950-81e0-4ac4-ad3a-37437db5c957', '527c3e1e-6b2d-4887-a747-9dfb7cb3bb1e', 'MEMBER');

-- # Aggregate - e9fe1f44-33f4-400f-9c0e-a73b8f12668d

INSERT INTO members (id, user_id, board_id, role)
VALUES ('e9fe1f44-33f4-400f-9c0e-a73b8f12668d', '6aa18950-81e0-4ac4-ad3a-37437db5c957', '0b79570d-0544-4545-96a8-8eac2c012526', 'ADMINISTRATOR');
