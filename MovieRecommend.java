import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;



public  class MovieRecommend {

    public static void main(String[] args) {
        // Mapping user IDs to list of ratings
        Map<Integer, List<Rating>> userToRatings = new HashMap<Integer, List<Rating>>();
        // Mapping user IDs to row indices to locate data in matrix by user ID
        Map<Integer, Integer> userIDToRowIndex = new HashMap<Integer, Integer>();
        // Mapping movie IDs to column indices to locate data in matrix by movie ID
        Map<Integer, Integer> movieIDToColIndex = new HashMap<Integer, Integer>();
        // Mapping movie IDs to movieTitles for cross referencing efficiently between the files
        Map<Integer, String> movieIDToTitle = new HashMap<Integer, String>();
        
        Map<Integer, List<Rating>> userUnrated = new HashMap<Integer, List<Rating>>();

    try{   

        
        Scanner fileRatings = new Scanner(new File("src/ratings.dat"));
        
        System.out.println("Reading in ratings data...");
        
        // Obtain the number of rows in the file = number of ratings 
        BufferedReader ratingsReader = new BufferedReader(new FileReader("src/ratings.dat"));
        int ratingsRows = 0;
        while (ratingsReader.readLine() != null) ratingsRows++;
        

        ratingsReader.close();
        

         
         // Read in the file line by line iteration
         // Store first three integers as userID, movieID, and rating
         for (int i = 0; i < ratingsRows; i++){
             int userID = fileRatings.nextInt();
             int movieID = fileRatings.nextInt();
             int rating = fileRatings.nextInt();
             
             
             
             // if user is already mapped, retrieve and update the user's rating list
             if(userToRatings.containsKey(userID)){
                 List<Rating> ratingList = userToRatings.get(userID);
                 ratingList.add(new Rating(movieID, rating, userID));
                 userToRatings.put(userID, ratingList);
             }
             // other wise new user to map, add new user and new list for rating
             else
             {
                 List<Rating> ratingList = new ArrayList<Rating>();
                 ratingList.add(new Rating(movieID, rating, userID));
                 userToRatings.put(userID, ratingList);             
                
             }
             
             // skip to next line in file and repeat iteration
             fileRatings.nextLine();
             
         }
        
         System.out.println("Complete.");
         
         
         
        System.out.println("Reading in movies data...");
         
         // Repeat same process to read in the movie data to its appropriate mapping
         Scanner fileMovies = new Scanner(new FileInputStream("src/movies.dat"));
         
        // Obtain the number of rows in the file = number of ratings 
        BufferedReader moviesReader = new BufferedReader(new FileReader("src/movies.dat"));
        int moviesRows = 0;
        while (moviesReader.readLine() != null) moviesRows++;

        moviesReader.close();     
        
         // Read in the file line by line iteration
         // Store first two items int movieID and String movieTitle
        

        fileMovies.useDelimiter("\\|");
         
        while(fileMovies.hasNextLine()){                       
            
             int movieID = fileMovies.nextInt();
             String movieTitle = fileMovies.next();
                         
             // add new movie to appropriate map movieIDToTitle
             movieIDToTitle.put(movieID, movieTitle);
             
            // advance to the next line
             fileMovies.nextLine();             
      
         }
         
         System.out.println("Complete.");
         
         
         // Load data to movieToRatings map, to create item vectors for similarity
         // if a user did not rate a movie, fill in a 0 rating for that movie in the list of ratings
         
         
         
         // rows = # of unique users mapped
         int userRows = userToRatings.size();
         // columns = # of unique movies mapped
         int movieCols = movieIDToTitle.size();
         
         // new 2D matrix for storing item vectors in columns
         int[][] ratingMatrix = new int[userRows][movieCols];
         
         // Required variables for algorithm to load ratings in to correct indices
         int movieCount = 0;
         int userCount = 0;
         boolean movieRated = false;
         int ratingRetrieved = 0;
         
         // Alert the user of possible delay to process data
         System.out.println("Building 2D User Rating Matrix...");
         
            // for each unique movie iteration
            for (Map.Entry<Integer, String> movie : movieIDToTitle.entrySet()) {

                
                int movieID = movie.getKey();
                
                // Add each movie ID to map with corresponding column index value
                movieIDToColIndex.put(movieID, movieCount);
                
                // for each unique user iteration
                for (Map.Entry<Integer, List<Rating>> user : userToRatings.entrySet()){

                    
                    int userID = user.getKey();
                    
                    // Only for one full iteration of all users, where movie iteration is 0
                    if (movieCount == 0){
                    // Add user to map with corresponding row index value
                    userIDToRowIndex.put(userID, userCount);
                    }
                    
                    // Store the list of ratings for the current user
                    List<Rating> ratingList = user.getValue();
                    


                    // for each rating by user iteration
                    for(int i = 0; i < ratingList.size(); i++){
                        if (ratingList.get(i).getmovieID() == movieID){
                            movieRated = true;
                            ratingRetrieved = ratingList.get(i).getRating();
                            break;
                        }
                        else{
                            movieRated = false;
                        }
                    }
                    
                    if(movieRated){
                        ratingMatrix[userCount][movieCount] = ratingRetrieved;
                    }
                    else {
                        // Movies has NOT been rated by user, input zero to matrix
                        ratingMatrix[userCount][movieCount] = 0;
                        

                        
                    }
                    
                    
                    userCount++;
                }
                
                
                
             userCount=0;
             movieCount++;
           
           }
           System.out.println("2D User Rating Matrix Built Successfully!"); 

           System.out.println("Building 2D Similarities Matrix...");
           
           double[][] similaritiesTable = similarityTable(ratingMatrix,movieIDToTitle,movieIDToColIndex);

           System.out.println("2D Similarities Matrix Built Successfully!"); 
   
           System.out.println("Predicting all user's top 5 recommendations...");

           for (Map.Entry<Integer, Integer> movie : movieIDToColIndex.entrySet()){
               
               int col = movie.getValue();
               
                // for each unique user iteration
                for (Map.Entry<Integer, Integer> user : userIDToRowIndex.entrySet()){
                
                    int row = user.getValue();
                    
                    // If movie NOT rated, then 0 present
                    // Add/update userUnrated map with list of predicted ratings
                    if(ratingMatrix[row][col] == 0){
                        // if user has already been added to map userUnrated, update list
                        if (userUnrated.containsKey(user.getKey())){
                            List<Rating> ratingListRetrieved = userUnrated.get(user.getKey());
                            Rating newRating = new Rating(movie.getKey(),0,user.getKey());
                            newRating.setPredRating(getRatingPrediction(user.getKey(),movie.getKey(),ratingMatrix,userToRatings,movieIDToColIndex, similaritiesTable));
                            ratingListRetrieved.add(newRating);
                            userUnrated.put(user.getKey(), ratingListRetrieved);
                            
                        }else{
                            // Add new user and new predicted rating list to map userUnrated
                            List<Rating> newRatingList = new ArrayList<Rating>();
                            Rating newRating = new Rating(movie.getKey(),0,user.getKey());
                            newRating.setPredRating(getRatingPrediction(user.getKey(),movie.getKey(),ratingMatrix,userToRatings,movieIDToColIndex, similaritiesTable));
                            newRatingList.add(newRating);
                            userUnrated.put(user.getKey(), newRatingList);
                            
                        }
                        
                    }
                               
               }
           }
            
           System.out.println("Complete.");
           

         
          // Output the results to a text file
           File output_recommendations = new File("output_recommendations.txt");
           BufferedWriter output = new BufferedWriter(new FileWriter(output_recommendations));
           
           System.out.println("Outputting results to text file...");
           
           for (Map.Entry<Integer, List<Rating>> user : userUnrated.entrySet()){
               List<Rating> currentRatings = user.getValue();
               
               Collections.sort(currentRatings, new predRatingComparator());
               
               output.write("User ID: " + user.getKey() + " Top 5 Recommendations: ");
               
               for (int i = 0; i < 5; i++){
                   output.write(" " + 
		   // gets movie name from movies hashmap
                   movieIDToTitle.get(currentRatings.get(i).getmovieID()) + "::" + 
	           // predicted rating
		   currentRatings.get(i).getPredictedRating() + "|");
		 }
               
               output.write("\n");
               }
               
           System.out.println("Output generated successfuly!");
           output.close();
                    
         
    } catch(Exception e){e.printStackTrace();}      

    } // end main method
    
    public static List<Integer> getItemVector(int movie, int[][] ratingMatrix, Map<Integer, Integer> movieIDToColIndex){
        // Helper method which converts a movie ID into a vector list for similarity formula
        
        int rows = ratingMatrix.length;
        
        
        int targetColumn = movie;
        
        List<Integer> itemVector = new ArrayList<Integer>();
        
        for(int row = 0; row < rows; row++){
            itemVector.add(ratingMatrix[row][targetColumn]);
        }
        
        return itemVector;      
        
    }
    public static double similarity(List<Integer> movie1, List<Integer> movie2){
       // calculates the similarity based on formula provided in assignment instructions
        double numerator = 0;
        double denominatorItem1 = 0;
        double denominatorItem2 = 0;
        double denominator = 0;
        double similarity;
        
        int vectorSize = movie1.size();
        
        for (int i = 0; i < vectorSize; i++){
            
                
                numerator+= (movie1.get(i) * movie2.get(i));
                
                denominatorItem1 += (movie1.get(i) * movie1.get(i));
                denominatorItem2 += (movie2.get(i)* movie2.get(i));
                       
        }
        
        denominator = (Math.sqrt(denominatorItem1)) * (Math.sqrt(denominatorItem2));
        
        
        similarity = numerator / denominator;
           
        
        return similarity;
        
    }
    
    public static double[][] similarityTable(int[][] ratingMatrix, Map<Integer, String> movies,
            Map<Integer,Integer> movieIDToColIndex){
        // empty double 2D array for storing similarity values
        double[][] similaritiesTable = new double[movies.size()][movies.size()];
        
        for (int movie = 0; movie < movies.size(); movie++) {
            similaritiesTable[movie][movie] = 1;
	}
        
        for (int movie = 0; movie < movies.size(); movie++){
            for (int movie2 = movie + 1; movie2 < movies.size(); movie2++){
                List<Integer> vectorI = getItemVector(movie, ratingMatrix, movieIDToColIndex);
                List<Integer> vectorJ = getItemVector(movie2, ratingMatrix, movieIDToColIndex);
                
                double similarity = similarity(vectorI, vectorJ);
                
                similaritiesTable[movie][movie2] = similarity;
                similaritiesTable[movie2][movie] = similarity;
                
            }
        }
       return similaritiesTable;  
    }
    
    public static double getRatingPrediction(int userID, int movieID,
            int[][] ratingMatrix, Map<Integer, List<Rating>> userToRatings, 
            Map<Integer, Integer> movieIDToColIndex, double[][] similaritiesTable){
        
        double predictedRating;
        
        // Collect all previous ratings by user in list from map
        List<Rating> ratingList = userToRatings.get(userID);
        
        //Iterate for every item j that has been previously rated by user
        double count = 0;
        double sum = 0;
        double similarity;
        sum = 0;
        count = 0;
        for(Rating rating : ratingList){
                           
           similarity = similaritiesTable[movieID - 1][rating.getmovieID() - 1];
           
           sum += similarity * rating.getRating();
           count += similarity;        
           
        }
        if (count == 0){
            predictedRating = 0;
        }else{
        predictedRating = sum / count;
        }
        return predictedRating;
        
        
    }
    
    
    
}// end class


class predRatingComparator implements Comparator<Rating>{

    @Override
    public int compare(Rating rating1, Rating rating2) {
       
        return Double.compare(rating2.getPredictedRating(), rating1.getPredictedRating());

        
    }
    
}
