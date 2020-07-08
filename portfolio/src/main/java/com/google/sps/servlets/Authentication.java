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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class Authentication extends HttpServlet {

  public final static String LOGOUT_URL_KEY = "logoutUrl";
  public final static String LOGIN_URL_KEY = "loginUrl";
  public final static String LOGGED_IN_KEY = "loggedIn";
  public final static String EMAIL_KEY = "email";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    
    HashMap<String, String> loginInfo = new HashMap<String, String>();
    Gson gson = new Gson();

    UserService userService = UserServiceFactory.getUserService();

    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String loggedIn = "true";
      String urlToRedirectToAfterUserLogsOut = "/";
      String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);

      loginInfo.put(EMAIL_KEY, userEmail);
      loginInfo.put(LOGGED_IN_KEY, loggedIn);
      loginInfo.put(LOGOUT_URL_KEY, logoutUrl);
    } else {
      String loggedIn = "false";
      String urlToRedirectToAfterUserLogsIn = "/";
      String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);

      loginInfo.put(LOGGED_IN_KEY, loggedIn);
      loginInfo.put(LOGIN_URL_KEY, loginUrl);
    }

    String loginInfoJson = gson.toJson(loginInfo);
    
    response.getWriter().println(loginInfoJson);
  }
}
