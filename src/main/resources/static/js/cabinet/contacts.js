const contacts = {
    // view: 'scrollview',
    // scroll: 'xy',
    // body: {
    //     type: 'space',
        id: 'contactsMainLayout',
        rows: [
            {
                // type: 'wide',
                responsive: 'contactsMainLayout',
                cols: [
                    {
                        view: "dataview",
                        id: "contact_grid",
                        minWidth: 320,
                        css: 'contacts',
                        select: 1,
                        url: "org_contacts",
                        xCount: 3,
                        type: {
                            template: "<div class='overall'>" +
                                "<div class='contactPerson'>#contactPerson#</div>" +
                                "<div class='contactValue'>#contactValue#</div></div>",
                            height: "auto",
                            width: "auto"
                        },
                        on: {
                            onItemDblClick: function (id) {
                                let item = this.getItem(id);
                                $$('contact_form').parse(item);
                                let contactValue = $$('contactValueText').getValue();
                                if (webix.rules.isEmail(contactValue)) {
                                    $$('type_combo').setValue("2")
                                    changeComboConfig(2)
                                } else {
                                    $$('type_combo').setValue("1")
                                    changeComboConfig(1)
                                }
                            }
                        }
                    },
                    {
                        gravity: 0.4,
                        view: 'form',
                        id: 'contact_form',
                        minWidth: 230,
                        complexData: true,
                        rules: {
                            "contactPerson": webix.rules.isNotEmpty,
                            "type": webix.rules.isNotEmpty
                        },
                        elements: [
                            {gravity: 0.5},
                            {
                                view: 'text',
                                id: 'contactPersonText',
                                name: 'contactPerson',
                                label: 'ФИО',
                                labelPosition: 'top',
                                invalidMessage: "ФИО не может быть пустым"
                            },
                            {
                                view: "combo",
                                id: "type_combo",
                                name: "type",
                                label: 'Вид',
                                labelPosition: 'top',
                                value: "1",
                                options: [
                                    {id: 1, value: "Номер телефона"},
                                    {id: 2, value: "Почтовый адрес"}
                                ],
                                on: {
                                    onChange: () => {
                                        let currentComboValue = $$('type_combo').getValue();
                                        changeComboConfig(currentComboValue)
                                    }
                                }
                            },
                            {
                                view: 'text',
                                id: 'contactValueText',
                                name: 'contactValue',
                                label: 'Номер телефона',
                                validate: webix.rules.isNotEmpty,
                                placeholder: '+7 (xxx) xxx-xx-xx',
                                labelPosition: 'top',
                                invalidMessage: "Контакт не может быть пустым"
                            },
                            {
                                cols: [
                                    {
                                        view: 'button',
                                        id: 'add_contact',
                                        css: 'webix_primary',
                                        label: "<span class='mdi mdi-plus-circle' style='padding-right: 5px'></span><span class='text'>Добавить</span>",
                                        hotkey: 'enter',
                                        click: () => addContact()
                                    },
                                    {
                                        view: 'button',
                                        id: 'del_contact',
                                        css: 'webix_primary',
                                        label: "<span class='mdi mdi-minus-circle' style='padding-right: 5px'></span><span class='text'>Удалить</span>",
                                        hotkey: 'delete',
                                        click: () => deleteContact()
                                    }
                                ]
                            },
                            {}
                        ],
                        elementsConfig: {
                            on: {
                                "onChange": function () {
                                    this.validate();
                                }
                            }
                        }
                    }
                ]
            },
        ],
    // }
}

function addContact() {
    let form = $$('contact_form')
    let params = form.getValues()
    if (params["type"] == 2) params["type"] = 0;
    if (form.validate()) {
        webix.ajax()
            .headers({'Content-type': 'application/json'})
            .post('/save_contact', JSON.stringify(params))
            .then(function (data) {
                if (data !== null) {
                    if (params.id) {
                        webix.message('Контакт обновлен', 'success');
                    } else {
                        webix.message("Контакт добавлен", 'success');
                    }
                    $$('contact_grid').load('org_contacts');
                } else {
                    webix.message("Не удалось добавить контакт", 'error');
                }
            });
        form.clear()
        form.clearValidation()
        if (params["type"] == 0) {
            $$('type_combo').setValue('2')
        } else {
            $$('type_combo').setValue('1')
        }
    }
}

function deleteContact() {
    let params = $$('contact_grid').getSelectedItem()
    webix.ajax()
        .headers({'Content-type': 'application/json'})
        .post('/delete_org_contact', JSON.stringify(params))
        .then(function (data) {
            if (data !== null) {
                $$("contact_grid").remove($$("contact_grid").getSelectedId());
                webix.message("Контакт удалён", 'success');
                $$('contact_grid').load('org_contacts');
            } else {
                webix.message("Не удалось удалить контакт", 'error');
            }
        });
}

function changeComboConfig(val) {
    if (val === 2) {
        $$('contactValueText').config.label = "Почта";
        $$('contactValueText').config.placeholder = "sibdigital@mail.ru";
        $$('contactValueText').config.validate = webix.rules.isEmail;
        $$('contactValueText').config.invalidMessage = "Неверный формат почты"
        $$('contactValueText').refresh();
    } else if (val === 1) {
        $$('contactValueText').config.label = "Номер телефона";
        $$('contactValueText').config.placeholder = "+7 (xxx) xxx-xx-xx";
        $$('contactValueText').config.validate = webix.rules.isNotEmpty;
        $$('contactValueText').config.invalidMessage = "Контакт не может быть пустым"
        $$('contactValueText').refresh()
    }
}