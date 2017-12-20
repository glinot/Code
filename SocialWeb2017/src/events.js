/**
 * Created by grego on 3/20/2017.
 */

var moment = require("moment")


function checkEventsClash(events, table) {
    var res = {};
    for (var d in table) {
        var m_d = moment(new Date(d))
        var clash = events.reduce(function (acc, current) {
            var m_cstart = moment(current.start)
            var m_cend = moment(current.end)
            return acc || (m_d.isAfter(m_cstart) && m_d.isBefore(m_cend));
        }, false);
        res[d] = clash;
    }
    return res;

}

function packCaves(table_time) {
    var cavesBlocks = []
    var temp_pile = []
    var day = -1;
    for (var c in table_time) {
        var m_d = moment(new Date(c))
        if (table_time[c] || day != m_d.day()) {
            if (temp_pile.length > 0) {
                var first = temp_pile[0]
                var last = moment(temp_pile[temp_pile.length - 1]).add(1,'hour').toDate()
                cavesBlocks.push(
                    {start: first, end: last}
                )
                temp_pile = []
            }
                day = m_d.day()

        }
        else {
            temp_pile.push(new Date(c))
        }
    }
    return cavesBlocks;
}
module.exports = {
    mergeEvents: function (arr) {
        var merged = [];
        while (arr.length > 1) {
            var group = []
            var base = arr[0];
            arr.splice(0, 1)
            // check overlap
            var toRemove = [];
            arr.forEach(function (e, i) {
                if (e.start.getTime() >= base.start.getTime() && e.start.getTime() <= e.end.getTime()) {
                    group.push(e)
                    toRemove.push(i)
                }
            });
            toRemove.forEach(function (e) {
                arr.splice(e, 1);
            });
            group.push(base);
            var merged_group = group.reduce(function (acc, curr) {
                if (acc.end.getTime() < curr.end.getTime()) acc.end = curr.end
                return acc;
            }, {start: base.start, end: base.end})
            merged.push(merged_group)
        }
        if (arr.length == 1) {
            merged.push(arr[0]);
        }
        return merged;
    },
    findFreeSpots: function (merged, week_offset) {
        var spots = [];

        var startDate = moment().set("week", moment().get("week") + week_offset).set("hour", 0).set('minute', 0).set('second', 0).toDate()
        if (week_offset == 0) startDate = new Date()
        var endDate = moment().set("week", moment().get("week") + week_offset).set("hour", 0).set('minute', 0).set('second', 0).toDate()

        if (merged.length == 0) {
            spots.push({start: startDate, end: endDate})
        }
        else {
            var base = merged[0]
            merged.splice(0, 1)
            spots.push({start: startDate, end: base.end})
            while (merged.length > 0) {
                var temp = merged[0]
                merged.splice(0, 1)
                spots.push({start: base.end, end: temp.start})
                base = temp;
            }
            spots.push({start: base.end, end: endDate})

        }
        // split days  results


    },
    findFreeCaves: function (events, start, end) {
        // Create Date Struct
        res = {}
        m_start = moment(start).set('minute',0).set('second',0)
        m_end = moment(end).set('minute',0).set('second',0)

        m_curr = m_start.clone()
        while (m_curr.isSameOrBefore(m_end)) {

            while (m_curr.hour() < 20) {  // before 8 PM
                res[m_curr.toDate()] = false;
                m_curr.add(1, 'hour')
            }
            m_curr.add(1, 'day').set('hour', 7).set('minute',0).set('second',0) // after 8pm WEIRD
        }

        res = checkEventsClash(events, res)

        return packCaves(res)

    }

}