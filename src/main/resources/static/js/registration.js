let descrStep1 = '<span style=" height: auto; font-size: 0.8rem; color: #fff6f6">' +
    'Вам необходимо ввести ИНН Вашей организации или ИП. ИНН будет проверен по ЕГРЮЛ и ЕГРИП.' +
    'Для регистрации необходимо, чтобы Ваша организация или ИП были зарегистрированы в ЕГРЮЛ или ЕГРИП.' +
    'Если вы открылись недавно, проверка может показать отсутствие сведений.' +
    'В таком случае рекомендуется начать регистрацию повторно через несколько рабочих дней.' +
    'Если вы являетесь самозанятым, то вам необходимо будет самостоятельно заполнить данные о себе</span>'

let descrStep2 = '<span style="font-size: 0.8rem; color: #fff6f6">' +
    'Вам необходимо ввести Адрес электронной почты Вашей организации и пароль. ' +
    'После регистрации, на указанный адрес электронной почты, будет отправлено письмо' +
    'со ссылкой на активацию учётной записи.</span>'

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
                                            view: "template",
                                            id: "description",
                                            css:{"background-color":"#475466", "text-align":"center", "padding-left":"2px","padding-right":"2px"},
                                            borderless: true,
                                            autoheight: true,
                                            template: descrStep1
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
                                        "password": (value) => {
                                            if(value.length < 8){
                                                $$("password").config.invalidMessage = "Длина пароля должна превышать 7 символов";
                                                return false
                                            }else if(!(/[0-9]/.test(value) && /[a-z]/i.test(value))){
                                                $$("password").config.invalidMessage = "Пароль должен содержать буквы и цифры";
                                                return false
                                            }else{
                                                return true
                                            }
                                        },
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
                                                            view: 'text',
                                                            name: 'searchInn',
                                                            id: 'searchInn',
                                                            minWidth: 250,
                                                            labelPosition: 'top',
                                                            label: 'Введите ИНН вашей организации',
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
                                                                                if (result == "Данный ИНН уже зарегистрирован в системе"){
                                                                                    var message = 'Ранее вы уже подавали заявки на портале Работающая Бурятия. ' +
                                                                                        'В настоящее время ведется перенос истории ваших заявок в личный кабинет. ' +
                                                                                        'Доступ к личному кабинету будет предоставлен в ближайшее время.' +
                                                                                        'Сейчас вы можете актуализировать заявку по адресу http://rabota.govrb.ru/actualize_form';
                                                                                    webix.alert({
                                                                                        title: "ВНИМАНИЕ!",
                                                                                        ok: "Актуализировать",
                                                                                        text: message
                                                                                    }).then(function () {
                                                                                        window.location.replace('http://rabota.govrb.ru/actualize_form');
                                                                                    });;
                                                                                } else if (result !== 'ИНН не зарегистрирован') {
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
                                                                    view: 'textarea',
                                                                    height: 75,
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
                                                                                    next(2, $$('organizationEmail').getValue());
                                                                                } else if (text === 'Не удалось отправить письмо') {
                                                                                    webix.message('Не удалось отправить ссылку для активации на указанный адрес электронной почты', 'error');
                                                                                }else {
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
                                                },
                                                {
                                                    rows:[
                                                        {
                                                            view: 'text',
                                                            id: 'orgEmailAddress',
                                                            name: 'orgEmailAddress',
                                                            minWidth: 250,
                                                            label: 'Ваш адрес электронной почты',
                                                            labelPosition: 'top',
                                                            readonly: true,
                                                        },
                                                        {
                                                            view: 'button',
                                                            css: 'myClass',
                                                            value: 'Авторизоваться',
                                                            gravity: 0.5,
                                                            click: () => {
                                                                window.location.href = "/login"
                                                            }
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
    }
    $$("wizard").back();
    $$("step").setValue(`<span style="font-size: 1rem; color: #fff6f6">Шаг 1</span>`)
    $$("description").setHTML(descrStep1)

}

function next(page, mail) {
    if(page === 1){
        if (document.body.clientWidth < 480) {
            $$('topSpacer').config.gravity = 0;$$('topSpacer').resize()
            $$("titleReg").config.height = 31; $$("titleReg").resize();
            $$("appNameReg").config.height =  27; $$("appNameReg").resize();
        }

        $$("step").setValue(`<span style="font-size: 1rem; color: #fff6f6">Шаг 2</span>`)
        $$("description").setHTML(descrStep2)
    }else{
        if (document.body.clientWidth < 480) {
            $$('topSpacer').config.gravity = 0.9;$$('topSpacer').resize()
            $$("titleReg").config.height = 50; $$("titleReg").resize();
            $$("appNameReg").config.height =  50; $$("appNameReg").resize();
        }
        $$("step").setValue(`<span style="font-size: 1rem; color: #fff6f6">Шаг 3</span>`)
        $$("description").setHTML('<span style="font-size: 0.8rem; color: #fff6f6">Необходимо активировать учетную запись. На Ваш почтовый ящик \"' + mail + '\" было оправлено письмо с подтверждением</span>')
        $$("orgEmailAddress").setValue(mail)
    }
    $$("wizard").getChildViews()[page].show();
}

webix.ready(function() {
    let clientScreenWidth = document.body.clientWidth;
    if (clientScreenWidth < 760) {
        $$("leftLayout").hide();
        $$("form").config.width = document.body.clientWidth-40;
        $$("titleReg").setValue(`<span style="font-size: 1.5rem; color: #475466">Регистрация на портале</span>`)
        $$("appNameReg").setValue(`<span style="font-size: 1.2rem; color: #475466">"${APPLICATION_NAME}"</span>`)
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