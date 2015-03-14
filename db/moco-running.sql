SET SESSION FOREIGN_KEY_CHECKS=0;

/* Drop Indexes */

DROP INDEX USER_ACCOUNT_IDX_EMAIL_PASSWORD ON user;
DROP INDEX idx_set_id ON mst_voice_set;



/* Drop Tables */

DROP TABLE user;
DROP TABLE mst_girl;
DROP TABLE mst_voice;
DROP TABLE mst_girl_mission;
DROP TABLE user_girl;
DROP TABLE user_activity;
DROP TABLE user_girl_voice;
DROP TABLE user_activity_detail;
DROP TABLE mst_voice_set;




/* Create Tables */

CREATE TABLE user
(
    user_id bigint NOT NULL,
    email varchar(255) NOT NULL,
    password varchar(255),
    name varchar(255),
    total_distance double(5,2),
    girl_id int,
    upd_datetime datetime NOT NULL,
    ins_datetime datetime NOT NULL,
    PRIMARY KEY (user_id)
);


CREATE TABLE mst_girl
(
    girl_id int NOT NULL,
    name varchar(32),
    birthday date,
    age int,
    height int,
    weight int,
    bust int,
    waist int,
    hip int,
    hobby varchar(255),
    profile varchar(255),
    image_id varchar(255),
    cv varchar(255),
    upd_datetime datetime NOT NULL,
    ins_datetime datetime NOT NULL,
    PRIMARY KEY (girl_id)
);


CREATE TABLE mst_voice
(
    girl_id int NOT NULL,
    voice_id int NOT NULL,
    word varchar(255),
    type int NOT NULL,
    upd_datetime datetime NOT NULL,
    ins_datetime datetime NOT NULL,
    PRIMARY KEY (girl_id, voice_id)
);


CREATE TABLE mst_girl_mission
(
    girl_id int NOT NULL,
    distance int NOT NULL,
    reward_voice_id int NOT NULL,
    description varchar(255),
    upd_datetime datetime NOT NULL,
    ins_datetime datetime NOT NULL,
    PRIMARY KEY (girl_id, distance, reward_voice_id)
);


CREATE TABLE user_girl
(
    user_id bigint NOT NULL,
    girl_id int NOT NULL,
    distance double(5,2),
    upd_datetime datetime NOT NULL,
    ins_datetime datetime NOT NULL,
    PRIMARY KEY (user_id, girl_id)
);


CREATE TABLE user_activity
(
    user_id bigint NOT NULL,
    activity_id int NOT NULL,
    run_date datetime NOT NULL,
    distance double(5,2) NOT NULL,
    time time NOT NULL,
    avg_time time NOT NULL,
    locations text NOT NULL,
    upd_datetime datetime NOT NULL,
    ins_datetime datetime NOT NULL,
    PRIMARY KEY (user_id, activity_id)
);


CREATE TABLE user_girl_voice
(
    user_id bigint NOT NULL,
    girl_id int NOT NULL,
    voice_id int NOT NULL,
    status int,
    upd_datetime datetime NOT NULL,
    ins_datetime datetime NOT NULL,
    PRIMARY KEY (user_id, girl_id, voice_id)
);


CREATE TABLE user_activity_detail
(
    user_id bigint NOT NULL,
    activity_id int NOT NULL,
    detail_id int NOT NULL,
    distance int NOT NULL,
    time_elapsed time NOT NULL,
    lap_time time NOT NULL,
    inc_dec time,
    upd_datetime datetime NOT NULL,
    ins_datetime datetime NOT NULL,
    PRIMARY KEY (user_id, activity_id, detail_id)
);


CREATE TABLE mst_voice_set
(
    girl_id int NOT NULL,
    voice_id int NOT NULL,
    set_id int NOT NULL,
    upd_datetime datetime NOT NULL,
    ins_datetime datetime NOT NULL,
    PRIMARY KEY (girl_id, voice_id)
);



/* Create Indexes */

CREATE INDEX USER_ACCOUNT_IDX_EMAIL_PASSWORD ON user (email ASC, password ASC);
CREATE INDEX idx_set_id ON mst_voice_set (set_id ASC);



