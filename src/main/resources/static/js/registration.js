let regLayout = webix.ui({
    height: windowHeight,
    css: {"background-color":"#ccd7e6"},
    rows: [
        {
            gravity: 0.9,
        },
        {
            view: "align",
            align: "middle,center",
            body: {
                cols: [
                    {
                        view: "align",
                        align: "middle,center",
                        body: {
                            cols: [
                                {
                                    id: "leftLayout",
                                    margin: 0,
                                    padding: {
                                        top: 55, bottom: 25
                                    },
                                    width: 400,
                                    css: {
                                        "background": "#475466 !important" //#2b334a
                                    },
                                    rows: [
                                        {
                                            view: "label",
                                            height: 200,
                                            id: "logo",
                                            align: "center",
                                            template: "<img src = \"logo.png\">"
                                        },
                                        {
                                            view: "label",
                                            label: `<span style="font-size: 1.5rem; color: #ccd7e6">Регистрация на портале</span>`,
                                            height: 50,
                                            align: "center"
                                        },
                                        {
                                            view: "label",
                                            label: `<span style="font-size: 1.2rem; color: #ccd7e6">"${APPLICATION_NAME}"</span>`,
                                            height: 50,
                                            align: "center"
                                        },
                                        {
                                            view: "label",
                                            id: "step",
                                            label: `<span style="font-size: 1rem; color: #fff6f6">Шаг 1</span>`,
                                            height: 50,
                                            align: "center"
                                        },
                                        {
                                            view: "label",
                                            id: "description",
                                            css:{"background-color":"#475466", "text-align":"center", "padding-left":"2px","padding-right":"2px"},
                                            borderless: true,
                                            template: `<span style="font-size: 0.8rem; color: #fff6f6">На шаге 1 Вам необходимо ввести ИНН Вашей организации. ИНН будет сверен с ЕГРЮЛ и ЕГРИП.</span>`,
                                            height: 70,
                                            align: "center"
                                        },
                                    ]

                                },
                                {
                                    id: 'form',
                                    view: 'form',
                                    maxWidth: 450,
                                    width: 350,
                                    minWidth: 250,
                                    complexData: true,
                                    rules:{
                                      "passwordConfirm":function (value){
                                          return value === $$("password").getValue()
                                      }
                                    },
                                    elements: [
                                        {},
                                        {
                                            id: "firstRow", rows:[]
                                        },
                                        {
                                            view: 'multiview',
                                            id: 'wizard',
                                            cells: [
                                                {
                                                    rows: [
                                                        {
                                                            view:"template",
                                                            autoheight: true,
                                                            borderless: true,
                                                            template:`<span style="padding-left: 0; font-size: 1.1rem; font-weight: 500; color: #6e6e6e">Введите ИНН Вашей организации</span>`
                                                        },
                                                        {
                                                            view: 'text',
                                                            name: 'searchInn',
                                                            id: 'searchInn',
                                                            minWidth: 250,
                                                            // label: 'ИНН',
                                                            placeholder: 'ИНН',
                                                        },
                                                        {height: 10},
                                                        {
                                                            cols: [
                                                                {
                                                                    view: 'button',
                                                                    css: 'myClass',
                                                                    value: 'Отмена',
                                                                    click: () => { window.location.href = '/login' }
                                                                },
                                                                {width: 5},
                                                                {
                                                                    view: 'button',
                                                                    id: 'egrul_load_button',
                                                                    css: 'myClass',
                                                                    value: 'Продолжить',
                                                                    align: 'center',
                                                                    click: () => {
                                                                        $$('egrul_load_button').disable();
                                                                        $$('searchInn').showProgress();

                                                                        setTimeout(function () {
                                                                            const inn = $$('searchInn').getValue();
                                                                            if (inn === '') {
                                                                                $$('searchInn').focus();
                                                                                webix.message('ИНН не введен', 'error');
                                                                                $$('searchInn').hideProgress();
                                                                                $$('egrul_load_button').enable();
                                                                                return;
                                                                            } else if (isNaN(inn)) {
                                                                                $$('searchInn').focus();
                                                                                webix.message('ИНН не соответствует формату', 'error');
                                                                                $$('searchInn').hideProgress();
                                                                                $$('egrul_load_button').enable();
                                                                                return;
                                                                            }

                                                                            let type = '';
                                                                            if (inn.length === 10) {
                                                                                type = 'egrul';
                                                                            } else if (inn.length === 12) {
                                                                                type = 'egrip';
                                                                            }
                                                                            if (type === '') {
                                                                                $$('searchInn').focus();
                                                                                webix.message('ИНН не соответствует формату', 'error');
                                                                                $$('searchInn').hideProgress();
                                                                                $$('egrul_load_button').enable();
                                                                                return;
                                                                            }

                                                                            webix.ajax().get('checkInn?inn=' + inn).then(function (data) {
                                                                                const result = data.text();
                                                                                if (result !== 'ИНН не зарегистрирован') {
                                                                                    webix.message(result, 'error');
                                                                                    $$('searchInn').hideProgress();
                                                                                    $$('egrul_load_button').enable();
                                                                                } else {
                                                                                    loadData(type, inn);
                                                                                    $$('egrul_load_button').enable();
                                                                                }
                                                                            })
                                                                        }, 500);
                                                                    }
                                                                },
                                                            ]
                                                        }
                                                    ]
                                                },
                                                {
                                                    rows: [
                                                        {
                                                            rows: [
                                                                {
                                                                    view: 'text',
                                                                    id: 'organizationInn',
                                                                    name: 'organizationInn',
                                                                    minWidth: 250,
                                                                    label: 'ИНН',
                                                                    labelPosition: 'top',
                                                                    validate: webix.rules.isNumber,
                                                                    readonly: true,
                                                                    required: true
                                                                },
                                                                {
                                                                    view: 'text',
                                                                    id: 'organizationName',
                                                                    name: 'organizationName',
                                                                    label: 'Наименование организации/ИП',
                                                                    labelPosition: 'top',
                                                                    readonly: true,
                                                                    required: true
                                                                },
                                                                {
                                                                    view: 'text',
                                                                    id: 'organizationShortName',
                                                                    name: 'organizationShortName',
                                                                    label: 'Краткое наименование организации',
                                                                    labelPosition: 'top',
                                                                    hidden: true,
                                                                    required: true
                                                                },
                                                                {
                                                                    view: 'checkbox',
                                                                    name: 'isSelfEmployed',
                                                                    labelPosition: 'top',
                                                                    minWidth: 90,
                                                                    label: 'Самозанятый',
                                                                    hidden: true,
                                                                    on: {
                                                                        onChange(newv, oldv) {
                                                                            if (newv === 1) {
                                                                                $$('organizationOgrn').setValue('');
                                                                                $$('organizationOgrn').disable();
                                                                            } else {
                                                                                $$('organizationOgrn').enable();
                                                                            }
                                                                        }
                                                                    }
                                                                },
                                                                {
                                                                    view: 'text',
                                                                    id: 'organizationOgrn',
                                                                    name: 'organizationOgrn',
                                                                    label: 'ОГРН',
                                                                    validate: webix.rules.isNumber,
                                                                    labelPosition: 'top',
                                                                    hidden: true,
                                                                    required: true
                                                                },
                                                                {
                                                                    view: 'text',
                                                                    id: 'organizationOkved',
                                                                    name: 'organizationOkved',
                                                                    label: 'Основной вид осуществляемой деятельности (отрасль)',
                                                                    labelPosition: 'top',
                                                                    placeholder: '01.11',
                                                                    required: true,
                                                                    hidden: true,
                                                                    on: {
                                                                        onKeyPress(code, event) {
                                                                            if (code === 32) {
                                                                                return false;
                                                                            }
                                                                        }
                                                                    }
                                                                },
                                                                {
                                                                    view: 'textarea',
                                                                    id: 'organizationOkvedAdd',
                                                                    name: 'organizationOkvedAdd',
                                                                    label: 'Дополнительные виды осуществляемой деятельности',
                                                                    height: 100,
                                                                    labelPosition: 'top',
                                                                    placeholder: '01.13\n01.13.1',
                                                                    hidden: true,
                                                                    on: {
                                                                        onKeyPress(code, event) {
                                                                            if (code === 32) {
                                                                                this.setValue(this.getValue() + '\n')
                                                                                return false;
                                                                            }
                                                                        }
                                                                    }
                                                                },
                                                                {
                                                                    view: 'textarea',
                                                                    id: 'organizationAddressJur',
                                                                    name: 'organizationAddressJur',
                                                                    label: 'Юридический адрес',
                                                                    labelPosition: 'top',
                                                                    height: 80,
                                                                    hidden: true,
                                                                    required: true
                                                                },
                                                                {
                                                                    view: 'text',
                                                                    id: 'organizationEmail',
                                                                    name: 'organizationEmail',
                                                                    label: 'Адрес электронной почты',
                                                                    labelPosition: 'top',
                                                                    validate: webix.rules.isEmail,
                                                                    required: true
                                                                },
                                                                {
                                                                    view: 'text',
                                                                    name: 'organizationPhone',
                                                                    label: 'Телефон',
                                                                    labelPosition: 'top',
                                                                    hidden: true,
                                                                    required: true
                                                                },
                                                                {
                                                                    view: 'text',
                                                                    id: 'password',
                                                                    name: 'password',
                                                                    type: 'password',
                                                                    label: 'Пароль',
                                                                    labelPosition: 'top',
                                                                    required: true,
                                                                    attributes: {autocomplete: 'new-password'},
                                                                },
                                                                {
                                                                    view: 'text',
                                                                    id: 'passwordConfirm',
                                                                    name: 'passwordConfirm',
                                                                    type: 'password',
                                                                    label: 'Пароль',
                                                                    labelPosition: 'top',
                                                                    required: true,
                                                                    attributes: {autocomplete: 'new-password'},
                                                                    invalidMessage: "Пароли не совпадают"
                                                                },
                                                            ]
                                                        },
                                                        {
                                                            cols: [
                                                                {
                                                                    view: 'button',
                                                                    css: 'myClass',
                                                                    value: 'Назад',
                                                                    gravity: 0.5,
                                                                    click: back
                                                                },
                                                                {
                                                                    id: 'send_btn',
                                                                    view: 'button',
                                                                    css: 'myClass',
                                                                    value: 'Зарегистрироваться',
                                                                    align: 'center',
                                                                    click: function () {
                                                                        this.disable();

                                                                        if ($$('form').validate()) {

                                                                            let params = $$('form').getValues();

                                                                            if (params.organizationShortName.length > 255) {
                                                                                webix.message('Превышена длина краткого наименования', 'error')
                                                                                return false
                                                                            }

                                                                            params.organizationInn = params.organizationInn.trim();
                                                                            params.organizationOgrn = params.organizationOgrn.trim();

                                                                            if (params.organizationInn.length > 12) {
                                                                                webix.message('Превышена длина ИНН', 'error');
                                                                                return false;
                                                                            }

                                                                            if (params.organizationOgrn.length > 15) {
                                                                                webix.message('Превышена длина ОГРН', 'error');
                                                                                return false;
                                                                            }

                                                                            if (params.organizationAddressJur.length > 255) {
                                                                                webix.message('Превышена длина юридического адреса', 'error')
                                                                                return false
                                                                            }

                                                                            if (params.organizationEmail.length > 100) {
                                                                                webix.message('Превышена длина электронной почты', 'error');
                                                                                return false;
                                                                            } else {
                                                                                let bad_val = params.organizationEmail.indexOf("*") > -1
                                                                                    || params.organizationEmail.indexOf("+") > -1
                                                                                    || params.organizationEmail.indexOf('"') > -1;
                                                                                if (bad_val == true) {
                                                                                    webix.message('Недопустимые символы в адресе электронной почты', 'error');
                                                                                    return false;
                                                                                }
                                                                            }

                                                                            if (params.organizationPhone.length > 100) {
                                                                                webix.message('Превышена длина номера телефона', 'error');
                                                                                return false;
                                                                            }

                                                                            webix.ajax()
                                                                                .headers({'Content-type': 'application/json'})
                                                                                .post('/registration', JSON.stringify(params)
                                                                                ).then(function (data) {
                                                                                const text = data.text();
                                                                                if (text === 'Ок') {
                                                                                    window.location = '/login';
                                                                                } else {
                                                                                    webix.message(text, 'error')
                                                                                }
                                                                            })
                                                                        } else {
                                                                            webix.message('Не заполнены обязательные поля', 'error')
                                                                        }

                                                                        this.enable();
                                                                    }
                                                                }
                                                            ]
                                                        }
                                                    ]
                                                }
                                            ]
                                        },
                                        {}
                                    ]
                                },
                            ]
                        }
                    },
                ]
            }
        },
        {}

    ]
})

function loadData(type, inn) {
    webix.ajax('/' + type + '?inn=' + inn).then(function (data) {
        const response = data.json();
        if (!response.data) {
            webix.message('Данные не найдены', 'error');
            $$('searchInn').hideProgress();
        } else {
            if (type === 'egrul') {
                const result = response.data;
                //let egrulOkvedAdd = [];
                //let okvedAdd = '';
                // if (result.свОКВЭД.свОКВЭДДоп) {
                //     result.свОКВЭД.свОКВЭДДоп.forEach(function (elem) {
                //         okvedAdd += elem.кодОКВЭД + ' ' + elem.наимОКВЭД + '\n';
                //         egrulOkvedAdd.push(elem.кодОКВЭД);
                //     })
                // }
                //let jurAddress = '';
                // if (result.свАдресЮЛ) {
                //     jurAddress += result.свАдресЮЛ.адресРФ.индекс
                //     jurAddress += ', ' + result.свАдресЮЛ.адресРФ.регион.наимРегион;
                //     jurAddress += result.свАдресЮЛ.адресРФ.район.наимРайон ? ', ' + result.свАдресЮЛ.адресРФ.район.наимРайон : '';
                //     jurAddress += result.свАдресЮЛ.адресРФ.город.наимГород ? ', ' + result.свАдресЮЛ.адресРФ.город.наимГород : '';
                //     jurAddress += result.свАдресЮЛ.адресРФ.населПункт.наимНаселПункт ? ', ' + result.свАдресЮЛ.адресРФ.населПункт.наимНаселПункт : '';
                //     jurAddress += result.свАдресЮЛ.адресРФ.улица.наимУлица ? ', ' + result.свАдресЮЛ.адресРФ.улица.наимУлица : '';
                //     jurAddress += result.свАдресЮЛ.адресРФ.дом ? ', ' + result.свАдресЮЛ.адресРФ.дом : '';
                //     jurAddress += result.свАдресЮЛ.адресРФ.корпус ? ', ' + result.свАдресЮЛ.адресРФ.корпус : '';
                //     jurAddress += result.свАдресЮЛ.адресРФ.квартира ? ', ' + result.свАдресЮЛ.адресРФ.квартира : '';
                // }
                $$('form').setValues({
                    searchInn: inn,
                    organizationName: result.name,
                    organizationShortName: result.shortName,
                    organizationInn: result.inn,
                    organizationOgrn: result.ogrn,
                    organizationEmail: result.email,
                    //organizationOkved: result.свОКВЭД.свОКВЭДОсн.кодОКВЭД + ' ' + result.свОКВЭД.свОКВЭДОсн.наимОКВЭД,
                    //egrulOkved: result.свОКВЭД.свОКВЭДОсн.кодОКВЭД,
                    //organizationOkvedAdd: okvedAdd,
                    //egrulOkvedAdd: egrulOkvedAdd,
                    //organizationAddressJur: jurAddress
                })
            } else {
                const result = response.data;
                let name = result.наимВидИП;
                name += result.свФЛ.фиорус.фамилия ? ' ' + result.свФЛ.фиорус.фамилия : '';
                name += result.свФЛ.фиорус.имя ? ' ' + result.свФЛ.фиорус.имя : '';
                name += result.свФЛ.фиорус.отчество ? ' ' + result.свФЛ.фиорус.отчество : '';
                let egrulOkvedAdd = [];
                let okvedAdd = '';
                // if (result.свОКВЭД.свОКВЭДДоп) {
                //     result.свОКВЭД.свОКВЭДДоп.forEach(function (elem) {
                //         okvedAdd += elem.кодОКВЭД + ' ' + elem.наимОКВЭД + '\n';
                //         egrulOkvedAdd.push(elem.кодОКВЭД);
                //     })
                // }
                // let jurAddress = '';
                // if (result.свАдрМЖ) {
                //     jurAddress += result.свАдрМЖ.адресРФ.регион.наимРегион;
                //     jurAddress += result.свАдрМЖ.адресРФ.район.наимРайон ? ', ' + result.свАдрМЖ.адресРФ.район.наимРайон : '';
                //     jurAddress += result.свАдрМЖ.адресРФ.город.наимГород ? ', ' + result.свАдрМЖ.адресРФ.город.наимГород : '';
                //     jurAddress += result.свАдрМЖ.адресРФ.населПункт.наимНаселПункт ? ', ' + result.свАдрМЖ.адресРФ.населПункт.наимНаселПункт : '';
                // }
                $$('form').setValues({
                    searchInn: inn,
                    organizationName: name,
                    organizationShortName: name,
                    organizationInn: result.иннфл,
                    organizationOgrn: result.огрнип,
                    organizationEmail: result.свАдрЭлПочты.email,
                    organizationOkved: result.свОКВЭД.свОКВЭДОсн.кодОКВЭД + ' ' + result.свОКВЭД.свОКВЭДОсн.наимОКВЭД,
                    //egrulOkved: result.свОКВЭД.свОКВЭДОсн.кодОКВЭД,
                    organizationOkvedAdd: okvedAdd,
                    //egrulOkvedAdd: egrulOkvedAdd,
                    organizationAddressJur: jurAddress
                })
            }

            $$('searchInn').hideProgress();
            next(1);
        }
    }).catch(function () {
        webix.message('Не удалось получить данные', 'error');
        $$('searchInn').hideProgress();
    });
}

function back() {
    $$("wizard").back();
    $$("step").setValue(`<span style="font-size: 1rem; color: #fff6f6">Шаг 1</span>`)
    $$("description").setValue(`<span style="font-size: 0.8rem; color: #fff6f6">На шаге 1 Вам необходимо ввести ИНН Вашей организации. ИНН будет сверен с ЕГРЮЛ и ЕГРИП.</span>`)
}

function next(page) {
    $$("wizard").getChildViews()[page].show();
    $$("step").setValue(`<span style="font-size: 1rem; color: #fff6f6">Шаг 2</span>`)
    $$("description").setValue(`<span style="font-size: 0.8rem; color: #fff6f6">На шаге 2 Вам необходимо ввести Адрес электронной почты Вашей организации и пароль. После регистрации, на указанный адрес электронной почты, будет отправлено письмо со ссылкой на активацию учётной записи.</span>`)
}

webix.ready(function() {
    let clientScreenWidth = document.body.clientWidth;
    if (clientScreenWidth < 760) {
        $$("leftLayout").hide();
        $$("form").config.width = clientScreenWidth - 40;
        $$("firstRow").addView($$("logo"),0);
        // $$("firstRow").addView($$("step"),-1);
        // $$("firstRow").addView($$("description"),-1);
        $$("form").adjust();
        $$("form").resize();
    }
    //if (EGRUL_ADDRESS) {
        webix.extend($$('searchInn'), webix.ProgressBar);
        $$('egrul_search').show();
    //}
    if (document.body.clientWidth < 480){
        regLayout.config.width = document.body.clientWidth; regLayout.resize();
        $$("organizationName").config.label = "Наим. орг./ФИО ИП"; $$("organizationName").refresh();
        $$("organizationShortName").config.label = "Краткое наим. огр."; $$("organizationShortName").refresh();
        $$("organizationOkved").config.label = "Осн. вид деятельности"; $$("organizationOkved").refresh();
        $$("organizationOkvedAdd").config.label = "Доп. виды деятельности"; $$("organizationOkvedAdd").refresh();
    }
})