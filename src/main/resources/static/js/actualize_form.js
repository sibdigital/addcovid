webix.i18n.setLocale("ru-RU");

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
                            label: `<span style="font-size: 1.3rem">Актуализация</span>`,
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
                                    value: 'Найти предыдущие заявки',
                                    align: 'center',
                                    click: function () {
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
                                        if (inn.length === 9) {
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
                                            webix.ajax('actualized_doc_requests?inn=' + inn).then(function (data) {
                                                const result = data.json();
                                                if (result.length === 0) {
                                                    let html = '<div>Заявка для указанного ИНН отсутствует в базе рассмотренных заявок на портале "Работающая Бурятия".</div>';
                                                    $$('result').setHTML(html);
                                                } else {
                                                    let template = '<table>';
                                                    template += '<thead><tr><th>Наименование организации</th><th>Дата подачи</th><th>Тип заявки</th><th></th></tr></thead>';
                                                    template += '<tbody>';
                                                    result.forEach(item => {
                                                        const a = "<a href='/typed_form?request_type=" + item.typeRequest.id + "&id=" + item.id + "'>Актуализировать</a>";
                                                        template += '<tr><td>' + item.organization.name + '</td><td>' + item.timeCreate + '</td><td>' + item.typeRequest.activityKind + '</td><td>' + a + '</td></tr>';
                                                    });
                                                    template += '</tbody>';
                                                    template += '</table>';
                                                    $$('result').setHTML(template);
                                                }
                                                $$('form').hideProgress();
                                            }).catch(function () {
                                                webix.message('Не удалось получить данные', 'error');
                                                $$('form').hideProgress();
                                            });
                                        }, 500);
                                    }
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
});
