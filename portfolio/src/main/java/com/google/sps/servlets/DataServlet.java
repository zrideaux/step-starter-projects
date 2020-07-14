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

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private String commentsJson = new String();
  private UserService userService = UserServiceFactory.getUserService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
          
    ArrayList<HashMap> allComments = new ArrayList<HashMap>();
    
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    Boolean userIsLoggedIn = userService.isUserLoggedIn();
    String currentUser = new String();
    if (userIsLoggedIn) {
        currentUser = (String) userService.getCurrentUser().getEmail(); 
    }

    for (Entity entity : results.asIterable()) {
      // Retrieve comment
      long id = entity.getKey().getId();
      String username = (String) entity.getProperty("user");
      String text = (String) entity.getProperty("comment");
      String key = (String) KeyFactory.keyToString(entity.getKey());
      String email = (String) entity.getProperty("email");


      // Put comment information into an object
      HashMap<String, String> comment = new HashMap<String, String>();
      comment.put("user", username);
      comment.put("comment", text);
      comment.put("key", key);
      if (userIsLoggedIn && (userService.isUserAdmin() || currentUser.equals(email))) {
        comment.put("deletable", "true");
      } else {
        comment.put("deletable", "false");
      }
      allComments.add(comment);
    }

    // Limit the number of comments shown.
    int numberOfCommentsShown = Integer.parseInt(getParameter(request, "comments", "5"));
    ArrayList<HashMap> shownComments = new ArrayList<HashMap>();
    if (numberOfCommentsShown == -1) {
      shownComments = allComments;
    } else {
      for (int i=0; i < numberOfCommentsShown  && i < allComments.size(); i++) {
        shownComments.add(allComments.get(i));
      }
    }
    

    // Convert comment data to Json
    Gson gson = new Gson();
    commentsJson = gson.toJson(shownComments);
    System.out.println("Displayed Comments [DataServlet.java] -- " + commentsJson);
    
    response.setContentType("application/json");
    response.getWriter().println(commentsJson);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get data from input form.
    String username = getParameter(request, "username", "");
    String text = getParameter(request, "comment", "");
    long timestamp = System.currentTimeMillis();
    String email = userService.getCurrentUser().getEmail();

    // Create comment entity
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("user", username);
    commentEntity.setProperty("comment", text);
    commentEntity.setProperty("timestamp", timestamp);
    commentEntity.setProperty("email", email);

    // Create datastore instance and store comment entity
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
  }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      System.out.println("Default");
      return defaultValue;
    }
    return value;
  }
}
