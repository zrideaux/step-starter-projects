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

/**
 * Displays relevant content for whichever tab button is clicked. Hides all
 * other content. 
 */
function openTab(event, tabName) {
    var tabContent, tabButton;

    // Hide all the content by default
    tabContent = document.getElementsByClassName('tab-content');
    for (var i=0; i < tabContent.length; i++) {
        tabContent[i].style.display = 'none';
    }

    // Remove the open class from each tab when one is clicked
    tabButton = document.getElementsByClassName('tab-button');
    for (var i=0; i < tabContent.length; i++) {
        tabButton[i].className = tabButton[i].className.replace(' open', '');
    }
    
    // Open the clicked tab (don't add class to default tab)
    buttonClicked = document.getElementById(tabName);
    buttonClicked.style.display = 'inline';
    if (event !== undefined) {
        event.currentTarget.className += ' open';   
    }
}

/**
 * Display a specified default tab when called.
 */
function defaultTab() {
    document.getElementById('default-tab').className += ' open';
    openTab(event, 'about');
}

/**
 * Get comments from the servlet and display them on the page.
 */
function getMessageFromServlet() {
    fetch('/data').then(response => response.json()).then(commentsArray => {
        commentSection = document.getElementById('comment-section');
        console.log(commentsArray);
        for (var i= commentsArray.length - 1; i >= 0; i--) {
            newComment = document.createElement("li");
            
            commentUser = document.createElement("p");
            commentUser.className = "comment-username";
            commentUser.innerText = commentsArray[i].user;
            commentText = document.createElement("p");
            commentText.className = "comment-text";
            commentText.innerText = commentsArray[i].comment;
            
            newComment.appendChild(commentUser);
            newComment.appendChild(commentText);
            
            commentSection.appendChild(newComment);
        }
    });
}
