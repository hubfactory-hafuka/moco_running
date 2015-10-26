SET SESSION FOREIGN_KEY_CHECKS=0;

/* Drop Tables */

DROP TABLE IF EXISTS mst_config;
DROP TABLE IF EXISTS mst_girl;
DROP TABLE IF EXISTS mst_girl_mission;
DROP TABLE IF EXISTS mst_information;
DROP TABLE IF EXISTS mst_login_bonus;
DROP TABLE IF EXISTS mst_ranking;
DROP TABLE IF EXISTS mst_ranking_reward;
DROP TABLE IF EXISTS mst_voice;
DROP TABLE IF EXISTS mst_voice_set;
DROP TABLE IF EXISTS mst_voice_set_detail;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS user_activity;
DROP TABLE IF EXISTS user_activity_detail;
DROP TABLE IF EXISTS user_girl;
DROP TABLE IF EXISTS user_girl_voice;
DROP TABLE IF EXISTS user_goal;
DROP TABLE IF EXISTS user_purchase_history;
DROP TABLE IF EXISTS user_ranking_point;
DROP TABLE IF EXISTS user_takeover;




/* Create Tables */

CREATE TABLE mst_config
(
	name varchar(255) NOT NULL,
	start_datetime datetime,
	end_datetime datetime,
	upd_datetime datetime NOT NULL,
	ins_datetime datetime NOT NULL,
	PRIMARY KEY (name)
);


CREATE TABLE mst_girl
(
	girl_id int NOT NULL,
	type int NOT NULL,
	name varchar(32),
	age int,
	height int,
	weight int,
	hobby varchar(255),
	profile varchar(255),
	cv varchar(255),
	price int NOT NULL,
	point bigint,
	comment varchar(128),
	start_datetime datetime,
	end_datetime datetime,
	upd_datetime datetime NOT NULL,
	ins_datetime datetime NOT NULL,
	PRIMARY KEY (girl_id)
);


CREATE TABLE mst_girl_mission
(
	girl_id int NOT NULL,
	voice_id int NOT NULL,
	distance double(8,2) NOT NULL,
	description varchar(255),
	upd_datetime datetime NOT NULL,
	ins_datetime datetime NOT NULL,
	PRIMARY KEY (girl_id, voice_id)
);


CREATE TABLE mst_information
(
	id int NOT NULL,
	title varchar(255) NOT NULL,
	text varchar(255) NOT NULL,
	start_datetime datetime NOT NULL,
	end_datetime datetime NOT NULL,
	upd_datetime datetime NOT NULL,
	ins_datetime datetime NOT NULL,
	PRIMARY KEY (id)
);


CREATE TABLE mst_login_bonus
(
	week int NOT NULL,
	point bigint NOT NULL,
	start_datetime datetime,
	end_datetime datetime,
	upd_datetime datetime NOT NULL,
	ins_datetime datetime NOT NULL,
	PRIMARY KEY (week)
);


CREATE TABLE mst_ranking
(
	type int NOT NULL,
	view_num int NOT NULL,
	start_datetime datetime,
	end_datetime datetime,
	PRIMARY KEY (type)
);


CREATE TABLE mst_ranking_reward
(
	id int NOT NULL,
	from_rank int NOT NULL,
	to_rank int NOT NULL,
	point bigint NOT NULL,
	upd_datetime datetime NOT NULL,
	ins_datetime datetime NOT NULL,
	PRIMARY KEY (id)
);


CREATE TABLE mst_voice
(
	girl_id int NOT NULL,
	voice_id int NOT NULL,
	word varchar(255),
	situation int NOT NULL,
	type int NOT NULL,
	upd_datetime datetime NOT NULL,
	ins_datetime datetime NOT NULL,
	PRIMARY KEY (girl_id, voice_id)
);


CREATE TABLE mst_voice_set
(
	set_id int NOT NULL,
	girl_id int NOT NULL,
	price int NOT NULL,
	point bigint,
	upd_datetime datetime NOT NULL,
	ins_datetime datetime NOT NULL,
	PRIMARY KEY (set_id, girl_id)
);


CREATE TABLE mst_voice_set_detail
(
	set_id int NOT NULL,
	girl_id int NOT NULL,
	voice_id int NOT NULL,
	upd_datetime datetime NOT NULL,
	ins_datetime datetime NOT NULL,
	PRIMARY KEY (set_id, girl_id, voice_id)
);


CREATE TABLE user
(
	user_id bigint NOT NULL,
	token varchar(255),
	name varchar(255),
	total_distance double(8,2) DEFAULT 0.00,
	total_count int DEFAULT 0 NOT NULL,
	total_avg_time varchar(8),
	girl_id int,
	mail_address varchar(255),
	height double(4,1),
	weight double(4,1),
	point bigint DEFAULT 0,
	login_bonus_datetime datetime,
	image_data text,
	prof_img_path varchar(255),
	upd_datetime datetime NOT NULL,
	ins_datetime datetime NOT NULL,
	PRIMARY KEY (user_id)
);


CREATE TABLE user_activity
(
	user_id bigint NOT NULL,
	activity_id int NOT NULL,
	girl_id int NOT NULL,
	run_date datetime NOT NULL,
	distance double(8,2) DEFAULT 0.00 NOT NULL,
	time varchar(8) NOT NULL,
	avg_time varchar(8) NOT NULL,
	calories int,
	locations text,
	upd_datetime datetime NOT NULL,
	ins_datetime datetime NOT NULL,
	PRIMARY KEY (user_id, activity_id)
);


CREATE TABLE user_activity_detail
(
	user_id bigint NOT NULL,
	activity_id int NOT NULL,
	detail_id int NOT NULL,
	distance int NOT NULL,
	time_elapsed varchar(8) NOT NULL,
	lap_time varchar(8) NOT NULL,
	inc_dec_time varchar(8),
	upd_datetime datetime NOT NULL,
	ins_datetime datetime NOT NULL,
	PRIMARY KEY (user_id, activity_id, detail_id)
);


CREATE TABLE user_girl
(
	user_id bigint NOT NULL,
	girl_id int NOT NULL,
	distance double(8,2) DEFAULT 0.00,
	upd_datetime datetime NOT NULL,
	ins_datetime datetime NOT NULL,
	PRIMARY KEY (user_id, girl_id)
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


CREATE TABLE user_goal
(
	user_id bigint NOT NULL,
	distance int NOT NULL,
	time int NOT NULL,
	upd_datetime datetime NOT NULL,
	ins_datetime datetime NOT NULL,
	PRIMARY KEY (user_id, distance)
);


CREATE TABLE user_purchase_history
(
	user_id bigint NOT NULL,
	type int NOT NULL,
	item_id int NOT NULL,
	upd_datetime datetime NOT NULL,
	ins_datetime datetime NOT NULL,
	PRIMARY KEY (user_id, type, item_id)
);


CREATE TABLE user_ranking_point
(
	user_id bigint NOT NULL,
	ranking_date varchar(8) NOT NULL,
	rank bigint NOT NULL,
	point bigint NOT NULL,
	upd_datetime datetime NOT NULL,
	ins_datetime datetime NOT NULL,
	PRIMARY KEY (user_id)
);


CREATE TABLE user_takeover
(
	user_id bigint NOT NULL,
	takeover_code varchar(16),
	upd_datetime datetime NOT NULL,
	ins_datetime datetime NOT NULL,
	PRIMARY KEY (user_id)
);



