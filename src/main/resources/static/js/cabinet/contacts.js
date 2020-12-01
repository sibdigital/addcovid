const contacts = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                type: 'wide',
                cols: [
                    {
                        view: "dataview",
                        id: "contact_grid",
                        css: 'contacts',
                        height: userWindowHeight - 90,
                        scroll: false,
                        select: 1,
                        url: "org_contacts",
                        xCount: 3,
                        type: {
                            template: "<div class='overall'>" +
                                "<div class='contactPerson'>#contactPerson#</div>" +
                                "<div class='contactValue'>#contactValue#</div></div>",
                            height: 95,
                            width: "auto"
                        }
                    },
                    {
                        gravity: 0.4,
                        view: 'form',
                        id: 'contact_form',
                        complexData: true,
                        rules: {
                            "contactPerson": webix.rules.isNotEmpty,
                            "type": webix.rules.isNotEmpty,
                            "contactValue": webix.rules.isNotEmpty,
                        },
                        elements: [
                            {gravity: 0.5},
                            {
                                view: 'text',
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
                                on:{
                                    onChange:() => {
                                        if($$('type_combo').getValue() === 2){
                                            $$('contactValueText').config.label = "Почта";
                                            $$('contactValueText').config.placeholder = "sibdigital@mail.ru";
                                            $$('contactValueText').refresh();
                                        }
                                        else if($$('type_combo').getValue() === 1)
                                        {
                                            $$('contactValueText').config.label = "Номер телефона";
                                            $$('contactValueText').config.placeholder = "+7 (xxx) xxx-xx-xx";
                                            $$('contactValueText').refresh()
                                        }
                                    }
                                }
                            },
                            {
                                view: 'text',
                                id: 'contactValueText',
                                name: 'contactValue',
                                label: 'Номер телефона',
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
                                        value: 'Добавить',
                                        click: () => addContact()
                                    },
                                    {
                                        view: 'button',
                                        id: 'del_contact',
                                        css: 'webix_primary',
                                        value: 'Удалить',
                                        click: () => deleteContact()
                                    }
                                ]
                            },
                            {}
                        ]

                    }
                ]
            },


        ],
    }
}

function addContact() {
    let form = $$('contact_form')
    let params = form.getValues()
    if (params["type"] === 1) params["type"] = 0;
    else params["type"] = 1;
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
                $$('contact_form').clear()
                $$('contact_grid').load('org_contacts');
            } else {
                webix.message("Не удалось удалить контакт", 'error');
            }
        });
    $$('contact_form').clear()
}