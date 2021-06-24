webix.i18n.setLocale("ru-RU");

const windowHeight = window.innerHeight

const descrStep1 = '<span style=" height: auto; font-size: 0.8rem; color: #fff6f6">' +
    'Для восстановления доступа к Вашему личному кабинету, Вам необходимо ввести ИНН.' +
    '</span>';

const descrStep2 = '<span style="font-size: 0.8rem; color: #fff6f6">' +
    'Вам необходимо ввести адрес электронной почты, который привязан к учетной записи.' +
    '</span>';

const leftLayout = {
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
            label: `<span style="font-size: 1.5rem; color: #ccd7e6">Восстановление доступа</span>`,
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
}

const step1 = {
    rows: [
        {
            view: 'label',
            css: 'errorLabel',
            //height: 19,
            id: 'invalidMessagesStep1',
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
                    id: 'next_button',
                    css: 'myClass',
                    value: 'Продолжить',
                    align: 'center',
                    click: checkInn,
                },
            ]
        }
    ]
};

const step2 = {
    rows: [
        {
            view: 'label',
            css: 'errorLabel',
            //height: 19,
            id: 'invalidMessagesStep2',
            borderless: true,
            autoheight: true,
            template:"<span style='padding: 2px;text-align: center; font-size: 0.8rem; color: red'></span>"
        },
        {
            view: 'text',
            id: 'email',
            name: 'email',
            label: 'Введите эл. почту указанную при регистрации',
            labelPosition: 'top',
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
                    value: 'Отправить пароль',
                    align: 'center',
                    click: recovery
                }
            ]
        },
    ]
};

const rightLayout = {
    id: 'form',
    view: 'form',
    maxWidth: 450,
    width: 350,
    minWidth: 250,
    complexData: true,
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
                    template: '',
                }
            ]
        },
        {}
    ]
}

const regLayout = webix.ui({
    height: windowHeight,
    css: {"background-color": "#ccd7e6"},
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
                    }
                ]
            }
        },
        {}
    ]
})

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
        $$("descriptionStep3").setHTML('<span style="font-size: 1rem;text-align: center">На Ваш почтовый ящик \"' + mail + '\" оправлен новый пароль.</span>')
        $$("description").hide()
    }
    $$("wizard").getChildViews()[page].show();

}

function checkInn() {
    const inn = $$('searchInn').getValue();
    if (inn === '') {
        $$('searchInn').focus();
        $$("invalidMessagesStep1").setValue("ИНН не введен");
        $$('searchInn').hideProgress();
        $$('next_button').enable();
        return;
    } else if (isNaN(inn)) {
        $$('searchInn').focus();
        $$("invalidMessagesStep1").setValue("ИНН не соответствует формату");
        $$('searchInn').hideProgress();
        $$('next_button').enable();
        return;
    }

    $$('next_button').enable();
    next(1);
}

function recovery() {
    $$('send_btn').disable();

    const email = $$('email').getValue();
    if (email === '') {
        $$('email').focus();
        $$("invalidMessagesStep2").setValue('Адрес электронной почты не введен');
        $$('send_btn').enable();
        return;
    } else if (!webix.rules.isEmail(email)) {
        $$('email').focus();
        $$("invalidMessagesStep2").setValue('Адрес электронной почты содержит ошибку');
        $$('send_btn').enable();
        return;
    }

    let params = $$('form').getValues();
    params.organizationInn = $$('searchInn').getValue();
    params.organizationEmail = $$('email').getValue();

    webix.ajax()
        .headers({'Content-type': 'application/json'})
        .post('recovery', JSON.stringify(params))
        .then(function (data) {
            const text = data.text();
            webix.message(text === 'Ок' ? 'Письмо отправлено на вашу почту' : text);
            if (text === 'Ок') {
                next(2, email);
            } else if (text === 'Не удалось отправить письмо') {
                $$("invalidMessagesStep2").config.height = 35;
                $$("invalidMessagesStep2").resize()
                $$("invalidMessagesStep2").setValue('Не удалось отправить пароль на указанный адрес электронной почты')
            } else {
                $$("invalidMessagesStep2").setValue(text)
            }
            $$('send_btn').enable();
        })
        .catch(function () {
            $$("invalidMessagesStep2").setValue('Не удалось отправить письмо');
            $$('send_btn').enable();
        })
}

webix.ready(function() {
    let clientScreenWidth = document.body.clientWidth;
    if (clientScreenWidth < 760) {
        $$("leftLayout").hide();
        $$("form").config.width = document.body.clientWidth-40;
        $$("titleReg").setValue(`<span style="font-size: 1.1rem; color: #475466">Восстановление доступа</span>`)
        $$("appNameReg").setValue(`<span style="font-size: 1.1rem; color: #475466">"${APPLICATION_NAME}"</span>`)
        $$("firstRow").addView($$("titleReg"),-1);
        $$("firstRow").addView($$("appNameReg"),-1);
        $$("firstRow").addView($$("description"),-1);
        $$("form").adjust();
        $$("form").resize();
    }
    webix.extend($$('searchInn'), webix.ProgressBar);
    if (document.body.clientWidth < 480){
        regLayout.config.width = document.body.clientWidth; regLayout.resize();
    }
})
