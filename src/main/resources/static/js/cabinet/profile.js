const profile = {
    view: 'form',
    id: 'common_info_form',
    complexData: true,
    elements: [
        view_section('Данные о вашей организации'),
        {

            //type: 'space',
            margin: 5,
            cols: [
                {
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
                            label: 'Юридический адрес',
                            labelPosition: 'top',
                            height: 80,
                            readonly: true,
                            required: true
                        },
                    ]
                },
                {
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
                            }
                        ]
                },
            ]
        },
        view_section("Управление личным кабинетом"),
        {
            cols: [
                {
                    rows: [
                        {
                            view: 'text',
                            name: 'email',
                            minWidth: 200,
                            maxWidth: 350,
                            label: 'Адрес электронной почты',
                            labelPosition: 'top',
                            validate: webix.rules.isEmail,
                            invalidMessage: 'Поле не может быть пустым'
                        },
                        {
                            view: 'text',
                            name: 'phone',
                            minWidth: 200,
                            maxWidth: 350,
                            label: 'Телефон',
                            labelPosition: 'top',
                            invalidMessage: 'Поле не может быть пустым'
                        },
                    ]
                },
                {
                    rows:[
                        {
                            view: 'text',
                            id: 'new_pass',
                            name: 'password',
                            label: 'Новый пароль',
                            labelPosition: 'top',
                            type: 'password',
                            maxWidth: 300
                        },
                        {
                            view: 'text',
                            id: 'retry_pass',
                            label: 'Подтвердите новый пароль',
                            labelPosition: 'top',
                            type: 'password',
                            maxWidth: 300,
                            on: {
                                onChange(newVal, oldVal) {
                                    if (newVal === $$('new_pass').getValue()) {
                                        $$('save_pass').enable();
                                    } else {
                                        $$('save_pass').disable();
                                    }
                                }
                            }
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
                    labelPosition: 'top',
                    type: 'password',
                    required: true,
                    on:{
                        onChange(newVal, oldVal) {
                            if(newVal.length != 0){
                                $$("save_org_data_changes").enable();
                            }
                        }
                    }
                },
                {
                    view: 'button',
                    css: 'webix_primary',
                    id: 'save_org_data_changes',
                    width: 300,
                    value: 'Сохранить изменения',
                    disabled: true,
                    click: () => {
                        let param = $$('current_pass').getValue()
                        webix.ajax().headers({'Content-Type': 'application/json'})
                            .post('check_current_pass', param).then(function (data) {
                                if (data.text() === "Пароли не совпадают") {
                                     webix.message("Введен неверный текущий пароль","error")
                                }
                            });
                    }
                }
            ]
        }
    ],
    url: 'organization'
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