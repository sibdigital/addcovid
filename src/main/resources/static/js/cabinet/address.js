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
                                const region = $$('regions').getList().find((reg) => reg.objectid === item.fias_region_objectid);
                                console.log(region);
                                $$('regions').setValue(region[0]);

                                console.log($$('raions').getList());
                                const raion = $$('raions').getList().find((raion) => raion.objectid === item.fias_raion_objectid);
                                console.log(raion);
                                if (raion.length > 0) {
                                    $$('raions').setValue(raion[0]);
                                }

                                console.log($$('cities').getList());
                                const city = $$('cities').getList().find((city) => city.objectid === item.fias_city_objectid);
                                console.log(city);
                                if (city.length > 0) {
                                    $$('cities').setValue(city[0]);
                                }

                                $$('fiasObjectGuid').setValue('');
                                const url = 'streets?objectid=' + item.fias_street_objectid;
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
                                            // const streetId = $$(suggest_id).getItemId(streetLabel.slice(streetLabel.lastIndexOf(' ') + 1));
                                            // console.log(streetId);
                                            // const street = $$(suggest_id).getList().getItem(streetId).objectid || streetLabel;
                                        }
                                    });
                                $$('house').setValue(item.house_hand);
                                $$('office').setValue(item.apartment_hand);
                            }
                        }
                    },
                    {
                        gravity: 0.4,
                        view: 'form',
                        id: 'contact_form',
                        complexData: true,
                        rules: {
                            "fiasRegionObjectId": webix.rules.isNotEmpty,
                            //"fiasRaionGuid": webix.rules.isNotEmpty,
                            "fiasStreetObjectId": webix.rules.isNotEmpty,
                            "fiasCityObjectId": webix.rules.isNotEmpty,
                            "house_hand": webix.rules.isNumber,
                            "apartment_hand": webix.rules.isNotEmpty
                        },
                        elements: [
                            { gravity: 0.5 },
                            {
                                view: "combo",
                                id: "regions",
                                name: "fiasRegionObjectId",
                                label: 'Регион',
                                labelPosition: 'top',
                                invalidMessage: 'Регион не может быть пустым',
                                options: {
                                    keyPressTimeout: 250,
                                    filter: (obj, filter) => {
                                        return obj.typename.toLowerCase().indexOf(filter.toLowerCase()) !== -1 || obj.value.toLowerCase().indexOf(filter.toLowerCase()) !== -1;
                                    },
                                    body: {
                                        dynamic: true,
                                        datafetch: 20,
                                        url: 'regions',
                                        template: '#typename#'.toLowerCase() === 'респ' ? '#typename#. #value#' : '#value# #typename#',

                                        // template: (item) => {
                                        //     //return item.value
                                        //     // const filter = this.getFilterFunction();
                                        //     // console.log(filter);
                                        //     //data.filter(obj => console.log(obj));
                                        //     return item.typename.toLowerCase() === 'респ' ? '#typename#. #value#' : '#value# #typename#';
                                        // },
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
                                                let url = 'raions?objectid=' + region.objectid;
                                                $$('raions').setValue('');
                                                $$('raions').getList().clearAll();
                                                $$('raions').getList().load(url);

                                                url = 'cities?objectid=' + region.objectid;
                                                $$('cities').setValue('');
                                                $$('cities').getList().clearAll();
                                                $$('cities').getList().load(url);

                                                const suggest_id = $$('fiasObjectGuid').config.suggest;
                                                $$(suggest_id).getList().clearAll();
                                            }
                                        } catch (e) {
                                            console.log(e);
                                        }
                                    }
                                },
                            },
                            {
                                view: "combo",
                                id: "raions",
                                //id: "cities",
                                name: "fiasRaionObjectId",
                                label: 'Район',
                                labelPosition: 'top',
                                invalidMessage: 'Район не может быть пустым',
                                options: {
                                    keyPressTimeout: 250,
                                    filter: (obj, filter) => {
                                        return obj.typename.toLowerCase().indexOf(filter.toLowerCase()) !== -1 || obj.value.toLowerCase().indexOf(filter.toLowerCase()) !== -1;
                                    },
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
                                        template: '#value# #typename#',
                                        //url: 'cities',
                                        // template: (item) => {
                                        //     return item.value;
                                        //     //return item.typename.toLowerCase() === 'г' ? item.typename + '. ' + item.value : item.value + ' ' + item.typename;
                                        // },
                                        ready: function () {
                                        }
                                    }
                                },
                                on: {
                                    onChange: (newval, oldval) => {
                                        try {
                                            const indexRaion = $$('raions').getValue();
                                            console.log(indexRaion);

                                            const raion = $$('raions').getList().getItem(indexRaion);
                                            console.log(raion);

                                            if (raion.value === '<Не выбрано>') {
                                                const indexRegion = $$('regions').getValue();
                                                const region = $$('regions').getList().getItem(indexRegion);
                                                raion.objectid = region.objectid;
                                            }

                                            if (raion) {
                                                const url = 'cities?objectid=' + raion.objectid;
                                                $$('cities').setValue('');
                                                $$('cities').getList().clearAll();
                                                $$('cities').getList().load(url);

                                                const suggest_id = $$('fiasObjectGuid').config.suggest;
                                                $$(suggest_id).getList().clearAll();
                                            }
                                        } catch(e) {
                                            err.log(e);
                                        }
                                    }
                                }
                            },
                            {
                                view: "combo",
                                id: "cities",
                                name: "fiasCityObjectId",
                                label: 'Населенный пункт',
                                labelPosition: 'top',
                                invalidMessage: 'Населенный пункт не может быть пустым',
                                options: {
                                    keyPressTimeout: 250,
                                    filter: (obj, filter) => {
                                        return obj.typename.toLowerCase().indexOf(filter.toLowerCase()) !== -1 || obj.value.toLowerCase().indexOf(filter.toLowerCase()) !== -1;
                                    },
                                    body: {
                                        dynamic: true,
                                        datafetch: 20,
                                        //url: 'cities',
                                        template: '#typename#. #value#',
                                        ready: function () {
                                        }
                                    }
                                },
                                on: {
                                    onChange: (newval, oldval) => {
                                        try {
                                            const indexCity = $$('cities').getValue();
                                            console.log(indexCity);

                                            const city = $$('cities').getList().getItem(indexCity);
                                            console.log(city);

                                            if (city) {
                                                const url = 'streets?objectid=' + city.objectid;
                                                $$('fiasObjectGuid').setValue('');
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
                            {
                                view: 'text',
                                id: 'fiasObjectGuid',
                                name: 'fiasStreetObjectId',
                                label: 'Адрес',
                                labelPosition: 'top',
                                invalidMessage: "Адрес не может быть пустым",
                                suggest: {
                                    keyPressTimeout: 500,
                                    filter: (obj, filter) => {
                                        return obj.typename.toLowerCase().indexOf(filter.toLowerCase()) !== -1 || obj.value.toLowerCase().indexOf(filter.toLowerCase()) !== -1;
                                    },
                                    body: {
                                        dynamic: true,
                                        datafetch: 15,
                                        // template: '#typename#' + '#typename#'.includes('.') ? '. ' : ' ' + '#value#',
                                        template: (item) => {
                                            console.log(item);
                                            const isDot = item.typename.includes('.');
                                            console.log(isDot);
                                            return item.typename + (isDot ? ' ' : '. ') + item.value;
                                        },
                                    }
                                },
                                on: {
                                    onChange: () => {

                                    }
                                }
                            },
                            {
                                view: 'text',
                                id: 'house',
                                name: 'house_hand',
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
                                view: 'text',
                                id: 'office',
                                name: 'apartment_hand',
                                label: 'Офис/помещение',
                                labelPosition: 'top',
                                invalidMessage: "Офис/помещение не может быть пустым",
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

        params.fiasRegionObjectId = region.objectid;

        const indexRaion = $$('raions').getValue();
        const raion = $$('raions').getList().getItem(indexRaion);
        console.log(raion);
        const raionLabel = raion ? (raion.value + '. ' + raion.typename) : null;

        params.fiasRaionObjectId = raion?.objectid ?? region.objectid;

        const indexCity = $$('cities').getValue();
        const city = $$('cities').getList().getItem(indexCity);
        const cityLabel = city.typename + '. ' + city.value;

        params.fiasCityObjectId = city.objectid;

        const suggest_id = $$('fiasObjectGuid').config.suggest;
        const streetLabel = $$('fiasObjectGuid').getValue();
        //streetLabel = streetLabel.slice(streetLabel.lastIndexOf(' ') + 1);
        const streetId = $$(suggest_id).getItemId(streetLabel.slice(streetLabel.lastIndexOf(' ') + 1));
        console.log(streetId);
        const street = $$(suggest_id).getList().getItem(streetId).objectid || streetLabel;
        console.log(street);

        params.fiasStreetObjectId = street;
        // console.log(streetId);
        // console.log(street);
        // const streetLabel = street !== inpStreet ? street.typename + '. ' + street.value : inpStreet;

        const house = 'д. ' + $$('house').getValue();
        console.log(house);

        const office = $$('office').getValue();
        console.log(office);

        params.fiasHouseObjectId = 0;

        params.fiasObjectId = 0;
        if (raionLabel) {
            params.fullAddress = [regionLabel, raionLabel, cityLabel, streetLabel, house, office].join(', ');
        } else {
            params.fullAddress = [regionLabel, cityLabel, streetLabel, house, office].join(', ');
        }
        params.apartment_hand = office;
        params.house_hand = house;

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

