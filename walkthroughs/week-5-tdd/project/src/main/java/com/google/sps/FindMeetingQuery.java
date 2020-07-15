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

  
    if (events.size() == 0) {
      // Return entire day if there are no attendees [optionsForNoAttendees()]
      // or no meeting conflicts [noConflicts()]
      return Arrays.asList(TimeRange.WHOLE_DAY);
    } else {
      // Split the day into two options before and after events [eventSplitsRestriction()]
      String requester = new String();
      if (request.getAttendees().size() == 1) {
        requester = request.getAttendees().iterator().next();
        System.out.println(requester);
      }


      for (int i = 0; i < eventsArray.length; i++) {
        for (int j = 0; j < availability.size(); j++) {
          if (request.getAttendees().size() != 1 || eventsArray[i].getAttendees().contains(requester)) {
            int eventStart = eventsArray[i].getWhen().start();
            int eventEnd = eventsArray[i].getWhen().end();
            int availabilityStart = availability.get(j).start();
            int availabilityEnd = availability.get(j).end();

            if (availability.get(j).overlaps(eventsArray[i].getWhen())) {
              availability.add(j+1, TimeRange.fromStartEnd(eventEnd, availabilityEnd, false));
              availability.set(j, TimeRange.fromStartEnd(availabilityStart, eventStart, false));
            } 
          }
        }
      }
    
      System.out.println(availability);
      for (int k = 0; k < availability.size(); k++) {
        int availabilityStart = availability.get(k).start();
        int availabilityEnd = availability.get(k).end();
        
        // Remove spans too short. 
        if (request.getDuration() > (availabilityEnd - availabilityStart)) {
          availability.remove(k);
          k--;
        }
      }
    
      if (true) {
        return availability;
      }
    }

    throw new UnsupportedOperationException("TODO: Implement this method."); 
  }
}
