$(document).ready(function () {

    // Update alias functionality
    $('.updateAlias').on('submit', function () {
        var $form = $(this);
        console.log($form);

        var accountId = $form.data('account-id');

        var $modal = $('#configure-modal-' + accountId);
        console.log($modal);

        var $aliasInput = $form.find('input:text');

        $.ajax({
            type: "POST",
            url: $form.attr('action'),
            data: $form.serialize(), // serializes the form's elements.
            cache: true,
            success: function (data) {
                $aliasInput.data('original-title', data);
                $modal.prev('.list-group-item-heading').find('.accountName').text(data);
                $modal.modal('hide');
            },
            error: function () {
                $('<div class="alert alert-danger" role="alert">Unable to rename account.</div>').prependTo($form);
            }
        });

        return false;
    });

    //Configure button tooltip and click to show modal
    $('.configure').tooltip({
        title: 'Click to configure account',
        trigger: 'hover'
    }).click(function (event) {
        console.log('configure button click');
        event.stopPropagation();

        var $this = $(this);
        console.log($this);

        var accountId = $this.data('account-id');
        console.log(accountId);

        var $modal = $('#configure-modal-' + accountId);
        console.log($modal);

        $modal.modal('show');
        console.log('modal shown');
        return false;
    });

    $('.modal').modal({
        show: false
    }).on('show.bs.modal', function () {
        console.log('modal show');

        var $modal = $(this);
        console.log($modal);

        var $form = $modal.find('form');
        console.log($form);

        $form.find('.alert').remove();

        var $aliasInput = $form.find('input:text');
        $aliasInput.val($aliasInput.data('original-title'));

        var accountId = $form.find('input:hidden').val();

        $modal.find('.user-applications').load('/app/central/loggedInUserOAuthApplications?accountId=' + accountId, function (responseText, textStatus) {
            if (textStatus == 'success') {
                console.log('ajax load');

                $modal.find('a.disconnect-user-application').on('click', function () {
                    console.log('disconnect click');

                    var $this = $(this);
                    var accountId = $this.data('account-id');
                    var userApplicationId = $this.data('user-application-id');
                    console.log(accountId);
                    console.log(userApplicationId);

                    $('#user-applications-ul-' + accountId).addClass("hide");
                    $('#disconnect-confirm-' + userApplicationId).removeClass("hide");
                });

                $modal.find('button.disconnect-cancel').on('click', function () {
                    console.log('disconnect cancel click');

                    var $this = $(this);
                    var accountId = $this.data('account-id');
                    var userApplicationId = $this.data('user-application-id');

                    $('#user-applications-ul-' + accountId).removeClass("hide");
                    $('#disconnect-confirm-' + userApplicationId).addClass("hide");
                });

                $modal.find('button.disconnect').on('click', function () {
                    console.log('disconnect confirm click');

                    var $this = $(this);
                    var accountId = $this.data('account-id');
                    var clientId = $this.data('client-id');
                    var userApplicationId = $this.data('user-application-id');
                    var $form = $('#disconnect-confirm-form-' + userApplicationId);

                    $.ajax({
                        type: "POST",
                        url: '/app/central/revokeAccessToken?accountId=' + accountId + '&clientId=' + clientId,
                        cache: false,
                        success: function () {
                            $('#disconnect-list-item-' + userApplicationId).remove();
                            $('#user-applications-ul-' + accountId).removeClass("hide");
                            $('#disconnect-confirm-' + userApplicationId).addClass("hide");
                        },
                        error: function () {
                            $('<div class="alert alert-danger" role="alert">Unable to disconnect application. Please try again later.</div>').prependTo($form);
                        }
                    });
                });

                $modal.find('a.disconnect-all-applications').on('click', function () {
                    console.log('disconnect all click');

                    var $this = $(this);
                    var accountId = $this.data('account-id');
                    console.log(accountId);

                    $('#user-applications-ul-' + accountId).addClass("hide");
                    $('#disconnect-confirm-all').removeClass("hide");
                });

                $modal.find('button.disconnect-all-cancel').on('click', function () {
                    console.log('disconnect all cancel click');

                    var $this = $(this);
                    var accountId = $this.data('account-id');

                    $('#user-applications-ul-' + accountId).removeClass("hide");
                    $('#disconnect-confirm-all').addClass("hide");
                });

                $modal.find('button.disconnect-all').on('click', function () {
                    console.log('disconnect all confirm click');

                    var $this = $(this);
                    var accountId = $this.data('account-id');

                    $.ajax({
                        type: "POST",
                        url: '/app/central/revokeAccessToken?accountId=' + accountId,
                        success: function () {
                            $('#user-applications-form-group-' + accountId).remove();
                            $('#disconnect-confirm-all').addClass("hide");
                        },
                        error: function () {
                            $('<div class="alert alert-danger" role="alert">Unable to disconnect applications. Please try again later.</div>').prependTo('#disconnect-confirm-form-all');
                        }
                    });
                });
            } else {
                var html = '<div class="alert alert-danger" role="alert">Unable to load applications</div>';

                $('#user-applications-' + accountId).html(html);
            }
        });
    })
});