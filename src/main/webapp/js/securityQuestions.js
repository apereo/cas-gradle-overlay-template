$(document).ready(function() {
    var $form = $("form");
    var $listGroup = $(".list-group");
    var $answer = $(".answer");
    var $animationHolder = $(".animationHolder");

    var $tempListGroup;
    var $tempAnswer = $answer.detach();

    var answerTransitionIn = "fadeInRight";
    var answerTransitionOut = "fadeOutLeft";
    var questionsTransitionIn = "fadeInLeft";
    var questionsTransitionOut = "fadeOutRight";
    var duration = 0.3;

    $listGroup.animo({animation: "bounce"});

    $(".skip").on("click", function(e) {
        e.preventDefault();
        e.stopPropagation();

        $("#skipInput").val("true");
        $form.submit();
    });

    $listGroup.on("click", ".list-group-item", function(e) {
        e.preventDefault();
        e.stopPropagation();

        var $this = $(this);
        var $securityQuestionId = $("#securityQuestionId");

        var id = $this.data("question-id");
        var questionText = $this.find(".question-text").text();

        $securityQuestionId.val(id);
        $answer.find(".question-text").text(questionText);

        $listGroup.animo({animation: questionsTransitionOut, duration: duration}, function() {
            $tempAnswer.appendTo($animationHolder).animo({animation: answerTransitionIn, duration: duration});
            $tempListGroup = $listGroup.detach();
        });
    });

    $answer.find(".well").on("click", function(e) {
        e.preventDefault();
        e.stopPropagation();

        $answer.animo({animation: answerTransitionOut, duration: duration}, function() {
            $listGroup.appendTo($animationHolder).animo({animation: questionsTransitionIn, duration: duration});
            $tempAnswer = $answer.detach();
        });
    });
});

