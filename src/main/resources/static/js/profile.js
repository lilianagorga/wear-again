function updateProfile() {
    let userData = {
        id: $('#id').val(),
        name: $('#name').val(),
        surname: $('#surname').val(),
        birthdate: $('#birthdate').val(),
        address: $('#address').val(),
        email: $('#email').val(),
        username: $('#username').val(),
        password: $('#password').val(),
        newPassword: $('#newPassword').val(),
        confirmNewPassword: $('#confirmNewPassword').val()
    };

    if (userData.newPassword !== userData.confirmNewPassword) {
        alert('Passwords do not match.');
        return;
    }

    $.ajax({
        url: '/profile/update',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(userData),
        success: function (response) {
            alert('Profile updated successfully');
            $('#name').val(response.name);
            $('#surname').val(response.surname);
            $('#birthdate').val(response.birthdate);
            $('#address').val(response.address);
            $('#email').val(response.email);
        },

        error: function (response) {
            let errorMessage = "Error during profile updating: ";
            if (response.responseText) {
                errorMessage += response.responseText;
            } else {
                errorMessage += "Unknown error";
            }
            alert(errorMessage);
        }
    });
}

$(document).ready(function () {
    let myModal = $('#myModal');
    $('#updateProfileButton').click(function (e) {
        e.preventDefault();
        updateProfile();
    });
    if (myModal.length) {
        myModal.show();
        $('body').css('overflow', 'hidden');
    }
    $('.close').on('click', function () {
        myModal.hide();
        $('body').css('overflow', 'auto');
    });
    $(window).on('click', function (event) {
        if ($(event.target).is(myModal)) {
            myModal.hide();
            $('body').css('overflow', 'auto');
        }
    });
});