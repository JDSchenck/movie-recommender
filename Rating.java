
import java.util.List;


// Rating object class

public class Rating implements Comparable<Rating>{
    // Each Rating will have the following 4 attributes: movieID, userID, Rating
    private int movieID;
    private int rating;
    private int userID;
    private double predictedRating;
    
    // Global counter used to keep track of total number of ratings stored
    private static int count = 0;

    public Rating(int movieIDpass, int ratingpass, int userID) {
        movieID = movieIDpass;
        this.userID = userID;
        rating = ratingpass;
        predictedRating = 0;
        count++;
    }
    
    

    
    // Getters & Setters
    
    public int getmovieID(){
        return movieID;
    }
    
    public void setmovieID(int movieIDpassed){
        movieID = movieIDpassed;
    }
    
    public int getUserID(){
        return this.userID;
    }
    
    public void setUserID(int userID){
        this.userID = userID;
    }
    
    public int getRating(){
        return rating;
    }
    
    public void setRating(int ratingpassed){
        rating = ratingpassed;
    }
    
    public int getCount(){
        return count;
    }
    public double getPredictedRating(){
        return predictedRating;
    }
    public void setPredRating(double predictedRating){
        this.predictedRating = predictedRating;
    }
    
    @Override
    public int compareTo(Rating compareRating){
        if (this.predictedRating < compareRating.predictedRating){
            return -1;
        }
        if (this.predictedRating > compareRating.predictedRating){
            return 1;
        }
        return 0;
    }
    
}
