const available_subsidy_list = {
    rows: [
        {
            view: 'label',
            label: "Выберите меру поддержки",
        },
        {
            view: "list",
            id: "availableSubsidyListId",
            template: "#shortName#",
            // type:{
            //     height:60
            // },
            // hover: {"background": "#ccd7e6"},
            select: true,
            autowidth: true,
            autoheight: true,
            on: {
                onItemClick: function (id) {
                    let item = $$('availableSubsidyListId').getItem(id);
                    showRequestSubsidyCreateForm();
                    $$('subsidyId').setValue(item.id);
                    $$('subsidyNameId').setValue(item.shortName);
                    $$('subsidyName').setValue(item.shortName);
                }
            }
        }
    ]
}

function showRequestSubsidyCreateForm(data) {
    webix.ui({
        id: 'content',
        rows: [
            requestSubsidyWizard
        ]
    }, $$('content'));

    if (data != null) {
        $$('requestSubsidyId').setValue(data.id);
        // let availableSubsidiesIds = availableSubsidies.map(a => a.id);

        // if (availableSubsidiesIds.includes(data.subsidyId)) {
            $$('subsidyId').setValue(data.subsidyId);
            $$('subsidyNameId').setValue(data.subsidyName);
            $$('subsidyName').setValue(data.subsidyName);
        // }

        $$('reqBasis').setValue(data.reqBasis);
        $$('reqBasisFinal').setValue(data.reqBasis);
        createDataView(data.id);
    }
}

function findAvailableSubsidies() {
    const xhr = webix.ajax().sync().get('available_subsidies');
    return JSON.parse(xhr.responseText);
}

function getFilesListByTypeView(docRequestSubsidyId) {
    webix.ajax(`request_subsidy_files_verification/${ docRequestSubsidyId }`).then(function (filesVerification) {
        filesVerification = filesVerification.json();

        filesVerification.map((file) => {
            switch (file.verify_status) {
                case 1: file.verify_status = "проверка прошла успешно"; break;
                case 2: file.verify_status = "подпись не соответствует файлу"; break;
                case 3: file.verify_status = "в сертификате или цепочке сертификатов есть ошибки"; break;
                case 4: file.verify_status = "в подписи есть ошибки"; break;
                default: file.verify_status = "проверка не проводилась"; break;
            }
            return file;
        });

        webix.ajax(`request_subsidy_files/${docRequestSubsidyId}`).then(function (data) {
            const views = [];
            data = data.json();
            console.dir({ data, filesVerification });
            if (data.length > 0) {
                const filesTypes = {};
                const byFileType = data.reduce(function (result, file) {
                    const fileVerificationStatus = filesVerification.find((fileVerification) => fileVerification.id_request_subsidy_file === file.id);
                    result[file.fileType.id] = result[file.fileType.id] || [];
                    result[file.fileType.id].push({ ...file, verificationStatus: fileVerificationStatus ?? { verify_status: 'отсутствует подпись / проверка не проводилась' } });

                    filesTypes[file.fileType.id] = file.fileType.name;

                    return result;
                }, Object.create(null));

                // console.dir({ byFileType });

                for (const [key, filesArray] of Object.entries(byFileType)) {
                    // console.dir({ filesArray });
                    views.push({
                        rows: [
                            view_section(filesTypes[key]),
                            {
                                id: `request_subsidy_files_table/${key}`,
                                view: 'datatable',
                                pager: `Pager/${key}`,
                                autoheight: true,
                                header: `id = ${key}`,
                                select: 'row',
                                resizeColumn: true,
                                readonly: true,
                                data: filesArray,
                                columns: [
                                    {
                                        id: 'viewFileName',
                                        header: 'Название файла',
                                        adjust: true,
                                        fillspace: true,
                                        sort: 'string',
                                    },
                                    {
                                        id: 'signature',
                                        template: function (request) {
                                            let label = '';
                                            let style = '';

                                            if (request.signature) {
                                                label = 'Подпись загружена';
                                                style = 'color: green';
                                            } else {
                                                label = 'Подпись не загружена';
                                                style = 'color: red';
                                            }

                                            return `<div style="${style}" role="gridcell" aria-rowindex="1" aria-colindex="1" aria-selected="true" tabindex="0" class="webix_cell webix_row_select">${label}</div>`;
                                        },
                                        header: 'Подпись',
                                        adjust: true,
                                        fillspace: true,
                                        sort: 'string',
                                    },
                                    {
                                        id: 'verificationStatus',
                                        header: 'Статус проверки подписи',
                                        adjust: true,
                                        sort: 'string',
                                        template: '#verificationStatus.verify_status#',
                                    },
                                ],
                            },
                            {
                                view: 'pager',
                                id: `Pager/${key}`,
                                height: 38,
                                size: 25,
                                group: 5,
                                template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                            },
                        ]
                    })
                }

                webix.ui({
                    id: 'filesListViewByType',
                    rows: views,
                }, $$('filesListViewByType'));
            }

        })
    });
}

const noAvailableSubsidiesForm = {
    rows: [
        {
            autowidth: true,
            autoheight: true,
            rows: [
                {
                    view: 'template',
                    template: "Заявки по всем доступным мерам поддержки созданы, вы не можете создать новую заявку.",
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
        // {
        //     view: 'richselect',
        //     id: 'subsidyId',
        //     label: 'Мера поддержки',
        //     labelPosition: 'top',
        //     required: true,
        //     invalidMessage: 'Поле не может быть пустым',
        //     options: 'available_subsidies',
        //     on: {
        //         onChange: function () {
        //             var textValue = $$('subsidyId').data.text;
        //             $$('subsidyName').setValue(textValue);
        //         }
        //     }
        // },
        {
          view: 'text',  id: 'subsidyId',  label: 'Мера поддержки', labelPosition: 'top', hidden: true,
        },
        {
            view: 'text',  id: 'subsidyNameId',  label: 'Мера поддержки', labelPosition: 'top', readonly: true,
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
                                id: "okved_main",
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
    url: () => {
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
        {
            id: 'filesListViewByType'
        }
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
                                            css: 'webix_secondary',
                                            value: 'Отменить',
                                            maxWidth: 200,
                                            click: function () {
                                                $$('menu').callEvent('onMenuItemClick', ['RequestSubsidy']);
                                            }
                                        },
                                        {
                                            view: 'button',
                                            css: 'webix_primary',
                                            maxWidth: 200,
                                            value: 'Продолжить',
                                            click: function () {
                                                if (($$('request_subsidy_step1').validate() === false || $$('subsidyId').validate() === false)) {
                                                    webix.message('Выберите меру поддержки', 'error');
                                                } else {
                                                    if ($$('requestSubsidyId').getValue() == '') {
                                                        let params = {
                                                            subsidyId: $$('subsidyId').getValue(),
                                                            reqBasis: $$('reqBasis').getValue(),
                                                        }
                                                        webix.ajax()
                                                            .headers({'Content-type': 'application/json'})
                                                            .post('save_request_subsidy_draft', JSON.stringify(params)).then(function (data) {
                                                            data = data.json();
                                                            console.log(data)
                                                            $$('requestSubsidyId').setValue(data.id);
                                                            createDataView();
                                                            nextRS(1);
                                                        }).catch(function () {
                                                                webix.message('Не удалось сохранить черновик', 'error');
                                                            }
                                                        )
                                                    } else {
                                                        nextRS(1);
                                                    }
                                                }
                                            }
                                        },
                                        {
                                            id: 'save_btn1',
                                            view: 'button',
                                            css: 'webix_primary',
                                            maxWidth: 200,
                                            value: 'Сохранить заявку',
                                            click: function () {
                                                saveRequestSubsidy("NEW");
                                            }
                                        },
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
                                            css: 'webix_secondary',
                                            maxWidth: 200,
                                            value: 'Назад',
                                            click: backRS
                                        },
                                        {
                                            view: 'button',
                                            css: 'webix_primary',
                                            maxWidth: 200,
                                            value: 'Продолжить',
                                            click: function () {
                                                let valid = checkRequiredFiles();
                                                if (valid) {
                                                    if (checkVerificationFiles()) {
                                                        nextRS(2);
                                                        getFilesListByTypeView($$('requestSubsidyId').getValue());
                                                    } else {
                                                        webix.message("Не все подписи файлов прошли проверку", "error", 10000);
                                                    }
                                                } else {
                                                    webix.message("Отсутствуют обязательные файлы", "error", 10000);
                                                }
                                            }
                                        },
                                        {
                                            id: 'save_btn2',
                                            view: 'button',
                                            css: 'webix_primary',
                                            maxWidth: 200,
                                            value: 'Сохранить заявку',
                                            click: function () {
                                                saveRequestSubsidy("NEW");
                                            }
                                        },
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
                                            css: 'webix_secondary',
                                            maxWidth: 200,
                                            value: 'Назад',
                                            click: backRS
                                        },
                                        {
                                            id: 'save_btn3',
                                            view: 'button',
                                            css: 'webix_primary',
                                            maxWidth: 200,
                                            value: 'Сохранить заявку',
                                            click: function () {
                                                saveRequestSubsidy("NEW");
                                            }
                                        },
                                        {
                                            id: 'send_btn',
                                            view: 'button',
                                            css: 'webix_primary',
                                            maxWidth: 200,
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
                if ($$('btnBackMainId')) {
                    $$('btnBackMainId').hide();
                }
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

function saveDraft() {
    let params = {
        subsidyId: $$('subsidyId').getValue(),
        reqBasis: $$('reqBasis').getValue(),
    }
    webix.ajax()
        .headers({'Content-type': 'application/json'})
        .post('save_request_subsidy_draft', JSON.stringify(params)).then(function (data) {
        data = data.json();
        $$('requestSubsidyId').setValue(data.id);
    }).catch(function () {
            webix.message('Не удалось сохранить черновик', 'error');
        }
    )

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
                    } else if (nextNumber === 1 && $$('requestSubsidyId').getValue() == '') {
                        let params = {
                            subsidyId: $$('subsidyId').getValue(),
                            reqBasis: $$('reqBasis').getValue(),
                        }
                        webix.ajax()
                            .headers({'Content-type': 'application/json'})
                            .post('save_request_subsidy_draft', JSON.stringify(params)).then(function (data) {
                            data = data.json();
                            console.log(data)
                            $$('requestSubsidyId').setValue(data.id);
                            createDataView();
                            nextRS(nextNumber);
                        }).catch(function () {
                                webix.message('Не удалось сохранить черновик', 'error');
                            }
                        )
                    } else if (nextNumber === 1 && $$('required_subsidy_files_templates').getChildViews().length === 0) {
                        createDataView();
                        nextRS(nextNumber);
                    } else if (nextNumber === 1) {
                        nextRS(nextNumber);
                    }  else if (nextNumber === 2) {
                        let valid = checkRequiredFiles();
                        if (valid) {
                            if (checkVerificationFiles()) {
                                nextRS(nextNumber);
                                getFilesListByTypeView($$('requestSubsidyId').getValue());
                            } else {
                                webix.message("Не все подписи файлов прошли проверку", "error", 10000);
                            }
                        } else {
                            webix.message("Отсутствуют обязательные файлы", "error", 10000);
                        }
                    }
                }
            },
            {
                view: 'button',
                type: 'icon',
                width: 40,
                icon: 'mdi mdi-content-save',
                tooltip: 'Сохранить',
                click: () => {
                    saveRequestSubsidy("NEW")
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