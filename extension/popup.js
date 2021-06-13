    
document.addEventListener('DOMContentLoaded', function () {   
    function editUrl() {
        if (document.getElementById('site-url').readOnly)
            document.getElementById('site-url').readOnly=false;
        else 
        document.getElementById('site-url').readOnly=true;
        
        
        //document.alert("ola")
    }

    function loginExists (res) {
        const div = document.createElement('div');
        div.textContent = `${res.login}`; //testing purpose
        document.body.appendChild(div);

        //TODO get saved logins
    }
    

    //checks page for login forms
    chrome.tabs.query({currentWindow: true, active: true},
        tabs => {
            chrome.tabs.sendMessage(tabs[0].id, 'check for login forms', loginExists)
        }
    )
    
    document.getElementById('toggle').onclick = editUrl;
    
    // automatically collects the url
    chrome.tabs.query({active: true, lastFocusedWindow: true}, tabs => {
        let url = tabs[0].url;
        document.getElementById('site-url').value = url;
        // use `url` here inside the callback because it's asynchronous!
    })

}, false);





