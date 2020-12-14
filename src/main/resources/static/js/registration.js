let regLayout = webix.ui({
    height: windowHeight,
    css: {"background-color":"#ccd7e6"},
    id: 'mainLayout',
    rows: [
        {
            id: 'topSpacer',
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
                                            id: "titleReg",
                                            label: `<span style="font-size: 1.5rem; color: #ccd7e6">Регистрация на портале</span>`,
                                            height: 50,
                                            align: "center"
                                        },
                                        {
                                            view: "label",
                                            id: "appNameReg",
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
                                            template: `<span style="font-size: 0.8rem; color: #fff6f6">
                                                           Вам необходимо ввести ИНН Вашей организации или ИП. ИНН будет проверен по ЕГРЮЛ и ЕГРИП. 
                                                           Для регистрации необходимо, чтобы Ваша организаци или ИП были зарегистрированы в ЕРЮЛ или ЕГРИП.
                                                           Если вы открылись недавно, проверка может показать отсутствие сведений. 
                                                           В таком случае рекомендуется начать регистрацию повторно через несколько рабочих дней.
                                                           Если вы являетесь самозанятым, то вам необходимо будет самостоятельно заполнить данные о себе</span>`,
                                            height: 50,
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
                                                            view:"label",
                                                            height: 20,
                                                            label:`<span style="font-size: 1rem; color: #6e6e6e">Введите ИНН вашей организации</span>`
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
                                                                    label: 'Наименование организации',
                                                                    labelPosition: 'top',
                                                                    readonly: true,
                                                                    hidden: true,
                                                                    required: true
                                                                },
                                                                {
                                                                    view: 'text',
                                                                    id: 'organizationShortName',
                                                                    name: 'organizationShortName',
                                                                    label: 'Наименование организации',
                                                                    labelPosition: 'top',
                                                                    readonly: true,
                                                                    required: true
                                                                },
                                                                {
                                                                    view: 'checkbox',
                                                                    id: 'isSelfEmployed',
                                                                    name: 'isSelfEmployed',
                                                                    labelPosition: 'top',
                                                                    minWidth: 90,
                                                                    label: 'Самозанятый',
                                                                    hidden: true,
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
                                                                    label: 'Подтверждение пароля',
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
                                                                        $$('send_btn').disable();

                                                                        if ($$('form').validate()) {

                                                                            let params = $$('form').getValues();
                                                                            params.organizationInn = params.organizationInn.trim();
                                                                            params.organizationOgrn = params.organizationOgrn.trim();

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
                                                                                } else if (text === 'Не удалось отправить письмо') {
                                                                                    webix.message('Не удалось отправить ссылку для активации на указанный адрес электронной почты', 'error')
                                                                                } else {
                                                                                    webix.message(text, 'error')
                                                                                }
                                                                                $$('send_btn').enable();
                                                                            })
                                                                        } else {
                                                                            webix.message('Не заполнены обязательные поля', 'error')
                                                                            $$('send_btn').enable();
                                                                        }
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
        if (!response.data && response.possiblySelfEmployed == false) {
            webix.message('Введеный ИНН не найден в ЕГРИП и ЕГРЮЛ. Проверьте введенные данные. Возможно, вы ввели неправильный ИНН.', 'error');
            $$('searchInn').hideProgress();
        } else {
            if (type === 'egrul') {
                const result = response.data;
                $$('form').setValues({
                    organizationType: '1',
                    searchInn: inn,
                    organizationName: result.name,
                    organizationShortName: (result.shortName ? result.shortName : result.name),
                    organizationInn: result.inn,
                    organizationOgrn: result.ogrn,
                    organizationEmail: result.email
                })
            } else {
                const result = response.data;
                if (response.possiblySelfEmployed == false){
                    $$('organizationShortName').config.label = 'ФИО ИП';
                    $$('form').setValues({
                        organizationType: '2',
                        searchInn: inn,
                        organizationName: result.name,
                        organizationShortName: result.name,
                        organizationInn: result.inn,
                        organizationEmail: result.email
                    })
                }else{
                    $$('organizationShortName').config.label = 'ФИО cамозанятого';
                    $$('organizationShortName').config.readonly = false;
                    $$('form').setValues({
                        organizationType: '3',
                        searchInn: inn,
                        isSelfEmployed: true,
                        organizationInn: inn
                    })

                }

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
    if (document.body.clientWidth < 480) {
        $$('topSpacer').config.gravity = 0.9;
        $$('topSpacer').resize()
        $$("description").config.height = 50;
        $$("description").resize()
    }else if(document.body.clientWidth > 480){
        $$("description").config.height = 35;
        $$("description").resize()
    }
    $$("wizard").back();
    $$("step").setValue(`<span style="font-size: 1rem; color: #fff6f6">Шаг 1</span>`)
    $$("description").setValue(`<span style="font-size: 0.8rem; color: #fff6f6">На шаге 1 Вам необходимо ввести ИНН Вашей организации. ИНН будет сверен с ЕГРЮЛ и ЕГРИП.</span>`)

}

function next(page) {
    if (document.body.clientWidth < 480) {
        $$('topSpacer').config.gravity = 0.5;
        $$('topSpacer').resize()
        $$("description").config.height = 95;
        $$("description").resize()
    }else if(document.body.clientWidth > 480){
        $$("description").config.height = 60;
        $$("description").resize()
    }
    $$("wizard").getChildViews()[page].show();
    $$("step").setValue(`<span style="font-size: 1rem; color: #fff6f6">Шаг 2</span>`)
    $$("description").setValue(`<span style="font-size: 0.8rem; color: #fff6f6">На шаге 2 Вам необходимо ввести Адрес электронной почты Вашей организации и пароль. После регистрации, на указанный адрес электронной почты, будет отправлено письмо со ссылкой на активацию учётной записи.</span>`)

}

webix.ready(function() {
    let clientScreenWidth = document.body.clientWidth;
    if (clientScreenWidth < 760) {
        $$("leftLayout").hide();
        $$("form").config.width = document.body.clientWidth-40;
        $$("titleReg").setValue(`<span style="font-size: 1.5rem; color: #475466">Регистрация на портале</span>`)
        $$("appNameReg").setValue(`<span style="font-size: 1.2rem; color: #475466">"${APPLICATION_NAME}"</span>`)
        $$("description").setValue(`<span style="font-size: 0.8rem; color: #475466">На шаге 1 Вам необходимо ввести ИНН Вашей организации. ИНН будет сверен с ЕГРЮЛ и ЕГРИП.</span>`)
        $$("firstRow").addView($$("titleReg"),-1);
        $$("firstRow").addView($$("appNameReg"),-1);
        $$("firstRow").addView($$("description"),-1);
        $$("form").adjust();
        $$("form").resize();
    }
    webix.extend($$('searchInn'), webix.ProgressBar);
    if (document.body.clientWidth < 480){
        regLayout.config.width = document.body.clientWidth; regLayout.resize();
        $$("organizationName").config.label = "Наим. орг./ФИО ИП"; $$("organizationName").refresh();
        $$("organizationShortName").config.label = "Краткое наим. огр."; $$("organizationShortName").refresh();

    }
})