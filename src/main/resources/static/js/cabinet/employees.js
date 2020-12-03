let updateFIOmodal = webix.ui({
    view: "window",
    id: "updateModal",
    minWidth: 200,
    width: 550,
    position: "center",
    modal: true,
    close: true,
    head: "Редактирование списка сотрудников",
    body: {
        view: 'form',
        id: 'modal_form_employee',
        complexData: true,
        rules: {
            "person.lastname": webix.rules.isNotEmpty,
            "person.firstname": webix.rules.isNotEmpty,
        },
        elements: [
            {
                view: 'text',
                name: 'person.lastname',
                label: 'Фамилия',
                labelPosition: 'top',
                invalidMessage: "Фамилия не может быть пустой"
            },
            {
                view: 'text',
                name: 'person.firstname',
                label: 'Имя',
                labelPosition: 'top',
                invalidMessage: "Имя не может быть пустым"
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
                css: 'webix_primary',
                value: 'Добавить',
                hotkey: "enter",

                click: function () {
                    var form = this.getParentView();
                    let params = $$('modal_form_employee').getValues()
                    if (form.validate()) {
                        webix.ajax()
                            .headers({'Content-type': 'application/json'})
                            .post('/employee', JSON.stringify(params))
                            .then(function (data) {
                                if (data !== null) {
                                    $$('modal_form_employee').clear()
                                    if (params.id) {
                                        webix.message('Сотрудник обновлен', 'success');
                                    } else {
                                        webix.message("Сотрудник добавлен", 'success');
                                    }
                                    $$('employees_table').load('employees');
                                } else {
                                    webix.message("Не удалось добавить сотрудника", 'error');
                                }
                            });
                        $$('modal_form_employee').clear()
                        updateFIOmodal.hide()
                    }
                }
            }
        ]

    }
});

let importEmployees = webix.ui({
    view: "window",
    id: "importModal",
    width: 550,
    position: "center",
    modal: true,
    close: true,
    head: "Импорт сотрудников",
    body:
        {
            view: 'form',
            position: 'center',
            elements: [
                {
                    id: 'upload',
                    view: 'uploader',
                    css: 'webix_secondary',
                    value: 'Загрузить Excel',
                    autosend: false,
                    upload: '/import-excel',
                    required: true,
                    accept: '.xlsx, .xls, .csv',
                    multiple: true,
                    link: 'filelist',
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
                },
                {
                    id: 'send_btn',
                    view: 'button',
                    css: 'webix_primary',
                    value: 'Импорт',
                    align: 'center',
                    click: function () {
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
                                webix.message('Не удалось загрузить файлы.', "error")
                                $$('upload').focus()
                            }
                            $$('employees_table').clearAll();
                            $$('employees_table').load('employees');
                            $$("upload").files.data.clearAll();
                            importEmployees.hide();
                            console.log(uploadedFiles)
                        })
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
                            maxWidth: 470,
                            placeholder: "Поиск по ФИО",
                            /*
                            on: {
                                onTimedKeyPress(){
                                    $$("employees_table").clearAll();
                                    let text = $$("dtFilter").getValue().replace(/\s/g, '').toLowerCase();
                                    if (!text)
                                        return $$("employees_table").load("employees");
                                    else{
                                        $$("employees_table").load(function(){
                                        return webix.ajax().
                                        headers({'Content-type':'application/json'}).
                                        post("/filter", text);
                                    });
                                    }
                                }
                            }
                            */
                        },
                        {
                            view: "button",
                            id: "findButton",
                            maxWidth: 150,
                            value: "Поиск",
                            css: 'webix_primary',
                            hotkey: "enter",
                            click: function (){
                                filterText();
                            }
                        }
                    ]
            },
            {
                id: 'tableIn',
                margin: 10,
                rows: [
                   {
                        align: 'center, middle',
                        body:
                            {
                                id: 'pagerIn',
                                rows: []
                            },
                    },
                    {
                        type: 'wide',
                        responsive: 'tableIn',
                        cols:
                            [
                                {
                                    view: 'datatable',
                                    id: "employees_table",
                                    height: userWindowHeight - 210,
                                    minWidth: 220,
                                    select: "row",
                                    navigation: true,
                                    resizeColumn: true,
                                    pager: 'Pager',
                                    datafetch: 25,
                                    columns: [
                                        {
                                            header: "Фамилия",
                                            template: "#person.lastname#",
                                            minWidth: 130, fillspace: true,
                                            sort: "text"
                                        },
                                        {
                                            header: "Имя",
                                            template: "#person.firstname#",
                                            adjust: true, minWidth: 250,
                                            sort: "text"
                                        },
                                        {
                                            header: "Отчество",
                                            template: "#person.patronymic#",
                                            minWidth: 160, fillspace: true,
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
                                        onItemClick: function (id) {
                                            let item = this.getItem(id);
                                            $$('form_employee').parse(item);
                                        },
                                        onItemDblClick: function (id) {
                                            let item = this.getItem(id);
                                            $$('modal_form_employee').parse(item);
                                            updateFIOmodal.show();

                                        }
                                    },
                                    url: 'employees'
                                },
                                {
                                    view: 'form',
                                    id: 'form_employee',
                                    minWidth: 220,
                                    width: 300,
                                    complexData: true,
                                    rules: {
                                        "person.lastname": webix.rules.isNotEmpty,
                                        "person.firstname": webix.rules.isNotEmpty,
                                    },
                                    elements: [
                                        {
                                            view: 'text',
                                            name: 'person.lastname',
                                            label: 'Фамилия',
                                            labelPosition: 'top',
                                            invalidMessage: "Фамилия не может быть пустой"
                                        },
                                        {

                                            view: 'text',
                                            name: 'person.firstname',
                                            label: 'Имя',
                                            labelPosition: 'top',
                                            invalidMessage: "Имя не может быть пустым"
                                        },
                                        {
                                            view: 'text',
                                            name: 'person.patronymic',
                                            label: 'Отчество',
                                            labelPosition: 'top'
                                        },
                                        /*
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
                                        },
                                        */
                                        {
                                            view: 'button',
                                            align: 'right',
                                            css: 'webix_primary',
                                            value: 'Добавить',
                                            click: function () {
                                                var form = this.getParentView();
                                                let params = $$('form_employee').getValues()
                                                if (form.validate()) {
                                                    webix.ajax()
                                                        .headers({'Content-type': 'application/json'})
                                                        .post('/employee', JSON.stringify(params))
                                                        .then(function (data) {
                                                            if (data !== null) {
                                                                $$('form_employee').clear()
                                                                if (params.id) {
                                                                    webix.message('Сотрудник обновлен', 'success');
                                                                } else {
                                                                    webix.message("Сотрудник добавлен", 'success');
                                                                }
                                                                $$('employees_table').load('employees');
                                                            } else {
                                                                webix.message("Не удалось добавить сотрудника", 'error');
                                                            }
                                                        });
                                                }
                                            }
                                        },
                                    ]
                                }
                            ]
                    },

                ]
            },
            {
                cols:
                    [
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
                            responsive: 'btnsIn',
                            cols:[
                                {
                                    view: "button",
                                    align: 'left',
                                    minWidth: 150,
                                    maxWidth: 350,
                                    css: 'webix_primary',
                                    value: 'Добавить',
                                    click: function () {
                                        $$('modal_form_employee').clear()
                                        updateFIOmodal.show();
                                    }
                                },
                                {
                                    view: 'button',
                                    align: 'right',
                                    minWidth: 150,
                                    maxWidth: 350,
                                    css: 'webix_primary',
                                    value: 'Удалить',
                                    hotkey: "delete",
                                    click: function () {

                                        let params = $$('employees_table').getSelectedItem()

                                        if (!$$("employees_table").getSelectedId()) {
                                            webix.message("Не выбрана строка!", "error");
                                            return;
                                        }
                                        $$("employees_table").remove($$("employees_table").getSelectedId());
                                        webix.ajax()
                                            .headers({'Content-type': 'application/json'})
                                            .post('/deleteEmployee', JSON.stringify(params))
                                            .then(function (data) {
                                                if (data !== null) {
                                                    webix.message("Сотрудник удалён", 'success');
                                                    $$('form_employee').clear()
                                                    $$('modal_form_employee').clear()
                                                    $$('employees_table').load('employees');
                                                } else {
                                                    webix.message("Не удалось удалить сотрудника", 'error');
                                                }
                                            });
                                        $$('form_employee').clear()
                                        $$('modal_form_employee').clear()
                                        updateFIOmodal.hide()
                                    }
                                },
                                {
                                    view: "button",
                                    id: "uploadButton",
                                    minWidth: 150,
                                    maxWidth: 350,
                                    value: "Загрузить",
                                    css: 'webix_primary',
                                    click: function () {
                                        importEmployees.show()
                                    }
                                },
                            ]
                        }
                    ]
            },
            {
                id: 'btnsIn',
                rows:[]
            }
        ]
    }
}

//Для поиска по кнопке
function filterText() {
    $$("employees_table").clearAll();
    let text = $$("dtFilter").getValue().replace(/\s/g, '').toLowerCase();
    if (!text)
        return $$("employees_table").load("employees");
    else {
        $$("employees_table").load(function () {
            return webix.ajax().headers({'Content-type': 'application/json'}).post("/filter", text);
        });
    }
    $$("dtFilter").setValue("")
}

function adaptiveEmployees(){
    let tableWidth = $$('employees_table').config.width;
    $$('form_employee').define({
        width: tableWidth,
    });$$('form_employee').resize();

    updateFIOmodal.config.width = 320; updateFIOmodal.resize();
    importEmployees.config.width = 320; importEmployees.resize();

    $$('findButton').hide();
    $$('dtFilter').attachEvent("onTimedKeyPress", function (id) {
        filterText()
    });

    $$("btnsIn").addView($$('uploadButton'), 0);
    $$('Pager').config.group = 2;
    $$("pagerIn").addView($$('Pager'),0)

}
