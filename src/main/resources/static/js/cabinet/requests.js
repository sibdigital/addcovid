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
                                id: "statusReviewName",
                                header: "Статус",
                                adjust: true,
                                width: 300
                            },
                        ],
                        scheme: {
                            $init: function (obj) {
                                obj.time_Create = obj.timeCreate.replace("T", " ") //dateUtil.toDateFormat(obj.timeCreate);
                                let requestStatus = obj.statusReviewName;
                                if(requestStatus == "На рассмотрении"){
                                    obj.$css = 'oncheck';
                                }else if(requestStatus == "Одобрена"){
                                    obj.$css = 'confirmed';
                                }else if(requestStatus == "Отклонена"){
                                    obj.$css = 'cancelled';
                                }
                                $$('requests_table').refresh()
                            }
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

                                        // let vtxt = '<br/>' + `<a href="/upload">Общие основания (более 100 сотрудников)</a><br/><br/>`;
                                        let vtxt = '<br/>';

                                        if (typeRequests.length > 0) {
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
                                        } else {
                                            vtxt += 'Отсутствуют типы заявок.</br></br>';
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
            requestWizard
        ]
    }, $$('content'));

    $$('requestId').setValue(data.id);

    webix.ajax('cls_type_request/' + data.typeRequest.id).then(function (data) {

        const typeRequest = data.json();

        $$('activityKind').setValue(typeRequest.activityKind);

        if (typeRequest.regTypeRequestRestrictionTypes && typeRequest.regTypeRequestRestrictionTypes.length > 0) {
            $$('restrictionType').setValue(typeRequest.regTypeRequestRestrictionTypes[0].regTypeRequestRestrictionTypeId.clsRestrictionType.name);
        }

        if (typeRequest.regTypeRequestPrescriptions && typeRequest.regTypeRequestPrescriptions.length > 0) {
            typeRequest.regTypeRequestPrescriptions.forEach((prescription, index) => {
                const files = [];
                if (prescription.regTypeRequestPrescriptionFiles && prescription.regTypeRequestPrescriptionFiles.length > 0) {
                    prescription.regTypeRequestPrescriptionFiles.forEach((file) => {
                        const filename = '<a href="' + LINK_PREFIX + file.fileName + LINK_SUFFIX + '" target="_blank">'
                                + file.originalFileName + '</a>'
                        files.push({id: file.id, value: filename})
                    })
                }
                $$('prescriptions').addView({
                    rows: [
                        {
                            view: 'text',
                            id: 'prescription_id' + index,
                            value: prescription.id,
                            hidden: true
                        },
                        {
                            cols: [
                                {
                                    view: 'label',
                                    label: 'Предписание ' + (index + 1),
                                    align: 'center'
                                },
                            ]
                        },
                        {
                            view: 'template',
                            height: 550,
                            readonly: true,
                            scroll: true,
                            template: prescription.content
                        },
                        {
                            view: 'list',
                            autoheight: true,
                            template: '#value#',
                            data: files,
                        },
                        {
                            view: 'template',
                            borderless: true,
                            css: 'personalTemplateStyle',
                            template: 'Подтверждаю обязательное выполнение предписания <span style = "color: red">*</span>',
                            autoheight: true
                        },
                        {
                            view: 'checkbox',
                            id: 'consentPrescription' + index,
                            labelPosition: 'top',
                            required: true,
                            on: {
                                onChange(newv, oldv) {
                                    if (allChecked()) {
                                        $$('send_btn').enable();
                                    } else {
                                        $$('send_btn').disable();
                                    }
                                }
                            }
                        },
                    ]
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

const requestWizard = {
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
                        view: 'text',
                        id: 'requestId',
                        name: 'requestId',
                        hidden: true,
                    },
                    {
                        view: 'text',
                        id: 'typeRequestId',
                        name: 'typeRequestId',
                        hidden: true,
                    },
                    {
                        view: 'multiview',
                        id: 'wizard',
                        cells: [
                            {
                                rows: [
                                    {type: 'header', id:"headerId", template: 'Шаг 1. Приложите документы'},
                                    {
                                        type: 'wide',
                                        responsive: 'documentsMainLayout',
                                        cols: [
                                            {
                                                view: "dataview",
                                                id: "docs_grid",
                                                css: 'contacts',
                                                scroll: 'y',
                                                minWidth: 320,
                                                select:true,
                                                multiselect:"touch",
                                                template: function (obj) {
                                                    let docImg;
                                                    let downloadTime = obj.timeCreate.substr(11, 8) + ', ' + obj.timeCreate.substr(0, 10)
                                                    if (obj.fileExtension == ".zip") {
                                                        docImg = "zip.png"
                                                    } else {
                                                        docImg = "pdf.png"
                                                    }
                                                    return "<div id='2' class='overallRequestNew'>" +
                                                                "<div>" +
                                                                    "<img style='position: absolute' src = " + docImg + "> " +
                                                                    "<div class='doc_title'>" + obj.originalFileName.slice(0, -4) + "</div>" +
                                                                    //"<div id='del_button' style='position: absolute;top: 0; right: 5px;' ondblclick='del_file()' class='mdi mdi-close-thick'></div>" +
                                                                    //"<div id='plus_button' style='position: absolute;top: 0; right: 20px;'  ondblclick='' class='mdi mdi-plus-thick'></div>" +
                                                                    "<div class='doc_time_create'>" + downloadTime + "</div>" +
                                                                    "<div class='download_docs'><a style='text-decoration: none; color: #1ca1c1' href=/uploads/" + obj.fileName + obj.fileExtension + " download>Скачать файл</a></div>" +
                                                                "</div>" +
                                                            "</div>"
                                                },
                                                url: "org_files",
                                                xCount: 1,
                                                type: {
                                                    height: "auto",
                                                    width: "auto",
                                                    float: "right"
                                                },
                                            },
                                            {
                                                rows:[
                                                    {
                                                        gravity: 0.4,
                                                        view: 'form',
                                                        id: 'formDocsLoad',
                                                        minWidth: 200,
                                                        position: 'center',
                                                        elements: [
                                                            {gravity: 0.6},
                                                            {
                                                                id: 'upload',
                                                                view: 'uploader',
                                                                css: 'webix_secondary',
                                                                value: 'Загрузить',
                                                                autosend: false,
                                                                upload: '/upload_files',
                                                                required: true,
                                                                accept: 'application/pdf, application/zip',
                                                                multiple: true,
                                                                link: 'docslist',
                                                            },
                                                            {
                                                                view: 'list', id: 'docslist', type: 'uploader',
                                                                autoheight: true, borderless: true
                                                            },
                                                            {
                                                                id: 'loadFileBtn',
                                                                view: 'button',
                                                                css: 'webix_primary',
                                                                value: 'Импорт',
                                                                align: 'center',
                                                                click: function () {
                                                                    $$('upload').send(function (response) {
                                                                        if (response.status == "server") {
                                                                            if (response.cause == "Файл успешно загружен") {

                                                                                webix.message(response.cause + ": " + response.sname, "success")
                                                                                console.log(response.cause)

                                                                            } else if (response.cause == "Ошибка сохранения" || response.cause == "Отсутствует организация") {

                                                                                webix.message(response.cause, "error")
                                                                                console.log(response.cause)

                                                                            } else if (response.cause == "Вы уже загружали этот файл") {
                                                                                webix.message(response.cause + ": " + response.sname, "error")
                                                                                console.log(response.cause)
                                                                            }
                                                                        }
                                                                        $$("upload").files.data.clearAll();
                                                                        $$('docs_grid').load('org_files');
                                                                    })
                                                                }
                                                            },
                                                            {}
                                                        ]
                                                    },
                                                     {
                                                        view: "button",
                                                        css: 'webix_primary',
                                                        value: 'Приложить файлы',
                                                        click: function (){
                                                            $$("choosedFiles").clearAll()
                                                            let params = $$('docs_grid').getSelectedItem(true) //Массив всех выбранных объектов
                                                            for(let i in params){
                                                                $$("choosedFiles").add({
                                                                    id: params[i].id,
                                                                    title: params[i].originalFileName,
                                                                },0)
                                                            }
                                                            $$("labelFiles").setValue("*Список прикрепленных к заявке файлов ("+params.length+")")
                                                            $$("labelFiles").refresh()
                                                        }
                                                    },
                                                    {
                                                        view: "label",
                                                        id: "labelFiles",
                                                        label: "*Список прикрепленных к заявке файлов",
                                                    },
                                                    {
                                                        view: "list",
                                                        id: "choosedFiles",
                                                        template: "#title#"
                                                    }
                                                ]
                                            },
                                        ]
                                    },
                                    {
                                        cols: [
                                            {},
                                            {
                                                view: 'button',
                                                css: 'webix_primary',
                                                maxWidth: 301,
                                                value: 'Продолжить',
                                                click: function () {
                                                    let params = $$('docs_grid').getSelectedItem(true) //Массив всех выбранных объектов
                                                    console.log(params)
                                                    next(1);
                                                }
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
                                                click: function () {
                                                    if ($$('employees_table').count() == 0) {
                                                        webix.message('Добавьте сотрудников', 'error');
                                                    } else {
                                                        next(2);
                                                    }
                                                }
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
                                                click: function () {
                                                    if ($$('address_fact_grid').count() == 0) {
                                                        webix.message('Добавьте фактические адреса', 'error');
                                                    } else {
                                                        next(3);
                                                    }
                                                }
                                            }
                                        ]
                                    }
                                ]
                            },
                            {
                                rows: [
                                    { type: 'header', template: 'Шаг 4. Подача заявки' },
                                    {
                                        type: 'form',
                                        rows: [
                                            {
                                                view: 'text',
                                                id: 'activityKind',
                                                autoheight: true,
                                                // align: 'center',
                                                label: 'Тип заявки',
                                                labelPosition: 'top',
                                                name: 'activityKind',
                                                readonly: true
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
                                                        options: 'cls_departments',
                                                    },
                                                ]
                                            },
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
                                            view_section('Информация о предписаниях'),
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

                                                                // params.requestId = $$('requestId').getValue();
                                                                params.organizationId = ID_ORGANIZATION;
                                                                // params.typeRequestId = $$('')

                                                                const selectedFiles = $$('docs_grid').getSelectedItem(true);
                                                                if (selectedFiles && selectedFiles.length > 0) {
                                                                    const fileIds = [];
                                                                    for (let i in selectedFiles) {
                                                                        fileIds.push(selectedFiles[i].id);
                                                                    }
                                                                    params.organizationFileIds = fileIds;
                                                                }

                                                                const countPrescriptions = $$('prescriptions').getChildViews().length;
                                                                console.log('countPrescriptions', countPrescriptions);
                                                                if (countPrescriptions > 0) {
                                                                    const docRequestPrescriptions = [];

                                                                    for (let num = 0; num < countPrescriptions; num++) {
                                                                        const docRequestPrescription = {};

                                                                        const prescriptionId = $$('prescription_id' + num).getValue();
                                                                        docRequestPrescription.prescriptionId = prescriptionId;

                                                                        const countPrescriptionTexts = $$('prescriptionTexts' + prescriptionId).getChildViews().length;
                                                                        console.log('countPrescriptionTexts', countPrescriptionTexts);
                                                                        if (countPrescriptionTexts > 0) {
                                                                            const additionalAttributes = {};

                                                                            let consentPrescriptions = [];
                                                                            for (let ptNum = 0; ptNum < countPrescriptionTexts; ptNum++) {
                                                                                const id = $$('prescriptionText_id' + prescriptionId + '_' + ptNum).getValue();
                                                                                console.log('prescriptionText_id' + prescriptionId + '_' + ptNum, id)
                                                                                consentPrescriptions.push({
                                                                                    id,
                                                                                    isAgree: $$('consentPrescription' + prescriptionId + '_' + ptNum).getValue()
                                                                                });
                                                                            }
                                                                            additionalAttributes.consentPrescriptions = consentPrescriptions;

                                                                            docRequestPrescription.additionalAttributes = additionalAttributes;
                                                                        }

                                                                        docRequestPrescriptions.push(docRequestPrescription);
                                                                    }

                                                                    params.docRequestPrescriptions = docRequestPrescriptions;
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

function allCheckedText() {
    const countPrescriptions = $$('prescriptions').getChildViews().length;
    if (countPrescriptions > 0) {
        for (let num = 0; num < countPrescriptions; num++) {
            const prescriptionId = $$('prescription_id' + num).getValue();
            const countPrescriptionTexts = $$('prescriptionTexts' + prescriptionId).getChildViews().length;
            for (let ptNum = 0; ptNum < countPrescriptionTexts; ptNum++) {
                if ($$('consentPrescription' + prescriptionId + '_' + ptNum).getValue() !== 1) {
                    return false;
                }
            }
        }
    }
    return true;
}

function back() {
    $$("wizard").back();
}

function next(page) {
    $$("wizard").getChildViews()[page].show();
}

function showRequestCreateForm(idTypeRequest, page) {
    webix.ui({
        id: 'content',
        rows: [
            requestWizard
        ]
    }, $$('content'))

    if (page) {
        $$("wizard").getChildViews()[page].show();
    }

    ID_TYPE_REQUEST = idTypeRequest;

    webix.ajax('cls_type_request/' + idTypeRequest).then(function (data) {

        webix.extend($$('newRequestForm'), webix.ProgressBar);

        const typeRequest = data.json();

        $$('typeRequestId').setValue(typeRequest.id);

        $$('activityKind').setValue(typeRequest.activityKind);

        if (typeRequest.department) {
            const department = typeRequest.department;
            departmentId = (department.id);
            $$("departmentId").setValue(department.id);
            $$("departmentId").disable();
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

        return webix.ajax('type_request_prescriptions', {idTypeRequest: idTypeRequest});
    }).then(function (data) {

        const prescriptions = data.json();

        if (prescriptions && prescriptions.length > 0) {
            // $$('prescriptions').addView({
            //     view: 'label',
            //     label: 'Ознакомтесь с предписаниями'
            // })

            prescriptions.forEach((prescription, index) => {
                if (prescription.prescriptionTexts && prescription.prescriptionTexts.length > 0) {
                    $$('prescriptions').addView({
                        id: 'prescription' + prescription.id,
                        rows: [
                            {
                                view: 'template',
                                type: 'section',
                                template: prescription.name
                            },
                            {
                                view: 'text',
                                id: 'prescription_id' + index,
                                value: prescription.id,
                                hidden: true
                            },
                            {
                                id: 'prescriptionTexts' + prescription.id,
                                rows: []
                            }
                        ]
                    });

                    prescription.prescriptionTexts.forEach((pt, ptIndex) => {
                        const files = [];

                        if (pt.prescriptionTextFiles && pt.prescriptionTextFiles.length > 0) {
                            pt.prescriptionTextFiles.forEach((file) => {
                                const filename = '<a href="' + LINK_PREFIX + file.fileName + LINK_SUFFIX + '" target="_blank">'
                                    + file.originalFileName + '</a>'
                                files.push({id: file.id, value: filename})
                            })
                        }

                        $$('prescriptionTexts' + prescription.id).addView({
                            rows: [
                                {
                                    view: 'text',
                                    id: 'prescriptionText_id' + prescription.id + '_' + ptIndex,
                                    value: pt.id,
                                    hidden: true
                                },
                                {
                                    cols: [
                                        {
                                            view: 'label',
                                            label: 'Текст №' + (ptIndex + 1),
                                            align: 'center'
                                        },
                                    ]
                                },
                                {
                                    view: 'template',
                                    height: 550,
                                    readonly: true,
                                    scroll: true,
                                    template: pt.content
                                },
                                {
                                    view: 'list',
                                    autoheight: true,
                                    template: '#value#',
                                    data: files,
                                },
                                {
                                    view: 'template',
                                    borderless: true,
                                    css: 'personalTemplateStyle',
                                    template: 'Подтверждаю обязательное выполнение предписания <span style = "color: red">*</span>',
                                    autoheight: true
                                },
                                {
                                    view: 'checkbox',
                                    id: 'consentPrescription' + prescription.id + '_' + ptIndex,
                                    labelPosition: 'top',
                                    required: true,
                                    on: {
                                        onChange(newv, oldv) {
                                            if (allCheckedText()) {
                                                $$('send_btn').enable();
                                            } else {
                                                $$('send_btn').disable();
                                            }
                                        }
                                    }
                                },
                            ]
                        });
                    })
                }
            })
        }
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
                                view_section('Обоснование'),
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
                                view_section('Информация о предписаниях'),
                                {
                                    id: 'prescriptions',
                                    rows: []
                                },
                                view_section('Информация о рассмотрении'),
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
                                    hidden: true,
                                    height: 100
                                },
                            ]
                        }
                    ]
                }
            }
        ]
    }, $$('content'));

    const typeRequest = data.typeRequest;

    // if (typeRequest.regTypeRequestRestrictionTypes && typeRequest.regTypeRequestRestrictionTypes.length > 0) {
    //     $$('restrictionType').setValue(typeRequest.regTypeRequestRestrictionTypes[0].regTypeRequestRestrictionTypeId.clsRestrictionType.name);
    // }

    if (data.attachmentPath) {
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
    } else if (data.docRequestFiles && data.docRequestFiles.length > 0) {
        console.log(data.docRequestFiles)
        let fileList = []
        data.docRequestFiles.forEach((drf, index) => {
            const file = drf.organizationFile;
            const filename = '<a href="' + LINK_PREFIX + file.filename + LINK_SUFFIX + '" target="_blank">'
                + file.originalFileName + '</a>'
            fileList.push({id: index, value: filename})
        })
        console.log(fileList)
        if (fileList.length > 0) {
            $$('filename').parse(fileList)
        } else {
            $$('filename_label').hide()
            $$('filename').hide()
        }
    }

    if (data.docRequestPrescriptions && data.docRequestPrescriptions.length > 0) {
        data.docRequestPrescriptions.forEach((drp, index) => {
            const prescription = drp.prescription;

            $$('prescriptions').addView({
                id: 'prescription' + prescription.id,
                rows: [
                    {
                        view: 'template',
                        type: 'section',
                        template: prescription.name
                    },
                    {
                        id: 'prescriptionTexts' + prescription.id,
                        rows: []
                    }
                ]
            })

            if (prescription.prescriptionTexts && prescription.prescriptionTexts.length > 0) {
                prescription.prescriptionTexts.forEach(pt => {
                    const files = [];
                    if (pt.prescriptionTextFiles && pt.prescriptionTextFiles.length > 0) {
                        pt.prescriptionTextFiles.forEach((file) => {
                            const filename = '<a href="' + LINK_PREFIX + file.fileName + LINK_SUFFIX + '" target="_blank">'
                                + file.originalFileName + '</a>'
                            files.push({id: file.id, value: filename})
                        })
                    }

                    let consentPrescriptionChecked = false;
                    if (drp.additionalAttributes && drp.additionalAttributes.consentPrescriptions) {
                        consentPrescriptionChecked = drp.additionalAttributes.consentPrescriptions.find(c => c.id == pt.id).isAgree;
                    }
                    $$('prescriptionTexts' + prescription.id).addView({
                        rows: [
                            {
                                cols: [
                                    {
                                        view: 'label',
                                        label: 'Текст №' + (index + 1),
                                        align: 'center'
                                    },
                                ]
                            },
                            {
                                view: 'template',
                                height: 550,
                                readonly: true,
                                scroll: true,
                                template: drp.content
                            },
                            {
                                view: 'list',
                                autoheight: true,
                                template: '#value#',
                                data: files,
                            },
                            {
                                view: 'checkbox',
                                name: 'agree',
                                labelPosition: 'top',
                                readonly: true,
                                labelRight: 'Подтверждено обязательное выполнение предписания',
                                value: consentPrescriptionChecked
                            },
                        ]
                    });
                })
            }
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

    if (data.statusReview === 1 || data.statusReview === 2) {
        $$('reject_comment').show();
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