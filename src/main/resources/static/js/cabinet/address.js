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
                                console.log($$('regions').getList());
                                const region = $$('regions').getList().find((reg) => reg.objectguid === item.fias_region_objectguid);
                                console.log(region);
                                $$('regions').setValue(region[0]);

                                console.log($$('cities').getList());
                                const raion = $$('cities').getList().find((city) => city.objectid === item.fias_raion_objectguid);
                                console.log(raion);
                                $$('cities').setValue(raion[0]);

                                $$('fiasObjectGuid').setValue(item.fias_objectguid);
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
                            "fiasObjectGuid": webix.rules.isNotEmpty,
                            "fiasHouseId": webix.rules.isNotEmpty
                        },
                        elements: [
                            { gravity: 0.5 },
                            {
                                view: "combo",
                                id: "regions",
                                name: "fiasRegionGuid",
                                label: 'Регион',
                                labelPosition: 'top',
                                invalidMessage: 'Регион не может быть пустым',
                                options: {
                                    keyPressTimeout: 250,
                                    body: {
                                        dynamic: true,
                                        datafetch: 20,
                                        url: 'regions',
                                        template: (item) => {
                                            return item.typename.toLowerCase() === 'респ' ? item.typename + '. ' + item.value : item.value + ' ' + item.typename;
                                        },
                                        ready: function () {
                                        }
                                    }
                                },
                                on : {
                                    onChange: (newval, oldval) => {
                                        try {
                                            const indexRegion = $$('regions').getValue();
                                            console.log(indexRegion);

                                            const region = $$('regions').getList().getItem(indexRegion);
                                            console.log(region);

                                            if (region) {
                                                const url = 'cities?objectid=' + region.objectguid;
                                                $$('cities').setValue('');
                                                $$('cities').getList().clearAll();
                                                $$('cities').getList().load(url);
                                            }
                                        } catch (e) {
                                            console.log(e);
                                        }
                                    }
                                },
                            },
                            {
                                view: "combo",
                                id: "cities",
                                name: "fiasRaionGuid",
                                label: 'Район / Населенный пункт',
                                labelPosition: 'top',
                                invalidMessage: 'Район / Населенный пункт  не может быть пустым',
                                options: {
                                    keyPressTimeout: 250,
                                    body: {
                                        // dataFeed: function(str) {
                                        //     if (!str.match(/\w/g))
                                        //         return;
                                        //     return webix.ajax().bind(this).get("cities?filter=" + str, function(data) {
                                        //         this.parse(data);
                                        //     });
                                        // },
                                        dynamic: true,
                                        datafetch: 20,
                                        //url: 'cities',
                                        template: (item) => {
                                            return item.typename.toLowerCase() === 'г' ? item.typename + '. ' + item.value : item.value + ' ' + item.typename;
                                        },
                                        ready: function () {
                                        }
                                    }
                                },
                                on: {
                                    onChange: (newval, oldval) => {
                                        try {
                                            const indexRaion = $$('cities').getValue();
                                            console.log(indexRaion);

                                            const raion = $$('cities').getList().getItem(indexRaion);
                                            console.log(raion);

                                            if (raion) {
                                                const url = 'streets?objectid=' + raion.objectid;
                                                $$('fiasObjectGuid').setValue('');
                                                console.log($$('fiasObjectGuid').getFormView());
                                                const suggest_id = $$('fiasObjectGuid').config.suggest;
                                                const list = $$(suggest_id).getList();
                                                list.clearAll();
                                                webix.ajax()
                                                    .headers({'Content-type': 'application/json'})
                                                    .get(url)
                                                    .then(function (data) {
                                                        console.log(data);
                                                        if (data !== null) {
                                                            list.parse(data);
                                                        }
                                                    });
                                            }
                                        } catch(e) {
                                            console.log(e);
                                        }
                                    }
                                }
                            },
                            // {
                            //     view: 'text',
                            //     id: 'fiasObjectGuid',
                            //     name: 'fiasObjectGuid',
                            //     label: 'Адрес',
                            //     labelPosition: 'top',
                            //     invalidMessage: "Адрес не может быть пустым",
                            //     suggest: {
                            //         keyPressTimeout: 500,
                            //         body: {
                            //             dynamic: true,
                            //             datafetch: 15
                            //         }
                            //     }
                            // },
                            {
                                view: 'text',
                                id: 'fiasObjectGuid',
                                name: 'fiasObjectGuid',
                                label: 'Адрес',
                                labelPosition: 'top',
                                invalidMessage: "Адрес не может быть пустым",
                                suggest: {
                                    keyPressTimeout: 500,
                                    body: {
                                        dynamic: true,
                                        datafetch: 15,
                                        template: (item) => {
                                            return item.typename + '. ' + item.value;
                                        },
                                    }
                                }
                            },
                            {
                                view: 'text',
                                id: 'house',
                                name: 'fiasHouseId',
                                label: 'Дом',
                                labelPosition: 'top',
                                invalidMessage: "Дом не может быть пустым",
                                suggest: {
                                    keyPressTimeout: 500,
                                    body: {
                                        dynamic: true,
                                        datafetch: 15
                                    }
                                }
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
                                        click: () => deleteAddress()
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
    const form = $$('contact_form');
    const params = form.getValues();
    console.log(params);

    if (form.validate()) {
        const indexRegion = $$('regions').getValue();
        const region = $$('regions').getList().getItem(indexRegion);
        console.log(region);
        const regionLabel = region.typename.toLowerCase() === 'респ' ? region.typename + '. ' + region.value : region.value + ' ' + region.typename;

        params.fiasRegionGuid = region.objectguid;

        const indexRaion = $$('cities').getValue();
        const raion = $$('cities').getList().getItem(indexRaion);
        console.log(raion);
        const raionLabel = raion.typename + '. ' + raion.value;

        params.fiasRaionGuid = raion.objectguid;

        const suggest_id = $$('fiasObjectGuid').config.suggest;
        const inpStreet = $$('fiasObjectGuid').getValue();
        // const streetId = $$(suggest_id).getItemId(inpStreet);
        // console.log(streetId);
        // const street = $$(suggest_id).getList().getItem(streetId) || inpStreet;
        // console.log(street);
        // const streetLabel = street !== inpStreet ? street.typename + '. ' + street.value : inpStreet;

        params.fiasObjectGuid = street.objectid ?? inpStreet;

        const house = $$('house').getValue();
        console.log(house);

        params.fullAddress = [regionLabel, raionLabel, inpStreet, house].join(', ');
        console.log(params);

        webix.ajax()
            .headers({'Content-type': 'application/json'})
            .post('/save_address_fact', JSON.stringify(params))
            .then(function (data) {
                if (data !== null) {
                    if (params.id) {
                        webix.message('Адрес обновлен', 'success');
                    } else {
                        webix.message("Адрес добавлен", 'success');
                    }
                    $$('address_fact_grid').load('address_facts');
                } else {
                    webix.message("Не удалось добавить адрес", 'error');
                }
            });
        form.clear()
        form.clearValidation()
    }
};

function deleteAddress() {
    let params = $$('address_fact_grid').getSelectedItem();
    webix.ajax()
        .headers({'Content-type': 'application/json'})
        .post('/delete_address_fact', JSON.stringify(params))
        .then(function (data) {
            if (data !== null) {
                $$("address_fact_grid").remove($$("address_fact_grid").getSelectedId());
                webix.message("Адрес удалён", 'success');
                $$('address_fact_grid').load('address_facts');
            } else {
                webix.message("Не удалось удалить адрес", 'error');
            }
        });
}

