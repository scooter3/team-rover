This project started off as a simple front-end, single-page app to show if the Los Angeles Kings have started the 2015 NHL season. It eventually evolved to show a "Yes" on the days the Kings had a game and "No" otherwise (similar to isitchristmas.com). It then further evolved to show the season schedule, the results of each game, and current statistics such as winning streak, current standing, season points, etc. Finally, I added every team in the league and used AngularJS for the front-end, as it was no longer just a simple site and the pages needed to be dynamic.

The site's data is updated daily using various services written in Java and packaged with Maven. It scrapes data from hockey-reference.com and nhl.com. Note that this was a personal project with no commercial aspect to it and all data obtained from the sites were for personal use.

I also created a service that calls SeatGeek's API to provide links for tickets to every game. This only needs to be run once when the schedules are generated because they do not change throughout the season. I only included this as a proof of concept and because SeatGeek's API is readily available and easy to use. Obviously, I wasn't going to launch this for public use because selling tickets means this would be a commercial site and I would need to get permission to use the data.

The various services can be scheduled as cron jobs or Windows scheduled tasks. Since I didn't have a server set up, I just scheduled the jar files to run twice a day. The service would scrape the data, save them as JSON files, upload them to my site's FTP server, and email me a summary of the results (success/failure and how long it took).

If you choose to use any components from this project, you are held liable for the data and how you use it.

Note: You'll find code for the NBA and NFL in there as well, but they are not complete or functional. I'm working on expanding this project to work with the four major sports in the US.

populatePage.js is used on the front-end to render each team's page. I have not included the main AngularJS file or other web assets. Most of the logic for driving the front-end can be found in populatPage.js. To get the page to fully render, you must create your own index.html and AngularJS app file.
