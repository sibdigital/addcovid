function showRequestSubsidyCreateForm(data) {
    const availableSubsidies = findAvailableSubsidies();
    showBtnBack(request_subsidy_list, 'request_subsidy_table');
    if (availableSubsidies.length > 0) {
        webix.ui({
            id: 'content',
            rows: [
                requestSubsidyWizard
            ]
        }, $$('content'));

        if (data != null) {
            $$('requestSubsidyId').setValue(data.id);
            let availableSubsidiesIds = availableSubsidies.map(a => a.id);

            if (availableSubsidiesIds.includes(data.subsidyId)) {
                $$('subsidyId').setValue(data.subsidyId);
                $$('subsidyName').setValue(data.subsidyName);
            }

            $$('reqBasis').setValue(data.reqBasis);
            $$('reqBasisFinal').setValue(data.reqBasis);
        }

    } else {
        webix.ui({
            id: 'content',
            rows: [
                noAvailableSubsidiesForm
            ]
        }, $$('content'))
    }
}

function findAvailableSubsidies() {
    const xhr = webix.ajax().sync().get('available_subsidies');
    return JSON.parse(xhr.responseText);
}

const noAvailableSubsidiesForm = {
    rows: [
        {
            autowidth: true,
            autoheight: true,
            rows: [
                {
                    view: 'template',
                    template: "Заявки по всем доступным мерам поддержки поданы, вы не можете подать новую заявку.",
                    autoheight: true,
                    borderless: true,
                },
                {}
            ]
        }
    ]
}

const requestSubsidyStep1 = {
    view: 'form',
    id: 'request_subsidy_step1',
    complexData: true,
    elements: [
        {
            view: 'richselect',
            id: 'subsidyId',
            label: 'Мера поддержки',
            labelPosition: 'top',
            required: true,
            invalidMessage: 'Поле не может быть пустым',
            options: 'available_subsidies',
            on: {
                onChange: function () {
                    var textValue = $$('subsidyId').data.text;
                    $$('subsidyName').setValue(textValue);
                }
            }
        },
        {
            view: 'textarea',
            id: 'reqBasis',
            label: 'Обоснование заявки',
            labelPosition: 'top',
            height: 150,
            minWidth: 250,
            on: {
                onChange: function () {
                    $$('reqBasisFinal').setValue($$('reqBasis').getValue());
                }
            }
        },
        view_section('Данные о вашей организации'),
        {
            margin: 5,
            responsive: "respLeftToRight",
            cols: [
                {
                    minWidth: 300,
                    rows: [
                        {
                            view: 'text',
                            name: 'shortName',
                            id: 'shortOrganizationName',
                            label: 'Краткое наименование организации',
                            labelPosition: 'top',
                            readonly: true,
                        },
                        {
                            view: 'textarea',
                            name: 'name',
                            height: 80,
                            id: 'organizationName',
                            label: 'Полное наименование организации/фамилия, имя, отчество индивидуального предпринимателя',
                            labelPosition: 'top',
                            readonly: true,
                        },
                        {
                            id: "innplace",
                            rows: []
                        },
                        {
                            responsive: 'innplace',
                            cols: [
                                {
                                    view: 'text',
                                    name: 'inn',
                                    id: "inn",
                                    label: 'ИНН',
                                    minWidth: 200,
                                    labelPosition: 'top',
                                    readonly: true,
                                },
                                {
                                    view: 'text',
                                    name: 'ogrn',
                                    id: 'ogrn',
                                    label: 'ОГРН',
                                    minWidth: 200,
                                    validate: function (val) {
                                        return !isNaN(val * 1);
                                    },
                                    labelPosition: 'top',
                                    readonly: true,
                                },
                            ]
                        },
                    ]
                },
                {
                    minWidth: 300,
                    id: "respLeftToRight",
                    rows:
                        [
                            {
                                height: 27,
                                view: 'label',
                                label: 'Основной вид осуществляемой деятельности (отрасль)',
                            },
                            {
                                view: 'list',
                                layout: 'x',
                                id:"okved_main",
                                css: {'white-space': 'normal !important;'},
                                height: 50,
                                template: '#kindCode# - #kindName#',
                                url: 'reg_organization_okved', //<span class="mdi mdi-close"></span>
                                type: {
                                    css: "chip",
                                    height: 'auto'
                                },
                            },
                            {
                                height: 26,
                                view: 'label',
                                label: 'Дополнительные виды осуществляемой деятельности',
                            },
                            {
                                view: "list",
                                layout: 'x',
                                id: 'okveds_add',
                                css: {'white-space': 'normal !important;'},
                                height: 170,
                                template: '#kindCode# - #kindName#',
                                url: "reg_organization_okved_add",
                                type: {
                                    css: "chip",
                                    height: 'auto'
                                },
                            },
                        ]
                },
            ]
        },
    ],
    borderless: true,
    url:() => {
        return webix.ajax().get("organization").then((data) => {
            return data;
        })
    }
}

const requestSubsidyStep2 = () => {
    return subs();
}

const requestSubsidyStep3 = {
    view: 'form',
    id: 'request_subsidy_step3',
    complexData: true,
    borderless: true,
    elements: [
        {
            view: 'text',
            id: 'subsidyName',
            label: 'Мера поддержки',
            labelPosition: 'top',
            required: true,
            invalidMessage: 'Поле не может быть пустым',
            readonly: true,
        },
        {
            view: 'textarea',
            id: 'reqBasisFinal',
            label: 'Обоснование заявки',
            labelPosition: 'top',
            height: 150,
            minWidth: 250,
            readonly: true,
        },
        view_section('Прикрепленные файлы'),
        {}
    ]
}

const requestSubsidyWizard = {
    rows: [
        {
            view: 'form',
            type: 'clean',
            id: 'newRequestSubsidyForm',
            minWidth: 200,
            complexData: true,
            elements: [
                {
                    view: 'text',
                    id: 'requestSubsidyId',
                    name: 'requestSubsidyId',
                    hidden: true,
                },
                {
                    view: 'multiview',
                    id: 'wizardRS',
                    cells: [
                        {
                            rows: [
                                multiviewSubsidyHeader('Шаг 1. Выберите меру поддержки', backRS, 1),
                                requestSubsidyStep1,
                                {},
                                {
                                    cols: [
                                        {},
                                        {
                                            view: 'button',
                                            css: 'webix_primary',
                                            value: 'Отменить',
                                            maxWidth: 301,
                                            click: function () {
                                                $$('menu').callEvent('onMenuItemClick', ['RequestSubsidy']);
                                            }
                                        },
                                        {
                                            view: 'button',
                                            css: 'webix_primary',
                                            maxWidth: 301,
                                            value: 'Продолжить',
                                            click: function () {
                                                if (($$('request_subsidy_step1').validate() === false || $$('subsidyId').validate() === false)) {
                                                    webix.message('Выберите меру поддержки', 'error');
                                                } else  {
                                                    if ($$('required_subsidy_files_templates').getChildViews().length === 0) {
                                                        createDataView();
                                                    }
                                                    nextRS(1);
                                                }
                                            }
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            rows: [
                                multiviewSubsidyHeader('Шаг 2. Прикрепите документы', backRS, 2),
                                subs(),
                                {
                                    cols: [
                                        {},
                                        {
                                            view: 'button',
                                            css: 'webix_primary',
                                            maxWidth: 301,
                                            value: 'Назад',
                                            click: backRS
                                        },
                                        {
                                            view: 'button',
                                            css: 'webix_primary',
                                            maxWidth: 301,
                                            value: 'Продолжить',
                                            click: function () {
                                                nextRS(2);
                                            }
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            rows: [
                                multiviewSubsidyHeader('Шаг 3. Подача заявки', backRS, 3),
                                requestSubsidyStep3,
                                {
                                    cols: [
                                        {},
                                        {
                                            view: 'button',
                                            css: 'webix_primary',
                                            maxWidth: 301,
                                            value: 'Назад',
                                            click: backRS
                                        },
                                        {
                                            id: 'save_btn',
                                            view: 'button',
                                            css: 'webix_primary',
                                            minWidth: 150,
                                            value: 'Сохранить заявку',
                                            click: function () {
                                                saveRequestSubsidy("NEW");
                                            }
                                        },
                                        {
                                            id: 'send_btn',
                                            view: 'button',
                                            css: 'webix_primary',
                                            minWidth: 150,
                                            value: 'Подать заявку',
                                            click: function () {
                                                saveRequestSubsidy("SUBMIT");
                                            }
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ]
}

function saveRequestSubsidy(statusCode) {
    let params = {
        id: $$('requestSubsidyId').getValue(),
        subsidyId: $$('subsidyId').getValue(),
        reqBasis: $$('reqBasis').getValue(),
        subsidyRequestStatusCode: statusCode
    }
    webix.ajax()
        .headers({'Content-type': 'application/json'})
        .post('save_request_subsidy', JSON.stringify(params))
        .then(function (data) {
            var response = JSON.parse(data.text());
            if (response.sname == "success") {
                webix.message({text: response.cause, type: 'success'});
                webix.ui({
                    id: 'content',
                    rows: [
                        webix.copy(request_subsidy_list)
                    ]
                }, $$('content'));
            } else {
                webix.message({text: response.cause, type: 'error'});
            }
        })
}

function multiviewSubsidyHeader(title, previous, nextNumber) {
    return {
        type: 'line',
        css: {"border-bottom": "1px solid #DADEE0"},
        cols: [
            {
                view: 'button',
                type: 'icon',
                width: 40,
                icon: 'mdi mdi-arrow-left',
                tooltip: 'Назад',
                click: () => {
                    previous()
                }
            },
            {
                view: 'button',
                type: 'icon',
                width: 40,
                icon: 'mdi mdi-arrow-right',
                tooltip: 'Продолжить',
                click: () => {
                    if (nextNumber === 1 && ($$('request_subsidy_step1').validate() === false || $$('subsidyId').validate() === false)) {
                        webix.message('Выберите меру поддержки', 'error');
                    } else {
                        if (nextNumber === 1 && $$('required_subsidy_files_templates').getChildViews().length === 0) {
                            createDataView();
                        }
                        nextRS(nextNumber);
                    }
                }
            },
            {type: 'header', template: title, borderless: true},
        ]
    }
}

function backRS() {
    $$("wizardRS").back();
}

function nextRS(page) {
    $$("wizardRS").getChildViews()[page].show();
}