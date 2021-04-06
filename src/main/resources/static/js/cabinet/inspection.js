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
            let item = this.getItem(id);
            // let inspectionForm1 = inspectionForm(item.id));
            // webix.ui(inspectionForm1, $$('inspectionListFormId'));
            changeContentView(inspectionForm(item.id));
            item.controlAuthorityId = item.controlAuthority.id;
            item.inspectionResultId = item.inspectionResult.id;
            $$('inspectionForm').parse(item);
        }
    },
    url: 'org_inspections'
}

const inspectionList = {
    view: 'scrollview',
    id: 'inspectionListFormId',
    scroll: 'xy',
    body: {
        type: 'space',
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
                        }
                    }
                ]
            }
        ],
    }
}

function inspectionFormElements(inspectionId) {
    return {
        view: 'form',
        id: 'inspectionForm',
        elements: [
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
                label: 'Контролирующий орган',
                labelPosition: 'top',
                name: 'controlAuthorityId',
                required: true,
                options: 'control_authorities_list_short',
            },
            {
                view: 'richselect',
                label: 'Результат контрольно-надзорного мероприятия',
                labelPosition: 'top',
                name: 'inspectionResultId',
                required: true,
                options: 'inspection_results_list_short',
            },
            {
                view: 'textarea',
                label: 'Комментарий',
                labelPosition: 'top',
                name: 'comment',
                minHeight: 200,
            },
            file_upload_view('inspection', 'upload_inspection_file', 'inspection_files/' + inspectionId, 'delete_inspection_file'),
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
            value: 'Отменить',
            click: () => {
                changeContentView(inspectionList);
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
        view: 'scrollview',
        scroll: 'y',
        id: 'inspectionFormId',
        autowidth: true,
        autoheight: true,
        body: {
            type: 'space',
            rows: [
                inspectionFormElements(inspectionId),
                inspectionFormPanel
            ]
        }
    }
}

function saveInspection() {
    let params = $$('inspectionForm').getValues();
    webix.ajax().headers({
        'Content-Type': 'application/json'
    }).post('save_inspection',
        JSON.stringify(params)
    ).then(function (data) {
        let successfullyUploaded = saveFiles('inspection', data.json(), {idInspection: data.json().id});
        if (successfullyUploaded) {
            webix.message({text: 'Сохранено', type: 'success'});
            changeContentView(inspectionList);
        } else {
            webix.message({text: 'Не удалось сохранить', type: 'error'});
        }
    })
}

function saveInspectionFiles(data) {
    let savedInspection = data.json();
    let successfullyUploaded = false;
    if (data.id) {
        let uploader = $$('inspection_upload');
        if (uploader) {
            successfullyUploaded = true
            uploader.define('formData', {idInspection: savedInspection.id})
            uploader.send(function (response) {
                if (response) {
                    console.log(response.cause);
                    if (response.cause != 'Файл успешно загружен') {
                        successfullyUploaded = false
                    }
                }
            })
        }
    }
}
