webix.i18n.setLocale("ru-RU");

const dateFormat = webix.Date.dateToStr("%d.%m.%Y %H:%i:%s")

function view_section(title) {
    return {
        view: 'template',
        type: 'section',
        template: title
    }
}

// ***** ОБЩАЯ ИНФОРМАЦИЯ *****

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
                            },
                            {
                                view: 'textarea',
                                name: 'addressJur',
                                label: 'Юридический адрес',
                                labelPosition: 'top',
                                height: 80,
                                required: true
                            },
                            {
                                view: 'text',
                                name: 'email',
                                label: 'Адрес электронной почты',
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
                    }
                ],
                url: 'organization'
            }
        ],
    }
}

// ***** ЗАЯВКИ *****

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
                                fillspace: true
                            },
                            {
                                id: "time_Create",
                                header: "Дата подачи",
                                adjust: true,
                                format: dateFormat,
                                fillspace: true
                            },
                            {
                                id: "status",
                                header: "Статус",
                                template: "#statusReview#",
                                adjust: true,
                                width: 300
                            },
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
                            onItemDblClick: function (id) {
                                let item = this.getItem(id);
                                setTimeout(function () {
                                    showRequestViewForm(item)
                                }, 10);
                            }
                        },
                        url: 'org_requests'
                    },
                    {
                        cols: [
                            {
                                view: 'pager',
                                id: 'Pager',
                                height: 38,
                                size: 25,
                                group: 5,
                                template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                            },
                            {
                                view: 'button',
                                align: 'right',
                                maxWidth: 200,
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

let ID_TYPE_REQUEST = '';
let departmentId = '';
let uploadFile = '';
let uploadFilename = '';
let pred_date = new Date();
let upload_chack_error = 'Загружать можно только PDF-файлы и ZIP-архивы!';

function showRequestCreateForm(idTypeRequest) {

    ID_TYPE_REQUEST = idTypeRequest;

    webix.ajax('cls_type_request/' + idTypeRequest).then(function (data) {

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
                                elements: [
                                    {
                                        view: 'label',
                                        id: 'activityKind',
                                        autoheight: true,
                                        // align: 'center'
                                    },
                                    {
                                        view: 'select',
                                        id: 'departmentId',
                                        name: 'departmentId',
                                        label: 'Министерство, курирующее вашу деятельность',
                                        labelPosition: 'top',
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true,
                                        options: [],
                                    },
                                    view_section('Обоснование заявки'),
                                    {
                                        rows: [
                                            {
                                                view: 'textarea',
                                                height: 150,
                                                label: 'Обоснование заявки',
                                                name: 'reqBasis',
                                                id: 'reqBasis',
                                                invalidMessage: 'Поле не может быть пустым',
                                                required: true,
                                                labelPosition: 'top'
                                            },
                                            {
                                                view: 'label',
                                                label: '<span  style="text-align: center; color: red">Для загрузки нескольких файлов выбирайте их с зажатой клавишей Ctrl или заранее сожмите в ZIP-архив и загрузите его</span>',
                                                //css: 'main_label'
                                            },
                                            {
                                                view: 'label',
                                                label: '<span  style="text-align: center; color: red">Общий размер загружаемых файлов не должен превышать 60 Мб</span>',
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
                                                    {id: 'id', header: '', css: 'rank'},
                                                    {
                                                        id: 'addressFact',
                                                        header: 'Фактический адрес осуществления деятельности',
                                                        width: 300,
                                                        //editor: 'text',
                                                        //fillspace: true
                                                    },
                                                    {
                                                        id: 'personOfficeFactCnt',
                                                        header: 'Численность работников, не подлежащих переводу на дистанционный режим работы, осуществляющих деятельность по указанному в  пункте 11 настоящей формы фактическому адресу',
                                                        //editor: 'text',
                                                        fillspace: true
                                                        //width: 200
                                                    }
                                                ],
                                                data: [],
                                                on: {
                                                    'data->onStoreUpdated': function () {
                                                        this.data.each(function (obj, i) {
                                                            obj.id = i + 1;
                                                        });
                                                    }
                                                },
                                            },
                                            {
                                                view: 'form',
                                                id: 'form_addr',
                                                elements: [
                                                    {
                                                        type: 'space',
                                                        cols: [
                                                            {
                                                                view: 'text',
                                                                name: 'addressFact',
                                                                label: 'Фактический адрес',
                                                                labelPosition: 'top',
                                                                required: true
                                                            },
                                                            {
                                                                view: 'text',
                                                                name: 'personOfficeFactCnt',
                                                                inputWidth: '250',
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
                                                        cols: [
                                                            {
                                                                view: 'button',
                                                                css: 'webix_primary',
                                                                value: 'Добавить',
                                                                width: 150,
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
                                                                value: 'Изменить',
                                                                width: 150,
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
                                                                value: 'Удалить',
                                                                width: 150,
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
                                                view: 'text', name: 'personRemoteCnt',
                                                label: 'Суммарная численность работников, подлежащих переводу на дистанционный режим работы',
                                                invalidMessage: 'Поле не может быть пустым',
                                                validate: function (val) {
                                                    return !isNaN(val * 1) && (val.trim() !== '')
                                                },
                                                required: true,
                                                labelPosition: 'top'
                                            },
                                            {
                                                view: 'text', name: 'personOfficeCnt',
                                                label: 'Суммарная численность работников, не подлежащих переводу на дистанционный режим работы (посещающие рабочие места)',
                                                labelPosition: 'top',
                                                validate: function (val) {
                                                    return !isNaN(val * 1) && (val.trim() !== '')
                                                },
                                                invalidMessage: 'Поле не может быть пустым',
                                                required: true
                                            },
                                        ]
                                    },
                                    view_section('Данные о ваших работниках, чья деятельность предусматривает выход на работу (Обязательный для заполнения раздел)'),
                                    {
                                        view: 'scrollview',
                                        type: 'space',
                                        height: 600,
                                        scroll: 'y',
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
                                                        {id: 'id', header: '', css: 'rank', width: 50},
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
                                                        {
                                                            id: 'patronymic',
                                                            header: 'Отчество',
                                                            adjust: true,
                                                            sort: 'string'
                                                        },
                                                        //{ id: 'isagree', header: 'Согласие', width: 100, template: '{common.checkbox()}', css: 'center' }
                                                    ],
                                                    on: {
                                                        'data->onStoreUpdated': function () {
                                                            this.data.each(function (obj, i) {
                                                                obj.id = i + 1;
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
                                                            margin: 0,
                                                            cols: [
                                                                {
                                                                    view: 'text',
                                                                    name: 'lastname',
                                                                    inputWidth: '250',
                                                                    label: 'Фамилия',
                                                                    labelPosition: 'top'
                                                                },
                                                                {
                                                                    view: 'text',
                                                                    name: 'firstname',
                                                                    inputWidth: '250',
                                                                    label: 'Имя',
                                                                    labelPosition: 'top'
                                                                },
                                                                {
                                                                    view: 'text',
                                                                    name: 'patronymic',
                                                                    inputWidth: '250',
                                                                    label: 'Отчество',
                                                                    labelPosition: 'top'
                                                                },
                                                                {},
                                                            ]
                                                        },
                                                        {
                                                            //type: 'space',
                                                            margin: 5,
                                                            cols: [
                                                                {
                                                                    view: 'button',
                                                                    css: 'webix_primary',
                                                                    value: 'Добавить',
                                                                    width: 150,
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
                                                                    width: 150,
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
                                                                    width: 150,
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
                                                                    width: 150,
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
                                        view: 'checkbox',
                                        name: 'isAgree',
                                        id: 'isAgree',
                                        labelPosition: 'top',
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true,
                                        label: 'Подтверждаю согласие работников на обработку персональных данных',
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
                                    {
                                        view: 'template',
                                        id: 'prescription',
                                        height: 550,
                                        readonly: true,
                                        scroll: true,
                                        template: ''
                                    },
                                    {
                                        view: 'checkbox',
                                        name: 'isProtect',
                                        id: 'isProtect',
                                        labelPosition: 'top',
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true,
                                        label: 'Подтверждаю обязательное выполнение предписания Управления Роспотребнадзора по Республике Бурятия',
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
                                    {
                                        id: 'label_sogl',
                                        view: 'label',
                                        label: 'Информация мною прочитана и я согласен с ней при подаче заявки',
                                        align: 'center'
                                    },
                                    {
                                        cols: [
                                            {
                                                view: 'button',
                                                css: 'webix_primary',
                                                value: 'Отменить',
                                                align: 'center',
                                                click: function () {
                                                    $$('sidebar').callEvent('onAfterSelect', ['Requests']);
                                                }
                                            },
                                            {
                                                id: 'send_btn',
                                                view: 'button',
                                                css: 'webix_primary',
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
                                                                                    $$('sidebar').callEvent('onAfterSelect', ['Requests']);
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

        $$('activityKind').setHTML('<span style="">Тип заявки: ' + typeRequest.activityKind + '</span>');
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

// ***** НАСТРОЙКИ *****

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
                        view: 'text',
                        id: 'new_pass',
                        name: 'password',
                        label: 'Новый пароль',
                        labelPosition: 'top',
                        type: 'password',
                        maxWidth: 300
                    },
                    {
                        view: 'text',
                        id: 'retry_pass',
                        label: 'Подтвердите новый пароль',
                        labelPosition: 'top',
                        type: 'password',
                        maxWidth: 300,
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
                        css: 'webix_primary',
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
            }
        ]
    }
}

// ***** ВАКЦИНАЦИЯ *****

var updateFIOmodal = webix.ui({
    view: "window",
    width: 550,
    position: "center",
    modal: true,
    close: true,
    head: "Редактирование списка сотрудников",
    body: {
        view: 'form',
        id: 'form_employee',
        complexData: true,
        elements: [
            {
                view: 'text',
                name: 'person.lastname',
                label: 'Фамилия',
                labelPosition: 'top'
            },
            {
                view: 'text',
                name: 'person.firstname',
                label: 'Имя',
                labelPosition: 'top'
            },
            {
                view: 'text',
                name: 'person.patronymic',
                label: 'Отчество',
                labelPosition: 'top'
            },/*
                    {
                        view: 'checkbox',
                        id: 'isVaccinatedFlu',
                        name: 'isVaccinatedFlu',
                        label: 'Привит от гриппа',
                        labelPosition: 'top'
                    },
                    {
                        view: 'checkbox',
                        id: 'isVaccinatedCovid',
                        name: 'isVaccinatedCovid',
                        label: 'Привит от COVID-19',
                        labelPosition: 'top'
                    },*/
            {
                view: 'button',
                align: 'right',
                maxWidth: 550,
                css: 'webix_primary',
                value: 'Добавить',
                hotkey: "enter",
                click: function () {

                    let params = $$('form_employee').getValues()

                    webix.ajax()
                        .headers({'Content-type': 'application/json'})
                        .post('/employee', JSON.stringify(params))
                        .then(function (data) {
                            if (data.text() === 'Сотрудник добавлен') {
                                $$('form_employee').clear()
                                if (params.id) {
                                    webix.message('Сотрудник обновлен', 'success');
                                } else {
                                    webix.message(data.text(), 'success');
                                }
                                $$('employees_table').load('employees');
                            } else {
                                webix.message(data.text(), 'error');
                            }
                        });
                    $$('form_employee').clear()
                    updateFIOmodal.hide()
                }
            }
        ]

    }
});

const employees = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                cols:
                    [
                        {
                            view: "text",
                            id: "dtFilter",
                            maxWidth: 400,
                            placeholder: "Поиск по ФИО",
                        },
                        {
                            view: "button",
                            maxWidth: 150,
                            value: "Поиск",
                            click: filterText
                        }
                    ]
            },
            {
                view: 'datatable',
                id: "employees_table",
                minHeight: 570,
                select: "row",
                navigation: true,
                resizeColumn: true,
                pager: 'Pager',
                datafetch: 25,
                columns: [
                    {
                        header: "Фамилия",
                        template: "#person.lastname#",
                        fillspace: true,
                        sort: "text"
                    },
                    {
                        header: "Имя",
                        template: "#person.firstname#",
                        fillspace: true,
                        sort: "text"
                    },
                    {
                        header: "Отчество",
                        template: "#person.patronymic#",
                        fillspace: true,
                        sort: "text"
                    },
                    /*
                    {
                        header: "Привит от гриппа",
                        template: function (obj) {
                            if (obj.isVaccinatedFlu) {
                                return 'Да';
                            } else {
                                return 'Нет';
                            }
                        },
                        fillspace: true,
                        adjust: true
                    },
                    {
                        header: "Привит от COVID-19",
                        template: function (obj) {
                            if (obj.isVaccinatedCovid) {
                                return 'Да';
                            } else {
                                return 'Нет';
                            }
                        },
                        fillspace: true,
                        adjust: true
                    }*/
                ],
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
                    onItemDblClick: function (id) {
                        let item = this.getItem(id);
                        $$('form_employee').parse(item);
                        updateFIOmodal.show();

                    }
                },
                url: 'employees'
            },
            {
                cols: [
                    {
                        view: 'pager',
                        id: 'Pager',
                        height: 38,
                        size: 25,
                        group: 5,
                        template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                    },
                    // {
                    //     view: 'button',
                    //     align: 'right',
                    //     maxWidth: 200,
                    //     css: 'webix_primary',
                    //     value: 'Добавить сотрудника',
                    //     click: function () {
                    //         showEmployeeCreateForm();
                    //     }
                    // }
                    {
                            view: "button",
                            align: 'left',
                            maxWidth: 350,
                            css: 'webix_primary',
                            value: 'Добавить',
                            click: function ()
                            {
                                $$('form_employee').clear();
                                updateFIOmodal.show();
                            }
                        },
                        {
                            view: 'button',
                            align: 'right',
                            maxWidth: 350,
                            css: 'webix_primary',
                            value: 'Удалить',
                            click: function () {

                                let params = $$('employees_table').getSelectedItem()

                                if(!$$("employees_table").getSelectedId()) {
                                    webix.message("Не выбрана строка!","error");
                                    return;
                                }$$("employees_table").remove($$("employees_table").getSelectedId());
                                webix.ajax()
                                    .headers({'Content-type': 'application/json'})
                                    .post('/deleteEmployee', JSON.stringify(params))
                                    .then(function (data) {
                                        if (data.text() === "Сотрудник удалён") {
                                            webix.message("Сотрудник удалён", 'success');
                                            $$('form_employee').clear()
                                            $$('employees_table').load('employees');
                                        } else {
                                            webix.message(data.text(), 'error');
                                        }
                                    });
                                $$('form_employee').clear()
                                updateFIOmodal.hide()
                            }
                        }
                ]
            },

            /*
            {
                view: 'form',
                id: 'form_employee',
                complexData: true,
                elements: [
                    {
                        view: 'text',
                        name: 'person.lastname',
                        label: 'Фамилия',
                        labelPosition: 'top'
                    },
                    {
                        view: 'text',
                        name: 'person.firstname',
                        label: 'Имя',
                        labelPosition: 'top'
                    },
                    {
                        view: 'text',
                        name: 'person.patronymic',
                        label: 'Отчество',
                        labelPosition: 'top'
                    },/*
                    {
                        view: 'checkbox',
                        id: 'isVaccinatedFlu',
                        name: 'isVaccinatedFlu',
                        label: 'Привит от гриппа',
                        labelPosition: 'top'
                    },
                    {
                        view: 'checkbox',
                        id: 'isVaccinatedCovid',
                        name: 'isVaccinatedCovid',
                        label: 'Привит от COVID-19',
                        labelPosition: 'top'
                    },*/
            /*
                    {
                        view: 'button',
                        align: 'right',
                        maxWidth: 200,
                        css: 'webix_primary',
                        value: 'Добавить',
                        click: function () {

                            let params = $$('form_employee').getValues()

                            webix.ajax()
                                .headers({'Content-type': 'application/json'})
                                .post('/employee', JSON.stringify(params))
                                .then(function (data) {
                                    if (data.text() === 'Сотрудник добавлен') {
                                        $$('form_employee').clear()
                                        if (params.id) {
                                            webix.message('Сотрудник обновлен', 'success');
                                        } else {
                                            webix.message(data.text(), 'success');
                                        }
                                        $$('employees_table').load('employees');
                                    } else {
                                        webix.message(data.text(), 'error');
                                    }
                                });
                        }
                    },
                ]
            }*/
        ]
    }
}
function filterText(node){
  var text = $$("dtFilter").getValue();
  if (!text)
        return $$("employees_table").filter();
  $$("employees_table").filter(function(obj){
      webix.message(obj.title,"success")
      return obj.title == text;
  });
}
webix.attachEvent("onFocusChange", function(to, from){
    if (from && from.getTopParentView().config.view == "window" && !to){
        from.getTopParentView().hide();
    }
})

webix.ready(function () {
    let layout = webix.ui({
        rows: [
            {
                view: 'toolbar',
                // padding: 5,
                elements: [
                    {
                        view: 'button',
                        type: 'icon',
                        icon: 'wxi-dots',
                        // css: 'webix_primary',
                        width: 37,
                        click: function () {
                            $$('sidebar').toggle();
                        }
                    },
                    {
                        view: 'label',
                        label: `<span style="font-size: 1.0rem">Личный кабинет</span>`,
                    },
                    {
                        view: 'label',
                        label: '<a href="/logout" title="Выйти">Выйти</a>',
                        align: 'right'
                    }
                ]
            },
            {
                cols: [
                    {
                        view: 'sidebar',
                        id: 'sidebar',
                        // collapsed: true,
                        css: 'webix_dark',
                        data: [
                            {id: "CommonInfo", value: 'Общая информация'},
                            {id: "Employees", value: 'Сотрудники'},
                            {id: "Requests", value: 'Заявки'},
                            {id: "Settings", value: 'Настройки'},
                        ],
                        on: {
                            onAfterSelect: function (id) {
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
                                    case 'Employees': {
                                        view = employees;
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
        layout.define("width", document.body.clientWidth);
        layout.resize();
    });
})
