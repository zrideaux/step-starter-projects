// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // First check if request is possible. If duration exceedsx a day return no options.
    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
      return Arrays.asList();
    }

    // Initialize list of possible times, start by making the whole day possible
    ArrayList<TimeRange> possibleTimes = new ArrayList<TimeRange>();
    possibleTimes.add(TimeRange.WHOLE_DAY);

    // Remove times when events take place from possibleTimes
    for (Event event : events) {
      if (!Collections.disjoint(event.getAttendees(), request.getAttendees())) {
        possibleTimes = updatePossibleTimes(possibleTimes, event);
      }
    }

    // Remove ranges in possibleTimes which aren't long enough for the request
    possibleTimes = removeRangesTooShort(possibleTimes, request);

    return possibleTimes;
  }

  /**
   * Compare each TimeRange in possibleTimes with an event's start and end times.
   * If the event contains the timeRange, it is removed from the possible times.
   * If there is an overlap, the duration of the event is cut from the existing 
   *   times and possibleTimes is updated accordingly.
   * If the there is no overlap, possibleTimes is not changed. 
   */
  private ArrayList<TimeRange> updatePossibleTimes(ArrayList<TimeRange> possibleTimes, Event event) {
    ArrayList<TimeRange> updatedTimes = new ArrayList<>();
    
    for (TimeRange timeRange : possibleTimes) {
      // If an an event completely encapsulates a time range, don't add the range.
      if (!event.getWhen().contains(timeRange)) {
        if (event.getWhen().overlaps(timeRange)) {
          // If an event conflicts with but doesn't completely contain a range in possibleTimes, 
          // add possible replacements.
          int possibleTimeStart = timeRange.start();
          int possibleTimeEnd = timeRange.end();
          int eventStart = event.getWhen().start();
          int eventEnd = event.getWhen().end();

          if (possibleTimeStart < eventStart) {
            // possibleTime |-----| 
            // eventStart      |-----|
            updatedTimes.add(TimeRange.fromStartEnd(possibleTimeStart, eventStart, false));
          }
          if (eventEnd < possibleTimeEnd) {
            // possibleTime    |-----|
            // eventStart   |-----|
            updatedTimes.add(TimeRange.fromStartEnd(eventEnd, possibleTimeEnd, false));
          }
        } else {
          // Otherwise add the original possibility back 
          updatedTimes.add(timeRange);
        }
      }
    }
    return updatedTimes;
  }

  /**
   * Remove TimeRange objects that are shorter than the requested meeting's duration from the
   * list of possible times.
   */
  private ArrayList<TimeRange> removeRangesTooShort(ArrayList<TimeRange> possibleTimes, MeetingRequest request) {
    ArrayList<TimeRange> updatedTimes = new ArrayList<TimeRange>();

    for (TimeRange timeRange: possibleTimes) {
      if (request.getDuration() <= timeRange.duration()) {
        // possibleTime |-----|
        // requestDur    |---|
        updatedTimes.add(timeRange);
      }
    }

    return updatedTimes;
  }
}
