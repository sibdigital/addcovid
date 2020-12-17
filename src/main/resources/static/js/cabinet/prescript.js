const prescript = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                view: 'datatable',
                id: "prescriptions_table",
                minWidth: 220,
                select: "row",
                navigation: true,
                resizeColumn: true,
                datafetch: 25,
                columns: [
                    {header: "Вид деятельности", template: "#typeRequestName#", fillspace: true},
                    {id: 'time_Publication', header: "Дата публикации", adjust: true, format: dateFormat},
                    {
                        id: 'accepted',
                        header: "Ознакомлен",
                        adjust: true,
                        template: function (obj, type, value) {
                            if (value) {
                                return '<span>Да</span>'
                            } else {
                                return '<span></span>'
                            }
                        }
                    },
                ],
                scheme: {
                    $init: function (obj) {
                        obj.time_Publication = obj.timePublication ? obj.timePublication.replace("T", " ") : "";
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
                        const item = $$('prescriptions_table').getItem(id);

                        showOrganizationPrescriptionCreateForm(item.id, item.accepted);
                    },
                },
                url: 'prescriptions'
            },
        ]
    }
}

function showOrganizationPrescriptionCreateForm(idPrescription, accepted) {
    webix.ui({
        id: 'content',
        rows: [
            organizationPrescriptionForm
        ]
    }, $$('content'))

    webix.ajax('prescription', {id: idPrescription}).then(function (data) {

        const prescription = data.json();

        $$('prescriptionId').setValue(prescription.id);
        $$('name').setValue(prescription.typeRequest.activityKind);

        if (prescription.prescriptionTexts && prescription.prescriptionTexts.length > 0) {
            prescription.prescriptionTexts.forEach((pt, index) => {
                const files = [];
                if (pt.prescriptionTextFiles && pt.prescriptionTextFiles.length > 0) {
                    pt.prescriptionTextFiles.forEach((file) => {
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
                            value: pt.id,
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
                            autoheight: true,
                            hidden: accepted
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
                            },
                            hidden: accepted
                        },
                    ]
                });
            })
        }

        if (accepted) {
            $$('label_sogl').hide();
            $$('buttons').hide();
        }

        webix.extend($$('organizationPrescriptionForm'), webix.ProgressBar);
    });
}

function allChecked() {
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

const organizationPrescriptionForm = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                view: 'form',
                id: 'organizationPrescriptionForm',
                minWidth: 200,
                complexData: true,
                elements: [
                    {
                        view: 'text',
                        id: 'prescriptionId',
                        name: 'prescriptionId',
                        hidden: true,
                    },
                    {
                        view: 'text',
                        id: 'name',
                        autoheight: true,
                        // align: 'center',
                        label: 'Вид деятельности',
                        labelPosition: 'top',
                        readonly: true
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
                        template: 'Информация мною прочитана и я согласен с ней',
                        autoheight: true
                    },
                    {
                        id: 'buttons',
                        cols: [
                            {},
                            {
                                view: 'button',
                                css: 'webix_primary',
                                value: 'Отменить',
                                minWidth: 150,
                                align: 'center',
                                click: function () {
                                    $$('menu').callEvent('onMenuItemClick', ['Prescript']);
                                }
                            },
                            {
                                id: 'send_btn',
                                view: 'button',
                                css: 'webix_primary',
                                minWidth: 150,
                                value: 'Ознакомлен',
                                disabled: true,
                                align: 'center',
                                click: function () {
                                    $$('send_btn').disable();

                                    if ($$('organizationPrescriptionForm').validate()) {

                                        let params = $$('organizationPrescriptionForm').getValues();

                                        const additionalAttributes = {};

                                        const countPrescriptions = $$('prescriptions').getChildViews().length;
                                        if (countPrescriptions > 0) {
                                            let consentPrescriptions = [];
                                            for (let num = 0; num < countPrescriptions; num++) {
                                                const id = $$('prescription_id' + num).getValue();
                                                consentPrescriptions.push({
                                                    id,
                                                    isAgree: $$('consentPrescription' + num).getValue()
                                                });
                                            }
                                            additionalAttributes.consentPrescriptions = consentPrescriptions;
                                        }

                                        params.additionalAttributes = additionalAttributes;

                                        $$('organizationPrescriptionForm').showProgress({
                                            type: 'icon',
                                            delay: 5000
                                        })

                                        webix.ajax()
                                            .headers({'Content-type': 'application/json'})
                                            .post('/cabinet/organization_prescription', JSON.stringify(params))
                                            .then(function (data) {
                                                const text = data.text();
                                                if (text.includes('Предписание сохранено')) {
                                                    webix.message('Предписание сохранено', 'success')
                                                } else {
                                                    webix.message('Не удалось сохранить предписание', 'error')
                                                }
                                                $$('organizationPrescriptionForm').hideProgress();
                                                $$('menu').callEvent('onMenuItemClick', ['Prescript']);
                                            })
                                    } else {
                                        webix.message('Не заполнены обязательные поля. Для просмотра прокрутите страницу вверх', 'error')
                                        $$('send_btn').enable();
                                    }
                                }
                            }
                        ]
                    },
                    {}
                ]
            }
        ]
    }
};
