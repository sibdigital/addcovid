webix.i18n.setLocale("ru-RU");

const windowHeight = window.innerHeight

let descrStep1 = '<span style=" height: auto; font-size: 0.8rem; color: #fff6f6">' +
    'Вам необходимо ввести ИНН Вашей организации или ИП. ИНН будет проверен по ЕГРЮЛ и ЕГРИП.' +
    'Для регистрации необходимо, чтобы Ваша организация или ИП были зарегистрированы в ЕГРЮЛ или ЕГРИП.' +
    'Если вы открылись недавно, проверка может показать отсутствие сведений.' +
    'В таком случае рекомендуется начать регистрацию повторно через несколько рабочих дней.' +
    'Если вы являетесь самозанятым, то вам необходимо будет самостоятельно заполнить данные о себе</span>';

let descrStep2 = '<span style="font-size: 0.8rem; color: #fff6f6">' +
    'Вам необходимо ввести Адрес электронной почты Вашей организации и пароль. ' +
    'После регистрации, на указанный адрес электронной почты, будет отправлено письмо ' +
    'со ссылкой на активацию учётной записи.</span>';

let leftLayout = {
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
            template: descrStep1,
        },
    ]

};

let validateUserInputRules = {
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
    },
    "organizationEmail":(value) => {
        if (value.length > 100) {
            $$("organizationEmail").config.invalidMessage = 'Превышена длина электронной почты'
            //webix.message('Превышена длина электронной почты', 'error');
            return false;
        } else {
            let bad_val = value.indexOf("*") > -1
                || value.indexOf("+") > -1
                || value.indexOf('"') > -1;
            if (bad_val == true) {
                $$("organizationEmail").config.invalidMessage = 'Адрес содержит недопустимые символы'
                //webix.message('Недопустимые символы в адресе электронной почты', 'error');
                return false;
            }
        }
        return true
    },
    "organizationPhone":(value) => {
        if (value.length > 100) {
            $$("organizationEmail").config.invalidMessage = "Превышена длина номера телефона"
            //webix.message('Превышена длина номера телефона', 'error');
            return false;
        }
        return true
    }
};

let step1 = {
    rows: [
        {
            view: 'label',
            css: 'erroLabel',
            //height: 19,
            id: 'invalidMessages',
            borderless: true,
            autoheight: true,
            template:"<span style='padding: 2px;text-align: center; font-size: 0.8rem; color: red'></span>"
        },
        {
            view: 'text',
            name: 'searchInn',
            id: 'searchInn',
            minWidth: 250,
            labelPosition: 'top',
            label: 'Введите ИНН вашей организации',
            placeholder: 'ИНН',
        },
        {
            cols: [
                {
                    view: 'button',
                    css: 'myClass',
                    value: 'Отмена',
                    click: () => { window.location.href = 'login' }
                },
                {width: 5},
                {
                    view: 'button',
                    id: 'egrul_load_button',
                    css: 'myClass',
                    value: 'Продолжить',
                    align: 'center',
                    click: searchByInn
                },
            ]
        }
    ]
};

let step2 = {
    rows: [
        {
            rows: [
                {
                    view: 'label',
                    height: 19,
                    id: 'invalidMessagesStep2',
                    template:"<span style='padding: 2px;text-align: center; font-size: 0.8rem; color: red'></span>"
                },
                {
                    view: 'text',
                    id: 'egrulId',
                    name: 'egrulId',
                    hidden: true,
                },
                {
                    view: 'text',
                    id: 'egripId',
                    name: 'egripId',
                    hidden: true,
                },
                {
                    view: 'text',
                    id: 'filialId',
                    name: 'filialId',
                    hidden: true,
                },
                {
                    view: 'text',
                    id: 'organizationType',
                    name: 'organizationType',
                    hidden: true,
                },
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
                    view: 'checkbox',
                    id: 'detached',
                    labelPosition: 'top',
                    labelRight: 'Обособленное подразделение',
                    value: false,
                    hidden: true,
                    on: {
                        onChange(newVal, oldVal) {
                            if (newVal === 1) {
                                $$('selectOrg').setValue('');
                                $$('selectOrg').hide();
                                $$('organizationShortName').define('readonly', false);
                                $$('organizationShortName').refresh();
                                $$('organizationShortName').show();
                                $$('organizationShortName').define('readonly', false);
                                $$('organizationType').setValue('6');
                            } else {
                                $$('selectOrg').setValue('');
                                $$('selectOrg').show();
                                $$('organizationShortName').define('readonly', true);
                                $$('organizationShortName').refresh();
                                $$('organizationShortName').hide();
                                $$('organizationShortName').define('readonly', true);
                                $$('organizationType').setValue('');
                            }
                            $$('organizationShortName').refresh();
                        }
                    }
                },
                {
                    view: 'richselect',
                    id: 'selectOrg',
                    label: 'Выберите организацию',
                    labelPosition: 'top',
                    hidden: true,
                    required: true,
                    on: {
                        onChange(newVal, oldVal) {
                            if (newVal && newVal !== oldVal) {
                                const data = organizations.find(org => org.id === newVal).data;
                                $$('egrulId').setValue(data.id);
                                $$('filialId').setValue(data.filialId);
                                $$('organizationType').setValue(data.type);
                                $$('organizationName').setValue(data.name);
                                $$('organizationShortName').setValue(data.shortName);
                                $$('organizationOgrn').setValue(data.ogrn);
                                $$('organizationKpp').setValue(data.kpp);
                                $$('organizationAddressJur').setValue(data.jurAddress);
                            }
                        }
                    }
                },
                {
                    view: 'richselect',
                    id: 'selectIP',
                    label: 'Выберите ИП/КФХ',
                    labelPosition: 'top',
                    hidden: true,
                    required: true,
                    on: {
                        onChange(newVal, oldVal) {
                            if (newVal && newVal !== oldVal) {
                                const data = organizations.find(org => org.id === newVal).data;
                                $$('egripId').setValue(data.id);
                                $$('organizationType').setValue(data.type);
                                $$('organizationName').setValue(data.name);
                                $$('organizationShortName').setValue(data.name);
                                $$('organizationOgrn').setValue(data.ogrn);
                                $$('organizationAddressJur').setValue(data.jurAddress);
                            }
                        }
                    }
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
                    readonly: true,
                    required: true
                },
                {
                    view: 'text',
                    id: 'organizationKpp',
                    name: 'organizationKpp',
                    label: 'КПП',
                    // validate: webix.rules.isNumber,
                    labelPosition: 'top',
                    hidden: true,
                    readonly: true,
                    // required: true
                },
                {
                    view: 'textarea',
                    height: 150,
                    id: 'organizationAddressJur',
                    name: 'organizationAddressJur',
                    label: 'Юридический адрес',
                    labelPosition: 'top',
                    hidden: true,
                    readonly: true,
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
                }
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
                    click: registrate
                }
            ]
        },
        {
            height: 19
        }
    ]
};

let rightLayout = {
    id: 'form',
    view: 'form',
    maxWidth: 450,
    width: 350,
    minWidth: 250,
    complexData: true,
    rules: validateUserInputRules,
    elements: [
        {},
        {
            id: "firstRow", rows:[]
        },
        {
            view: 'multiview',
            id: 'wizard',
            cells: [
                step1,
                step2,
                {
                    view: 'template',
                    id: 'descriptionStep3',
                    borderless: true,
                    autoheight: true,
                    template: descrStep1,
                }
            ]
        },
        {}
    ]
};

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
                                leftLayout,
                                rightLayout,
                            ]
                        }
                    },
                ]
            }
        },
        {}

    ]
})

let organizations;

function loadData(type, inn) {
    webix.ajax(type + '?inn=' + inn).then(function (data) {
        const response = data.json();
        var doNext = false;
        if (type === 'egrul') {
            if (response.finded == false){
                $$("invalidMessages").setValue('Введеный ИНН не найден в ЕГРЮЛ. \nВозможно, вы ввели неправильный ИНН.');
            } else {
                $$('organizationOgrn').show();
                $$('organizationKpp').show();
                $$('detached').show();
                const result = response.data;
                if (result.filials && result.filials.length > 0) {
                    $$('organizationShortName').hide();
                    organizations = [];
                    organizations.push({id: result.id + '', data: result});
                    const filials = result.filials.map(filial => {
                        return {id: filial.id + '_' + filial.filialId, data: filial}
                    })
                    organizations = organizations.concat(filials);
                    $$('selectOrg').define('options', {
                        body: {
                            template: '<div style="line-height: 1em">#data.shortName#</div><div style="font-size: 0.65em; line-height: 1em">#data.jurAddress#</div>',
                            data: organizations,
                            type: {
                                height: 80,
                            }
                        },
                    });
                    $$('selectOrg').refresh();
                    $$('selectOrg').show();
                    $$('selectIP').hide();
                    $$('form').setValues({
                        egrulId: result.id,
                        egripId: '',
                        filialId: '',
                        searchInn: inn,
                        isSelfEmployed: false,
                        organizationInn: inn,
                        organizationShortName: result.shortName,
                        organizationOgrn: result.ogrn,
                        organizationKpp: result.kpp,
                    });
                } else {
                    $$('organizationShortName').define('label', 'Наименование организации');
                    var osnIsReadonly = (result.shortName ? result.shortName : result.name) != '';
                    $$('organizationShortName').define('readonly', osnIsReadonly);
                    $$('organizationShortName').refresh();
                    $$('organizationShortName').show();

                    var osnIsReadonly = result.kpp != '';
                    $$('organizationKpp').define('readonly', osnIsReadonly);
                    $$('organizationKpp').refresh();
                    $$('organizationKpp').show();

                    $$('selectOrg').hide();
                    $$('form').setValues({
                        egrulId: result.id,
                        egripId: '',
                        filialId: '',
                        organizationType: result.type,
                        searchInn: inn,
                        isSelfEmployed: false,
                        organizationName: result.name,
                        organizationShortName: (result.shortName ? result.shortName : result.name),
                        organizationInn: result.inn,
                        organizationOgrn: result.ogrn,
                        organizationKpp: result.kpp,
                        organizationAddressJur: result.jurAddress,
                        organizationEmail: result.email
                    });
                }
                doNext = true;
            }
        } else { //egrip
            if (response.finded == true){
                $$('organizationOgrn').show();
                $$('organizationKpp').hide();
                $$('detached').hide();
                const result = response.data;
                if (result.length > 1) {
                    $$('organizationShortName').hide();
                    organizations = result.map(egrip => {
                        return {id: egrip.id, data: egrip}
                    })
                    $$('selectIP').define('options', {
                        body: {
                            template: '<div style="line-height: 1em">#data.name#</div><div style="font-size: 0.65em; line-height: 1em">#data.jurAddress#</div>',
                            data: organizations,
                            type: {
                                height: 65
                            }
                        },
                    });
                    $$('selectOrg').hide();
                    $$('selectIP').refresh();
                    $$('selectIP').show();
                    $$('form').setValues({
                        egrulId: '',
                        filialId: '',
                        searchInn: inn,
                        isSelfEmployed: false,
                        organizationInn: inn,
                        organizationKpp: '',
                    });
                } else {
                    $$('organizationShortName').define('label', 'Наименование ИП');
                    var osnIsReadonly = result[0].name != '';
                    $$('organizationShortName').define('readonly', osnIsReadonly);
                    $$('organizationShortName').refresh();
                    $$('organizationShortName').show();
                    $$('selectOrg').hide();
                    $$('selectIP').hide();
                    $$('form').setValues({
                        egrulId: '',
                        egripId: result[0].id,
                        filialId: '',
                        organizationType: result[0].type,
                        searchInn: inn,
                        isSelfEmployed: false,
                        organizationName: result[0].name,
                        organizationShortName: result[0].name,
                        organizationInn: result[0].inn,
                        organizationOgrn: result[0].ogrn,
                        organizationKpp: '',
                        organizationAddressJur: result[0].jurAddress,
                        organizationEmail: result[0].email
                    });
                }
                doNext = true;
            }else if (response.possiblySelfEmployed == true){
                $$('organizationShortName').define('label', 'ФИО cамозанятого');
                $$('organizationShortName').define('readonly', false);
                $$('organizationShortName').refresh();
                $$('organizationShortName').show();
                $$('selectOrg').hide();
                $$('selectIP').hide();
                $$('organizationOgrn').hide();
                $$('organizationKpp').hide();
                $$('detached').hide();
                $$('form').setValues({
                    egrulId: '',
                    egripId: '',
                    filialId: '',
                    organizationType: '3',
                    searchInn: inn,
                    isSelfEmployed: true,
                    organizationName: '',
                    organizationShortName: '',
                    organizationInn: inn,
                    organizationOgrn: '',
                    organizationKpp: '',
                    organizationAddressJur: '',
                    organizationEmail: ''
                });
                doNext = true;
            } else{
                $$("invalidMessages").setValue('Введеный ИНН не найден в ЕГРИП. \nВозможно, вы ввели неправильный ИНН.');
            }
        }
        $$('searchInn').hideProgress();
        if (doNext){
            next(1);
        }
    }).catch(function (reason) {
        console.log(reason);
        $$('searchInn').hideProgress();
        $$("invalidMessages").setValue('Не удалось получить данные', 'error');
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
        $$('invalidMessagesStep2').setValue('');
        $$("description").setHTML(descrStep2)
    }else{
        if (document.body.clientWidth < 480) {
            $$('topSpacer').config.gravity = 0.9;$$('topSpacer').resize()
            $$("titleReg").config.height = 50; $$("titleReg").resize();
            $$("appNameReg").config.height =  50; $$("appNameReg").resize();
        }
        $$("step").setValue(`<span style="font-size: 1rem; color: #fff6f6">Шаг 3</span>`)
        $$("descriptionStep3").setHTML('<span style="font-size: 1rem;text-align: center">Для завершения регистрации активируйте учетную запись. На Ваш почтовый ящик \"' + mail + '\" оправлена ссылка для активации</span>')
        $$("description").hide()
    }
    $$("wizard").getChildViews()[page].show();

}

function registrate() {
    $$('send_btn').disable();

    $$("invalidMessagesStep2").config.height = 35;
    $$("invalidMessagesStep2").resize()

    if ($$('form').validate()) {

        let params = $$('form').getValues();
        params.organizationInn = params.organizationInn.trim();
        params.organizationOgrn = params.organizationOgrn.trim();

        webix.ajax()
            .headers({'Content-type': 'application/json'})
            .post('registration', JSON.stringify(params)
            ).then(function (data) {
            const text = data.text();
            // webix.message(text === 'Ок' ? 'Письмо отправлено на вашу почту' : text)
            if (text === 'Ок') {
                next(2, $$('organizationEmail').getValue());
            } else if (text === 'Не удалось отправить письмо') {
                $$("invalidMessagesStep2").setValue('Не удалось отправить ссылку для активации на указанный адрес электронной почты')
                //webix.message('Не удалось отправить ссылку для активации на указанный адрес электронной почты', 'error');
            } else {
                $$("invalidMessagesStep2").setValue(text)
                //webix.message(text, 'error')
            }
            $$('send_btn').enable();
        })
    } else {
        $$("invalidMessagesStep2").setValue('Не заполнены обязательные поля');
        //webix.message('Не заполнены обязательные поля', 'error')
        $$('send_btn').enable();
    }
}

function searchByInn(){
    $$('egrul_load_button').disable();
    $$('searchInn').showProgress();

    setTimeout(function () {
        const inn = $$('searchInn').getValue();
        if (inn === '') {
            $$('searchInn').focus();
            $$("invalidMessages").setValue("ИНН не введен");
            $$('searchInn').hideProgress();
            $$('egrul_load_button').enable();
            return;
        } else if (isNaN(inn)) {
            $$('searchInn').focus();
            $$("invalidMessages").setValue("ИНН не соответствует формату");
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
            $$("invalidMessages").setValue("ИНН не соответствует формату");
            $$('searchInn').hideProgress();
            $$('egrul_load_button').enable();
            return;
        }

        loadData(type, inn);

        $$('egrul_load_button').enable();
    }, 500);

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