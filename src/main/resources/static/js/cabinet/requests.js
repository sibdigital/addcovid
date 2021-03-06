const requests = {
    // view: 'scrollview',
    // scroll: 'xy',
    // body: {
    //     type: 'space',
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
                        fixedRowHeight:false,
                        tooltip: true,
                        rowLineHeight:28,
                        pager: 'Pager',
                        datafetch: 25,
                        columns: [
                            {
                                id: "typeRequest",
                                header: "Вид деятельности",
                                template: "#activityKind#",
                                tooltip: false,
                                minWidth: 400,
                                fillspace: true
                            },
                            {
                                id: "statusReviewName",
                                header: "Статус",
                                tooltip: false,
                                width: 200
                            },
                            {
                                id: "department",
                                header: "Рассматривает",
                                template: "#departmentName#",
                                tooltip: false,
                                width: 200
                            },
                            {
                                id: "time_Create",
                                header: "Дата подачи",
                                width: 200,
                                tooltip: false,
                            },
                            {
                                id: "time_Review",
                                header: "Дата рассмотрения",
                                width: 200,
                                tooltip: false,
                            },
                            {
                                tooltip: false,
                                width: 100,
                                header: "Действия",
                                template: function (obj) {
                                    if (obj.statusReview == 0) {
                                        return '<span webix_tooltip="Просмотреть" class="webix_icon mdi mdi-eye-outline"></span>';
                                    } else if (obj.statusReview == 100) {
                                        return '<span webix_tooltip="Редактировать" class="webix_icon mdi mdi-pencil-plus-outline"></span>';
                                    } else {
                                        return '<span webix_tooltip="Просмотреть" class="webix_icon mdi mdi-eye-outline"></span>' +
                                            '<span webix_tooltip="Подать повторно" style="margin-left: 5px" class="webix_icon mdi mdi-keyboard-return"></span>';
                                    }
                                }
                            },
                        ],
                        scheme: {
                            $init: function (obj) {
                                let tmp_time_create = obj.timeCreate.substr(0, 16);
                                obj.time_Create = tmp_time_create.replace("T", " ");
                                if (obj.statusReview == 1 || obj.statusReview == 2) {
                                    if (obj.timeReview) {
                                        let tmp_time_review = obj.timeReview.substr(0, 16);
                                        obj.time_Review = tmp_time_review.replace("T", " ");
                                    }
                                }
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
                            'data->onStoreUpdated': function() {
                                this.adjustRowHeight(null, true);
                            },
                        },
                        onClick: {
                            "mdi-eye-outline": function (ev, id, html) {
                                let item = this.getItem(id);
                                setTimeout(function () {
                                    showRequestViewForm(item);
                                }, 100);
                            },
                            "mdi-pencil-plus-outline": function (ev, id, html) {
                                let item = this.getItem(id);
                                setTimeout(function () {
                                    showRequestWizard(item);
                                }, 100);
                            },
                            "mdi-keyboard-return": function (ev, id, html) {
                                let item = this.getItem(id);
                                setTimeout(function () {
                                    const request = findRequestByType(item.typeRequestId)
                                    if (request.id != 0) {
                                        if (request.status == 100) {
                                            webix.alert({
                                                title: "Подача заявки",
                                                ok: "ОК",
                                                text: "Заявка с таким видом деятельности уже создана. Отредактируйте и подайте её."
                                            }).then(function () {
                                                $$('menu').callEvent('onMenuItemClick', ['Requests']);
                                            });
                                            return;
                                        } else if (request.status == 0) {
                                            webix.alert({
                                                title: "Подача заявки",
                                                ok: "ОК",
                                                text: "Заявка с таким видом деятельности уже находится на рассмотрении. Повторная подача невозможна."
                                            }).then(function () {
                                                $$('menu').callEvent('onMenuItemClick', ['Requests']);
                                            });
                                            return;
                                        }
                                    }
                                    showRequestWizard(item);
                                }, 100)
                            },
                            'data->onStoreUpdated': function() {
                                this.adjustRowHeight(null, true);
                            },
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
                                    showTypeRequestsPage();
                                }
                            }
                        ]
                    }
                ]
            }
        ]
    // }
}

function findRequestByType(idTypeRequest) {
    const xhr = webix.ajax().sync().get('check_existence_request', {id: idTypeRequest});
    return JSON.parse(xhr.responseText);
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

function showRequestViewForm(data) {
    showBtnBack(requests, 'requests_table');
    webix.ui({
        id: 'content',
        rows: [
            // {
                // view: 'scrollview',
                // scroll: 'xy',
                // body: {
                //     type: 'space',
                //     rows: [
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
                                    readonly: true
                                },
                                {
                                    view: 'text',
                                    id: 'departmentId',
                                    autoheight: true,
                                    // align: 'center',
                                    label: 'Рассматривает',
                                    labelPosition: 'top',
                                    readonly: true
                                },
                                view_section('Обоснование'),
                                {
                                    rows: [
                                        {
                                            view: 'textarea',
                                            id: 'reqBasis',
                                            height: 150,
                                            label: 'Обоснование заявки',
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
            //         ]
            //     }
            // }
        ]
    }, $$('content'));

    webix.ajax('org_requests/' + data.id).then(function (data) {
        data = data.json();

        $$('departmentId').setValue(data.department.name);
        $$('reqBasis').setValue(data.reqBasis);

        if (data.docRequestFiles && data.docRequestFiles.length > 0) {
            let fileList = []
            data.docRequestFiles.forEach((drf, index) => {
                const file = drf.organizationFile;
                const filename = '<a href="' + LINK_PREFIX + file.fileName + LINK_SUFFIX + '" target="_blank">'
                    + file.originalFileName + '</a>'
                fileList.push({id: index, value: filename})
            })
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
                            view: 'label',
                            label: prescription.name,
                            align: 'center'
                        },
                        {
                            id: 'prescriptionTexts' + prescription.id,
                            rows: []
                        }
                    ]
                })

                if (prescription.prescriptionTexts && prescription.prescriptionTexts.length > 0) {
                    prescription.prescriptionTexts.forEach((pt, ptIndex) => {
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
                            const consentPrescription = drp.additionalAttributes.consentPrescriptions.find(c => c.id == pt.id);
                            if (consentPrescription) {
                                consentPrescriptionChecked = consentPrescription.isAgree;
                            }
                        }

                        $$('prescriptionTexts' + prescription.id).addView({
                            rows: [
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
                                    view: 'checkbox',
                                    name: 'agree',
                                    labelPosition: 'top',
                                    readonly: true,
                                    labelRight: 'Подтверждено обязательное выполнение',
                                    value: consentPrescriptionChecked
                                },
                            ]
                        });
                    })
                }
            })
        } else {
            $$('prescriptions').addView({
                view: 'label',
                label: 'Отсутствуют предписания'
            });
        }

        if (data.statusReview === 1 || data.statusReview === 2) {
            $$('reject_comment').show();
        }

        const typeRequest = data.typeRequest;

        $$('activityKind').setValue(typeRequest.activityKind);
    });
}

function showTypeRequestsPage() {
    webix.ajax('cls_type_requests').then(function (data) {
        let typeRequests = data.json();

        // let vtxt = '<br/>' + `<a href="upload">Общие основания (более 100 сотрудников)</a><br/><br/>`;
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
            vtxt += 'Отсутствуют доступные виды деятельности.</br></br>';
        }

        const v = {
            id: 'content',
            // type: 'space',
            rows: [
            //     {
            //         view: 'scrollview',
            //         scroll: 'xy',
            //         body: {
            //             rows: [
                            {
                                view: 'template',
                                template: '<p><h3 style="text-align: center; color: #000000">Выберите вид деятельности, по которому хотите подать заявку</h3></p>',
                                autoheight: true,
                                align: 'center'
                            },
                            {
                                view: 'template',
                                template: vtxt,
                                // width: 200,
                                autoheight: true,
                            }
            //             ]
            //         }
            //     }
            ]
        };
        webix.ui(v, $$('content'));
    });
    showBtnBack(requests, );
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