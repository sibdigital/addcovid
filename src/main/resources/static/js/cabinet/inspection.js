webix.Date.startOnMonday = true;
webix.i18n.setLocale("ru-RU");

const inspectionTable = {
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
            readonly: true,
            format: dateFormatWOTime,
            sort: 'date',
        },
        {
            header: "Контролирующий орган",
            template: '#controlAuthority.name#',
            minWidth: 150,
            fillspace: true,
            adjust: true,
            readonly: true,
            sort: 'text',
        },
        {
            header: "Результат проверки",
            // template: '#inspectionResult.name#',
            template: function (obj) {
                if (obj.inspectionResult) {
                    return obj.inspectionResult.name;
                } else {
                    return "";
                }
            },
            adjust: true,
            fillspace: true,
            minWidth: 150,
            sort: 'text',
        },
    ],
    scheme: {
        $init: function (obj) {
            obj.dateOfInspection = obj.dateOfInspection.replace("T", " ");
            obj.dateOfInspection = xml_format(obj.dateOfInspection);
        },
        $update:function (obj) {
            obj.dateOfInspection = obj.dateOfInspection.replace("T", " ");
            obj.dateOfInspection = xml_format(obj.dateOfInspection);
        },

    },
    on: {
        'data->onStoreUpdated': function(){
            this.data.each(function(obj, i){
                obj.index = i + 1;
            });
        },
        onItemDblClick: function (id) {
            let item = this.getItem(id);

            changeContentView(inspectionForm(item.id));
            item.controlAuthorityId = item.controlAuthority.id;
            if (item.inspectionResult != null) {
                item.inspectionResultId = item.inspectionResult.id;
            }
            $$('inspectionForm').parse(item);
            showBtnBack(inspectionList, 'inspections_table');
        }
    },
    url: 'org_inspections'
}

const inspectionList = {
    rows: [
        inspectionTable,
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
                        changeContentView(inspectionForm(-1));
                        showBtnBack(inspectionList, 'inspections_table');
                    }
                }
            ]
        }
    ],
}

function inspectionFormElements(inspectionId) {
    var formData = {idInspection: inspectionId};
    return {
        view: 'form',
        id: 'inspectionForm',
        rows: [
            {
                cols: [
                    {
                        view: 'datepicker',
                        label: 'Дата проведения контрольно-надзорного мероприятия',
                        labelPosition: 'top',
                        name: 'dateOfInspection',
                        required: true,
                        timepicker: false,
                    },
                    {
                        view: 'richselect',
                        label: 'Результат контрольно-надзорного мероприятия',
                        labelPosition: 'top',
                        name: 'inspectionResultId',
                        // required: true,
                        options: 'inspection_results_list_short',
                    },
                ]
            },
            {
                view: 'richselect',
                label: 'Контролирующий орган',
                labelPosition: 'top',
                name: 'controlAuthorityId',
                required: true,
                options: {
                    view: 'suggest',
                    body: {
                        view: 'list',
                        css: 'multiline', // чтобы в моб версии было понятны, что за службы
                        type: {
                            autoheight: true,
                        },
                        url: 'control_authorities_list_short',
                    }
                },
            },
            {
                view: 'textarea',
                label: 'Комментарий',
                labelPosition: 'top',
                name: 'comment',
                minHeight: 200,
            },
            {
                id: 'formDataForUpload',
                hidden: true,
            },
            file_upload_view('inspectionForm','inspection', 'upload_inspection_file','inspection_files/' + inspectionId, formData, 'application/pdf, application/msword, application/vnd.openxmlformats-officedocument.wordprocessingml.document, image/jpeg'),
        ]
    }
}

const inspectionFormPanel = {
    cols: [
        {},
        {
            view: 'button',
            align: 'right',
            minWidth: 200,
            maxWidth: 300,
            css: 'webix_secondary',
            value: 'Отмена',
            click: () => {
                changeContentView(inspectionList);
                hideBtnBack();
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
                if ($$("inspectionForm").validate()) {
                    saveInspection();
                }
            }
        }
    ]
}

function inspectionForm(inspectionId) {
    return {
        view: 'form',
        id: 'inspectionId',
        rows: [
            inspectionFormElements(inspectionId),
            inspectionFormPanel
        ]
    }
}

function saveInspection() {
    let params = $$('inspectionForm').getValues();
    webix.ajax().headers({
        'Content-Type': 'application/json'
    }).post('save_inspection',
        JSON.stringify(params)
    ).then(function (data) {
        let savedInspection = data.json();
        if (savedInspection.id) {
            saveInspectionFiles(savedInspection);
        } else {
            return webix.message({text: 'Не удалось сохранить', type: 'error'});
        }
    })
}

function saveInspectionFiles(savedInspection) {
    let params = {
        'inspectionFileList': $$('inspection_docs_grid').serialize(),
        'idInspection': savedInspection.id
    };

    webix.ajax()
        .headers({'Content-type': 'application/json'})
        .post('save_inspection_files', JSON.stringify(params))
        .then(function (data) {
            if (data.json() == true) { //  if (data.json()). what if data.json() return not boolean
                webix.message({text: 'Сохранено', type: 'success'});
                changeContentView(inspectionList);
                hideBtnBack();
            } else  {
                webix.message({text: 'Не удалось сохранить', type: 'error'});
            }
        });
}

