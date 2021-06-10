function editUrl() {
    if (document.getElementById('site-url').readOnly)
        document.getElementById('site-url').readOnly=false;
    else 
    document.getElementById('site-url').readOnly=true;
    
    
    //document.alert("ola")
};

document.getElementById('toggle').onclick = editUrl;

chrome.tabs.query({active: true, lastFocusedWindow: true}, tabs => {
    let url = tabs[0].url;
    document.getElementById('site-url').value = url;
    // use `url` here inside the callback because it's asynchronous!
});



