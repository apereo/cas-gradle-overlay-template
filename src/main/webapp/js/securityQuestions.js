$(document).ready(function () {
    var $form = $("form");
    var $listGroup = $(".list-group");
    var $answer = $(".answer");
    var $answerInput = $answer.find("input");
    var $animationHolder = $(".animationHolder");

    var $tempListGroup;
    var $tempAnswer = $answer.detach();

    var answerTransitionIn = "fadeInUp";
    var answerTransitionOut = "fadeOutDown";
    var questionsTransitionIn = "fadeInUp";
    var questionsTransitionOut = "fadeOutDown";
    var duration = 0.3;

    $listGroup.find(".list-group-item").animo({animation: "infBounceIn", duration: .5});

    $(".skip").on("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        $("#skipInput").val("true");
        $form.submit();
    });

    $listGroup.on("click", ".list-group-item", function (e) {
        e.preventDefault();
        e.stopPropagation();

        var $this = $(this);
        var $securityQuestionId = $("#securityQuestionId");

        var id = $this.data("question-id");
        var icon = $this.data("question-icon");
        var questionText = $this.find(".question-text").text();

        $securityQuestionId.val(id);
        $answer.find("#question-icon").removeClass().addClass("ic").addClass("vcenter").addClass("hidden-xs").addClass(icon);
        $answer.find(".question-text").text(questionText);

        $listGroup.animo({animation: questionsTransitionOut, duration: duration}, function () {
            $tempAnswer.appendTo($animationHolder).animo({animation: answerTransitionIn, duration: duration});
            $tempListGroup = $listGroup.detach();
            $answerInput.val("");
            $answerInput.focus();
        });
    });

    $answer.find(".well").on("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        $answer.animo({animation: answerTransitionOut, duration: duration}, function () {
            $listGroup.appendTo($animationHolder).animo({animation: questionsTransitionIn, duration: duration});
            $tempAnswer = $answer.detach();
        });
    });
});

