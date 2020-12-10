##pass in id of song
## set @javaVal to val of song value with id
set @javaVal = 0.2; ##example line
SELECT artists, `name`, `year`, acousticness, danceability, energy, instrumentalness, liveness, speechiness, tempo, valence, id
FROM musicdatabase 
WHERE acousticness >= @javaVal - 0.05 and acousticness <= @javaVal + 0.05 ORDER BY RAND() LIMIT            
10;