function populate(teamName, scheduleJson, resultsJson, statsJson) {

  var schedule = scheduleJson.data;
  var results = resultsJson.data;
  var stats = statsJson.data;

  var today = moment().tz("America/Los_Angeles", "ddd MMM D, YYYY");
  var todayEastern = moment().tz("America/New_York", "YYYY-MM-DD");

// Construct the schedule table
  var scheduleTable = document.getElementById("scheduleTable");
  for (var i = 0; i < schedule.length; i++) {
    var tr = document.createElement("tr");

    // Tickets
    var gameDate = moment.tz(schedule[i].date, moment.ISO_8601, "America/New_York");
    if (!gameDate.isBefore(today, "day")) {
      var ticketTd = document.createElement("td");
      var link = document.createElement("a");
      link.textContent = "Buy Tickets";
      link.href = schedule[i].ticketUrl;
      ticketTd.appendChild(link);
      tr.appendChild(ticketTd);
    } else {
      tr.appendChild(document.createElement("td"));
    }

    // Date
    var dateTd = document.createElement("td");
    dateTd.textContent = gameDate.format("ddd MMM D, YYYY");
    tr.appendChild(dateTd);

    // Visitor
    var visitorTd = document.createElement("td");
    visitorTd.textContent = schedule[i].away;
    tr.appendChild(visitorTd);

    // Home
    var homeTd = document.createElement("td");
    homeTd.textContent = schedule[i].home;
    tr.appendChild(homeTd);

    // Time
    var timeTd = document.createElement("td");
    timeTd.textContent = gameDate.local().format("h:mm A");
    tr.appendChild(timeTd);

    // If the game is finished, construct score and results
    var thisGameResult = results[i];
    if (!thisGameResult) {
      tr.appendChild(document.createElement("td"));
      tr.appendChild(document.createElement("td"));
      scheduleTable.appendChild(tr);
    } else {
      // Construct the score text
      var scoreTd = document.createElement("td");
      if (thisGameResult.homeGame) {
        scoreTd.textContent = thisGameResult.opponentGoals + "-" + thisGameResult.ownGoals;
      } else {
        scoreTd.textContent = thisGameResult.ownGoals + "-" + thisGameResult.opponentGoals;
      }

      if (thisGameResult.overtime === "OVERTIME") {
        scoreTd.textContent += " (OT)";
      } else if (thisGameResult.overtime === "SHOOTOUT") {
        scoreTd.textContent += " (SO)";
      }

      tr.appendChild(scoreTd);

      // Result
      var resultTd = document.createElement("td");
      if (thisGameResult.result === "WIN") {
        resultTd.textContent = "W";
        tr.className = tr.className + " win-cell";
      } else if (thisGameResult.result === "LOSS") {
        resultTd.textContent = "L";
        tr.className = tr.className + " loss-cell";
      } else if (thisGameResult.result === "OTLOSS") {
        resultTd.textContent = "OL";
        tr.className = tr.className + " loss-cell";
      }

      tr.appendChild(resultTd);
    }

    scheduleTable.appendChild(tr);

  }

// Find next game and cross out previous games
  var nextGameDate;
  var nextGameIndex;
  var nextGame;
  $("#scheduleTable td:nth-child(2)").each(function (index) {
    // Skip the first row
    if ($(this).text() === "Date") {
      return true;
    }

    // If the game is in the past, grey it out
    var date = moment($(this).text(), "ddd MMM D, YYYY");
    if (date.isBefore(today, "day")) {
      $(this).parent().addClass("grey-cell");
    }

    // Bold the next game
    if (date.isSame(today, "day") || date.isAfter(today, "day")) {
      $(this).parent().addClass("next-game");
      nextGameDate = date;
      nextGameIndex = index - 1;
      nextGame = schedule[nextGameIndex];
      return false;
    }
  });

// Determine if there's a game today
  var gameToday = false;
  for (var gameData in schedule) {
    if (schedule.hasOwnProperty(gameData)) {
      var thisGameDate = moment(schedule[gameData].date, moment.ISO_8601);
      if (today.isSame(thisGameDate, 'day')) {
        gameToday = true;
        break;
      }
    }
  }

// Figure out opponent
  var nextOpponent;
  if(nextGame) {
    if (nextGame.away.toUpperCase() === teamName.toUpperCase()) {
      nextOpponent = nextGame.home;
    } else {
      nextOpponent = nextGame.away;
    }
    var nextOpponentCity = teamNames[nextOpponent];
    var nextOpponentFull = nextOpponentCity + " " + nextOpponent;
  }


// Update text blocks
  if (gameToday) {
    document.getElementById("answer").textContent = "YES";
    document.getElementById("nextGameText").textContent = "They're playing the";
    document.getElementById("nextGameDate").textContent = nextOpponentFull;
    document.getElementById("nextGameOppText").textContent = "at";
    document.getElementById("nextGameOpp").textContent = moment.tz(nextGame.time, "h:mm A", "America/New_York").local().format("h:mm A");
  } else {
    document.getElementById("answer").textContent = "NO";
    document.getElementById("nextGameDate").textContent = moment(nextGameDate).format("dddd, MMM D");
    document.getElementById("nextGameOpp").textContent = nextOpponentFull;
  }

 //streak
  document.getElementById("streak").textContent = stats.streak;

 //record
  document.getElementById("record").innerHTML = stats.wins + "-" + stats.losses + "-" + stats.overtimeLosses;
  document.getElementById("points").textContent = "(" + stats.points + " points)";

 //div standing
  var divisionStanding = stats.divisionStanding;
  var divOrdinal = getOrdinal(divisionStanding);
  document.getElementById("divStanding").textContent = divisionStanding + divOrdinal + " in the " + toTitleCase(stats.division) + " Division";

 //conf standing
  var confStanding = stats.conferenceStanding;
  var confOrdinal = getOrdinal(confStanding);
  document.getElementById("confStanding").textContent = confStanding + confOrdinal + " in the " + toTitleCase(stats.conference) + " Conf.";

  // location
  //document.getElementById("location").textContent = teamNames[toTitleCase(teamName)];
}

function getOrdinal(number) {
  var ordinal;
  if(number === 1) {
    ordinal = "st";
  } else if(number === 2) {
    ordinal = "nd";
  } else if(number === 3) {
    ordinal = "rd";
  } else {
    ordinal = "th";
  }

  return ordinal;
}

function toTitleCase(string)
{
  // \u00C0-\u00ff for a happy Latin-1
  return string.toLowerCase().replace(/_/g, ' ').replace(/\b([a-z\u00C0-\u00ff])/g, function (_, initial) {
    return initial.toUpperCase();
  }).replace(/(\s(?:de|a|o|e|da|do|em|ou|[\u00C0-\u00ff]))\b/ig, function (_, match) {
    return match.toLowerCase();
  });
}