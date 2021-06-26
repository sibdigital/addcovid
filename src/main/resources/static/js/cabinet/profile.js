let profileRules = {
    "new_pass":(val) => {
        if(val.length !== 0 ){
            if(val.length < 8){
                $$("new_pass").config.invalidMessage = "Длина пароля должна превышать 7 символов";
                return false
            }else if(!(/[0-9]/.test(val) && /[a-z]/i.test(val))){
                $$("new_pass").config.invalidMessage = "Пароль должен содержать буквы и цифры";
                return false
            }
        }
        return true
    },
    "retry_pass":(val) => {
        if(val === $$("new_pass").getValue()){
            return true;
        }else {
            $$("retry_pass").config.invalidMessage = "Пароли не совпадают";
            return false;
        }
    },
    "email":(val) => {
        if (val.length > 100) {
            $$("email").config.invalidMessage = 'Превышена длина электронной почты'
            //webix.message('Превышена длина электронной почты', 'error');
            return false;
        } else {
            let bad_val = val.indexOf("*") > -1
                || val.indexOf("+") > -1
                || val.indexOf('"') > -1;
            if (bad_val == true) {
                $$("email").config.invalidMessage = 'Адрес содержит недопустимые символы'
                //webix.message('Недопустимые символы в адресе электронной почты', 'error');
                return false;
            }
        }
        return true
    },
    "phone":(val) => {
        if (val.length > 100) {
            $$("phone").config.invalidMessage = "Превышена длина номера телефона"
            //webix.message('Превышена длина номера телефона', 'error');
            return false;
        }
        return true
    },
}

const profile = {
    view: 'form',
    id: 'common_info_form',
    complexData: true,
    rules: profileRules,
    elements: [
        view_section('Данные о вашей организации'),
        {

            //type: 'space',
            margin: 5,
            responsive: "respLeftToRight",
            cols: [
                {
                    minWidth: 300,
                    rows: [
                        {
                            view: 'text',
                            name: 'shortName',
                            id: 'shortOrganizationName',
                            label: 'Краткое наименование организации',
                            labelPosition: 'top',
                            invalidMessage: 'Поле не может быть пустым',
                            readonly: true,
                            required: true
                        },
                        {
                            view: 'textarea',
                            name: 'name',
                            height: 80,
                            id: 'organizationName',
                            label: 'Полное наименование организации/фамилия, имя, отчество индивидуального предпринимателя',
                            labelPosition: 'top',
                            invalidMessage: 'Поле не может быть пустым',
                            readonly: true,
                            required: true
                        },
                        {
                            id: "innplace",
                            rows: []
                        },
                        {
                            responsive: 'innplace',
                            cols: [
                                {
                                    view: 'text',
                                    name: 'inn',
                                    id: "inn",
                                    label: 'ИНН',
                                    minWidth: 200,
                                    labelPosition: 'top',
                                    validate: function (val) {
                                        return !isNaN(val * 1);
                                    },
                                    //attributes:{ type:"number" },
                                    invalidMessage: 'Поле не может быть пустым',
                                    readonly: true,
                                    required: true
                                },
                                {
                                    view: 'text',
                                    name: 'ogrn',
                                    id: 'ogrn',
                                    label: 'ОГРН',
                                    minWidth: 200,
                                    validate: function (val) {
                                        return !isNaN(val * 1);
                                    },
                                    //attributes:{ type:"number" },
                                    labelPosition: 'top',
                                    //validate:webix.rules.isNumber(),
                                    invalidMessage: 'Поле не может быть пустым',
                                    readonly: true,
                                    required: true
                                },
                            ]
                        },
                        {
                            view: 'textarea',
                            name: 'addressJur',
                            id: 'addressJur',
                            label: 'Юридический адрес',
                            labelPosition: 'top',
                            height: 80,
                            readonly: true,
                            required: true
                        },
                        {
                            cols:[
                                {},
                                {
                                    view: "richselect",
                                    id: "organizationTypeCombo",
                                    name: "idTypeOrganization",
                                    label: "Тип организации",
                                    labelWidth: 130,
                                    options: {
                                        body: {
                                            on: {
                                                onItemClick:(val) => {
                                                    let prevValue = $$('organizationTypeCombo').getValue();
                                                    webix.confirm({
                                                        title: "Изменение типа организации",
                                                        ok: "Да", cancel: "Нет",
                                                        text: "Вы уверены что хотите изменить тип организации?"
                                                    }).then(() => {
                                                        webix.ajax().get("update_org_type",{ "type": val }).then((answer) => {
                                                            if(answer.json() != null){
                                                                webix.message("Тип успешно изменён","success");
                                                            } else {
                                                                webix.message("Не удалось изменить тип", "error");
                                                            }
                                                        })
                                                    }).fail(() => {
                                                        $$('organizationTypeCombo').setValue(prevValue);
                                                    })
                                                }
                                            }
                                        }
                                    },

                                }
                            ]
                        }
                    ]
                },
                {
                    minWidth: 300,
                    id: "respLeftToRight",
                    rows:
                        [
                            {
                                height: 27,
                                view: 'label',
                                label: 'Основной вид осуществляемой деятельности (отрасль)',
                            },
                            {
                                view: 'list',
                                layout: 'x',
                                id:"okved_main",
                                css: {'white-space': 'normal !important;'},
                                height: 50,
                                template: '#kindCode# - #kindName#',
                                url: 'reg_organization_okved', //<span class="mdi mdi-close"></span>
                                type: {
                                    css: "chip",
                                    height: 'auto'
                                },
                            },
                            {
                                height: 26,
                                view: 'label',
                                label: 'Дополнительные виды осуществляемой деятельности',
                            },
                            {
                                view: "list",
                                layout: 'x',
                                id: 'okveds_add',
                                css: {'white-space': 'normal !important;'},
                                height: 170,
                                template: '#kindCode# - #kindName#',
                                url: "reg_organization_okved_add",
                                type: {
                                    css: "chip",
                                    height: 'auto'
                                },
                            },
                            {
                                cols: [
                                    {},
                                    {
                                        view: 'button',
                                        id: 'updByEgrulBtn',
                                        css: 'webix_primary',
                                        width: 270,
                                        value: 'Обновить по ЕГРЮЛ/ЕГРИП',
                                        click:() => {
                                            let inn = $$('inn').getValue();
                                            let param = {"inn":inn}
                                            webix.ajax().get('update_org_by_egrul',param).then((answer) => {
                                                let data = answer.json();
                                                console.log(data);
                                                if(data !== null) {
                                                    webix.message("Данные успешно обновлены","success");
                                                    $$('common_info_form').parse(data);
                                                } else {
                                                    webix.message("По данному ИНН не найдено юр. лицо","error");
                                                }
                                            })
                                        }

                                    }
                                ]
                            }
                        ]
                },
            ]
        },
        view_section("Управление личным кабинетом"),
        {
            responsive: "managmentInfoToRightSide",
            cols: [
                {
                    minWidth: 200,
                    rows: [
                        {
                            view: 'text',
                            id: 'email',
                            name: 'email',
                            minWidth: 200,
                            maxWidth: 300,
                            label: 'Адрес электронной почты',
                            labelPosition: 'top'
                        },
                        {
                            view: 'text',
                            id: 'phone',
                            name: 'phone',
                            minWidth: 200,
                            maxWidth: 300,
                            label: 'Телефон',
                            labelPosition: 'top',
                            invalidMessage: 'Поле не может быть пустым'
                        },
                    ]
                },
                {
                    id:"managmentInfoToRightSide",
                    minWidth: 200,
                    rows:[
                        {
                            view: 'text',
                            id: 'new_pass',
                            name: 'new_pass',
                            label: 'Новый пароль',
                            labelPosition: 'top',
                            type: 'password',
                            maxWidth: 300
                        },
                        {
                            view: 'text',
                            id: 'retry_pass',
                            name: 'retry_pass',
                            label: 'Подтвердите новый пароль',
                            labelPosition: 'top',
                            type: 'password',
                            maxWidth: 300
                        },
                    ]
                }
            ]
        },
        {
            rows:[
                {
                    view: 'text',
                    id: 'current_pass',
                    name: 'password',
                    width: 300,
                    label: 'Введите текущий пароль',
                    tooltip: "После ввода пароля нажмите Enter",
                    labelPosition: 'top',
                    type: 'password',
                    required: true,
                    on:{
                        onEnter() {
                            let param = $$('current_pass').getValue()
                            webix.ajax().headers({'Content-Type': 'application/json'})
                                .post('check_current_pass', param).then(function (data) {
                                    if (data.text() === "Пароли не совпадают") {
                                        $$("save_org_data_changes").disable();
                                        $$("invalidMessages").setValue("Введен неверный текущий пароль")
                                    }else{
                                        $$("invalidMessages").setValue("");
                                        $$("shortOrganizationName").config.readonly = false;
                                        $$("organizationName").config.readonly = false;
                                        $$("addressJur").config.readonly = false;
                                        $$("shortOrganizationName").refresh();
                                        $$("organizationName").refresh();
                                        $$("addressJur").refresh();

                                        $$("save_org_data_changes").enable();
                                    }
                                });
                        }
                    }
                },
                {
                    view: 'label',
                    css: 'errorLabel',
                    height: 19,
                    id: 'invalidMessages',
                    borderless: true,
                    autoheight: true,
                    template:"<span style='padding: 2px;text-align: center; font-size: 0.8rem; color: red'></span>"
                },
                {
                    view: 'button',
                    css: 'webix_primary',
                    id: 'save_org_data_changes',
                    width: 300,
                    value: 'Сохранить изменения',
                    disabled: true,
                    click: () => {
                        if($$("common_info_form").validate()){
                            let params = {
                                "organizationEmail": $$("email").getValue(),
                                "organizationPhone": $$("phone").getValue(),
                                "shortOrganizationName": $$("shortOrganizationName").getValue(),
                                "organizationName": $$("organizationName").getValue(),
                                "addressJur": $$("addressJur").getValue(),
                                "newPass": $$("retry_pass").getValue()
                            }
                            webix.ajax().headers({'Content-Type': 'application/json'})
                                .post('edit_common_info', params).then(function (response) {
                                    if(response.json().status == "server"){
                                        webix.message(response.json().cause,"success")
                                    }else{
                                        webix.message("Не удалось обновить данные","error")
                                    }
                                });
                        }
                    }
                }
            ]
        }
    ],
    url:() => {
        webix.ajax().get("org_types").then((data) => {
            data = data.json();
            let selects = [
                {id: data[0], value: "Самозанятый"},
                {id: data[1], value: "ИП"},
                {id: data[2], value: "КФХ"}
            ]
            $$('organizationTypeCombo').getPopup().getList().parse(selects);
            $$('organizationTypeCombo').refresh();
            console.log(list);
        })
        return webix.ajax().get("organization").then((data) => {
            console.log(data.json());
            let typeOrg = data.json().idTypeOrganization;
            console.log(typeOrg)
            // if(typeOrg !== 3 && typeOrg !== 7 && typeOrg !== 8){
            //     $$('organizationTypeCombo').hide();
            // } else {
            //     $$('updByEgrulBtn').hide();
            // }
            return data;
        })
    }
}


function adaptiveCommonInfo() {

    $$("organizationName").config.label = "Наим. орг./ФИО ИП";
    $$("organizationName").refresh();
    $$("shortOrganizationName").config.label = "Краткое наим. орг.";
    $$("shortOrganizationName").refresh();
    $$("okveds_add").config.height = 201;
    $$("okveds_add").resize();
    $$("okved_main").config.height = 77;
    $$("okved_main").resize();

}