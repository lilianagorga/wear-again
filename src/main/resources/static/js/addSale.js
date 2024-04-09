$(document).ready(function () {
    const addSaleModal = $('#addSaleModal');

    $('.add-to-sale').on('click', function () {
        const itemId = $(this).data('item-id');
        fetch(`/sales/add/${itemId}`, { method: 'POST' })
            .then(response => {
                if (response.ok) {
                    return response.text();
                }
                else if (response.status === 401) {
                    addSaleModal.show();
                    $('body').css('overflow', 'hidden');
                    return Promise.reject('Unauthorized');
                } else {
                    return response.text().then(text => { throw new Error(text); });
                }
            }).then(message => {
                alert(message);
            }).catch(error => {
                if (error.message === 'Not authorized') {
                    console.error('An error occurred:', error);
                }
            });
    });

    $('.close', addSaleModal).on('click', function () {
        addSaleModal.hide();
        $('body').css('overflow', 'auto');
    });

    $(window).on('click', function (event) {
        if ($(event.target).is(addSaleModal)) {
            addSaleModal.hide();
            $('body').css('overflow', 'auto');
        }
    });
});
