DROP TABLE musicDatabase;

CREATE TABLE musicDatabase (
	acousticness FLOAT,
    artists VARCHAR(50),
    danceability FLOAT,
    duration_ms INTEGER,
    energy FLOAT,
    explicit INTEGER,
    id VARCHAR(50) NOT NULL,
    instrumentalness FLOAT,
    `key` INTEGER,
    liveness FLOAT,
    loudness FLOAT,
    mode INTEGER,
    name VARCHAR(70),
    popularity INTEGER,
    release_date VARCHAR(20),
    speechiness FLOAT,
    tempo FLOAT,
    valence FLOAT,
    year INTEGER,
    PRIMARY KEY (id)
    );
    
SET GLOBAL local_infile=1;

LOAD DATA LOCAL INFILE '/Users/ava11350/Desktop/CS\ 480/Final\ Project/data.csv'
INTO TABLE musicDatabase
COLUMNS TERMINATED BY ','
IGNORE 1 ROWS;

SELECT * FROM musicDatabase
WHERE artists LIKE "%Frank Ocean%";