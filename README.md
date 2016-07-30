This project started off as a simple front-end, single-page site to show if the Los Angeles Kings has started the 2015 NHL season. It eventually evolved to show a "Yes" on the days the Kings had a game and "No" otherwise (similar to isitchristmas.com). It then further evolved to show the season schedule, the results of each game, and current statistics such as winning streak, current standing, season points, etc. Finally, I added every team in the league and used AngularJS for the front-end, as it was no longer just a simple site and the pages needed to be dynamic.

The site's data is updated daily using various services written in Java and packaged with Maven. It scrapes data from hockey-reference.com and nhl.com. Note that this was a personal project with no commercial aspect to it and all data obtained from the sites were for personal use.

The various services can be scheduled as cron jobs or Windows scheduled tasks. Since I didn't have a server set up, I just scheduled the jar files to run twice a day. The service would scrape the data, save them as JSON files, upload them to my site's FTP server, and email me a summary of the results (success/failure and how long it took).

If you choose to use any components from this project, you are held liable for the data and how you use it.

Note: You'll find code for the NBA and NHL in there as well, but they are not complete or functional. I'm working on expanding this project to work with the four major sports in the US.
