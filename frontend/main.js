$(document).ready(function() {
    var location = "http://localhost:8080/vcf-upload";

    var submitButton = $("button");
    var workingIcon = submitButton.children(".icon");
    var errorHolder = $(".error");
    var errorWrapper = $(".error-wrapper");

        $('#fileupload').fileupload({
            dataType: 'json',
            start: function(){
                errorWrapper.hide();
                workingIcon.show();
            },
            done: function (e, data) {
                $.each(data.result.files, function (index, file) {
                    $('<p/>').text(file.name).appendTo(document.body);
                });
            },
            fail: function(e, response) {
                errorHolder.html(response.textStatus);
                errorWrapper.show();
            },
            always: function() {
                workingIcon.hide();
            }
        });
});