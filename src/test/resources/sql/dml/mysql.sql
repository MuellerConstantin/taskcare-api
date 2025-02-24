-- ### Seed 'users' ###

-- # Aggregate - 8d031fe3-e445-4d51-8c70-ac3e3810da87

INSERT INTO users (id, username, password, display_name, role, identity_provider, locked)
VALUES ('8d031fe3-e445-4d51-8c70-ac3e3810da87', 'maxi123', '$2y$10$xHTfzAglrIZISiZf3vL8yeBP.1C9aBHlvof2kRoEF7YMV0YjdR75G', 'Maximilian Mustermann', 'ADMINISTRATOR', 'LOCAL', false);

-- # Aggregate - 6aa18950-81e0-4ac4-ad3a-37437db5c957

INSERT INTO users (id, username, password, display_name, role, identity_provider, locked)
VALUES ('6aa18950-81e0-4ac4-ad3a-37437db5c957', 'erika123', '$2y$10$xHTfzAglrIZISiZf3vL8yeBP.1C9aBHlvof2kRoEF7YMV0YjdR75G', 'Erika Musterfrau', 'USER', 'LOCAL', false);

-- # Aggregate - 6aa18950-81e0-4ac4-ad3a-37437db5c957

INSERT INTO users (id, username, password, display_name, role, identity_provider, locked)
VALUES ('7c559b1c-82b6-4e91-8e8e-c637c0bbda14', 'john123', '$2y$10$xHTfzAglrIZISiZf3vL8yeBP.1C9aBHlvof2kRoEF7YMV0YjdR75G', 'John Doe', 'USER', 'LOCAL', false);

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

-- ### Seed 'statuses' ###

-- # Aggregate - 09271e9a-c145-4a6b-92e4-2bc0d5a0710a

INSERT INTO statuses (id, board_id, name, description, category)
VALUES ('09271e9a-c145-4a6b-92e4-2bc0d5a0710a', '527c3e1e-6b2d-4887-a747-9dfb7cb3bb1e', 'To Do', 'Task has not been started yet.', 'TO_DO');

-- # Aggregate - 60d8b592-6916-4ac5-ab1c-1183577d5fc4

INSERT INTO statuses (id, board_id, name, description, category)
VALUES ('60d8b592-6916-4ac5-ab1c-1183577d5fc4', '527c3e1e-6b2d-4887-a747-9dfb7cb3bb1e', 'In Progress', 'Task is currently being worked on.', 'IN_PROGRESS');

-- # Aggregate - 9e3baa5e-0f1c-4ad1-b3d9-19f48e02c0a2

INSERT INTO statuses (id, board_id, name, description, category)
VALUES ('9e3baa5e-0f1c-4ad1-b3d9-19f48e02c0a2', '527c3e1e-6b2d-4887-a747-9dfb7cb3bb1e', 'Done', 'Task has been completed.', 'DONE');

-- ### Seed 'components' ###

-- # Aggregate - 0ef48e1e-d77d-4335-8fd1-425d1b669014

INSERT INTO components (id, board_id, name, description)
VALUES ('0ef48e1e-d77d-4335-8fd1-425d1b669014', '527c3e1e-6b2d-4887-a747-9dfb7cb3bb1e', 'Frontend', 'Frontend Tasks');

-- # Aggregate - 109aae63-2830-4255-b3e9-49bc0efe6b2f

INSERT INTO components (id, board_id, name, description)
VALUES ('109aae63-2830-4255-b3e9-49bc0efe6b2f', '527c3e1e-6b2d-4887-a747-9dfb7cb3bb1e', 'Backend', 'Backend Tasks');
