const inspectionList = {
    view: 'scrollview',
    id: 'inspectionListFormId',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                view: 'datatable',
                id: "inspections_table",
                select: "row",
                navigation: true,
                fixedRowHeight:false,
                columns: [
                    {
                        header: "№",
                        id: 'index',
                        width: 70,
                        name: 'index',
                        readonly: true,
                    },
                    {
                        header: "Дата",
                        id: 'dateOfInspection',
                        minWidth: 150,
                        adjust: true,
                        fillspace: true,
                        readonly: true,
                        sort: 'date',
                    },
                    {
                        header: "Контролирующий орган",
                        template: '#controlAuthority.name#',
                        minWidth: 150,
                        fillspace: true,
                        adjust: true,
                        readonly: true,
                    },
                    {
                        header: "Результат проверки",
                        template: '#inspectionResult.name#',
                        fillspace: true,
                        // adjust: true,
                        minWidth: 150,
                        readonly: true,
                    },
                ],
                on: {
                    'data->onStoreUpdated': function(){
                        this.data.each(function(obj, i){
                            obj.index = i + 1;
                        });
                    },
                    onItemDblClick: function (id) {
                        item = this.getItem(id);
                        webix.ui(inspectionForm, $$('inspectionListFormId'));
                        item.controlAuthorityId = item.controlAuthority.id;
                        item.inspectionResultId = item.inspectionResult.id;
                        $$('inspectionForm').parse(item);
                    }
                },
                url: 'org_inspections'
            },
            {
                cols: [
                    {},
                    {
                        view: 'button',
                        align: 'right',
                        minWidth: 220,
                        maxWidth: 350,
                        css: 'webix_primary',
                        value: 'Добавить',
                        click: function () {
                            // webix.ui(inspectionForm, $$('inspectionListFormId'));
                            webix.ui({
                                id: 'content',
                                rows: [
                                    inspectionForm
                                ]
                            }, $$('content'));
                        }
                    }
                ]
            }
        ],
    }
}

const inspectionForm = {
    view: 'scrollview',
    scroll: 'y',
    id: 'inspectionFormId',
    autowidth: true,
    autoheight: true,
    body: {
        type: 'space',
        rows: [
            {
                view: 'form',
                id: 'inspectionForm',
                elements: [
                    {
                        view: 'datepicker',
                        label: 'Дата проведения контрольно-надзорного мероприятия',
                        labelPosition: 'top',
                        name: 'dateOfInspection',
                        timepicker: false,
                    },
                    {
                        view: 'richselect',
                        label: 'Контролирующий орган',
                        labelPosition: 'top',
                        name: 'controlAuthorityId',
                        required: true,
                        validate: webix.rules.isNotEmpty,
                        options: 'control_authorities_list_short',
                    },
                    {
                        view: 'richselect',
                        label: 'Результат контрольно-надзорного мероприятия',
                        labelPosition: 'top',
                        name: 'inspectionResultId',
                        required: true,
                        validate: webix.rules.isNotEmpty,
                        options: 'inspection_results_list_short',
                    },
                    {
                        view: 'textarea',
                        label: 'Комментарий',
                        labelPosition: 'top',
                        name: 'comment',
                        required: true,
                        minHeight: 200,
                    },
                ]
            },
            {
                cols: [
                    {},
                    {
                        view: 'button',
                        align: 'right',
                        minWidth: 200,
                        maxWidth: 300,
                        css: 'webix_primary',
                        value: 'Отменить',
                        click: () => {
                            webix.ui({
                                id: 'content',
                                rows: [
                                    inspectionList
                                ]
                            }, $$('content'));
                        }
                    },
                    {
                        view: 'button',
                        align: 'right',
                        minWidth: 200,
                        maxWidth: 300,
                        css: 'webix_primary',
                        value: 'Сохранить',
                        click: () => {
                            if($$("inspectionForm").validate()){
                                let params = $$('inspectionForm').getValues();
                                webix.ajax().headers({
                                    'Content-Type': 'application/json'
                                }).post('save_inspection',
                                    JSON.stringify(params)
                                ).then(function (data) {
                                    if (data.text() === 'Сохранено') {
                                        webix.message({text: data.text(), type: 'success'});
                                        webix.ui({
                                            id: 'content',
                                            rows: [
                                                inspectionList
                                            ]
                                        }, $$('content'));
                                    } else {
                                        webix.message({text: data.text(), type: 'error'});
                                    }
                                })
                            }
                        }
                    }
                ]
            }
        ]
    }
}