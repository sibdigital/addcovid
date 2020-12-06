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
                        id: "address_grid",
                        css: 'contacts',
                        scroll: true,
                        select: 1,
                        url: "address_fact",
                        xCount: 1,
                        type: {
                            template: "<div class='overall'>" +
                                "<div>#name#</div></div>",
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
                        complexData: true,
                        rules: {
                            "fiasRegionGuid": webix.rules.isNotEmpty,
                            "fiasRaionGuid": webix.rules.isNotEmpty,
                            "fiasObjectGuid": webix.rules.isNotEmpty
                        },
                        elements: [
                            { gravity: 0.5 },
                            {
                                view: "combo",
                                id: "regions",
                                name: "fiasRegionGuid",
                                label: 'Регион',
                                labelPosition: 'top',
                                suggest: {
                                    url: 'regions',

                                },
                                // on: {
                                //     onChange: () => {
                                //         const index = $$('regions').getValue();
                                //         console.log(index);
                                //         console.log($$('regions').getList().getItem(index));
                                //     }
                                // }
                            },
                            {
                                view: "combo",
                                id: "cities",
                                name: "fiasRaionGuid",
                                label: 'Город',
                                labelPosition: 'top',
                                suggest: {
                                    url: 'cities',
                                }
                            },
                            {
                                view: 'text',
                                id: 'fiasObjectGuid',
                                name: 'fiasObjectGuid',
                                label: 'Полный адрес',
                                labelPosition: 'top',
                                invalidMessage: "Полный адрес не может быть пустым"
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
                                onChange: function () {
                                    this.validate();
                                }
                            }
                        }
                    }
                ]
            },
        ],
    }
}

function addAddress() {
    const form = $$('contact_form');
    let params = form.getValues();
    console.log(params);
    const indexRegion = $$('regions').getValue();
    const region = $$('regions').getList().getItem(indexRegion);
    console.log(region);
    params.fiasRegionGuid = region.objectguid;

    const indexRaion = $$('cities').getValue();
    const raion = $$('cities').getList().getItem(indexRaion);
    console.log(raion);
    params.fiasRaionGuid = raion.objectguid;

    params.fullAddress = [region.value, raion.value, params.fiasObjectGuid].join(', ');
    console.log(params);

    if (form.validate()) {
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
                    $$('address_grid').load('address_fact');
                } else {
                    webix.message("Не удалось добавить контакт", 'error');
                }
            });
        form.clear()
        form.clearValidation()
    }
}

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