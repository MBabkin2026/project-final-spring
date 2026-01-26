INSERT INTO users (username, email, password, role)
VALUES ('john_doe', 'john@example.com', 'password123', 'USER'),
       ('admin', 'admin@example.com', 'admin123', 'ADMIN');


INSERT INTO tasks (title, description, deadline, status, user_id)
VALUES ('Complete homework', 'Finish math and science homework', '2023-12-01', 'PENDING', 1),
       ('Fix server', 'Resolve critical issue on production server', '2023-11-25', 'IN_PROGRESS', 2);