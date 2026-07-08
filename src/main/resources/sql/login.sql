DROP TABLE IF EXISTS Users;
CREATE TABLE IF NOT EXISTS Users (
  `username` VARCHAR(64) PRIMARY KEY NOT NULL,
  `hash_password` CHAR(32) NOT NULL, --hash md5
  `diary_list_path` VARCHAR(256)
);


-- utenti predefiniti. password: 1234
INSERT INTO Users (username, hash_password, diary_list_path)
SELECT 'marco', '81dc9bdb52d04dc20036dbd8313ed055', '/home/giacomo-framework/IdeaProjects/terajournal/diaryLists/marco.jm'
WHERE NOT EXISTS (SELECT 1 FROM Users WHERE username = 'marco');

INSERT INTO Users (username, hash_password, diary_list_path)
SELECT 'giulia', '81dc9bdb52d04dc20036dbd8313ed055', '/home/giacomo-framework/IdeaProjects/terajournal/diaryLists/giulia.jm'
WHERE NOT EXISTS (SELECT 1 FROM Users WHERE username = 'giulia');


ALTER USER sa SET PASSWORD 'LaPassword';
