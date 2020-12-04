const address = {
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
                        id: "address_fact_grid",
                        css: 'contacts',
                        scroll: true,
                        select: 1,
                        url: "address_facts",
                        xCount: 1,
                        type: {
                            height: "auto",
                            width: "auto",
                            template: "<div class='overall'><div>#full_address#</div></div>",
                        },
                        on: {
                            onItemDblClick: function (id) {
                                let item = this.getItem(id);
                                console.log(item);
                                console.log(this.data);
                            }
                        }
                    },
                    {
                        gravity: 0.4,
                        view: 'form',
                        id: 'contact_form',
                        complexData: true,
                        rules: {
                            "fiasRegionObjectGuid": webix.rules.isNotEmpty,
                            "fiasRaionObjectGuid": webix.rules.isNotEmpty,
                            "fullAddress": webix.rules.isNotEmpty
                        },
                        elements: [
                            { gravity: 0.5 },
                            {
                                view: 'combo',
                                id: 'regionTextFromDB',
                                name: 'fiasRegionObjectGuid',
                                label: 'Регион',
                                labelPosition: 'top',
                                invalidMessage: "Регион не может быть пустым",
                                options: {
                                    view: "suggest",
                                    body: {
                                        url: 'regions',
                                        template: '#name#',
                                    }
                                },
                            },
                            {
                                view: 'combo',
                                id: 'citiesTextFromDB',
                                name: 'fiasRaionObjectGuid',
                                label: 'Район / Населенный пункт',
                                labelPosition: 'top',
                                invalidMessage: "Район / Населенный пункт  не может быть пустым",
                                options: {
                                    view: "suggest",
                                    body: {
                                        url: 'cities',
                                        template: '#name#',
                                    }
                                },
                            },
                            {
                                view: 'text',
                                id: 'addressText',
                                name: 'fullAddress',
                                label: 'Адрес',
                                labelPosition: 'top',
                                invalidMessage: "Адрес не может быть пустым"
                            },
                            {
                                cols: [
                                    {
                                        view: 'button',
                                        id: 'add_contact',
                                        css: 'webix_primary',
                                        label: "<span class='mdi mdi-plus-circle' style='padding-right: 5px'></span><span class='text'>Добавить</span>",
                                        hotkey: 'enter',
                                        click: () => addAddress()
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
                                onChange: function() {
                                    this.validate();
                                }
                            }
                        }
                    }
                ]
            },
        ],
    }
};

function addAddress() {
    let form = $$('contact_form');
    let params = form.getValues();
    if (form.validate()) {
        console.log(params);
        webix.ajax()
            .headers({'Content-type': 'application/json'})
            .post('/save_address_fact', JSON.stringify(params))
            .then(function (data) {
                if (data !== null) {
                    if (params.id) {
                        webix.message('Контакт обновлен', 'success');
                    } else {
                        webix.message("Контакт добавлен", 'success');
                    }
                    $$('address_fact_grid').load('address_facts');
                } else {
                    webix.message("Не удалось добавить контакт", 'error');
                }
            });
        form.clear()
        form.clearValidation()
    }
};

function deleteAddress() {
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

