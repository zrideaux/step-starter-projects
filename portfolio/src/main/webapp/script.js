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

    window.history.pushState('page2', 'Title', '?tab=' + tabName);
}

/**
 * Display a specified default tab when called.
 */
function defaultTab(tabName='about') {
    var queryParameters = new URLSearchParams(window.location.search);
    if (queryParameters.has('tab') === false) {
        openTab(event, tabName);
        document.getElementById(tabName + '-tab').className += ' open';
    }
}

/**
 * Display a previously open tab when called.
 * Used for refreshes such as comment submissions.
 */
function reopenTab() {
    var queryParameters = new URLSearchParams(window.location.search);
    if (queryParameters.has('tab')) {
        tabName = queryParameters.get('tab');
        openTab(event, tabName);
        document.getElementById(tabName + '-tab').className += ' open';
    }
}

/**
 * Get comments from the servlet and display them on the page.
 */
function getCommentsFromServlet() {
    var numberOfComments = document.getElementById('number-of-comments').value;
    var url = '/data?comments=' + numberOfComments;
    console.log(url);

    fetch(url, {method: 'GET'})
    .then(response => response.json())
    .then(commentsArray => {
        // Clear comment section
        commentSection = document.getElementById('comment-section');
        commentSection.innerHTML = '';
        console.log(commentsArray);
        
        // Fill comment section based on selection
        for (var i = 0; i < commentsArray.length; i++) {
            newComment = document.createElement('li');

            commentUser = document.createElement('span');
            commentUser.className = 'comment-username';
            commentUser.innerText = commentsArray[i].user;
            deleteLink = document.createElement('button');
            deleteLink.className = 'comment-delete';
            deleteLink.innerText = 'Delete ' + commentsArray[i].key;
            deleteLink.setAttribute('onclick', 'deleteComment(\'' + commentsArray[i].key + '\')');
            commentText = document.createElement('p');
            commentText.className = 'comment-text';
            commentText.innerText = commentsArray[i].comment;

            newComment.appendChild(commentUser);
            newComment.appendChild(deleteLink);
            newComment.appendChild(commentText);
            
            commentSection.appendChild(newComment);
        }
    });
}

/**
 * Add a comment to datastore when called.
 */
function addComment() {
    // Get comment from page when submit pressed.
    var username = document.getElementById('input-username').value;
    var comment = document.getElementById('input-comment').value;

    var http = new XMLHttpRequest();
    var url = '/data';
    var commentData = 'username=' + username + '&comment=' + comment;

    // Send a POST request to DataServlet
    http.open('POST', url, true);
    http.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');

    http.onreadystatechange = function () {
        console.log(http.status);
        if (http.readyState == 4 && http.status == 200) {
            // Refresh comment section
            getCommentsFromServlet();
        }
    }
    
    http.send(commentData);

    // fetch('/data', {method: "POST"}).then(response => response.text()).then(text => {
    //     // Clear comment section
    //     commentSection = document.getElementById('comment-section');
    //     commentSection.innerHTML = '';
    //     console.log(text);
    // });
}

/**
 * Delete all comments in datastore when called.
 */
function deleteAllComments() {
    fetch('/delete-data', {method: "POST"}).then(response => response.text()).then(text => {
        // Clear comment section
        commentSection = document.getElementById('comment-section');
        commentSection.innerHTML = '';
        console.log(text);
    });
}

/**
 * Delete a single specified comment when called.
 */
function deleteComment(key) {
    fetch('/delete-data?key=' + key, {method: "POST"}).then(response => response.text()).then(text => {
        // Refresh comment section
        getCommentsFromServlet();
        console.log(text);
    });
}