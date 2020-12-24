const consentPersonalDataModal = {
    view: "window",
    id: "consentPersonalDataModalId",
    minWidth: 200,
    maxWidth: 550,
    position: "center",
    // modal: true,
    move: false,
    close: false,
    head: "Согласие на обработку персональных данных",
    body: {
        view: 'form',
        rows: [
            {
                view: 'template',
                height: 550,
                readonly: true,
                scroll: true,
                src: 'getConsentPersonalData'
            },
            {
                view: 'checkbox',
                id: 'isAgreed',
                name: 'agree',
                labelPosition: 'top',
                labelRight: 'Согласен на обработку персональных данных',
                value: false
            },
            {
                cols: [
                    {},
                    {
                        view: 'button',
                        css: 'webix_primary',
                        value: 'Подтвердить',
                        align: 'right',
                        click: function () {
                            if ($$('isAgreed').getValue() == 0) {
                                webix.send("logout");
                            } else {
                                webix.ajax().get('saveConsentPersonalData').then(function (data) {
                                    if (data.text() === 'Согласие сохранено') {
                                        if (document.body.clientWidth < 760) {
                                            layout = webix.ui(smallMainForm)
                                        } else {
                                            layout = webix.ui(bigMainForm);
                                        }
                                        $$('consentPersonalDataModalId').hide();
                                    }
                                    else {
                                        webix.message(data.text(), 'error');
                                    }
                                })
                            }
                        }
                    }
                ]
            }
        ]
    }
}

function checkConsentPersonalDataProc(){
    var xhr = webix.ajax().sync().get('check_consent');
    var jsonResponse = JSON.parse(xhr.responseText);

    if (jsonResponse.isAgreed != null) {
        return jsonResponse.isAgreed;
    }
    else {
        return false;
    }
}
