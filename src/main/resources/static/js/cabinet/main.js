webix.i18n.setLocale("ru-RU");

const dateFormat = webix.Date.dateToStr("%d.%m.%Y %H:%i:%s")

function view_section(title) {
    return {
        view: 'template',
        type: 'section',
        template: title
    }
}

const commonInfo = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                view: 'form',
                id: 'form',
                complexData: true,
                elements: [
                    view_section('Данные о вашей организации'),
                    {
                        type: 'space',
                        margin: 5,
                        cols: [
                            {
                                rows: [
                                    {
                                        view: 'text',
                                        name: 'name',
                                        id: 'organizationName',
                                        label: 'Полное наименование организации/фамилия, имя, отчество индивидуального предпринимателя',
                                        labelPosition: 'top',
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true
                                    },
                                    {
                                        view: 'text',
                                        name: 'shortName',
                                        label: 'Краткое наименование организации',
                                        labelPosition: 'top',
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true
                                    },
                                    {
                                        view: 'text',
                                        name: 'inn',
                                        label: 'ИНН',
                                        labelPosition: 'top',
                                        validate: function (val) {
                                            return !isNaN(val * 1);
                                        },
                                        //attributes:{ type:"number" },
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true
                                    },
                                    {
                                        view: 'text',
                                        name: 'ogrn',
                                        label: 'ОГРН',
                                        validate: function (val) {
                                            return !isNaN(val * 1);
                                        },
                                        //attributes:{ type:"number" },
                                        labelPosition: 'top',
                                        //validate:webix.rules.isNumber(),
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true
                                    },
                                    {
                                        view: 'text',
                                        name: 'email',
                                        label: 'e-mail',
                                        labelPosition: 'top',
                                        validate: webix.rules.isEmail,
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true
                                    },
                                    {
                                        view: 'text',
                                        name: 'phone',
                                        label: 'Телефон',
                                        labelPosition: 'top',
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true
                                    },
                                ]
                            },
                            {
                                rows: [
                                    {
                                        view: 'text',
                                        name: 'okved',
                                        id: 'organizationOkved',
                                        label: 'Основной вид осуществляемой деятельности (отрасль)',
                                        labelPosition: 'top',
                                        required: true
                                    },
                                    {
                                        view: 'textarea',
                                        name: 'okvedAdd',
                                        label: 'Дополнительные виды осуществляемой деятельности',
                                        height: 100,
                                        labelPosition: 'top'
                                    }
                                ]
                            }
                        ]
                    },
                    view_section('Адресная информация'),
                    {
                        view: 'textarea',
                        name: 'addressJur',
                        label: 'Юридический адрес',
                        labelPosition: 'top',
                        height: 80,
                        required: true
                    }
                ],
                url: 'organization'
            }
        ],
    }
}

const requests = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                autowidth: true,
                autoheight: true,
                rows: [
                    {
                        view: 'datatable',
                        id: 'requests_table',
                        minHeight: 570,
                        select: 'row',
                        navigation: true,
                        resizeColumn: true,
                        pager: 'Pager',
                        datafetch: 25,
                        columns: [
                            {
                                id: "orgName",
                                header: "Организация/ИП",
                                template: "#organization.name#",
                                adjust: true
                            },
                            {id: "inn", header: "ИНН", template: "#organization.inn#", adjust: true},
                            {id: "ogrn", header: "ОГРН", template: "#organization.ogrn#", adjust: true},
                            {
                                id: "typeRequest",
                                header: "Тип заявки",
                                template: "#typeRequest.activityKind#",
                                adjust: true
                            },
                            {id: "orgPhone", header: "Телефон", template: "#organization.phone#", adjust: true},
                            {id: "time_Create", header: "Дата подачи", adjust: true, format: dateFormat},
                            {id: "personSlrySaveCnt", header: "Числ. с сохр. зп", adjust: true},
                            {id: "personOfficeCnt", header: "Числ. работающих", adjust: true},
                            {id: "personRemoteCnt", header: "Числе. удал. режим", adjust: true},
                        ],
                        scheme: {
                            $init: function (obj) {
                                obj.time_Create = obj.timeCreate.replace("T", " ") //dateUtil.toDateFormat(obj.timeCreate);
                            },
                        },
                        on: {
                            onBeforeLoad: function () {
                                this.showOverlay("Загружаю...");
                            },
                            onAfterLoad: function () {
                                this.hideOverlay();
                                if (!this.count()) {
                                    this.showOverlay("Отсутствуют данные")
                                }
                            },
                            onLoadError: function () {
                                this.hideOverlay();
                            },
                        },
                        url: 'org_requests'
                    },
                    {
                        view: 'pager',
                        id: 'Pager',
                        height: 38,
                        size: 25,
                        group: 5,
                        template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                    }
                ]
            }
        ]
    }
}

const settings = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                view: 'form',
                id: 'form_pass',
                complexData: true,
                elements: [
                    {
                        cols: [
                            {
                                rows: [
                                    {
                                        view: 'text',
                                        id: 'new_pass',
                                        name: 'password',
                                        label: 'Новый пароль',
                                        labelPosition: 'top',
                                        type: 'password'
                                    },
                                    {
                                        view: 'text',
                                        id: 'retry_pass',
                                        label: 'Подтвердите новый пароль',
                                        labelPosition: 'top',
                                        type: 'password',
                                        on: {
                                            onChange(newVal, oldVal) {
                                                if (newVal === $$('new_pass').getValue()) {
                                                    $$('save_pass').enable();
                                                } else {
                                                    $$('save_pass').disable();
                                                }
                                            }
                                        }
                                    },
                                    {
                                        view: 'button',
                                        id: 'save_pass',
                                        value: 'Применить',
                                        click: () => {
                                            webix.ajax().headers({'Content-Type': 'application/json'})
                                                .post('/save_pass', $$('form_pass').getValues()).then(function (data) {
                                                if (data.text() === 'Пароль обновлен') {
                                                    $$('new_pass').setValue('');
                                                    $$('retry_pass').setValue('');
                                                    $$('save_pass').disable();
                                                    webix.message(data.text(), 'success');
                                                } else {
                                                    webix.message(data.text(), 'error');
                                                }
                                            });
                                        },
                                        disabled: true
                                    }
                                ]
                            },
                            {},
                            {}
                        ]
                    }
                ]
            }
        ]
    }
}

webix.ready(function() {
    let layout = webix.ui({
        rows: [
            {
                view: 'toolbar',
                autoheight: true,
                rows: [
                    {
                        responsive: 't1',
                        css: 'webix_dark',
                        cols: [
                            {
                                view: 'label',
                                width: 300,
                                label: `<span style="font-size: 1.0rem">${APPLICATION_NAME}. </span>`,
                            },
                            {
                                view: 'label',
                                minWidth: 400,
                                label: '<span style="font-size: 1.0rem">Личный кабинет</span>',
                            },
                            {},
                            {
                                view: 'label',
                                label: '<a href="/logout" title="Выйти">Выйти</a>',
                                align: 'right'
                            }
                        ]
                    }
                ]
            },
            {
                cols: [
                    {
                        view: 'sidebar',
                        css: 'webix_dark',
                        data: [
                            { id: "CommonInfo", value: 'Общая информация' },
                            { id: "Requests", value: 'Заявки' },
                            { id: "Settings", value: 'Настройки' },
                        ],
                        on: {
                            onAfterSelect: function(id) {
                                let view;
                                switch (id) {
                                    case 'CommonInfo': {
                                        view = commonInfo;
                                        break;
                                    }
                                    case 'Requests': {
                                        view = requests;
                                        break;
                                    }
                                    case 'Settings': {
                                        view = settings;
                                        break;
                                    }
                                }
                                webix.ui({
                                    id: 'content',
                                    rows: [
                                        view
                                    ]
                                }, $$('content'))
                            }
                        }
                    },
                    {
                        id: 'content'
                    }
                ],
            }
        ]
    })

    webix.event(window, "resize", function (event) {
        layout.define("width",document.body.clientWidth);
        layout.resize();
    });
})
