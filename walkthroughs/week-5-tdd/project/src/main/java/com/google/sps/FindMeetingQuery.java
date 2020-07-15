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
    ArrayList<TimeRange> availability = new ArrayList<TimeRange>();
    Event[] eventsArray = events.toArray(new Event[events.size()]);
    availability.add(TimeRange.WHOLE_DAY);
    
    // Return no options if request duration exceeds a day [noOptionsForTooLongOfARequest()]
    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
      return Arrays.asList();
    }

    // Return entire day if there are no attendees [optionsForNoAttendees()]
    if (events.size() == 0) {
      if (request.getAttendees().isEmpty()) {
        return Arrays.asList(TimeRange.WHOLE_DAY);
      }
    }


    // // Split the day into two options before and after events [eventSplitsRestriction()]
    // for (int i = 0; i < eventsArray.length; i++) {
    //   for (int j = 0; j < availability.size(); i++) {
    //     if (availability.get(j).contains(eventsArray[i].getWhen())) {
    //       availability.add(j+1, TimeRange.fromStartEnd(
    //         eventsArray[i].getWhen().end(),
    //         availability.get(j).end(),
    //         true
    //       ));
    //       availability.set(j, TimeRange.fromStartEnd(
    //         availability.get(j).start(),
    //         eventsArray[i].getWhen().start(),
    //         false
    //       ));
    //       System.out.println("Availability: " + availability);
    //     } 
    //   }
    // }
    // return availability;
    
    throw new UnsupportedOperationException("TODO: Implement this method.");
    
  }
}
