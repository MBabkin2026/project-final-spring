CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL
);

CREATE TABLE tasks (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       description TEXT,
                       deadline DATE NOT NULL,
                       status VARCHAR(20) NOT NULL,
                       user_id BIGINT NOT NULL,
                       CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id)
);