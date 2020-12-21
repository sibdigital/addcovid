const setObject = async (url, id, idView) => {
    webix.ajax()
        .headers({'Content-type': 'application/json'})
        .get(url + id)
        .then(function (data) {
            if (data !== null) {
                console.log(data.json());
                $$(idView).setValue(data.json());
                $$(idView).refresh();
            }
        });
    return null;
}

const setStreetList = async (url, id, idView, streetHand) => {
    const suggest_id = $$(idView).config.suggest;
    const list = $$(suggest_id).getList();
    list.clearAll();
    webix.ajax()
        .headers({'Content-type': 'application/json'})
        .get(url + id)
        .then(function (data) {
            if (data !== null) {
                list.parse(data);
                $$(idView).setValue(streetHand);
            }
        });
}


const Regions = {
    view: "combo",
    id: "regions",
    name: "fiasRegionObjectId",
    label: "Регион<span style='color: red;'>*</span>",
    maxWidth: 515,
    css: { 'margin-inline-end': '10px' },
    value: 'Респ. Бурятия',
    labelPosition: 'left',
    invalidMessage: 'Регион не может быть пустым',
    placeholder: 'Введите регион',
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
            },
            ready: function () {
                $$('regions').setValue(60635); //60635 = Респ. Бурятия
            }

        }
    },
    on : {
        onChange: (newval, oldval) => {
            try {
                const indexRegion = $$('regions').getValue();
                if (!indexRegion) return;

                const region = $$('regions').getList().getItem(indexRegion);

                if (region) {
                    let url = 'cities?regionCode=' + region.regioncode;

                    const suggest_id = $$('fiasObjectGuid').config.suggest;
                    $$(suggest_id).getList().clearAll();

                    $$('cities').setValue('');
                    $$('cities').getList().clearAll();
                    $$('cities').getList().load(url);

                }
            } catch (e) {
                console.log(e);
            }
        }
    },
};

const Cities = {
    view: "combo",
    id: "cities",
    name: "fiasCityObjectId",
    label: "Населенный пункт<span style='color: red;'>*</span>",
    labelPosition: 'left',
    labelWidth: 150,
    maxWidth: 1105,
    invalidMessage: 'Населенный пункт не может быть пустым',
    placeholder: 'Введите город, деревню, село или населенный пункт',
    tooltip: {
        template: 'Вводите название населенного пункта без обозначения (город/село), например: Москва',
    },
    options: {
        keyPressTimeout: 350,
        maxWidth: 945,
        body: {
            dataFeed: function(inputString) {
                const stapleIndex = inputString.indexOf('(');
                const spaceIndex = inputString.indexOf(' ');
                let str = stapleIndex !== -1 ? inputString.slice(0, stapleIndex - 1) : inputString;
                str = spaceIndex !== -1 ? str.slice(spaceIndex + 1) : str;

                const region = $$('regions').getList().getItem($$('regions').getValue());

                const prevValue = $$('cities').getList().getItem($$('cities').getValue());
                console.log(str);
                console.log(prevValue);

                if (!region || (str && prevValue && str === prevValue.value)) {
                    return;
                }

                let url = `cities?regionCode=${region.regioncode}&name=${str}`;

                if (!str.match(/[а-я]/gi)) {
                    url = `cities?regionCode=${region.regioncode}`;
                }

                return webix.ajax().bind(this).get(url, function(data) {
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
                if (!indexCity) return;

                const city = $$('cities').getList().getItem(indexCity);

                if (city) {
                    const suggest_id = $$('fiasObjectGuid').config.suggest;
                    const list = $$(suggest_id).getList();
                    list.clearAll();
                    const url = 'streets?objectid=' + city.objectid;
                    webix.ajax()
                        .headers({'Content-type': 'application/json'})
                        .get(url)
                        .then(function (data) {
                            if (data !== null) {
                                list.parse(data);
                                $$('fiasObjectGuid').focus();
                            }
                        });
                }
            } catch(e) {
                console.log(e);
            }
        }
    }
};

const Streets = {
    cols: [
        {
            view: 'text',
            id: 'fiasObjectGuid',
            name: 'fiasStreetObjectId',
            maxWidth: 850,
            width: 850,
            css: { 'margin-inline-end': '10px' },
            label: "Улица<span style='color: red;'>*</span>",
            labelPosition: 'left',
            invalidMessage: "Адрес не может быть пустым",
            placeholder: 'Введите улицу',
            suggest: {
                keyPressTimeout: 200,
                filter: (street, inputString) => {
                    const value = (street.typename + (street.typename.includes('.') ? ' ' : '. ') + street.value).toLowerCase();
                    const preFilter = inputString.toLowerCase();
                    return street.typename.toLowerCase().indexOf(preFilter) !== -1 || street.value.toLowerCase().indexOf(preFilter) !== -1 || value.indexOf(preFilter) !== -1;
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
                    $$('house').focus();
                }
            }
        },
        {
            maxWidth: 630,
            width: 630,
            css: { 'margin-inline-end': '10px' },
            cols: [
                {
                    view: 'text',
                    id: 'house',
                    name: 'house_hand',
                    label: "Дом<span style='color: red;'>*</span>",
                    labelPosition: 'left',
                    labelWidth: 45,
                    maxWidth: 305,
                    css: { 'margin-inline-end': '10px' },
                    invalidMessage: "Дом не может быть пустым",
                    placeholder: 'Введите номер дома',
                    suggest: {
                        keyPressTimeout: 500,
                        body: {
                            dynamic: true,
                            datafetch: 15
                        }
                    },
                },
                {
                    view: 'text',
                    id: 'office',
                    name: 'apartment_hand',
                    label: "Помещение<span style='color: red;'>*</span>",
                    labelPosition: 'left',
                    labelWidth: 100,
                    maxWidth: 305,
                    tooltip: 'Помещение',
                    placeholder: 'Введите номер помещения/офиса',
                    invalidMessage: "Офис/помещение не может быть пустым",
                },
            ]
        },

    ]
};

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
                    view: 'form',
                    id: 'contact_form',
                    complexData: true,
                    maxHeight: 110,
                    height: 110,
                    rules: {
                        "fiasRegionObjectId": webix.rules.isNotEmpty,
                        "fiasStreetObjectId": webix.rules.isNotEmpty,
                        "fiasCityObjectId": webix.rules.isNotEmpty,
                        "house_hand": webix.rules.isNotEmpty,
                        "apartment_hand": webix.rules.isNotEmpty
                    },
                    elements: [
                        {
                            cols: [
                                Regions,
                                Cities,
                                {
                                    view: 'button',
                                    css: 'webix_transparent',
                                    label: "<div id='del_button' onclick=clearCityForm() class='mdi mdi-delete'></div>",
                                    width: 36,
                                    maxWidth: 36,
                                }
                            ]
                        },
                        {
                            cols: [
                                Streets,
                                {
                                    cols: [
                                        {
                                            view: 'button',
                                            id: 'add_contact',
                                            css: 'webix_primary',
                                            maxWidth: 150,
                                            label: "<span class='mdi mdi-plus-circle' style='padding-right: 5px'></span><span class='text'>Сохранить</span>",
                                            hotkey: 'enter',
                                            click: () => addAddress()
                                        }
                                    ]
                                }
                            ]
                        },
                    ],
                    elementsConfig: {
                        on: {
                            onChange: function() {
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
                    on: {
                        onItemDblClick: async function (id) {
                            const item = this.getItem(id);
                            const region = $$('regions').getList().find((reg) => reg.objectid === item.fias_region_objectid);
                            $$('regions').setValue(region[0]);

                            await setObject('city?objectid=', item.fias_city_objectid, 'cities');

                            $$('house').setValue(item.house_hand);
                            $$('office').setValue(item.apartment_hand);

                            await setStreetList('streets?objectid=', item.fias_city_objectid, 'fiasObjectGuid', item.street_hand);

                            // const suggest_id = $$('fiasObjectGuid').config.suggest;
                            // const list = $$(suggest_id).getList();
                            // list.clearAll();
                            // const url = 'streets?objectid=' + item.fias_city_objectid;
                            // webix.ajax()
                            //     .headers({'Content-type': 'application/json'})
                            //     .get(url + id)
                            //     .then(function (data) {
                            //         if (data !== null) {
                            //             list.parse(data);
                            //             $$('fiasObjectGuid').setValue(item.street_hand);
                            //         }
                            //     });
                        }
                    }
                },

            ]
        },
    ],
    // }
};

const getRegion = () => {
    const indexRegion = $$('regions').getValue();
    const region = $$('regions').getList().getItem(indexRegion);
    return region;
};

const getCity = () => {
    const indexCity = $$('cities').getValue();
    const city = $$('cities').getList().getItem(indexCity);
    return city;
};

const getStreet = () => {
    const suggest_id = $$('fiasObjectGuid').config.suggest;
    const streetLabel = $$('fiasObjectGuid').getValue();
    const streetId = $$(suggest_id).getItemId(streetLabel.slice(streetLabel.lastIndexOf(' ') + 1));
    const street = streetId !== undefined ? $$(suggest_id).getList().getItem(streetId).objectid : null;
    return street;
};

function addAddress() {
    const form = $$('contact_form');
    const params = form.getValues();
    console.log(params);

    if (form.validate()) {
        const region = getRegion();
        const regionLabel = region.typename.toLowerCase() === 'респ' ? region.typename + '. ' + region.value : region.value + ' ' + region.typename;

        params.fiasRegionObjectId = region.objectid;
        params.fiasRaionObjectId = 0;

        const city = getCity();
        const cityLabel = city ? (city.typename + '. ' + city.value) : null;
        let raionLabel = null;

        if (city && city.districttypename) {
            raionLabel = city.districttypename.toLowerCase() === 'респ' ? city.districttypename + '. ' + city.districtname : city.districtname + ' ' + city.districttypename;
            params.fiasRaionObjectId = city ? city.districtobjectid : 0;
        }

        params.fiasCityObjectId = city ? city.objectid : 0;

        const street = getStreet();
        const streetLabel = $$('fiasObjectGuid').getValue();

        params.streetHand = streetLabel;
        params.fiasStreetObjectId = street ? street : 0;

        const houseValue = $$('house').getValue();
        const house = houseValue.match(/[a-zа-я]/gi) ? houseValue : 'д. ' + houseValue;

        const office = $$('office').getValue();

        params.fiasHouseObjectId = 0;
        params.fiasObjectId = 0;

        if (raionLabel && raionLabel !== regionLabel) {
            params.fullAddress = [regionLabel, raionLabel, cityLabel, streetLabel, house, office].join(', ');
        } else {
            params.fullAddress = [regionLabel, cityLabel, streetLabel, house, office].join(', ');
        }

        params.houseHand = house;
        params.apartmentHand = office;

        const selectedItem = $$('address_fact_grid').getSelectedItem();
        if (selectedItem && selectedItem.id) {
            params.id = selectedItem.id;
        }

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
        form.clear();
        form.clearValidation();
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
}

function clearRegionForm() {
    $$('regions').setValue('');
}

function clearCityForm() {
    $$('cities').setValue('');
}