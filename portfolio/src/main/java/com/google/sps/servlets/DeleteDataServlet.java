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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that deletes comments. */
@WebServlet("/delete-data")
public class DeleteDataServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/plain");

    UserService userService = UserServiceFactory.getUserService();

    String toDelete = getParameter(request, "key", "all");
    // Get all the comments in datastore
    Query query = new Query("Comment");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    if (!toDelete.equals("all")) {
      try {
        // Get information about specified comment entity.
        Key commentKey = KeyFactory.stringToKey(toDelete);
        System.out.println(commentKey);
        Entity commentEntity = new Entity("Comment");    
        commentEntity = datastore.get(commentKey);
        String originalPoster = (String) commentEntity.getProperty("email");
        String currentUser = userService.getCurrentUser().getEmail();

        // Check if current user has sufficient permissions to delete specified comment.
        if (userService.isUserAdmin() || (currentUser.equals(originalPoster))) {
          datastore.delete(KeyFactory.stringToKey(toDelete));
          response.getWriter().println("Comment Deleted.");
        } else {
          response.getWriter().println("Error: User lacks sufficient permissions to delete comment.");
        }
      } catch (Exception e) {
        System.out.println(e);
        response.getWriter().println("Error: Comment could not be deleted. Please try again.");
      }
    } else {
      // Delete all comments if user is admin.
      if (userService.isUserAdmin()) {  
        for (Entity entity : results.asIterable()) {
            datastore.delete(entity.getKey());
        }
        response.getWriter().println("All Comments Deleted.");
      } else {
        response.getWriter().println("Error: No comments deleted. User is not admin.");
      }
    }
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
