/**
 * Created by grego on 01/05/2016.
 */


Date.isSameWeek = function (d1, d2) {
    return d1.getWeekNumber() == d2.getWeekNumber() && d1.getYear() == d2.getYear();
}
Date.prototype.getWeekNumber = function () {
    var d = new Date(+this);
    d.setHours(0, 0, 0);
    d.setDate(d.getDate() + 4 - (d.getDay() || 7));
    return Math.ceil((((d - new Date(d.getFullYear(), 0, 1)) / 8.64e7) + 1) / 7);
};


function getDateOfWeek(w, y) {
    var d = (1 + (w - 1) * 7); // 1st of January + 7 days for each week
    return new Date(y, 0, d);
}


function firstDayOfWeek(week, year) {

    if (year == null) {
        year = (new Date()).getFullYear();
    }

    var date = firstWeekOfYear(year),
        weekTime = weeksToMilliseconds(week),
        targetTime = date.getTime() + weekTime;

    return date.setTime(targetTime);

}

function weeksToMilliseconds(weeks) {
    return 1000 * 60 * 60 * 24 * 7 * (weeks - 1);
}

function firstWeekOfYear(year) {
    var date = new Date();
    date = firstDayOfYear(date, year);
    date = firstWeekday(date);
    return date;
}

function firstDayOfYear(date, year) {
    date.setYear(year);
    date.setDate(1);
    date.setMonth(0);
    date.setHours(0);
    date.setMinutes(0);
    date.setSeconds(0);
    date.setMilliseconds(0);
    return date;
}
function firstWeekday(firstOfJanuaryDate) {
    // 0 correspond au dimanche et 6 correspond au samedi.
    var FIRST_DAY_OF_WEEK = 1; // Monday, according to iso8601
    var WEEK_LENGTH = 7; // 7 days per week
    var day = firstOfJanuaryDate.getDay();
    day = (day === 0) ? 7 : day; // make the days monday-sunday equals to 1-7 instead of 0-6
    var dayOffset = -day + FIRST_DAY_OF_WEEK; // dayOffset will correct the date in order to get a Monday
    if (WEEK_LENGTH - day + 1 < 4) {
        // the current week has not the minimum 4 days required by iso 8601 => add one week
        dayOffset += WEEK_LENGTH;
    }
    return new Date(firstOfJanuaryDate.getTime() + dayOffset * 24 * 60 * 60 * 1000);
}

String.prototype.isHour = function () {
    var t = this.split(":");
    if (t.length == 2) {
        try {
            var h = parseInt(t[0]);
            var m = parseInt(t[1]);
            return h >= 0 && h <= 24 && m >= 0 && m <= 60;
        }
        catch (e) {
            return false;
        }
    }
    else if (t.length == 1) {
        var h = parseInt(t[0]);
        return (h <= 24 && h >= 0);
    }
    else {
        return false;
    }
}