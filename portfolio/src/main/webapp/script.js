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
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

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
    
    // Add the open class to the clicked tab
    console.log('tabName', tabName);
    buttonClicked = document.getElementById(tabName);
    console.log('buttonClicked', buttonClicked);
    buttonClicked.style.display = 'inline';
    event.currentTarget.className += ' open';   
}

/**
 * Display specified tab when called.
 */
function defaultTab() {
    document.getElementById('default-tab').click();
}