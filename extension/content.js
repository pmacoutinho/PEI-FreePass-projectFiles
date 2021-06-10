
chrome.runtime.onMessage.addListener(function (request, sender, sendResponse) {
    var inputs = document.getElementsByTagName("input");
    var loginExists = false;

    for (let i = 0; i<inputs.length; i++) {
        if (inputs[i].type == "password") {
            loginExists = true;        
        }
    }
    
    sendResponse({login: loginExists});
});
