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
                                id: "typeRequest",
                                header: "Тип заявки",
                                template: "#typeRequest.activityKind#",
                                minWidth: 350,
                                fillspace: true
                            },
                            {
                                id: "time_Create",
                                header: "Дата подачи",
                                adjust: true,
                                format: dateFormat,
                            },
                            {
                                id: "status",
                                header: "Статус",
                                template: "#statusReviewName#",
                                adjust: true,
                                width: 300
                            },
                            {id: "personSlrySaveCnt", header: "Числ. с сохр. зп", minWidth: 160, fillspace: true},
                            {id: "personOfficeCnt", header: "Числ. работающих", minWidth: 160, fillspace: true},
                            {id: "personRemoteCnt", header: "Числе. удал. режим", minWidth: 160, fillspace: true},
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
                            onItemClick: function (id) {
                                let item = this.getItem(id);
                                setTimeout(function () {
                                    if (item.new) {
                                        showRequestWizard(item);
                                    } else {
                                        showRequestViewForm(item);
                                    }
                                }, 10);
                            }
                        },
                        url: 'org_requests'
                    },
                    {
                        align: 'center, middle',
                        body:
                            {
                                id: 'requestPagerIn',              //pager responsive-target
                                rows: []
                            },
                    },
                    {
                        responsive: "requestPagerIn",
                        cols: [
                            {
                                view: 'pager',
                                id: 'Pager',
                                minWidth: 220,
                                height: 38,
                                size: 25,
                                group: 5,
                                template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                            },
                            {
                                view: 'button',
                                align: 'right',
                                minWidth: 220,
                                maxWidth: 350,
                                css: 'webix_primary',
                                value: 'Подать заявку',
                                click: function () {

                                    webix.ajax('/cls_type_requests').then(function (data) {
                                        let typeRequests = data.json();

                                        let vtxt = '<br/>' + `<a href="/upload">Общие основания (более 100 сотрудников)</a><br/><br/>`;

                                        for (let j = 0; j < typeRequests.length; j++) {
                                            if (typeRequests[j].id == 100) {
                                                continue;
                                            }
                                            if (typeRequests[j].statusRegistration == 1 && typeRequests[j].statusVisible == 1) {
                                                let labl = typeRequests[j].activityKind;//.replace(new RegExp(' ', 'g'), '&nbsp');
                                                let vdid = typeRequests[j].id;
                                                let reqv = 'typed_form?request_type=' + vdid;
                                                vtxt += `<a href="#${reqv}" onclick="showRequestCreateForm(${vdid})">` + labl + '</a><br/><br/>';
                                            }
                                        }

                                        const v = {
                                            id: 'content',
                                            rows: [
                                                {
                                                    view: 'scrollview',
                                                    scroll: 'xy',
                                                    body: {
                                                        type: 'space',
                                                        rows: [
                                                            {
                                                                view: 'template',
                                                                template: vtxt,
                                                                // width: 200,
                                                                autoheight: true,
                                                            }
                                                        ]
                                                    }
                                                }
                                            ]
                                        };
                                        webix.ui(v, $$('content'));
                                    });
                                }
                            }
                        ]
                    }
                ]
            }
        ]
    }
}

function showRequestWizard(data) {
    webix.ui({
        id: 'content',
        rows: [
            {
                view: 'scrollview',
                scroll: 'xy',
                body: {
                    type: 'space',
                    rows: [
                        {
                            view: 'form',
                            id: 'newRequestForm',
                            minWidth: 200,
                            complexData: true,
                            elements: [
                                {
                                    view: 'multiview',
                                    id: 'wizard',
                                    cells: [
                                        {
                                            rows: [
                                                { type: 'header', template: 'Шаг 1. Приложите документы' },
                                                documents,
                                                {
                                                    cols: [
                                                        {},
                                                        {
                                                            view: 'button',
                                                            css: 'webix_primary',
                                                            maxWidth: 301,
                                                            value: 'Продолжить',
                                                            click: next
                                                        }
                                                    ]
                                                }
                                            ]
                                        },
                                        {
                                            rows: [
                                                { type: 'header', template: 'Шаг 2. Актуализируйте список сотрудников' },
                                                employees,
                                                {
                                                    cols: [
                                                        {},
                                                        {
                                                            view: 'button',
                                                            css: 'webix_primary',
                                                            maxWidth: 301,
                                                            value: 'Назад',
                                                            click: back
                                                        },
                                                        {
                                                            view: 'button',
                                                            css: 'webix_primary',
                                                            maxWidth: 301,
                                                            value: 'Продолжить',
                                                            click: next
                                                        }
                                                    ]
                                                }
                                            ]
                                        },
                                        {
                                            rows: [
                                                { type: 'header', template: 'Шаг 3. Актуализируйте список фактических адресов' },
                                                address,
                                                {
                                                    cols: [
                                                        {},
                                                        {
                                                            view: 'button',
                                                            css: 'webix_primary',
                                                            maxWidth: 301,
                                                            value: 'Назад',
                                                            click: back
                                                        },
                                                        {
                                                            view: 'button',
                                                            css: 'webix_primary',
                                                            maxWidth: 301,
                                                            value: 'Продолжить',
                                                            click: next
                                                        }
                                                    ]
                                                }
                                            ]
                                        },
                                        {
                                            rows: [
                                                { type: 'header', template: 'Шаг 4. Ознакомьтесь с предписаниями' },
                                                {
                                                    type: 'form',
                                                    rows: [
                                                        {
                                                            view: 'template',
                                                            id: 'consent',
                                                            height: 200,
                                                            readonly: true,
                                                            scroll: true,
                                                            template: ''
                                                        },
                                                        {
                                                            rows: [
                                                                {
                                                                    view: 'template',
                                                                    borderless: true,
                                                                    css: 'personalTemplateStyle',
                                                                    template: 'Подтверждаю согласие работников на обработку персональных данных <span style = "color: red">*</span>',
                                                                    autoheight: true
                                                                },
                                                                {
                                                                    view: 'checkbox',
                                                                    name: 'isAgree',
                                                                    id: 'isAgree',
                                                                    labelPosition: 'top',
                                                                    invalidMessage: 'Поле не может быть пустым',
                                                                    required: true,
                                                                    on: {
                                                                        onChange(newv, oldv) {
                                                                            if ($$('isAgree').getValue() == 1) {
                                                                                $$('send_btn').enable();
                                                                            } else {
                                                                                $$('send_btn').disable();
                                                                            }
                                                                        }
                                                                    }
                                                                },
                                                            ]
                                                        },
                                                        {
                                                            id: 'prescriptions',
                                                            rows: []
                                                        },
                                                        {
                                                            view: 'template',
                                                            id: 'label_sogl',
                                                            borderless: true,
                                                            css: {
                                                                'font-family': 'Roboto, sans-serif',
                                                                'font-size': '14px;',
                                                                'font-weight': '500;',
                                                                'color': '#313131;',
                                                                'padding': ' 0px 3px !important;',
                                                                'text-align': 'center'
                                                            },
                                                            template: 'Информация мною прочитана и я согласен с ней при подаче заявки',
                                                            autoheight: true
                                                        },
                                                        {
                                                            cols: [
                                                                {},
                                                                {
                                                                    view: 'button',
                                                                    css: 'webix_primary',
                                                                    value: 'Отменить',
                                                                    minWidth: 150,
                                                                    align: 'center',
                                                                    click: function () {
                                                                        $$('menu').callEvent('onMenuItemClick', ['Requests']);
                                                                    }
                                                                },
                                                                {
                                                                    id: 'send_btn',
                                                                    view: 'button',
                                                                    css: 'webix_primary',
                                                                    minWidth: 150,
                                                                    value: 'Подать заявку',
                                                                    disabled: true,
                                                                    align: 'center',
                                                                    click: function () {
                                                                        this.disabled = true;

                                                                        if ($$('newRequestForm').validate()) {

                                                                            let params = $$('newRequestForm').getValues();

                                                                            params.requestId = data.id;

                                                                            if (params.isAgree != 1) {
                                                                                webix.message('Необходимо подтвердить согласие работников на обработку персональных данных', 'error')
                                                                                return false
                                                                            }

                                                                            $$('newRequestForm').showProgress({
                                                                                type: 'icon',
                                                                                delay: 5000
                                                                            })

                                                                            webix.ajax()
                                                                                .headers({'Content-type': 'application/json'})
                                                                                .post('/cabinet/new_request', JSON.stringify(params))
                                                                                .then(function (data) {
                                                                                    const text = data.text();
                                                                                    console.log(text);
                                                                                    let errorText = "ЗАЯВКА НЕ ВНЕСЕНА. ОБНАРУЖЕНЫ ОШИБКИ ЗАПОЛНЕНИЯ: ";
                                                                                    if (text.includes(errorText)) {
                                                                                        webix.alert({
                                                                                            title: "ИСПРАВЬТЕ ОШИБКИ",
                                                                                            ok: "Вернуться к заполнению заявки",
                                                                                            text: text
                                                                                        });
                                                                                        $$('newRequestForm').hideProgress();
                                                                                    } else if (text.includes("Невозможно подать заявку")) {
                                                                                        webix.alert({
                                                                                            title: "ВНИМАНИЕ!",
                                                                                            ok: "ОК",
                                                                                            text: text
                                                                                        });
                                                                                        $$('newRequestForm').hideProgress();
                                                                                    } else {
                                                                                        webix.alert({
                                                                                            title: "Заявка подана",
                                                                                            ok: "ОК",
                                                                                            text: text
                                                                                        })
                                                                                            .then(function () {
                                                                                                $$('newRequestForm').hideProgress();
                                                                                                $$('menu').callEvent('onMenuItemClick', ['Requests']);
                                                                                            })
                                                                                            .fail(function () {
                                                                                                $$('newRequestForm').hideProgress()
                                                                                                $$('newRequestForm').clear();
                                                                                            });
                                                                                    }
                                                                                })
                                                                        } else {
                                                                            webix.message('Не заполнены обязательные поля. Для просмотра прокрутите страницу вверх', 'error')
                                                                        }
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
                    ]
                }
            }
        ]
    }, $$('content'));

    webix.ajax('cls_type_request/' + data.typeRequest.id).then(function (data) {

        let typeRequest = data.json();

        $$('consent').setHTML(typeRequest.consent);

        if (typeRequest.regTypeRequestPrescriptions && typeRequest.regTypeRequestPrescriptions.length > 0) {
            typeRequest.regTypeRequestPrescriptions.forEach(prescription => {
                console.log(prescription)
                $$('prescriptions').addView({
                    view: 'template',
                    id: 'prescription' + prescription.id,
                    height: 550,
                    readonly: true,
                    scroll: true,
                    template: prescription.content
                });
            })
        }

        if (typeRequest.settings) {
            const settings = JSON.parse(typeRequest.settings, function (key, value) {
                if (value === 'webix.rules.isChecked') {
                    return webix.rules.isChecked;
                }
                return value;
            });
            if (settings.fields) {
                settings.fields.forEach(field => {
                    $$('form').addView(field.ui, field.pos);
                })
            }
        }

        webix.extend($$('newRequestForm'), webix.ProgressBar);
    });
}

function back() {
    $$("wizard").back();
}

function next() {
    const parentCell = this.getParentView().getParentView();
    const index = $$("wizard").index(parentCell);
    const next = $$("wizard").getChildViews()[index + 1]
    if (next) {
        next.show();
    }
}

function showRequestCreateForm(idTypeRequest) {

    ID_TYPE_REQUEST = idTypeRequest;

    webix.ajax('cls_type_request/' + idTypeRequest).then(function (data) {

        webix.ui({
            id: 'content',
            rows: [
                {
                    view: 'scrollview',
                    name: 'showRequestCreateForm',
                    minWidth: 200,
                    scroll: 'xy',
                    body: {
                        rows: [
                            {
                                id: 'form',
                                view: 'form',
                                minWidth: 200,
                                complexData: true,
                                elements: [
                                    {
                                        view: 'template',
                                        borderless: true,
                                        css: 'personalTemplateStyle',
                                        id: 'activityKind',
                                        autoheight: true,
                                        // align: 'center'
                                    },
                                    {
                                        rows:[
                                            {
                                                view: 'template',
                                                borderless: true,
                                                css:{
                                                    'font-family' : 'Roboto, sans-serif;',
                                                    'font-size' : '14px;',
                                                    'font-weight' : '500;',
                                                    'color' : '#313131;',
                                                },
                                                template: 'Министерство, курирующее вашу деятельность <span style = "color: red">*</span>',
                                                autoheight: true

                                            },
                                            {
                                                view: 'select',
                                                id: 'departmentId',
                                                name: 'departmentId',
                                                labelPosition: 'top',
                                                invalidMessage: 'Поле не может быть пустым',
                                                required: true,
                                                options: [],
                                            },
                                        ]
                                    },
                                    view_section('Обоснование заявки'),
                                    {
                                        rows: [
                                            {
                                                view: 'textarea',
                                                height: 150,
                                                minWidth: 250,
                                                label: 'Обоснование заявки',
                                                name: 'reqBasis',
                                                id: 'reqBasis',
                                                invalidMessage: 'Поле не может быть пустым',
                                                required: true,
                                                labelPosition: 'top'
                                            },
                                            {
                                                view: 'template',
                                                css: 'personalTemplateStyle',
                                                borderless: true,
                                                template: '<span style="text-align: center; color: red">Для загрузки нескольких файлов выбирайте их с зажатой клавишей Ctrl или заранее сожмите в ZIP-архив и загрузите его</span>',
                                                autoheight: true,
                                                //css: 'main_label'
                                            },
                                            {
                                                view: 'template',
                                                css: 'personalTemplateStyle',
                                                borderless: true,
                                                template: '<span  style="text-align: center; color: red">Общий размер загружаемых файлов не должен превышать 60 Мб</span>',
                                                autoheight: true,
                                                //css: 'main_label'
                                            },
                                            {
                                                id: 'upload',
                                                view: 'uploader',
                                                css: 'webix_primary',
                                                value: 'Загрузить PDF-файл(-ы) или ZIP-архив(-ы)  с пояснением обоснования',
                                                autosend: false,
                                                upload: '/uploadpart',
                                                required: true,
                                                accept: 'application/pdf, application/zip',
                                                multiple: true,
                                                link: 'filelist',
                                                on: {
                                                    onBeforeFileAdd: function (upload) {
                                                        if (upload.type.toUpperCase() !== 'PDF' && upload.type.toUpperCase() !== 'ZIP') {
                                                            $$('no_pdf').setValue(upload_chack_error);
                                                            $$('file').setValue('');
                                                            $$('send_btn').disable();
                                                            return false;
                                                        }

                                                        if ($$('file').getValue()) {
                                                            $$('file').setValue($$('file').getValue() + ',' + upload.name)
                                                        } else {
                                                            $$('file').setValue(upload.name)
                                                        }
                                                        $$('no_pdf').setValue('');
                                                        let cnt = $$('person_table').data.count();
                                                        if ($$('isAgree').getValue() == 1 && $$('isProtect').getValue() == 1 && cnt > 0) {
                                                            $$('send_btn').enable();
                                                        } else {
                                                            $$('send_btn').disable();
                                                        }
                                                        return true
                                                    }
                                                }
                                            },
                                            {
                                                view: 'list', id: 'filelist', type: 'uploader',
                                                autoheight: true, borderless: true
                                            },
                                            {
                                                paddingLeft: 10,
                                                view: 'label',
                                                visible: false,
                                                label: '',
                                                id: 'no_pdf'
                                            }
                                        ]
                                    },
                                    view_section('Адресная информация'),
                                    {
                                        rows: [
                                            {
                                                view: 'datatable', name: 'addressFact', label: '', labelPosition: 'top',
                                                height: 200,
                                                select: 'row',
                                                editable: true,
                                                id: 'addr_table',
                                                columns: [
                                                    {id: 'index', header: '', css: 'rank'},
                                                    {
                                                        id: 'addressFact',
                                                        header: 'Фактический адрес осуществления деятельности',
                                                        width: 400,
                                                        //editor: 'text',
                                                        //fillspace: true
                                                    },
                                                    {
                                                        id: 'personOfficeFactCnt',
                                                        header: 'Численность работников, не подлежащих переводу на дистанционный режим работы, осуществляющих деятельность по указанному в  пункте 11 настоящей формы фактическому адресу',
                                                        //editor: 'text',
                                                        fillspace: true,
                                                        minWidth: 1300
                                                    }
                                                ],
                                                data: [],
                                                on: {
                                                    'data->onStoreUpdated': function () {
                                                        this.data.each(function (obj, i) {
                                                            obj.index = i + 1;
                                                        });
                                                    }
                                                },
                                            },
                                            {
                                                view: 'form',
                                                id: 'form_addr',
                                                elements: [
                                                    {
                                                        margin: 5,
                                                        id: 'requestBtnRows',
                                                        rows:[

                                                        ]
                                                    },
                                                    {
                                                        type: 'space',
                                                        id: 'form_addr_cols',
                                                        cols: [
                                                            {
                                                                view: 'text',
                                                                name: 'addressFact',
                                                                id: 'addressFact',
                                                                label: 'Фактический адрес',
                                                                labelPosition: 'top',
                                                                required: true
                                                            },
                                                            {
                                                                view: 'text',
                                                                name: 'personOfficeFactCnt',
                                                                id: 'personOfficeFactCnt',
                                                                label: 'Численность работников',
                                                                labelPosition: 'top',
                                                                invalidMessage: 'Поле не может быть пустым',
                                                                required: true,
                                                            },
                                                            {},
                                                        ]
                                                    },

                                                    {
                                                        //type: 'space',
                                                        margin: 5,
                                                        responsive: 'requestBtnRows',
                                                        cols: [
                                                            {
                                                                view: 'button',
                                                                css: 'webix_primary',
                                                                id: 'addAddressInfo',
                                                                value: 'Добавить',
                                                                minWidth: 150,
                                                                maxWidth: 301,
                                                                click: function () {
                                                                    let values = $$('form_addr').getValues()
                                                                    if (values.addressFact == '' || values.personOfficeFactCnt == '') {
                                                                        webix.message('не заполнены обязательные поля')
                                                                        return;
                                                                    }
                                                                    if (values.addressFact.length > 255) {
                                                                        webix.message('Фактический адрес превышает 255 знаков!')
                                                                        return;
                                                                    }
                                                                    if (isNaN(values.personOfficeFactCnt * 1)) {
                                                                        webix.message('требуется числовое значение')
                                                                        return;
                                                                    }

                                                                    $$('addr_table').add({
                                                                        personOfficeFactCnt: values.personOfficeFactCnt,
                                                                        addressFact: values.addressFact,
                                                                    }, $$('addr_table').count() + 1)

                                                                    $$('form_addr').clear()
                                                                }
                                                            },
                                                            {
                                                                view: 'button',
                                                                css: 'webix_primary',
                                                                id: 'updAddressInfo',
                                                                value: 'Изменить',
                                                                minWidth: 150,
                                                                maxWidth: 301,
                                                                click: function () {
                                                                    let values = $$('form_addr').getValues()
                                                                    if (values.addressFact == '' || values.personOfficeFactCnt == '') {
                                                                        webix.message('обязательные поля')
                                                                        return;
                                                                    }
                                                                    if (values.addressFact == '' || values.personOfficeFactCnt == '') {
                                                                        webix.message('не заполнены обязательные поля')
                                                                        return;
                                                                    }
                                                                    if (isNaN(values.personOfficeFactCnt * 1)) {
                                                                        webix.message('требуется числовое значение')
                                                                        return;
                                                                    }

                                                                    $$('form_addr').save()
                                                                }
                                                            },
                                                            {
                                                                view: 'button',
                                                                css: 'webix_primary',
                                                                id: 'delAddressInfo',
                                                                value: 'Удалить',
                                                                minWidth: 150,
                                                                maxWidth: 301,
                                                                click: function () {
                                                                    if (!$$("addr_table").getSelectedId()) {
                                                                        webix.message("Ничего не выбрано!");
                                                                        return;
                                                                    }
                                                                    webix.confirm('Вы действительно хотите удалить выбранную запись?')
                                                                        .then(
                                                                            function () {
                                                                                $$("addr_table").remove($$("addr_table").getSelectedId());
                                                                            }
                                                                        )
                                                                }
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    },
                                    view_section('Данные о численности работников'),
                                    {
                                        type: 'space',
                                        rows: [
                                            {
                                                view: 'text', name: 'personSlrySaveCnt',
                                                label: 'Суммарная численность работников, в отношении которых установлен режим работы нерабочего дня с сохранением заработной платы',
                                                labelPosition: 'top',
                                                validate: function (val) {
                                                    return !isNaN(val * 1) && (val.trim() !== '')
                                                },
                                                invalidMessage: 'Поле не может быть пустым',
                                                required: false,
                                                hidden: true
                                            },
                                            {
                                                type: 'space',
                                                borderless: true,
                                                padding: {bottom: 0},
                                                rows:[
                                                    {
                                                        view: 'template',
                                                        borderless: true,
                                                        css: {
                                                            'font-family' : 'Roboto, sans-serif;',
                                                            'font-size' : '14px;',
                                                            'font-weight' : '500;',
                                                            'color' : '#313131;',
                                                            'background-color' : '#EBEDF0',
                                                        },
                                                        template: 'Суммарная численность работников, подлежащих переводу на дистанционный режим работы',
                                                        autoheight: true,
                                                    },
                                                    {
                                                        view: 'text', name: 'personRemoteCnt',
                                                        //label: 'Суммарная численность работников, подлежащих переводу на дистанционный режим работы',
                                                        invalidMessage: 'Поле не может быть пустым',
                                                        validate: function (val) {
                                                            return !isNaN(val * 1) && (val.trim() !== '')
                                                        },
                                                        required: true,
                                                    },
                                                ]
                                            },
                                            {
                                                type: 'space',
                                                borderless: true,
                                                padding: {top: 0},
                                                rows:[
                                                    {
                                                        view: 'template',
                                                        borderless: true,
                                                        css: {
                                                            'font-family' : 'Roboto, sans-serif;',
                                                            'font-size' : '14px;',
                                                            'font-weight' : '500;',
                                                            'color' : '#313131;',
                                                            'background-color' : '#EBEDF0',
                                                        },
                                                        template: 'Суммарная численность работников, не подлежащих переводу на дистанционный режим работы (посещающие рабочие места)',
                                                        autoheight: true,
                                                    },
                                                    {
                                                        view: 'text', name: 'personOfficeCnt',
                                                        //label: 'Суммарная численность работников, не подлежащих переводу на дистанционный режим работы (посещающие рабочие места)',

                                                        validate: function (val) {
                                                            return !isNaN(val * 1) && (val.trim() !== '')
                                                        },
                                                        invalidMessage: 'Поле не может быть пустым',
                                                        required: true
                                                    },
                                                ],
                                            },
                                        ]
                                    },
                                    view_section('Данные о ваших работниках, чья деятельность предусматривает выход на работу (Обязательный для заполнения раздел)'),
                                    {
                                        view: 'scrollview',
                                        type: 'space',
                                        height: 600,
                                        body: {
                                            rows: [
                                                {
                                                    id: 'person_table',
                                                    view: 'datatable',
                                                    height: 400,
                                                    name: 'persons',
                                                    select: 'row',
                                                    resizeColumn: true,
                                                    readonly: true,
                                                    columns: [
                                                        {id: 'index', header: '', css: 'rank', width: 50},
                                                        {
                                                            id: 'lastname',
                                                            header: 'Фамилия',
                                                            adjust: true,
                                                            minWidth: 250,
                                                            sort: 'string',
                                                            fillspace: true
                                                        },
                                                        {
                                                            id: 'firstname',
                                                            header: 'Имя',
                                                            adjust: true,
                                                            minWidth: 250,
                                                            sort: 'string',
                                                            fillspace: true
                                                        },
                                                        {
                                                            id: 'patronymic',
                                                            header: 'Отчество',
                                                            fillspace: true,
                                                            minWidth: 600,
                                                            sort: 'string'
                                                        },
                                                        //{ id: 'isagree', header: 'Согласие', width: 100, template: '{common.checkbox()}', css: 'center' }
                                                    ],
                                                    on: {
                                                        'data->onStoreUpdated': function () {
                                                            this.data.each(function (obj, i) {
                                                                obj.index = i + 1;
                                                            });
                                                        }
                                                    },
                                                    data: []
                                                },
                                                {
                                                    view: 'form',
                                                    id: 'form_person',
                                                    elements: [
                                                        {
                                                            type: 'space',
                                                            id: 'form_person_rows',
                                                            hidden: true,
                                                            rows:[]
                                                        },
                                                        {
                                                            margin: 5,
                                                            id: 'form_person_button_rows',
                                                            rows:[]
                                                        },
                                                        {
                                                            type: 'space',
                                                            id: 'form_person_cols',
                                                            margin: 0,
                                                            cols: [
                                                                {
                                                                    view: 'text',
                                                                    name: 'lastname',
                                                                    id: 'person_lastname',
                                                                    //inputWidth: '250',
                                                                    label: 'Фамилия',
                                                                    labelPosition: 'top'
                                                                },
                                                                {
                                                                    view: 'text',
                                                                    name: 'firstname',
                                                                    id: 'person_firstname',
                                                                    //inputWidth: '250',
                                                                    label: 'Имя',
                                                                    labelPosition: 'top'
                                                                },
                                                                {
                                                                    view: 'text',
                                                                    name: 'patronymic',
                                                                    id: 'person_patronymic',
                                                                    //inputWidth: '250',
                                                                    label: 'Отчество',
                                                                    labelPosition: 'top'
                                                                },
                                                                {},
                                                            ]
                                                        },
                                                        {
                                                            //type: 'space',
                                                            responsive: 'form_person_button_rows',
                                                            margin: 5,
                                                            cols: [
                                                                {
                                                                    view: 'button',
                                                                    css: 'webix_primary',
                                                                    value: 'Добавить',
                                                                    id: 'add_person_btn',
                                                                    minWidth: 150,
                                                                    maxWidth: 301,
                                                                    click: function () {
                                                                        let values = $$('form_person').getValues()
                                                                        if (values.lastname == '' || values.firstname == '') {
                                                                            webix.message('Фамилия, Имя - обязательные поля')
                                                                            return;
                                                                        }

                                                                        if (values.lastname.length > 100 || values.firstname.length > 100 || values.patronymic.length > 100) {
                                                                            webix.message('Фамилия, имя или отчество - длиннее 100 знаков')
                                                                            return;
                                                                        }

                                                                        $$('person_table').add({
                                                                            lastname: values.lastname.trim(),
                                                                            firstname: values.firstname.trim(),
                                                                            patronymic: values.patronymic.trim(),
                                                                            //isagree: values.isagree
                                                                        }, $$('person_table').count() + 1)

                                                                        let is_no_pdf = $$('no_pdf').getValue() == upload_chack_error;
                                                                        if ($$('isAgree').getValue() == 1 && $$('isProtect').getValue() == 1 && !is_no_pdf) {
                                                                            $$('send_btn').enable();
                                                                        } else {
                                                                            $$('send_btn').disable();
                                                                        }

                                                                        $$('clearPersonsBtn').enable();

                                                                        $$('form_person').clear()
                                                                    }
                                                                },
                                                                {
                                                                    view: 'button',
                                                                    css: 'webix_primary',
                                                                    value: 'Изменить',
                                                                    minWidth: 150,
                                                                    maxWidth: 301,
                                                                    click: function () {
                                                                        let values = $$('form_person').getValues()
                                                                        if (values.lastname == '' || values.firstname == '') {
                                                                            webix.message('Фамилия, Имя - обязательные поля')
                                                                            return;
                                                                        }

                                                                        if (values.lastname.length > 100 || values.firstname.length > 100 || values.patronymic.length > 100) {
                                                                            webix.message('Фамилия, имя или отчество - длиннее 100 знаков')
                                                                            return;
                                                                        }

                                                                        $$('form_person').save()
                                                                    }
                                                                },
                                                                {
                                                                    view: 'button',
                                                                    css: 'webix_primary',
                                                                    value: 'Удалить',
                                                                    minWidth: 150,
                                                                    maxWidth: 301,
                                                                    click: function () {
                                                                        if (!$$("person_table").getSelectedId()) {
                                                                            webix.message("Ничего не выбрано!");
                                                                            return;
                                                                        }
                                                                        webix.confirm('Вы действительно хотите удалить выбранную запись?')
                                                                            .then(
                                                                                function () {
                                                                                    $$("person_table").remove($$("person_table").getSelectedId());
                                                                                    let cnt = $$('person_table').data.count();
                                                                                    let is_no_pdf = $$('no_pdf').getValue() == upload_chack_error;

                                                                                    if (cnt > 0) {
                                                                                        $$('clearPersonsBtn').enable();
                                                                                    } else {
                                                                                        $$('clearPersonsBtn').disable();
                                                                                    }

                                                                                    if ($$('isAgree').getValue() == 1 && $$('isProtect').getValue() == 1 && cnt > 0 && !is_no_pdf) {
                                                                                        $$('send_btn').enable();
                                                                                    } else {
                                                                                        $$('send_btn').disable();
                                                                                    }
                                                                                }
                                                                            )
                                                                    }
                                                                },
                                                                {
                                                                    view: 'button',
                                                                    css: 'webix_primary',
                                                                    value: 'Очистить',
                                                                    id: 'clearPersonsBtn',
                                                                    minWidth: 150,
                                                                    maxWidth: 301,
                                                                    disabled: true,
                                                                    click: function () {
                                                                        webix.confirm('Вы действительно хотите очистить данные о ваших работниках?')
                                                                            .then(
                                                                                function () {
                                                                                    $$("person_table").clearAll();
                                                                                    $$('send_btn').disable();
                                                                                    $$('clearPersonsBtn').disable();
                                                                                }
                                                                            )
                                                                    }
                                                                }
                                                            ]
                                                        }
                                                    ]
                                                }
                                            ]
                                        }
                                    },
                                    view_section('Подача заявки'),
                                    {
                                        view: 'template',
                                        id: 'consent',
                                        height: 200,
                                        readonly: true,
                                        scroll: true,
                                        template: ''
                                    },
                                    {
                                        rows:[
                                            {
                                                view: 'template',
                                                borderless: true,
                                                css: 'personalTemplateStyle',
                                                template: 'Подтверждаю согласие работников на обработку персональных данных <span style = "color: red">*</span>',
                                                autoheight: true
                                            },
                                            {
                                                view: 'checkbox',
                                                name: 'isAgree',
                                                id: 'isAgree',
                                                labelPosition: 'top',
                                                invalidMessage: 'Поле не может быть пустым',
                                                required: true,
                                                on: {
                                                    onChange(newv, oldv) {
                                                        let cnt = $$('person_table').data.count();
                                                        let is_no_pdf = $$('no_pdf').getValue() == upload_chack_error;
                                                        if ($$('isAgree').getValue() == 1 && $$('isProtect').getValue() == 1 && cnt > 0 && !is_no_pdf) {
                                                            $$('send_btn').enable();
                                                        } else {
                                                            $$('send_btn').disable();
                                                        }
                                                        //$$('send_btn').disabled = !($$('isAgree').getValue() && $$('isProtect').getValue() )
                                                    }
                                                }
                                            },
                                        ]
                                    },
                                    {
                                        view: 'template',
                                        id: 'prescription',
                                        height: 550,
                                        readonly: true,
                                        scroll: true,
                                        template: ''
                                    },
                                    { rows:
                                            [
                                             {
                                                view: 'template',
                                                borderless: true,
                                                css: 'personalTemplateStyle',
                                                template: 'Подтверждаю обязательное выполнение предписания Управления Роспотребнадзора по Республике Бурятия <span style = "color: red">*</span>',
                                                autoheight: true
                                             },
                                             {
                                                view: 'checkbox',
                                                name: 'isProtect',
                                                id: 'isProtect',
                                                labelPosition: 'top',
                                                invalidMessage: 'Поле не может быть пустым',
                                                required: true,
                                                on: {
                                                    onChange(newv, oldv) {
                                                        let cnt = $$('person_table').data.count();
                                                        let is_no_pdf = $$('no_pdf').getValue() == upload_chack_error;
                                                        if ($$('isAgree').getValue() == 1 && $$('isProtect').getValue() == 1 && cnt > 0 && !is_no_pdf) {
                                                            $$('send_btn').enable();
                                                        } else {
                                                            $$('send_btn').disable();
                                                        }
                                                    }
                                                }
                                            },
                                        ]
                                    },
                                    {
                                        view: 'template',
                                        id: 'label_sogl',
                                        borderless: true,
                                        css: {
                                            'font-family' : 'Roboto, sans-serif',
                                            'font-size' : '14px;',
                                            'font-weight' : '500;',
                                            'color' : '#313131;',
                                            'padding' : ' 0px 3px !important;',
                                            'text-align' : 'center'
                                        },
                                        template: 'Информация мною прочитана и я согласен с ней при подаче заявки',
                                        autoheight: true
                                    },
                                    {
                                        id: 'send_request_btn_rows',
                                        rows:[]
                                    },
                                    {
                                        responsive: 'send_request_btn_rows',
                                        cols: [
                                            {
                                                view: 'button',
                                                css: 'webix_primary',
                                                value: 'Отменить',
                                                minWidth: 150,
                                                align: 'center',
                                                click: function () {
                                                    $$('menu').callEvent('onMenuItemClick', ['Requests']);
                                                }
                                            },
                                            {
                                                id: 'send_btn',
                                                view: 'button',
                                                css: 'webix_primary',
                                                minWidth: 150,
                                                value: 'Подать заявку',
                                                //НЕ МЕНЯТЬ!
                                                disabled: true, //НЕ МЕНЯТЬ!
                                                //НЕ МЕНЯТЬ!
                                                align: 'center',
                                                click: function () {

                                                    if ($$('form').validate()) {

                                                        let params = $$('form').getValues();

                                                        params.organizationId = ID_ORGANIZATION;

                                                        if (params.isAgree != 1) {
                                                            webix.message('Необходимо подтвердить согласие работников на обработку персональных данных', 'error')
                                                            return false
                                                        }
                                                        if (params.isProtect != 1) {
                                                            webix.message('Необходимо подтвердить обязательное выполнение предписания Управления Роспотребнадзора по Республике Бурятия', 'error')
                                                            return false
                                                        }

                                                        let cur_date = new Date();
                                                        let dif = Math.abs((cur_date.getTime() - pred_date.getTime()) / 1000);
                                                        pred_date = new Date();
                                                        if (dif < 5) {
                                                            webix.message('Слишком частое нажатие на кнопку', 'error')
                                                            return false
                                                        }
                                                        // if(!$$('upload').files.data.count()){
                                                        //     webix.message('Необходимо вложить файл', 'error')
                                                        //     $$('upload').focus()
                                                        //     return false
                                                        // }

                                                        let persons = []
                                                        $$('person_table').data.each(function (obj) {
                                                            let person = {
                                                                lastname: obj.lastname,
                                                                firstname: obj.firstname,
                                                                patronymic: obj.patronymic
                                                            }
                                                            persons.push(person)
                                                        })
                                                        params.persons = persons

                                                        let addrs = []
                                                        $$('addr_table').data.each(function (obj) {
                                                            let addr = {
                                                                addressFact: obj.addressFact,
                                                                personOfficeFactCnt: obj.personOfficeFactCnt
                                                            }
                                                            addrs.push(addr)
                                                        })
                                                        params.addressFact = addrs

                                                        //params.attachment = uploadFile
                                                        //params.attachmentFilename = uploadFilename

                                                        $$('label_sogl').showProgress({
                                                            type: 'icon',
                                                            delay: 5000
                                                        })


                                                        $$('upload').send(function (response) {
                                                            let uploadedFiles = []
                                                            $$('upload').files.data.each(function (obj) {
                                                                let status = obj.status
                                                                let name = obj.name
                                                                if (status == 'server') {
                                                                    let sname = obj.sname
                                                                    uploadedFiles.push(sname)
                                                                }
                                                            })

                                                            if (uploadedFiles.length != $$('upload').files.data.count()) {
                                                                webix.message('Не удалось загрузить файлы.')
                                                                $$('upload').focus()
                                                                return false
                                                            }
                                                            console.log(uploadedFiles)
                                                            params.attachment = uploadedFiles.join(',')
                                                            console.log(params)

                                                            webix.ajax()
                                                                .headers({'Content-type': 'application/json'})
                                                                //.headers({'Content-type': 'application/x-www-form-urlencoded'})
                                                                .post('/cabinet/typed_form?request_type=' + ID_TYPE_REQUEST,
                                                                    JSON.stringify(params),
                                                                    //params,
                                                                    function (text, data, xhr) {
                                                                        console.log(text);
                                                                        let errorText = "ЗАЯВКА НЕ ВНЕСЕНА. ОБНАРУЖЕНЫ ОШИБКИ ЗАПОЛНЕНИЯ: ";
                                                                        if (text.includes(errorText)) {
                                                                            webix.alert({
                                                                                title: "ИСПРАВЬТЕ ОШИБКИ",
                                                                                ok: "Вернуться к заполнению заявки",
                                                                                text: text
                                                                            });
                                                                            $$('label_sogl').hideProgress();
                                                                        } else if (text.includes("Данный тип заявки не существует!")) {
                                                                            webix.alert({
                                                                                title: "ВНИМАНИЕ!",
                                                                                ok: "ОК",
                                                                                text: text
                                                                            });
                                                                            $$('label_sogl').hideProgress();
                                                                        } else if (text.includes("Подача заявок с данным типом закрыта!")) {
                                                                            webix.alert({
                                                                                title: "ВНИМАНИЕ!",
                                                                                ok: "ОК",
                                                                                text: text
                                                                            });
                                                                            $$('label_sogl').hideProgress();
                                                                        } else if (text.includes("Невозможно сохранить заявку")) {
                                                                            webix.alert({
                                                                                title: "ВНИМАНИЕ!",
                                                                                ok: "ОК",
                                                                                text: text
                                                                            });
                                                                            $$('label_sogl').hideProgress();
                                                                        } else {
                                                                            webix.confirm({
                                                                                title: "Заявка внесена",
                                                                                ok: "Закрыть",
                                                                                cancel: "Внести еще одну заявку",
                                                                                text: text
                                                                            })
                                                                                .then(function () {
                                                                                    $$('label_sogl').hideProgress();
                                                                                    $$('menu').callEvent('onMenuItemClick', ['Requests']);
                                                                                })
                                                                                .fail(function () {
                                                                                    $$('label_sogl').hideProgress()
                                                                                    $$('form').clear();
                                                                                    $$('upload').setValue();
                                                                                    $$('form_person').clear();
                                                                                    $$('form_addr').clear();
                                                                                    $$('addr_table').clearAll();
                                                                                    $$('person_table').clearAll();
                                                                                    $$('reqBasis').focus();
                                                                                    $$("departmentId").setValue(departmentId);
                                                                                });
                                                                        }
                                                                    })
                                                        })
                                                    } else {
                                                        webix.message('Не заполнены обязательные поля. Для просмотра прокрутите страницу вверх', 'error')
                                                    }
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        paddingLeft: 10,
                                        view: 'label',
                                        label: '',
                                        id: 'file'
                                    }
                                ],
                            }
                        ]
                    }
                }
            ]
        }, $$('content'))

        let typeRequest = data.json();

        webix.ajax('cls_departments').then(function (data) {
            let departments = data.json();

            $$('departmentId').define('options', departments);
            $$('departmentId').refresh();

            // let descDepartments = '№\tНаименование органа власти\tОписание\n';
            // departments.forEach((dep, index) => {
            //     if (dep.description) {
            //         descDepartments += (index + 1) + '. ' + dep.name + '\n';
            //         descDepartments += dep.description + '\n';
            //     }
            // });
            // $$('desc_departments').setValue(descDepartments);
        });

        $$('activityKind').setHTML('<span style="font-family: Roboto, sans-serif;font-size: 14px;font-weight: 500;color: #313131;">' +
            'Тип заявки: ' + typeRequest.activityKind + '</span>');
        let department = typeRequest.department;
        if (department) {
            departmentId = (department.id);
            $$("departmentId").setValue(department.id);
            $$("departmentId").disable();
        }
        $$('prescription').setHTML(typeRequest.prescription);
        $$('consent').setHTML(typeRequest.consent);

        if (typeRequest.settings) {
            const settings = JSON.parse(typeRequest.settings, function (key, value) {
                if (value === 'webix.rules.isChecked') {
                    return webix.rules.isChecked;
                }
                return value;
            });
            if (settings.fields) {
                settings.fields.forEach(field => {
                    $$('form').addView(field.ui, field.pos);
                })
            }
        }

        webix.extend($$('label_sogl'), webix.ProgressBar);
    });
}

function showRequestViewForm(data) {
    webix.ui({
        id: 'content',
        rows: [
            {
                view: 'scrollview',
                scroll: 'xy',
                body: {
                    type: 'space',
                    rows: [
                        {
                            id: 'form',
                            view: 'form',
                            complexData: true,
                            data: data,
                            elements: [
                                {
                                    view: 'text',
                                    id: 'activityKind',
                                    autoheight: true,
                                    // align: 'center',
                                    label: 'Тип заявки',
                                    labelPosition: 'top',
                                    name: 'typeRequest.activityKind',
                                    readonly: true
                                },
                                view_section('Рассмотрение заявки'),
                                {
                                    view: 'checkbox',
                                    name: 'agree',
                                    labelPosition: 'top',
                                    readonly: true,
                                    labelRight: 'Подтверждено согласие работников на обработку персональных данных',
                                },
                                {
                                    view: 'checkbox',
                                    name: 'protect',
                                    labelPosition: 'top',
                                    readonly: true,
                                    labelRight: 'Подтверждено обязательное выполнение требований по защите от COVID-19',
                                },
                                {
                                    view: 'text',
                                    name: 'statusReviewName',
                                    labelPosition: 'top',
                                    readonly: true,
                                    label: 'Статус',
                                },
                                {
                                    view: 'textarea',
                                    id: 'reject_comment',
                                    readonly: true,
                                    name: 'rejectComment',
                                    label: 'Обоснование принятия или отказа',
                                    labelPosition: 'top',
                                    height: 100
                                },
                                view_section('Обоснование заявки'),
                                {
                                    rows: [
                                        {
                                            view: 'textarea',
                                            height: 150,
                                            label: 'Обоснование заявки',
                                            name: 'reqBasis',
                                            readonly: true,
                                            labelPosition: 'top'
                                        },
                                        {
                                            cols: [
                                                {
                                                    id: 'filename_label',
                                                    view: "label",
                                                    label: 'Вложенный файл:',
                                                    width: 150
                                                },
                                                {
                                                    paddingLeft: 10,
                                                    view: 'list',
                                                    //height: 100,
                                                    autoheight: true,
                                                    select: false,
                                                    template: '#value#',
                                                    label: '',
                                                    name: 'attachmentFilename',
                                                    borderless: true,
                                                    data: [],
                                                    id: 'filename'
                                                }
                                            ]
                                        },
                                    ]
                                },
                                view_section('Адресная информация'),
                                {
                                    view: 'datatable',
                                    name: 'addressFact',
                                    label: '',
                                    labelPosition: 'top',
                                    height: 200,
                                    select: 'row',
                                    editable: true,
                                    id: 'addr_table',
                                    resizeColumn: true,
                                    readonly: true,
                                    // resizeRow:true,
                                    fixedRowHeight: false,
                                    rowLineHeight: 25,
                                    rowHeight: 25,
                                    //autowidth:true,
                                    columns: [
                                        {
                                            id: 'addressFact',
                                            header: 'Фактический адрес осуществления деятельности',
                                            fillspace: 5
                                        },
                                        {
                                            id: 'personOfficeFactCnt',
                                            header: 'Числ. работающих',
                                            fillspace: 1
                                        }
                                    ],
                                    url: 'doc_address_fact/' + data.id
                                },
                                view_section('Данные о численности работников'),
                                {
                                    type: 'space',
                                    rows: [
                                        // {
                                        //     view: 'text',
                                        //     name: 'personSlrySaveCnt',
                                        //     label: 'Суммарная численность работников, в отношении которых установлен режим работы нерабочего дня с сохранением заработной платы',
                                        //     labelPosition: 'top',
                                        //     validate: function (val) {
                                        //         return !isNaN(val * 1);
                                        //     },
                                        //     invalidMessage: 'Поле не может быть пустым',
                                        //     readonly: true
                                        // },
                                        {
                                            view: 'text',
                                            name: 'personRemoteCnt',
                                            label: 'Суммарная численность работников, подлежащих переводу на дистанционный режим работы',
                                            readonly: true,
                                            labelPosition: 'top'
                                        },
                                        {
                                            view: 'text',
                                            name: 'personOfficeCnt',
                                            label: 'Суммарная численность работников, не подлежащих переводу на дистанционный режим работы (посещающие рабочие места)',
                                            labelPosition: 'top',
                                            readonly: true
                                        },
                                    ]
                                },
                                view_section('Данные о работниках, чья деятельность предусматривает выход на работу'),
                                {
                                    id: 'person_table',
                                    view: 'datatable',
                                    height: 600,
                                    name: 'persons',
                                    select: 'row',
                                    resizeColumn: true,
                                    readonly: true,
                                    columns: [
                                        {
                                            id: 'lastname',
                                            header: 'Фамилия',
                                            adjust: true,
                                            sort: 'string',
                                            fillspace: true
                                        },
                                        {
                                            id: 'firstname',
                                            header: 'Имя',
                                            adjust: true,
                                            sort: 'string',
                                            fillspace: true
                                        },
                                        {id: 'patronymic', header: 'Отчество', adjust: true, sort: 'string'},
                                    ],
                                    url: '/doc_persons/' + data.id
                                }
                            ]
                        }
                    ]
                }
            }
        ]
    }, $$('content'));

    let paths = data.attachmentPath.split(',')

    let fileList = []
    paths.forEach((path, index) => {
        let filename = path.split('\\').pop().split('/').pop()
        if (filename != '' &&
            ((filename.toUpperCase().indexOf('.PDF') != -1) ||
                (filename.toUpperCase().indexOf('.ZIP') != -1)
            )) {
            filename = '<a href="' + LINK_PREFIX + filename + LINK_SUFFIX + '" target="_blank">'
                + filename + '</a>'
            fileList.push({id: index, value: filename})
        }
    })
    if (fileList.length > 0) {
        $$('filename').parse(fileList)
    } else {
        $$('filename_label').hide()
        $$('filename').hide()
    }

    if (data.statusReview === 0) {
        $$('reject_comment').hide()
    } else if (data.statusReview === 1) {
        $$('reject_comment').hide()
    }
}

function adaptiveRequests(){
    let form_addrChildViewId = $$('form_addr').getChildViews()[0].config.id;

    if(form_addrChildViewId !== 'requestTextRows'){
        $$('form_addr').addView({
            type: 'space',
            id: 'requestTextRows',
            rows:[]
        },0); $$('requestTextRows').addView($$('addressFact')); $$('requestTextRows').addView($$('personOfficeFactCnt'));

        $$('form_person_rows').show();
        $$('form_person_rows').addView($$('person_lastname')); $$('form_person_rows').addView($$('person_firstname'));
        $$('form_person_rows').addView($$('person_patronymic'));

        $$('form_person').removeView($$('form_person_cols'));
        $$('form_addr').removeView($$('form_addr_cols'));

        $$('upload').config.height = 80; $$('upload').resize();
    }

}
