const setObject = async (url, id, idView) => {
    webix.ajax()
        .headers({'Content-type': 'application/json'})
        .get(url + id)
        .then(function (data) {
            if (data !== null) {
                console.log(data.json());
                $$(idView).setValue(data.json());
                $$(idView).refresh();
                // const streetId = $$(suggest_id).getItemId(streetLabel.slice(streetLabel.lastIndexOf(' ') + 1));
                // console.log(streetId);
                // const street = $$(suggest_id).getList().getItem(streetId).objectid || streetLabel;
            }
        });
    return null;
}


const address = {
    // view: 'scrollview',
    // scroll: 'xy',
    // body: {
    //     type: 'space',
    rows: [
        {
            // type: 'wide',
            rows: [
                {
                    //gravity: 0.2,
                    view: 'form',
                    id: 'contact_form',
                    complexData: true,
                    maxHeight: 210,
                    height: 210,
                    scroll: 'y',
                    rules: {
                        "fiasRegionObjectId": webix.rules.isNotEmpty,
                        //"fiasRaionObjectId": webix.rules.isNotEmpty,
                        "fiasStreetObjectId": webix.rules.isNotEmpty,
                        "fiasCityObjectId": webix.rules.isNotEmpty,
                        "house_hand": webix.rules.isNotEmpty,
                        "apartment_hand": webix.rules.isNotEmpty
                    },
                    elements: [
                        {
                            cols: [
                                {
                                    view: "combo",
                                    id: "regions",
                                    name: "fiasRegionObjectId",
                                    label: 'Регион',
                                    value: 'Респ. Бурятия',
                                    labelPosition: 'top',
                                    invalidMessage: 'Регион не может быть пустым',
                                    options: {
                                        keyPressTimeout: 250,
                                        filter: (obj, filter) => {
                                            const value = (obj.typename.toLowerCase() === 'респ' ? obj.typename + '. ' + obj.value : obj.value + ' ' + obj.typename).toLowerCase();
                                            const preFilter = filter.toLowerCase();

                                            return obj.typename.toLowerCase().indexOf(preFilter) !== -1 || obj.value.toLowerCase().indexOf(preFilter) !== -1 || value.indexOf(preFilter) !== -1;
                                        },
                                        body: {

                                            url: 'regions',
                                            template: (item) => {
                                                const value = item.typename.toLowerCase() === 'респ' ? item.typename + '. ' + item.value : item.value + ' ' + item.typename;
                                                return value;
                                                // return `${value}"<div id='del_button' style='position: absolute;top: 0; right: 5px; font-size: 20px;' onclick='$$('regions').setValue('')' class='mdi mdi-delete'></div>",`;
                                            },
                                            ready: function () {
                                                console.log('ready function');
                                                $$('regions').setValue(60635); //60635 = Респ. Бурятия
                                            }

                                        }
                                    },
                                    on: {
                                        onChange: (newval, oldval) => {
                                            try {
                                                console.log('onChange regions');
                                                console.log(newval);
                                                const indexRegion = $$('regions').getValue();
                                                console.log(indexRegion);
                                                if (!indexRegion) return;

                                                const region = $$('regions').getList().getItem(indexRegion);
                                                console.log(region);

                                                if (region) {
                                                    // let url = 'raions?objectid=' + region.objectid;
                                                    let url = 'cities?regionCode=' + region.regioncode;

                                                    const suggest_id = $$('fiasObjectGuid').config.suggest;
                                                    $$(suggest_id).getList().clearAll();

                                                    $$('cities').show();
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
                                    name: "fiasCityObjectId",
                                    label: 'Населенный пункт<div id=\'del_button\' style=\'position: absolute; right: 0px; font-size: 20px; margin-block-start: 6px;\' onclick=clearCityForm() class=\'mdi mdi-delete\'></div>',
                                    labelPosition: 'top',
                                    invalidMessage: 'Населенный пункт не может быть пустым',
                                    tooltip: {
                                        template: 'Вводите название населенного пункта без обозначения (город/село), например: Москва',
                                    },
                                    options: {
                                        keyPressTimeout: 350,
                                        body: {
                                            dataFeed: function (inputString) {
                                                const stapleIndex = inputString.indexOf('(');
                                                const spaceIndex = inputString.indexOf(' ');
                                                let str = stapleIndex !== -1 ? inputString.slice(0, stapleIndex - 1) : inputString;
                                                str = spaceIndex !== -1 ? str.slice(spaceIndex + 1) : str;

                                                const region = $$('regions').getList().getItem($$('regions').getValue());

                                                const prevValue = $$('cities').getList().getItem($$('cities').getValue());
                                                console.log(str);
                                                console.log(prevValue);
                                                //console.log(region);

                                                if (!region || (str && prevValue && str === prevValue.value)) {
                                                    return;
                                                }

                                                let url = `cities?regionCode=${region.regioncode}&name=${str}`;

                                                if (!str.match(/[а-я]/gi)) {
                                                    url = `cities?regionCode=${region.regioncode}`;
                                                }

                                                return webix.ajax().bind(this).get(url, function (data) {
                                                    if (data) {
                                                        //console.log(data);
                                                        this.parse(data);
                                                    }
                                                });
                                            },
                                            dynamic: true,
                                            template: (item) => {
                                                //console.log(item);
                                                let result = `<span>${item.typename}. ${item.value}</span>`;
                                                if (item.level !== 5) {
                                                    const raion = item.districtname + '. ' + item.districttypename;
                                                    result += ` <span>(${raion})</span>`;
                                                }
                                                return result;

                                            },
                                            scheme: {
                                                $sort: {
                                                    by: 'value',
                                                    dir: 'asc',
                                                    as: 'string'
                                                }
                                            },
                                            ready: function () {
                                            }
                                        }
                                    },
                                    on: {
                                        onChange: (newval, oldval) => {
                                            try {
                                                const indexCity = $$('cities').getValue();
                                                console.log(indexCity);
                                                if (!indexCity) return;

                                                const city = $$('cities').getList().getItem(indexCity);
                                                console.log(city);
                                                console.log($$('cities').getList());

                                                if (city) {
                                                    const suggest_id = $$('fiasObjectGuid').config.suggest;
                                                    const list = $$(suggest_id).getList();
                                                    list.clearAll();
                                                    const url = 'streets?objectid=' + city.objectid;
                                                    //$$('fiasObjectGuid').setValue('');
                                                    webix.ajax()
                                                        .headers({'Content-type': 'application/json'})
                                                        .get(url)
                                                        .then(function (data) {
                                                            console.log(data);
                                                            if (data !== null) {
                                                                list.parse(data);
                                                                $$('fiasObjectGuid').focus();
                                                            }
                                                        });
                                                }
                                            } catch (e) {
                                                console.log(e);
                                            }
                                        }
                                    }
                                },
                            ]
                        },

                        {
                            view: "combo",
                            id: "raions",
                            //id: "cities",
                            name: "fiasRaionObjectId",
                            label: 'Район<div id=\'del_button\' style=\'position: absolute; right: 0px; font-size: 20px; margin-block-start: 6px;\' onclick=clearRaionForm() class=\'mdi mdi-delete\'></div>',
                            labelPosition: 'top',
                            hidden: true,
                            invalidMessage: 'Район не может быть пустым',
                            options: {
                                keyPressTimeout: 150,
                                filter: (obj, filter) => {
                                    const value = (obj.typename.toLowerCase() === 'г' ? obj.typename + '. ' + obj.value : obj.value + ' ' + obj.typename).toLowerCase();
                                    const preFilter = filter.toLowerCase();
                                    return obj.typename.toLowerCase().indexOf(preFilter) !== -1 || obj.value.toLowerCase().indexOf(preFilter) !== -1 || value.indexOf(preFilter) !== -1;
                                },
                                body: {
                                    template: (item) => {
                                        return item.typename.toLowerCase() === 'г' ? item.typename + '. ' + item.value : item.value + ' ' + item.typename;
                                    },
                                    ready: function () {
                                    }
                                }
                            },
                            on: {
                                onChange: (newval, oldval) => {
                                }
                            }
                        },

                        {
                            cols: [
                                {
                                    view: 'text',
                                    id: 'fiasObjectGuid',
                                    name: 'fiasStreetObjectId',
                                    label: 'Улица',
                                    labelPosition: 'top',
                                    invalidMessage: "Адрес не может быть пустым",
                                    suggest: {
                                        keyPressTimeout: 200,
                                        filter: (obj, filter) => {
                                            const value = (obj.typename + (obj.typename.includes('.') ? ' ' : '. ') + obj.value).toLowerCase();
                                            const preFilter = filter.toLowerCase();
                                            return obj.typename.toLowerCase().indexOf(preFilter) !== -1 || obj.value.toLowerCase().indexOf(preFilter) !== -1 || value.indexOf(preFilter) !== -1;
                                        },
                                        body: {
                                            template: (item) => {
                                                const isDot = item.typename.includes('.');
                                                return item.typename + (isDot ? ' ' : '. ') + item.value;
                                            },
                                        }
                                    },
                                    on: {
                                        onChange: (newval, oldval) => {
                                            console.log('fiasObjectGuid', newval);
                                            $$('house').focus();
                                        }
                                    }
                                },
                                {
                                    cols: [
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
                                            },
                                            on: {
                                                onChange: (newval, oldval) => {
                                                    //$$('office').focus();
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
                                    ]
                                },

                            ]
                        },


                        {
                            cols: [
                                {
                                    view: 'button',
                                    id: 'add_contact',
                                    css: 'webix_primary',
                                    label: "<span class='mdi mdi-plus-circle' style='padding-right: 5px'></span><span class='text'>Сохранить</span>",
                                    hotkey: 'enter',
                                    click: () => addAddress()
                                }
                            ]
                        },
                    ],
                    elementsConfig: {
                        on: {
                            onChange: function () {
                                this.validate();
                            }
                        }
                    }
                },
                {
                    view: "dataview",
                    id: "address_fact_grid",
                    css: 'contacts',
                    scroll: true,
                    select: 1,
                    url: "address_facts",
                    xCount: 1,
                    label: 'Список сохраненных адресов',
                    labelPosition: 'top',
                    type: {
                        height: "auto",
                        width: "auto",
                        template: "<div class='overall'><div>#full_address#</div>" +
                            "<div id='del_button' style='position: absolute;top: 0; right: 5px; font-size: 20px;' onclick='deleteAddress(#id#)' class='mdi mdi-close-thick'></div></div>",
                    },
                    body: {
                        cols: [
                            {
                                view: 'button',
                                label: 'del'
                            }
                        ]
                    },
                    on: {
                        onItemDblClick: async function (id) {
                            let item = this.getItem(id);
                            console.log(item);
                            const region = $$('regions').getList().find((reg) => reg.objectid === item.fias_region_objectid);
                            console.log(region);
                            $$('regions').setValue(region[0]);

                            await setObject('city?objectid=', item.fias_city_objectid, 'cities');

                            $$('house').setValue(item.house_hand);
                            $$('office').setValue(item.apartment_hand);
                            const suggest_id = $$('fiasObjectGuid').config.suggest;
                            const list = $$(suggest_id).getList();
                            list.clearAll();
                            const url = 'streets?objectid=' + item.fias_city_objectid;
                            webix.ajax()
                                .headers({'Content-type': 'application/json'})
                                .get(url)
                                .then(function (data) {
                                    console.log(data);
                                    if (data !== null) {
                                        list.parse(data);
                                        $$('fiasObjectGuid').setValue(item.street_hand);
                                    }
                                });
                        }
                    }
                },

            ]
        },
    ],
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
        params.fiasRaionObjectId = 0;

        const indexCity = $$('cities').getValue();
        const city = $$('cities').getList().getItem(indexCity);
        const cityLabel = city ? (city.typename + '. ' + city.value) : null;
        let raionLabelTest = null;

        console.log(city);

        if (city && city.districttypename) {
            raionLabelTest = city.districttypename.toLowerCase() === 'респ' ? city.districttypename + '. ' + city.districtname : city.districtname + ' ' + city.districttypename;
            params.fiasRaionObjectId = city ? city.districtobjectid : 0;
        }

        console.log(raionLabelTest);

        params.fiasCityObjectId = city ? city.objectid : 0;

        const suggest_id = $$('fiasObjectGuid').config.suggest;
        const streetLabel = $$('fiasObjectGuid').getValue();
        //streetLabel = streetLabel.slice(streetLabel.lastIndexOf(' ') + 1);
        const streetId = $$(suggest_id).getItemId(streetLabel.slice(streetLabel.lastIndexOf(' ') + 1));
        console.log(streetId);
        const street = streetId !== undefined ? $$(suggest_id).getList().getItem(streetId).objectid : null;
        console.log(street);

        if (street) {
            params.fiasStreetObjectId = street;
            params.streetHand = streetLabel;
        } else {
            params.fiasStreetObjectId = 0;
            params.streetHand = streetLabel;
        }
        const houseValue = $$('house').getValue();
        const house = houseValue.match(/[a-zа-я]/gi) ? houseValue : 'д. ' + houseValue;
        console.log(house);

        const office = $$('office').getValue();
        console.log(office);

        params.fiasHouseObjectId = 0;

        params.fiasObjectId = 0;
        if (raionLabelTest && raionLabelTest !== regionLabel) {
            params.fullAddress = [regionLabel, raionLabelTest, cityLabel, streetLabel, house, office].join(', ');
        } else {
            params.fullAddress = [regionLabel, cityLabel, streetLabel, house, office].join(', ');
        }
        params.houseHand = house;
        params.apartmentHand = office;
        const selectedItem = $$('address_fact_grid').getSelectedItem();
        if (selectedItem && selectedItem.id) {
            params.id = selectedItem.id;
        }
        console.log(selectedItem);

        console.log(params);

        //return;

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
        form.clear();
        form.clearValidation();
        //$$('cities').enable();
    } else {
        console.log('false');
    }
};

function deleteAddress(id = null) {
    let params = id === null ? $$('address_fact_grid').getSelectedItem() : $$("address_fact_grid").getItem(id);
    webix.confirm({
        title: 'Подтверждение',
        type: 'confirm-warning',
        ok: 'Да', cancel: 'Нет',
        text: 'Вы уверены что хотите удалить адрес?'
    }).then(() => {
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
    });
}

function clearDataFromForm() {
    const form = $$('contact_form');
    form.clear();
    form.clearValidation();
    $$('cities').show();
    $$('raions').config.label = 'Район';
    $$('raions').refresh();
}

function clearRegionForm() {
    $$('regions').setValue('');
}

function clearRaionForm() {
    $$('raions').setValue('');
}

function clearCityForm() {
    $$('cities').setValue('');
}