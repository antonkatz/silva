$(document).ready(function ()
{
    var locationRetrieve = "http://localhost:8080/results/";

    var progressHolder = $(".progress-holder");
    var uploaded = $(".progress-holder .uploaded");
    var errorHolder = $(".error");
    var errorWrapper = $(".error-wrapper");
    var dataHolder = $("#data-holder");

    var requestInProgress = false;

    var orderOfRowData = ["gene", "position", "ref", "alt", "harmfulness"];

    $('#fileupload').fileupload({
        dataType: 'text',
        submit: function ()
        {
            if (requestInProgress) {
                errorHolder.html("You can only submit one file at a time");
                errorWrapper.show();
                return false
            }
        },
        start: function ()
        {
            requestInProgress = true;
            errorWrapper.hide();
            progressHolder.show();
        },
        progress: function (e, data) {
            var progress = parseInt(data.loaded / data.total * 100);
            uploaded.html(progress + "%");
        },
        done: function (e, response)
        {
            retrieveResults(response.result)
        },
        fail: function (e, response)
        {
            errorOccurred(response.jqXHR);
        }
    });

    function retrieveResults(requestId)
    {
        $.get(locationRetrieve + requestId,
            function (data, status, xhr)
            {
                if (xhr.status == 204) {
                    setTimeout(function() {retrieveResults(requestId)}, 5000)
                } else {
                    requestInProgress = false;
                    progressHolder.hide();
                    dataHolder.html(displayResults(data));
                }
            }, "json")
            .fail(function (xhr)
            {
                errorOccurred(xhr)
            })
    }

    function errorOccurred(xhr)
    {
        requestInProgress = false;
        errorHolder.html(xhr.responseText);
        errorWrapper.show();
        progressHolder.hide();
    }

    function displayResults(results) {
        var html = "";
        $.each(results, function(i, elem) {
            html += "<div>" + displayRow(elem) + "</div>";
        });
        return html;
    }

    function displayRow(rowData) {
        var html = "";
        $.each(rowData, function(i, data) {
            html += "<span class='" + orderOfRowData[i] + "'>" + data + "</span>";
        });
        return html;
    }
});