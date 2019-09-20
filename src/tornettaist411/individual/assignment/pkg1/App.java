// Stephen Tornetta PSU 2019. Java Main Class.

package tornettaist411.individual.assignment.pkg1;

//Gson imports
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author stept
 */
public class App {

    private static void displayCategories() throws IOException
    {
        String decodedString = "";
        URL url = new URL("https://api.chucknorris.io/jokes/categories");
        
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        
        httpConn.setRequestMethod("GET"); // getting formation
        httpConn.setRequestProperty("Accept-Charset", "UTF-8"); // key value pair
        httpConn.setRequestProperty("Content-type", "application/x-www-form-urlencoded"); // sending parameter values embeded in the URL line
        httpConn.setRequestProperty("User-Agent", "Mozilla/5.0"); // sending parameter values embeded in the URL line
                
        BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
        
        //System.out.println(httpConn.getResponseCode()); //Checking for http response
        //System.out.println(httpConn.getResponseMessage());
        
        while((decodedString = in.readLine()) != null)
        {
            if(decodedString.contains("political") && decodedString.contains("explicit") && decodedString.contains("religion") )
            {
                decodedString = decodedString.replace(",\"political\"", "");
                decodedString = decodedString.replace(",\"explicit\"", "");
                decodedString = decodedString.replace(",\"religion\"", "");
                
            }      
            
            System.out.println(decodedString);
        }
        
    }
    
    private static String getJoke(String answer)
    {
        try
        {
            List<String> jokelist = new ArrayList<String>();
            
            String decodedString = "";
            int counter = 3; // number of jokes we want
            int attempts = 0; // tracking requests to the server
            while(counter != 0)
            {

                    List<String> templist = new ArrayList<String>(); //used to see if any jokes are duplicates. Duplicates are added to this list
    
                    URL url = new URL("https://api.chucknorris.io/jokes/random?category="+answer);
                    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

                    httpConn.setRequestMethod("GET"); // getting formation
                    httpConn.setRequestProperty("Accept-Charset", "UTF-8"); // key value pair
                    httpConn.setRequestProperty("Content-type", "application/x-www-form-urlencoded"); // sending parameter values embeded in the URL line
                    httpConn.setRequestProperty("User-Agent", "Mozilla/5.0"); // sending parameter values embeded in the URL line

                    BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));   
                    
                    while((decodedString = in.readLine()) != null)
                    {

                        List<String> splitlist = new ArrayList<String>(); 
                        splitlist = Arrays.asList(decodedString.split("value\":\"")); 
                        String temp = splitlist.get(1).replace("\"}", ""); //getting just the joke text from the String split
                        
                        /*
                        Where we do duplicate checking and add it to a templist. This wont do anything on the first addition of a joke to a list because jokelist is empty.
                        */
                        for(String s : jokelist) 
                        {
                            if(temp.equals(s))
                            {
                                templist.add(temp);
                            }
                        }
                        
                        
                        jokelist.add(temp); // joke is added to jokelist
                        
                        /*
                        If we recieved a duplicated we remove the duplicate from the jokelist and templist and add one to our counter to repeat
                        */
                        if(templist.isEmpty() == false) 
                        {
                            jokelist.remove(templist.get(0)); //removing the repeated joke in the jokelist

                            templist.remove(templist.get(0));
                            counter = counter +1; //adding one back to our counter to grab another joke
                        }                     
                        
                        counter = counter -1;
                    }                
                    
                    attempts++;
                    
                    if(attempts > 10) //added to check cases that have less than 3 jokes
                    {
                        break;
                    }
                    
                    System.out.println("Loading Joke...");
            }
            
            System.out.println("Joke List:");
            
            for(String s : jokelist)
            {
                System.out.println(s);
                
            }
            
            return jokelist.toString();


        }
        catch(IOException e)
        {
            e.printStackTrace();
            
            return "Try a different File. Choose one of the categories or type \"quit\" ";
            
        }
    }
        
    
    private static void checkSpelling(String joke) //https://docs.microsoft.com/en-us/azure/cognitive-services/bing-spell-check/quickstarts/java
    {
        try{
            
            String decodedString = "";
            String key = "689d925b91ef4227b3ad4f6acbe5ab9d";
            URL url = new URL("https://javaspellcheckapp.cognitiveservices.azure.com/bing/v7.0/spellcheck");


            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

            httpConn.setRequestMethod("POST"); // getting formation
            httpConn.setRequestProperty("Accept-Charset", "UTF-8"); // key value pair
            httpConn.setRequestProperty("Content-type", "application/x-www-form-urlencoded"); // sending parameter values embeded in the URL line
            httpConn.setRequestProperty("Ocp-Apim-Subscription-Key", key);
            httpConn.setRequestProperty("User-Agent", "Mozilla/5.0"); // sending parameter values embeded in the URL line
            httpConn.setDoOutput(true);
            
            DataOutputStream wr = new DataOutputStream(httpConn.getOutputStream()); //sending the joke to the server
            wr.writeBytes("text=" +joke);
            wr.flush();
            wr.close();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));

            //System.out.println(httpConn.getResponseCode()); //Checking for http response
            //System.out.println(httpConn.getResponseMessage());
            
            while((decodedString = in.readLine()) != null)
            {

                System.out.println("SpellCheck:");
                System.out.println(formatJSON(decodedString));
            }       
            
        }
        
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
    }
    
    
    // This function prettifies the json response.
    // NEED the GSON Library
    public static String formatJSON(String text) { 
        
        JsonParser parser = new JsonParser();
        
        JsonElement json = parser.parse(text);
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        return gson.toJson(json);
}
    
    public static void main(String[] args) {
        try
        {
            while(true)
            {
                System.out.println("Choose a Category from a list below, you can type \"quit\" whenever:");
                displayCategories();            
                Scanner intro = new Scanner(System.in);
                String useranswer = new String(intro.nextLine());
                System.out.println(useranswer);
                if(useranswer.startsWith("q"))
                {
                    break;
                }
                if(useranswer.equals("political") || useranswer.equals("religion") || useranswer.equals("explicit")) // just to check user is not trying to use one of the "bad" categories.
                {
                    System.out.println("This is not a valid joke category. Try again.");
                }
                
                /* Used this if statement to demo the SpellCheck API
                
                if(useranswer.equals("Hallo my nmae is Steve"))
                {
                    System.out.println("Spell Check:");
                    checkSpelling(useranswer);
                
                }
                */
                
                else
                {
                    checkSpelling(getJoke(useranswer));
                }
                
            }            
           
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
            }

}
