#Spotify Mood Search Engine (WIP)

##Currently it goes through most of your top artists and gives you results

##Requirements

Java 17 and up
Maven
Spotify Account

#How to run 

1. Build the project with "mvn clean package"
2. Run with "mvn spring-boot:run"
3. Kinda wait a bit
4. Go to http://127.0.0.1:8080. This URL should show text saying "Spotify Mood Login is running on 127.0.0.1:8080"

#Functions

http://127.0.0.1:8080/login allows you to login with your spotify account (do this first so you can get your recommendations)

http://127.0.0.1:8080/me/top shows you your top 20 listened artists in JSON form. Make sure to enable pretty-print

##Currently it supports 5 moods: happy, chill, pumped, melancholic, and romantic. Make sure to replace [INSERT_MOOD] with these keywords

http://127.0.0.1:8080/mood/by?mood=[INSERT_MOOD] makes recommendations based on mood. Also in JSON form

http://127.0.0.1:8080/playlist/from-mood?mood=[INSERT_MOOD]&limit=10 generates your playlist. Once generated it should be placed on your account. I think you can change the number of songs on there

#IMPORTANT NOTES:

-Some recommendations might be inaccurate or not matching the vibes, working to tweak the results

-Some moods may produce less than 20 songs

-The recommendations only put 3-4 artists and give you their songs, it's not really varied at the moment

-Songs with remixes,features,remasters seep in so you will get two of the same songs

-From my experience I had a happy playlist that was just 10 songs of Ed Sheeran

-The recommendations rely on key words, and to my knowledge it's not the most efficient way of recommending. Back then the API allows you to literally analyze the tracks and determine the "vibes" of it, but they removed it because of corporate greed.

-I can add more moods, but it'll be a tedious effort because it's using hashmapping 

-If you remain idle for an hour the access token expires, so I think what you need to do is do step 2 of How to Run. I think there's a way to refresh it.

-Im not 100% sure but you can probably add more than 20 songs because documentation says you can add like 50.

-If you're testing the program, I suggest deleting the playlists considering this is a work in progress, but honestly you can keep them if you want.


Current Accuracy (based on my Spotify, not very objective. Basically, take total songs recommended, count the accurate songs)

Happy = 8/11 songs ~72%
Chill = 7/14 songs 50%
Pumped = 6/11 ~55%
Melancholic = 7/15 ~50%
Romantic = 7/12 ~58%
Energtic = 5/12 ~42%

From the small sample, its about 40%-50% accuracy 