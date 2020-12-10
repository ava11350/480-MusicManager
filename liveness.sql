##pass in id of song
## set @javaVal to val of song value with id
set @javaVal = 0.991; ##example line
SELECT artists, `name`, `year`, acousticness, danceability, energy, instrumentalness, liveness, speechiness, tempo, valence, id
FROM musicdatabase 
WHERE liveness >= @javaVal - 0.08 and liveness <= @javaVal + 0.08 ORDER BY RAND() LIMIT 10;