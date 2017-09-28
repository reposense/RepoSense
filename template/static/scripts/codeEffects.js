$(document).ready(function() {


    $(".line-content").mouseover(function() {
        var methodId = ($(this).attr('id'));
        if (methodId == "") return;
        $("[id=" + methodId + "]").addClass('hover');
    });

    $(".line-content").mouseout(function() {
        var methodId = ($(this).attr('id'));
        if (methodId == "") return;

        $("[id=" + methodId + "]").removeClass('hover');
    });

    $(document).tooltip();

});