-- user: melion, password: a-z, host: localhost, db: melion

use melion;

DROP TABLE melion.friends;
DROP TABLE melion.point;
DROP TABLE melion.picture_rating;
DROP TABLE melion.picture;
DROP TABLE melion.achievement_unlocked;
DROP TABLE melion.achievement;
DROP TABLE melion.user;
DROP TABLE melion.teamg;

CREATE TABLE `teamg` (
	`id` int(10) unsigned NOT NULL AUTO_INCREMENT,
	`name` char(64) DEFAULT NULL,
	`color` binary(4) NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user` (
	`id` int(10) unsigned NOT NULL AUTO_INCREMENT,
	`name` CHAR(64) DEFAULT '',
	`message` CHAR(255) DEFAULT '',
	`team` int(10) unsigned DEFAULT NULL,
	`phone` bigint(20) unsigned DEFAULT NULL,
	`status` int(10) unsigned DEFAULT NULL,
	`apikey` binary(64) DEFAULT NULL,
	`distance` int(10) unsigned NOT NULL DEFAULT 0,
	PRIMARY KEY (`id`),
	UNIQUE KEY(`phone`),
	CONSTRAINT `user_ibfk_1` FOREIGN KEY (`team`) REFERENCES `teamg` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `friends` (
	`id` int(10) unsigned NOT NULL AUTO_INCREMENT,
	`id_added` int(10) unsigned NOT NULL,
	`status` int(10) unsigned DEFAULT NULL,
	`id_accepted` int(10) unsigned NOT NULL,
	`painted_together` int(10) unsigned NOT NULL DEFAULT 0,
	`since` bigint(20) unsigned NOT NULL DEFAULT 0,
	PRIMARY KEY (`id`),
	KEY `id_added` (`id_added`),
	KEY `id_accepted` (`id_accepted`),
	CONSTRAINT `friends_ibfk_1` FOREIGN KEY (`id_added`) REFERENCES `user` (`id`),
	CONSTRAINT `friends_ibfk_2` FOREIGN KEY (`id_accepted`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `point` (
	`id` int(10) unsigned NOT NULL AUTO_INCREMENT,
	`creator` int(10) unsigned DEFAULT NULL,
	`team` int(10) unsigned DEFAULT NULL,
	`latitude` float(14,12) DEFAULT NULL,
	`longitude` float(15,12) DEFAULT NULL,
	`size` int(10) DEFAULT NULL,
	`color` binary(4) DEFAULT NULL,
	PRIMARY KEY (`id`),
	KEY `creator` (`creator`),
	CONSTRAINT `point_ibfk_1` FOREIGN KEY (`creator`) REFERENCES `user` (`id`),
	CONSTRAINT `point_ibfk_2` FOREIGN KEY (`team`) REFERENCES `teamg` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `achievement` (
	`id` int(10) unsigned NOT NULL AUTO_INCREMENT,
	`name` char(64) DEFAULT NULL,
	`description` char(255) DEFAULT NULL,
	`value` int(10) DEFAULT 0,
	`objective` int(10) DEFAULT 0,
	`objectiveValue` int(10) DEFAULT 0,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `achievement_unlocked` (
	`id` int(10) unsigned NOT NULL AUTO_INCREMENT,
	`id_a` int(10) unsigned NOT NULL,
	`id_u` int(10) unsigned NOT NULL,
	`time` BIGINT(20) unsigned NOT NULL,
	PRIMARY KEY (`id`),
	KEY `id_a` (`id_a`),
	KEY `id_u` (`id_u`),
	CONSTRAINT `achievement_unlocked_ibfk_1` FOREIGN KEY (`id_a`) REFERENCES `achievement` (`id`),
	CONSTRAINT `achievement_unlocked_ibfk_2` FOREIGN KEY (`id_u`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `picture` (
	`id` int(10) unsigned NOT NULL AUTO_INCREMENT,
	`creator` int(10) unsigned DEFAULT NULL,
	`id_challenge` int(10) unsigned DEFAULT NULL,
	`latitude` float(14,12) DEFAULT NULL,
	`longitude` float(15,12) DEFAULT NULL,
	`time` BIGINT(20) unsigned NOT NULL,
	`name` char(64) DEFAULT NULL,
	`description` char(255) DEFAULT NULL,
	`path` char(255) DEFAULT NULL,
	`profile` BOOLEAN NOT NULL DEFAULT false,
	PRIMARY KEY (`id`),
	CONSTRAINT `picture_ibfk_1` FOREIGN KEY (`creator`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `picture_rating` (
	`id` int(10) unsigned NOT NULL AUTO_INCREMENT,
	`id_p` int(10) unsigned NOT NULL,
	`id_u` int(10) unsigned NOT NULL,
	`value` int(10) unsigned NOT NULL,
	PRIMARY KEY (`id`),
	KEY `id_p` (`id_p`),
	KEY `id_u` (`id_u`),
	CONSTRAINT `picture_rated_ibfk_1` FOREIGN KEY (`id_p`) REFERENCES `picture` (`id`),
	CONSTRAINT `picture_rated_ibfk_2` FOREIGN KEY (`id_u`) REFERENCES `user` (`id`),
	UNIQUE KEY(`id_p`,`id_u`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- test data
INSERT INTO teamg VALUES (1,'RED',0xFFFF0000),(2,'GREEN',0xFF00FF00),(3,'BLUE',0xFF0000FF);

INSERT INTO user VALUES
(1,'Bart Simpson','Eat My Shorts!',1,1234,3,x'21b4f4bd9e64ed355c3eb676a28ebedaf6d8f17bdc365995b319097153044080516bd083bfcce66121a3072646994c8430cc382b8dc543e84880183bf856cff5',0),
(2,'Lisa Simpson','MSG_2',2,49123456789,3,x'282154720abd4fa76ad7cd5f8806aa8a19aefb6d10042b0d57a311b86087de4de3186a92019d6ee51035106ee088dc6007beb7be46994d1463999968fbe9760e',0),
(3,'Maggie Simpson','MSG_3',3,49894233,3,x'8d708d18b54df3962d696f069ad42dad7762b5d4d3c97ee5fa2dae0673ed46545164c078b8db3d59c4b96020e4316f17bb3d91bf1f6bc0896bbe75416eb8c385',0),
(4,'Marge Simpson','MSG_4',1,49894671892,3,x'53b74be8b295b733fdfafbd7d2a22b1686733740de7fdc592b26cf3e1874cfce158170ce9230e24696331a61829244e5d9f48abdacc9ffa8c4cb498724844cf8',0),
(5,'Homer Simpson','Doh!',2,4989467812,3,x'232d43e52834558e9457b0901ee65c86196bf8777c8ff4fc61fdd5e69fd1d24f964fed1bf481b6ef52a69d17372554fecb098fb07f839e64916bdd0d2abf018a',0),
(6,'Milhouse Van Houten','MSG_6',3,49894389143,3,x'fa069700da28b1e41a603e314d30cc7774ae0a730b5d03addcf701d67cbdc9c668a100210b5809f6c9f560447c9b0e61403b24ed785ce05a104658ef70f52244',0),
(7,'Nelson Mandela Muntz','Haw Haw!',1,498935671278,3,x'5f26ac324f43ebe475bcb2c28407d9372100c149ac7d6e160984691b771e4505a71e9d3ba7a30593bcd02c120cbecdede8753cf58ffa9b0e75f0aa1095270a93',0),
(8,'Herschel Shmoikel Pinchas Yerucham Krustofsky','Hey! Hey!',2,4989536781,3,x'4ce72a574115e04e27cab883ce8bf5108213da64373263bdf1b129143aa2c6aae0920f3f628bd9bb416fccf99e1abab6ca8cc6f14fe8e82e9f5e6ea636f0508d',0),
(9,'Ned Flanders','',3,493578125,3,x'1e9471ae2743c093063017a105cb1979014bee37da5ed99186fd50296cb8a30d3ff0c81ba9930bb3cbe7642b15e0e05e882af5b9be5201891d997e0d78e859fa',0),
(10,'W. Seymour Skinner','Bart!',2,4947261879,3,x'2e75db45ffc1734a00608542d8a7635d7f599e4bdacbfcf0c4d5ab85bcc817aa461f1bd1d56de1b72e4ea91b94763a788ec764a4eb456b9ddbc98f0170f4abb7',0);

INSERT INTO achievement VALUES
(1,'Getting Started!','Start up the competetive or the free mode!',1,1,0),
(2,'Paint with a friend!','paint with a friend at least once!',1,2,1),
(3,'A small start of something great!','Unlock 1 Achievement!',1,3,1),
(4,'Well... atleast u are friends!','Be a friend of someone for a day',1,4,1),
(5,'Yay... you are not alone!','Be a friend of someone',1,5,1),
(6,'Walking is hard at the beginning','Walk a distance of 1000 meters',2,1,1000),
(7,'Wow.. you like playing with friends','paint with a friend at least 500 meters!',2,2,500),
(8,'We are going places!','Unlock 8 Achievement!',2,3,8),
(9,'It is already a month?','Be a friend of someone for 14 days',2,4,14),
(10,'You are quite popular','Be a friend of 7 people',2,5,7),
(11,'Walking is getting better after a while!','Walk a distance of 10000 meters',3,1,10000),
(12,'You guys sure like walking','paint with a friend at least 5000!',3,2,5000),
(13,'That is just amazing!','Unlock 14 Achievement!',3,3,14),
(14,'Is it already a month?','Be a friend of someone for a 30 day',3,4,30),
(15,'You are really popular','Be a friend of 15 people',3,5,15);


INSERT INTO friends VALUES
(1,1,3,3,512,1563019684),
(2,1,3,4,512,1563019684),
(7,1,3,5,512,1563019684),
(3,1,3,6,13371337,0),
(4,1,3,8,256,0),
(5,2,1,1,0,0),
(6,2,3,10,1024,0),
(8,2,3,3,1024,0),
(9,2,3,4,1024,1563019684),
(10,2,3,5,1024,1563019684),
(11,6,3,2,192,1563019684),
(12,2,3,7,256,1563019684),
(13,2,3,8,128,1563019684),
(14,4,3,7,64,1563019684),
(15,2,3,9,1024,1563019684),
(16,2,3,10,512,1563019684),
(17,3,3,4,65536,1563019684),
(18,3,3,5,8192,1563019684),
(19,4,3,5,32768,1563019684),
(20,4,3,9,32,1563019684),
(21,10,1,1,0,0),
(22,9,1,1,0,0);


INSERT INTO picture VALUES
(1,1,null,42,11,23456789,'Mein Profilbild','https://upload.wikimedia.org/wikipedia/en/a/aa/Bart_Simpson_200px.png','/var/www-uploads/user1.png',true),
(2,2,null,42,10,23456789,'HIER_KOENNTE_DEIN_NAME_STEHEN','https://upload.wikimedia.org/wikipedia/en/e/ec/Lisa_Simpson.png','/var/www-uploads/user2.png',true),
(3,3,null,43,11,23456789,'HIER_KOENNTE_DEIN_NAME_STEHEN','https://upload.wikimedia.org/wikipedia/en/9/9d/Maggie_Simpson.png','/var/www-uploads/user3.png',true),
(4,4,null,44,11,23456789,'HIER_KOENNTE_DEIN_NAME_STEHEN','https://upload.wikimedia.org/wikipedia/en/0/0b/Marge_Simpson.png','/var/www-uploads/user4.png',true),
(5,5,null,42,11.1,23456789,'HIER_KOENNTE_DEIN_NAME_STEHEN','https://upload.wikimedia.org/wikipedia/en/0/02/Homer_Simpson_2006.png','/var/www-uploads/user5.png',true),
(6,6,null,42,11.2,23456789,'HIER_KOENNTE_DEIN_NAME_STEHEN','https://upload.wikimedia.org/wikipedia/en/1/11/Milhouse_Van_Houten.png','/var/www-uploads/user6.png',true),
(7,7,null,42.1,11,23456789,'HIER_KOENNTE_DEIN_NAME_STEHEN','https://upload.wikimedia.org/wikipedia/en/c/c6/Nelson_Muntz.PNG','/var/www-uploads/user7.png',true),
(8,8,null,42.3,11,23456789,'HIER_KOENNTE_DEIN_NAME_STEHEN','https://upload.wikimedia.org/wikipedia/en/5/5a/Krustytheclown.png','/var/www-uploads/user8.png',true),
(9,9,null,42,11.5,23456789,'HIER_KOENNTE_DEIN_NAME_STEHEN','https://upload.wikimedia.org/wikipedia/en/8/84/Ned_Flanders.png','/var/www-uploads/user9.png',true),
(10,10,null,42.123,13.37,23456789,'HIER_KOENNTE_DEIN_NAME_STEHEN','https://upload.wikimedia.org/wikipedia/en/3/3a/Seymour_Skinner.png','/var/www-uploads/user10.png',true),
(11,1,null,42,11,23456789,'My skateboard','Ay, caramba!','/var/www-uploads/bart-board.png',false),
(12,2,null,42,11,23456789,'My Sax','Listen to me!','/var/www-uploads/lisa-saxophone.png',false),
(13,1,null,42,11,23456789,'Being Right Sucks','Trump made it...','/var/www-uploads/bart-trump.png',false),
(14,5,null,42,11,23456789,'Best life!','Doh!','/var/www-uploads/homer-donuts.jpg',false);
