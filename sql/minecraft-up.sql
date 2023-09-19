-- CREATE DATABASE "minecraft" ENCODING 'UTF8' LC_COLLATE 'Slovenian_Slovenia.UTF8' LC_CTYPE 'Slovenian_Slovenia.UTF8';
CREATE DATABASE minecraft;
\c minecraft

-- for storing user data
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(128) NOT NULL,
    email VARCHAR(128) NOT NULL,
    name VARCHAR(128) NOT NULL,
    surname VARCHAR(128) NOT NULL,
    description TEXT,
    registered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmed BOOLEAN NOT NULL DEFAULT FALSE
);

-- for storing user's home location
CREATE TABLE home_locations (
    user_id INTEGER NOT NULL,
    x FLOAT NOT NULL,
    y FLOAT NOT NULL,
    z FLOAT NOT NULL,
    PRIMARY KEY (user_id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- for storing session data
CREATE TABLE log_session (
    id SERIAL PRIMARY KEY,
    user_id INTEGER,
    join_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    leave_time TIMESTAMP,
    leave_reason TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- for storing commands used in a session
CREATE TABLE log_command (
    id SERIAL PRIMARY KEY,
    session_id INTEGER NOT NULL,
    command TEXT NOT NULL,
    FOREIGN KEY (session_id) REFERENCES log_session(id)
);
