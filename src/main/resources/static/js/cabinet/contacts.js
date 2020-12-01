const contacts = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                view: "dataview",
                id: "contact_grid",
                css: "movies",
                template: "<div class='webix_strong'>#contactPerson#</div> <span class='webix_light'>#contactValue#</span>",
                url: "org_contacts",
                xCount: 3,
                type: {
                    height: 60,
                    width: "auto"
                }
            },
            {
                cols: [
                    {
                        view: 'button',
                        id: 'add_contact',
                        css: 'webix_primary',
                        minWidth: 150,
                        maxWidth: 350,
                        value: 'Добавить',
                        click: () => {
                            $$('contact_form').clear()
                            contactWindow.show()
                        }
                    },
                    {
                        view: 'button',
                        id: 'del_contact',
                        css: 'webix_primary',
                        minWidth: 150,
                        maxWidth: 350,
                        value: 'Удалить',
                        click: () => {

                        }
                    }
                ]
            }

        ],
    }
}

let contactWindow = webix.ui({
    view: "window",
    id: "updateModal",
    minWidth: 200,
    width: 550,
    position: "center",
    modal: true,
    close: true,
    head: "Редактирование контактов",
    body: {
        view: 'form',
        id: 'contact_form',
        complexData: true,
        rules: {
            "contactPerson": webix.rules.isNotEmpty,
            "contactValue": webix.rules.isNotEmpty,
        },
        elements: [
            {
                view: 'text',
                name: 'contactPerson',
                label: 'contactPerson',
                labelPosition: 'top',
                invalidMessage: "contactPerson не может быть пустой"
            },
            {
                view: 'text',
                name: 'type',
                label: 'type',
                labelPosition: 'top',
                invalidMessage: "Type не может быть пустым"
            },
            {
                view: 'text',
                name: 'contactValue',
                label: 'contactValue',
                labelPosition: 'top',
                invalidMessage: "contactValue не может быть пустым"
            },
            {
                view: 'button',
                align: 'right',
                css: 'webix_primary',
                value: 'Добавить',
                hotkey: "enter",
                click: function () {
                    let form = this.getParentView();
                    let params = $$('contact_form').getValues()
                    console.log(params)
                    if (form.validate()) {
                        webix.ajax()
                            .headers({'Content-type': 'application/json'})
                            .post('/save_contact', JSON.stringify(params))
                            .then(function (data) {
                                if (data !== null) {
                                    $$('contact_form').clear()
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
                        $$('contact_form').clear()
                        contactWindow.hide()
                    }
                }
            }
        ]

    }
});