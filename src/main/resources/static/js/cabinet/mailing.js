const mailing = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                view: "segmented", type: "bottom", multiview: true, options: [
                    {value: "Мои рассылки", id: 'myMailing'},
                    {value: "Доступные рассылки", id: 'availableMailing'},],
                on: {
                    onAfterRender() {
                        this.callEvent('onChange', ['myMailing']);
                    },
                    onChange:
                        function (id) {
                            switch (id) {
                                case 'myMailing':
                                    getMyMailingList($$('my_mailing_table'));
                                    break;
                                case 'availableMailing':
                                    getAllAvailableMailing();
                                    break;
                            }
                        }}
            },
            {
                id: 'views',
                cells: [
                    {
                        view: 'form', id: 'myMailing',
                        rows: [
                            {
                                view: 'datatable', id: 'my_mailing_table',
                                columns: [
                                    {
                                        id: 'checkbox',
                                        name: 'checkbox',
                                        header: '',
                                        template: '{common.checkbox()}',
                                        editor: 'checkbox',
                                        value: true
                                    },
                                    {id: 'name', header: 'Рассылка', adjust: true, fillspace: true},
                                    {id: 'deactivationDate', header: 'Время деактивации', hidden: true},
                                ],
                                on: {
                                    onCheck: function (row, column, state) {
                                        if (column == 'checkbox' && state == 0) {
                                            this.updateItem(row, {deactivationDate: new Date()});
                                        } else if (column == 'checkbox' && state == 1) {
                                            this.updateItem(row, {deactivationDate: null});
                                        }
                                    },
                                    onAfterLoad: function () {
                                        this.eachRow(function (row) {
                                            var record = this.getItem(row);
                                            if (record.id == 1) {
                                                this.addCellCss(row, 'checkbox',
                                                    webix.html.createCss({
                                                        "pointer-events": "none",
                                                        "opacity": "0.5"
                                                    }));
                                            }
                                        })
                                    },
                                    onItemDblClick: function (id) {
                                        let data = this.getItem(id);
                                        $$('mailingForm').setValues({
                                            index: data.id,
                                            name: data.name,
                                            description: data.description
                                        });
                                        $$('mailingForm').show();
                                    }
                                },
                            },
                            {
                                cols: [
                                    {
                                        view: 'button',
                                        align: 'right',
                                        maxWidth: 200,
                                        css: 'webix_primary',
                                        value: 'Сохранить',
                                        click: function () {
                                            let selectedMailing = $$('my_mailing_table').find(
                                                function (obj) {
                                                    return obj.checkbox == true;
                                                });
                                            saveMailing(selectedMailing);

                                            let deactivatingMailing = $$('my_mailing_table').find(
                                                function (obj) {
                                                    return obj.deactivationDate != null;
                                                });
                                            deactivateMailing(deactivatingMailing);
                                        }
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        view: 'form', id: 'availableMailing',
                        rows: [
                            {
                                view: 'datatable', id: 'available_mailing_table',
                                ready: function () {
                                    this.eachRow(function (row) {
                                        var record = this.getItem(row);
                                        if (record.id == 1) {
                                            this.addCellCss(row, 'checkbox',
                                                webix.html.createCss({
                                                    "pointer-events": "none",
                                                    "opacity": "0.5"
                                                }));
                                        }
                                    })
                                },
                                columns: [
                                    {id: 'checkbox', header: '', template: '{common.checkbox()}', editor: 'checkbox'},
                                    {id: 'name', header: 'Рассылка', adjust: true, fillspace: true},
                                    {id: 'deactivationDate', header: 'Время деактивации', hidden: true},
                                ],
                                on: {
                                    onCheck: function (row, column, state) {
                                        if (column == 'checkbox' && state == 0) {
                                            this.updateItem(row, {deactivationDate: new Date()});
                                        } else if (column == 'checkbox' && state == 1) {
                                            this.updateItem(row, {deactivationDate: null});
                                        }
                                    },
                                    onAfterLoad: function () {
                                        this.eachRow(function (row) {
                                            var record = this.getItem(row);
                                            if (record.id == 1) {
                                                this.addCellCss(row, 'checkbox',
                                                    webix.html.createCss({
                                                        "pointer-events": "none",
                                                        "opacity": "0.5"
                                                    }));
                                            }
                                        })
                                    },
                                    onItemDblClick: function (id) {
                                        let data = this.getItem(id);
                                        $$('mailingForm').setValues({
                                            index: data.id,
                                            name: data.name,
                                            description: data.description
                                        });
                                        $$('mailingForm').show();
                                    }
                                },
                            },
                            {
                                cols: [
                                    {
                                        view: 'button',
                                        css: 'webix_primary',
                                        align: 'right',
                                        maxWidth: 200,
                                        value: 'Сохранить',
                                        click: function () {
                                            let selectedMailing = $$('available_mailing_table').find(
                                                function (obj) {
                                                    return obj.checkbox == true;
                                                });
                                            saveMailing(selectedMailing);

                                            let deactivatingMailing = $$('available_mailing_table').find(
                                                function (obj) {
                                                    return obj.deactivationDate != null;
                                                });
                                            deactivateMailing(deactivatingMailing);
                                        }
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        view: 'form', id: 'mailingForm',
                        elements:[
                            {id: 'index', name: 'index',  view: 'text', label: 'Код', labelPosition: 'top', readonly: true,},
                            {id: 'name', name: 'name', view: 'text', label: 'Наименование', labelPosition: 'top', readonly: true,},
                            {id: 'description', name: 'description', view: 'textarea', label: 'Описание', labelPosition: 'top', readonly: true},
                            {cols: [ {id: 'btnBack', view: 'button',  align: 'right', maxWidth: 200,
                                    css: 'webix_primary', value: 'Назад', click: function () {
                                        $$('views').back();
                                    }
                                }]}],
                    }
                ]
            },

        ]
    }
}

function getMyMailingList(dtable){
    dtable.clearAll();
    var data = webix.ajax("/my_mailing_list").then(function (data) {
        var js = data.json();
        for (el in js) {
            js[el].checkbox = true;
        }
        return js;
    });
    dtable.parse(data);
}

function getAllAvailableMailing() {
    dtable = $$("available_mailing_table");
    getMyMailingList(dtable);

    var data2 = webix.ajax("/available_not_mine_mailing_list");
    $$("available_mailing_table").parse(data2);
}

function saveMailing(selectedMailing){
    webix.ajax()
        .headers({'Content-type': 'application/json'})
        .post('/saveMailing', JSON.stringify(selectedMailing))
        .then(function (data) {
            if (data !== null) {
                webix.message('Рассылки сохранены', 'success');
            } else {
                webix.message("Не удалось сохранить рассылки", 'error');
            }
        });
}

function deactivateMailing(selectedMailing){
    webix.ajax()
        .headers({'Content-type': 'application/json'})
        .post('/deactivateMailing', JSON.stringify(selectedMailing))
        .then(function (data) {
            if (data.text() != 'Рассылки деактивированы') {
                webix.message({text: data.text(), type: 'error'});
            }
        });
}
