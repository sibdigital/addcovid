webix.i18n.setLocale("ru-RU");

const searchRequests = () => {
    const inn = $$('searchInn').getValue();
    if (inn === '') {
        $$('searchInn').focus();
        webix.message('ИНН не введен', 'error');
        return;
    } else if (isNaN(inn)) {
        $$('searchInn').focus();
        webix.message('ИНН не соответствует формату', 'error');
        return;
    }
    let type = '';
    if (inn.length === 10) {
        type = 'egrul';
    } else if (inn.length === 12) {
        type = 'egrip';
    }
    if (type === '') {
        $$('searchInn').focus();
        webix.message('ИНН не соответствует формату', 'error');
        return;
    }

    $$('form').showProgress();

    setTimeout(function () {
        webix.ajax('actualized_doc_requests?inn=' + inn)
            .then(function (data) {
                const result = data.json();
                let template;
                var options = {
                    year: 'numeric',
                    month: 'numeric',
                    day: 'numeric',
                };
                if (result.length === 0) {
                    template = MESSAGES.actualization.requestNotFound;
                    $$('result').setHTML(template);
                } else {
                    template = MESSAGES.actualization.requestFound;
                    template += '<div class="table-responsive-sm"> <table class="table">';
                    template += '<thead><tr section="header">' +
                        '<th scope="col" class="text-center" style="width: 30%">Наименование организации</th>' +
                        '<th scope="col" class="text-center" style="width: 10%" >Дата подачи</th>' +
                        '<th scope="col" class="text-center" style="width: 10%" >Дата утверждения</th>' +
                        '<th scope="col" class="text-center" style="width: 30%" >Тип заявки</th>' +
                        '<th scope="col" class="text-center" style="width: 20%" ></th>' +
                        '</tr>' +
                        '</thead>';
                    template += '<tbody>';
                    result.forEach(item => {
                        var a = "<a href='/typed_form?request_type=" + item.typeRequest.id + "&id=" + item.id + "'>Актуализировать</a>";

                        if (item.idActualizedRequest != undefined) {
                            a = "ВЫ АКТУАЛИЗИРОВАЛИ ЗАЯВКУ";
                        }
                        var timeCreate = item.timeCreate ? new Date(item.timeCreate) : "";
                        var dateReview = item.timeReview ? new Date(item.timeReview) : "";
                        template += '<tr style="border-bottom:solid grey 2px;">' +
                            '<td class="text-center">' + item.organization.name + '</td>' +
                            '<td class="text-center">' + timeCreate.toLocaleString("ru", options) + '</td>' +
                            '<td class="text-center">' + dateReview.toLocaleString("ru", options) + '</td>' +
                            '<td class="text-center">' + item.typeRequest.activityKind + '</td>' +
                            '<td class="text-center">' + a + '</td>' +
                            '</tr>';
                    });
                    template += '</tbody>';
                    template += '</table> </div>';
                }
                $$('result').setHTML(template);
                $$('form').hideProgress();
            }).catch(function () {
            webix.message('Не удалось получить данные', 'error');
            $$('form').hideProgress();
        });
    }, 500);
};

webix.ui({
    container: 'app',
    height: document.body.clientHeight,
    css: {margin: 'auto'},
    rows: [
        {
            view: 'toolbar',
            autoheight: true,
            id: 't1',
            rows: [
                {
                    responsive: 't1',
                    css: 'webix_dark',
                    cols: [
                        {},
                        {
                            view: 'label',
                            maxWidth: 300,
                            align: 'center',
                            label: `<span style="font-size: 1.3rem">Актуализация утвержденных заявок</span>`,
                        },
                        {}
                    ]
                }
            ]
        },
        {
            rows: [
                {
                    id: 'form',
                    view: 'form',
                    complexData: true,
                    elements: [
                        {
                            cols: [
                                {
                                    view: 'text',
                                    name: 'searchInn',
                                    id: 'searchInn',
                                    // label: 'ИНН',
                                    placeholder: 'ИНН',
                                },
                                {
                                    view: 'button',
                                    id: 'egrul_load_button',
                                    css: 'webix_primary',
                                    value: 'Найти утвержденные заявки',
                                    align: 'center',
                                    click: searchRequests
                                },
                            ],
                        },
                    ]
                },
                {
                    view: 'template',
                    id: 'result',
                    autoheight: 'auto',
                }
            ]
        }
    ]
});

webix.ready(function () {
    webix.extend($$('form'), webix.ProgressBar);

    if (INN) {
        $$('searchInn').setValue(INN);
        searchRequests();
    }
});
