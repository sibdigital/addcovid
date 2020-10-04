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
                    template = '<p><b>Заявка для указанного ИНН отсутствует в базе рассмотренных заявок на портале "Работающая Бурятия".</b></p>';
                    template += '<p>Проверьте правильность ввода ИНН и повторите поиск.</p>';
                    template += '<p>Если вы ранее не подавали заявку на портале "Работающая Бурятия" воспользуйтесь формой подачи заявки выбрав форму соответствующую вашему виду деятельности. <a href="/">Подать новое заявление</a></p>';
                    template += '<p>Проверить ранее поданные заявки можно на сервисе <a href="http://cr.govrb.ru/org_check">Проверка сведений</a> указав ИНН в форме ввода.</p>';
                    $$('result').setHTML(template);
                } else {
                    template = '<p style="color: red"><b>Если вы ранее подавали заявку используя форму (Общие основания (более 100 сотрудников)) "Импорт Excel" актуализируйте информацию в шаблоне и отправьте его используя форму <a href="/upload">Общие основания (более 100 сотрудников)</a></b></p>';
                    if (result.length > 1) {
                        template += '<p><b>Выберите для актуализации наиболее подходяющую для вашего вида деятельности форму заявки.</b></p>';
                    }
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
                        const a = "<a href='/typed_form?request_type=" + item.typeRequest.id + "&id=" + item.id + "'>Актуализировать</a>";
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
