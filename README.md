# movie_recommender
[Java] Final Project - Data Structures &amp; Algorithms - Data Analytics: Analyzes 100,000 reviews of 1,682 movies by 943 unique users. Predicts top 5 unseen movie recommendations for each user.

Files:
- MovieRecommend.java : Application file/driver
- Ratings.dat : 100,000 user movie ratings - Structure - UserID / neglible / Rating / MovieID
- Movies.dat : 1,682 movies with ID numbers and title information - Structure - MovieID / Title / neglible / neglible
- README.txt : Data structures and algorithms analyzed for efficiency

**Efficiency Analysis**

Main Data Structures Used:
•	HashMaps
o	userToRatings: map of unique user IDs to list of rating objects
o	userIDToRowIndex: map of user IDs to corresponding row index
o	movieIDToColIndex: map of movie IDs to corresponding column index
o	movieIDToTitle: map of movie IDs to movie titles
o	userUnrated: map of user IDs to list of unrated rating objects (unrated movies)
•	Collections.sort: implementing a comparator for my rating objects based on comparison 
of predicted ratings (double).

For my project, I first read in the ratings.dat file and stored all of the users and 
ratings in to a hashmap userToRatings where only unique user IDs were mapped to a list of 
rating objects. In each rating object are attributes: movie ID, rating, predicted rating 
(for unrated movies).
→ O(users)


I then read in the movies.dat file and stored each movie ID and movie title in my hashmap 
movieIDToTitle.
→ O(movies)

I then built my 2D rating matrix by using the following nested loop structure:
For each movie: (1682)
For each user: (943)
By looping in this way I was able to correctly fill in the entire 2D ratings matrix where 
users are rows and movies are columns. As I went through this process, I updated my maps 
for each movie column and user row reference accordingly.
→O(users * movies)

I built my similarities table by using dependent nested loops with compare each movie to 
every other movie in the database. Since in the worst case, every user rates every movie, 
the efficiency is users * movies^2.
→O(users * movies^2)

To calculate my top 5 predictions for each user, I followed a similar approach as reading 
in the data. I found the set of unrated movies for each user, and stored them as rating 
objects in a map userUnrated(userID, List<Rating>). 
→O(users)

I then created a comparator which allowed me to sort my arraylists of rating objects based
on their attribute of predicted rating in descending order O(NLOGN). I used a for loop to 
iterate for each user, performed a sort on their list of unrated movies, and then 
outputted the top 5 predictions for each user.
→ O(users)

**Comments on potential efficiency improvements**

After reviewing the above time efficiencies, it appears to me that my entire program would
operate around O(users * movies^2) in the worst case scenario.

As I worked on this, I learned that I could have combined many of the loops that I used 
into each other eliminating some time wasting calculations. For example, I realized that
instead of first reading in the data to a hashmap and then building my matrices I could 
have simply implemented a map of maps, ie. A hashmap inside of a hashmap which would have
reduced my time efficiency even further. 

Another option I could have used would have been a 2d arraylist that is dynamic for the 
number of users or rows. 
