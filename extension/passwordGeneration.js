


var lowerCase = "abcdefghijklmnopqrstuvwxyz";
var upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
var number = "0123456789";
var symbol = "!#$%&'()*+,-./:;<=>?@[]^_`{|}~";


// equivalent to "string".getBytes() in java
String.prototype.getBytes = function () {
    var bytes = new Int8Array(this.length);
    for (var i = 0; i < this.length; i++) {
        bytes[i] = this.charCodeAt(i);
    }

    return bytes;
};

function main(site, user, master, length, count, workbench,
    lowerCase_checked, upperCase_checked, number_checked, symbol_checked) {

    var alpha = "";

    if (lowerCase_checked)
        alpha += lowerCase;
    if (upperCase_checked)
        alpha += upperCase;
    if (number_checked)
        alpha += number;
    if (symbol_checked)
        alpha += symbol;

    var pepper = genPepper(workbench.trim(), count);
    var hash = genHash(user, site, master, pepper, length);
    var value = BigInt("0x" + toHexString(hash));

    return generate("", value, alpha, length);
}

function toHexString(byteArray) {
    return Array.prototype.map.call(byteArray, function(byte) {
      return ('0' + (byte & 0xFF).toString(16)).slice(-2);
    }).join('');
}

function genPepper(pep, count) {
    return "" + pep[ (pep.length - 1) / count ] + pep[(pep.length - 1) % count];
}

function genHash(user, site, master, pepper, length) {
    
    var salt = (user + site).getBytes();
    var password = master + pepper;

    var pbkdf2 = require('pbkdf2');
    var derivedKey = pbkdf2.pbkdf2Sync(password, salt, 10000, length, 'sha1');

    return derivedKey;
}

function generate(res, quo, alpha, max) {
    if (res.length >= max)
        return res;

    var l = "" + alpha.length;
    
    var len = BigInt(l);
    var rem = quo % len;

    quo = quo / len;
    res += alpha[rem];

    return generate(res, quo, alpha, max);
}


// Helper function to print to the screen. 
function print(line) {
    const appDiv = document.getElementById('app');
    const div = document.createElement('div');
    div.innerHTML = line;
    appDiv.appendChild(div)
}





