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

            prescriptions.forEach((prescription, index) => {
                if (prescription.prescriptionTexts && prescription.prescriptionTexts.length > 0) {
                    $$('prescriptions').addView({
                        id: 'prescription' + prescription.id,
                        rows: [
                            {
                                view: 'label',
                                label: prescription.name,
                                align: 'center'
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

const requestWizard = {
    // view: 'scrollview',
    // scroll: 'xy',
    // body: {
    //     type: 'space',
        rows: [
            {
                view: 'form',
                type: 'clean',
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
                                        // type: 'wide',
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
                                                on:{
                                                    "onItemClick":function (obj){
                                                        attachFile(obj)
                                                    },
                                                    onAfterSelect:function (id){
                                                        document.getElementById(id).innerHTML="<img style='width: 65px; height: 65px' src='galochka.png'>"
                                                    }
                                                },
                                                template: function (obj) {
                                                    let docImg;
                                                    let downloadTime = obj.timeCreate.substr(11, 8) + ', ' + obj.timeCreate.substr(0, 10)
                                                    if (obj.fileExtension == ".zip") {
                                                        docImg = "zip.png"
                                                    } else {
                                                        docImg = "pdf.png"
                                                    }
                                                    return "<div id='2' class='overallRequestNew' >" +
                                                                "<div>" +
                                                                    "<div style='position: absolute; ' id='"+obj.id+"'><img src = " + docImg + "></div>" +
                                                                    "<div class='doc_title'>" + obj.originalFileName.slice(0, -4) + "</div>" +
                                                                    //"<div id='del_button' style='position: absolute;top: 0; right: 5px;' ondblclick='del_file()' class='mdi mdi-close-thick'></div>" +
                                                                    //"<div id='plus_button' style='position: absolute;top: 0; right: 20px;'  ondblclick='' class='mdi mdi-plus-thick'></div>" +
                                                                    "<div class='doc_time_create'>" + downloadTime + "</div>" +
                                                                    "<div class='download_docs'><a style='text-decoration: none; color: #1ca1c1' href=/uploads/" + obj.fileName + obj.fileExtension + " download>Скачать файл</a></div>" +
                                                                    //"<div id='"+obj.id+"' style='right: 0'></div> "+
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
                                                                value: 'Выбрать',
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
                                                                value: 'Добавить',
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
                                                        view: "label",
                                                        id: "labelFiles",
                                                        label: "Приложенные к заявке файлы",
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
                                                id: 'firstBackButton',
                                                css: 'webix_primary',
                                                maxWidth: 301,
                                                value: 'Назад',
                                                click: function () {
                                                    showTypeRequestsPage();
                                                }
                                            },
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
                                                label: 'Вид деятельности',
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
                                                        maxWidth: 301,
                                                        value: 'Назад',
                                                        click: back
                                                    },
                                                    {
                                                        view: 'button',
                                                        css: 'webix_primary',
                                                        value: 'Отменить',
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
    // }
}

function attachFile(id){
    let originalFileName = $$("docs_grid").getItem(id).originalFileName
    if($$("docs_grid").isSelected(id)){
        $$("choosedFiles").remove(id)
        $$("labelFiles").refresh()
    }else{
        $$("choosedFiles").add({
            id: id,
            title: originalFileName,
        },id)
    }
    $$("labelFiles").setValue("К заявке приложено "+$$('choosedFiles').count()+" файлов из " + $$("docs_grid").count())
}

function showRequestWizard(data) {
    webix.ui({
        id: 'content',
        rows: [
            requestWizard
        ]
    }, $$('content'));

    $$('firstBackButton').hide();

    $$('requestId').setValue(data.id);
    $$('reqBasis').setValue(data.reqBasis);

    webix.ajax('cls_type_request/' + data.typeRequest.id).then(function (data) {
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

        return webix.ajax('type_request_prescriptions', {idTypeRequest: typeRequest.id});
    }).then(function (data) {
        const prescriptions = data.json();
        if (prescriptions && prescriptions.length > 0) {

            prescriptions.forEach((prescription, index) => {
                if (prescription.prescriptionTexts && prescription.prescriptionTexts.length > 0) {
                    $$('prescriptions').addView({
                        id: 'prescription' + prescription.id,
                        rows: [
                            {
                                view: 'label',
                                label: prescription.name,
                                align: 'center'
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

function back() {
    $$("wizard").back();
}

function next(page) {
    $$("wizard").getChildViews()[page].show();
}