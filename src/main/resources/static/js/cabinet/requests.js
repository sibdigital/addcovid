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
                                header: "Предписание",
                                template: "#typeRequest.activityKind#",
                                minWidth: 350,
                                fillspace: true
                            },
                            {
                                id: "statusReviewName",
                                header: "Статус",
                                adjust: true,
                                width: 300
                            },
                            {
                                id: "time_Create",
                                header: "Дата подачи",
                                adjust: true,
                                format: dateFormat,
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

                                    webix.ajax('/prescriptions').then(function (data) {
                                        let typeRequests = data.json();

                                        // let vtxt = '<br/>' + `<a href="/upload">Общие основания (более 100 сотрудников)</a><br/><br/>`;
                                        let vtxt = '<br/>';

                                        if (typeRequests.length > 0) {
                                            for (let j = 0; j < typeRequests.length; j++) {
                                                if (typeRequests[j].id == 100) {
                                                    continue;
                                                }
                                                // if (typeRequests[j].statusRegistration == 1 && typeRequests[j].statusVisible == 1) {
                                                let labl = typeRequests[j].activityKind;//.replace(new RegExp(' ', 'g'), '&nbsp');
                                                let vdid = typeRequests[j].id;
                                                let reqv = 'typed_form?request_type=' + vdid;
                                                vtxt += `<a href="#${reqv}" onclick="showRequestCreateForm(${vdid})">` + labl + '</a><br/><br/>';
                                                // }
                                            }
                                        } else {
                                            vtxt += 'Отсутствуют предписания.</br></br>';
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

function allChecked() {
    if ($$('isAgree').getValue() !== 1) {
        return false;
    }
    const countPrescriptions = $$('prescriptions').getChildViews().length;
    if (countPrescriptions > 0) {
        for (let num = 0; num < countPrescriptions; num++) {
            if ($$('consentPrescription' + num).getValue() !== 1) {
                return false;
            }
        }
    }
    return true;
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
                                view_section('Рассмотрение заявки'),
                                {
                                    view: 'checkbox',
                                    name: 'agree',
                                    labelPosition: 'top',
                                    readonly: true,
                                    labelRight: 'Подтверждено согласие работников на обработку персональных данных',
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
                                    hidden: true,
                                    height: 100
                                },
                                view_section('Информация о предписании'),
                                {
                                    view: 'text',
                                    id: 'activityKind',
                                    autoheight: true,
                                    // align: 'center',
                                    label: 'Наименование предписания',
                                    labelPosition: 'top',
                                    name: 'typeRequest.activityKind',
                                    readonly: true
                                },
                                {
                                    view: 'text',
                                    id: 'restrictionType',
                                    autoheight: true,
                                    // align: 'center',
                                    label: 'Тип ограничения',
                                    labelPosition: 'top',
                                    readonly: true
                                },
                                {
                                    id: 'prescriptions',
                                    rows: []
                                },
                            ]
                        }
                    ]
                }
            }
        ]
    }, $$('content'));

    const typeRequest = data.typeRequest;

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
            let consentPrescriptionChecked = false;
            if (data.additionalAttributes && data.additionalAttributes.consentPrescriptions) {
                consentPrescriptionChecked = data.additionalAttributes.consentPrescriptions.find(c => c.id == prescription.id).isAgree;
            }
            $$('prescriptions').addView({
                rows: [
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