CREATE TABLE ITEM(
	itemId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
	price INT NOT NULL, 
	imageURL VARCHAR(75) NOT NULL
);

CREATE TABLE WEAPON(
	itemId INT PRIMARY KEY NOT NULL, 
	attack INT NOT NULL, 
	specialAttack INT NOT NULL,
	FOREIGN KEY(itemId) REFERENCES ITEM(itemId)
);

CREATE TABLE ARMOR(
	itemId INT PRIMARY KEY NOT NULL, 
	defense INT NOT NULL, 
	specialDefense INT NOT NULL,
	FOREIGN KEY(itemId) REFERENCES ITEM(itemId)
);

CREATE TABLE CONSUMABLE(
	itemId INT PRIMARY KEY NOT NULL, 
	effectedStat VARCHAR(10) NOT NULL, 
	amount INT NOT NULL,
	FOREIGN KEY(itemId) REFERENCES ITEM(itemId)
);

CREATE TABLE CHARACTERS(
	characterId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	name VARCHAR(20) NOT NULL,
	health INT NOT NULL,
	attack INT NOT NULL,
	defense INT NOT NULL,
	specialAttack INT NOT NULL,
	specialDefense INT NOT NULL
);

CREATE TABLE INVENTORY(
	characterId INT NOT NULL,
	itemId INT NOT NULL,
	FOREIGN KEY(itemId) REFERENCES ITEM(itemId),
	FOREIGN KEY(characterId) REFERENCES CHARACTERS(characterId),
	PRIMARY KEY(characterId, itemId)
);

PRAGMA foreign_keys = ON;

INSERT INTO ITEM VALUES(
	NULL,
	300,
	"res/sword.png"
);

INSERT INTO WEAPON VALUES(
	1,
	10,
	5
);

INSERT INTO ITEM VALUES(
	NULL,
	350,
	"res/axe.png"
);

INSERT INTO WEAPON VALUES(
	2,
	20,
	3
);

INSERT INTO ITEM VALUES(
	NULL,
	250,
	"res/hammer.png"
);

INSERT INTO WEAPON VALUES(
	3,
	8,
	15
);

INSERT INTO ITEM VALUES(
	NULL,
	100,
	"res/helmet.png"
);

INSERT INTO ARMOR VALUES(
	4,
	5,
	5
);

INSERT INTO ITEM VALUES(
	NULL,
	75,
	"res/gauntlet.png"
);

INSERT INTO ARMOR VALUES(
	5,
	8,
	2
);

INSERT INTO ITEM VALUES(
	NULL,
	10,
	"res/potion.png"
);

INSERT INTO CONSUMABLE VALUES(
	6,
	"Health",
	25
);

INSERT INTO ITEM VALUES(
	NULL,
	100,
	"res/revive.png"
);

INSERT INTO CONSUMABLE VALUES(
	7,
	"Health",
	50
);

INSERT INTO CHARACTERS VALUES(
	NULL,
	"Tyrion",
	100,
	5,
	5,
	15,
	10
);

INSERT INTO CHARACTERS VALUES(
	NULL,
	"Rheagar",
	200,
	45,
	35,
	25,
	20
);

INSERT INTO INVENTORY VALUES(
	2,
	1
);

INSERT INTO INVENTORY VALUES(
	2,
	4
);

INSERT INTO INVENTORY VALUES(
	2,
	5
);

INSERT INTO INVENTORY VALUES(
	1,
	3
);

INSERT INTO INVENTORY VALUES(
	1,
	6
);

INSERT INTO INVENTORY VALUES(
	1,
	7
);

--Example code copied from Java files
UPDATE CHARACTERS SET name = '" + name + "', health = " + health + ", attack = " +
					attack + ", defense = " + defense + ", specialAttack = " + specialAttack + ", specialDefense = " + 
					specialDefense + " WHERE characterId = " + characterId;
					
"UPDATE ITEM SET price = " + price + ", imageURL = '" + imageURL + "' WHERE itemId = " + itemId;

"DELETE FROM CHARACTERS WHERE characterId = " + characterId

--This gets the last primary key that was used for the given table
"SELECT * FROM SQLITE_SEQUENCE WHERE name = '" + tableName + "'"