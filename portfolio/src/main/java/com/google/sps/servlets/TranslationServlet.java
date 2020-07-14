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

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/translate")
public class TranslationServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    
    // Get the original comments and the desired language.
    String originalComments = request.getParameter("comments");
    String languageCode = request.getParameter("lang");
    System.out.println("Original Comments [TranslationServlet] -- " + originalComments);

    // Convert the comments query string to a JSON array.
    JsonParser parser = new JsonParser();
    JsonArray commentsJsonArray = parser.parse(originalComments).getAsJsonArray();

    // Add each comment's text to a list of strings to be translated. 
    ArrayList<String> toTranslate = new ArrayList<String>();
    for (int i = 0; i < commentsJsonArray.size(); i++) {
      toTranslate.add(commentsJsonArray.get(i).getAsJsonObject().get("comment").getAsString());
    }
    
    // Do the translation and save to a list of translation objects
    Translate translate = TranslateOptions.getDefaultInstance().getService();
    List<Translation> translations =
      translate.translate(toTranslate, Translate.TranslateOption.targetLanguage(languageCode));
    
    // Update each comment in the JSON with the translation
    for (int i = 0; i < commentsJsonArray.size(); i++) {
      commentsJsonArray.get(i).getAsJsonObject().addProperty("comment", translations.get(i).getTranslatedText());
    }

    // Output the translation
    String translatedCommentsJson = gson.toJson(commentsJsonArray);
    System.out.println("Translated Comments [TranslationServlet] -- " + translatedCommentsJson);
    response.setContentType("application/json; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(translatedCommentsJson);
  }
}
