##pass in id of song
## set @javaVal to val of song value with id
set @javaVal = '1996'; ##example line
SELECT artists, `name`, `year`, acousticness, danceability, energy, instrumentalness, liveness, speechiness, tempo, valence, id
FROM musicdatabase 
WHERE year = @javaVal
ORDER BY RAND() LIMIT 10;