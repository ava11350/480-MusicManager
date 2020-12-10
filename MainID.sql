##pass in id of song
## set @javaVal to val of song value with id
set @javaVal = '2ZywW3VyVx6rrlrX75n3JB'; ##example line
SELECT artists, `name`, `year`, acousticness, danceability, energy, instrumentalness, liveness, speechiness, tempo, valence, id
FROM musicdatabase 
WHERE id = @javaVal
