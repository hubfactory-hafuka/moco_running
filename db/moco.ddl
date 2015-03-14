DROP TABLE mst_voice IF EXISTS;
DROP TABLE mst_girl IF EXISTS;

/**********************************/
/* テーブル名: ガールマスタ */
/**********************************/
CREATE TABLE mst_girl(
		girl_id INT NOT NULL,
		name VARCHAR(16),
		upd_datetime DATETIME NOT NULL,
		ins_datetime DATETIME NOT NULL
);

/**********************************/
/* テーブル名: 音声マスタ */
/**********************************/
CREATE TABLE mst_voice(
		girl_id INT NOT NULL,
		voice_id INT NOT NULL,
		word VARCHAR(255),
		upd_datetime DATETIME NOT NULL,
		ins_datetime DATETIME NOT NULL
);


ALTER TABLE mst_girl ADD CONSTRAINT IDX_mst_girl_PK PRIMARY KEY (girl_id);

ALTER TABLE mst_voice ADD CONSTRAINT IDX_mst_voice_PK PRIMARY KEY (girl_id, voice_id);

