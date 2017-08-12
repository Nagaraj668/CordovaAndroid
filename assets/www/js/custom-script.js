
var URL = "http://192.168.0.5/http/";
var requestData = {
    name: 'Nagaraj',
    ID: 3123
};

function getData() {

    $.ajax ({
        url : URL + 'getJson.php',
        type: 'GET',
        accept: 'application/json',
        crossDomain: true,
        success: function (data, status, headers) {
            $('#get-response').text(JSON.stringify(data));
            console.log("Status: " + status + "\nResponse: "+ JSON.stringify(data));
        }
    });

}

function postData() {
    $.ajax ({
        url : URL + 'postJson.php',
        type: 'POST',
        accept: 'application/json',
        contentType: 'application/json',
        crossDomain: true,
        data: JSON.stringify(requestData),
        success: function (data, status, headers) {
            $('#post-response').text(JSON.stringify(data));
            console.log("Status: " + status + "\nResponse: "+ JSON.stringify(data));
        }
    });
}