##pass in id of song
## set @javaVal to val of song value with id
set @javaVal = 80; ##example line
SELECT artists, `name`, `year`, acousticness, danceability, energy, instrumentalness, liveness, speechiness, tempo, valence, id
FROM musicdatabase 
WHERE tempo >= @javaVal - 8 and tempo <= @javaVal + 8 ORDER BY RAND() LIMIT 10;