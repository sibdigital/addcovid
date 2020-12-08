const commonInfo = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                view: 'form',
                id: 'form',
                complexData: true,
                elements: [
                    view_section('Данные о вашей организации'),
                    {
                        type: 'space',
                        margin: 5,
                        rows: [
                            {
                                view: 'text',
                                name: 'name',
                                id: 'organizationName',
                                label: 'Полное наименование организации/фамилия, имя, отчество индивидуального предпринимателя',
                                labelPosition: 'top',
                                invalidMessage: 'Поле не может быть пустым',
                                required: true
                            },
                            {
                                view: 'text',
                                name: 'shortName',
                                id: 'shortOrganizationName',
                                label: 'Краткое наименование организации',
                                labelPosition: 'top',
                                invalidMessage: 'Поле не может быть пустым',
                                required: true
                            },
                            {
                                id:"innplace",
                                rows:[]
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
                                        required: true
                                    },
                                ]
                            },

                            {
                                rows:
                                    [
                                        {
                                            height: 30,
                                            view: 'label',
                                            label: 'Основной вид осуществляемой деятельности (отрасль)',
                                        },
                                        {
                                            view: 'list',
                                            layout: 'x',
                                            css:{'white-space':'normal !important;'},
                                            height: 50,
                                            template: '#kindCode# - #kindName#',
                                            url: 'reg_organization_okved', //<span class="mdi mdi-close"></span>
                                            type:{
                                                css: "chip",
                                                height:'auto'
                                            },
                                        },
                                    ]
                            },
                            {
                                rows:
                                    [
                                        {
                                            height: 30,
                                            view: 'label',
                                            label: 'Дополнительные виды осуществляемой деятельности',
                                        },
                                        {
                                            view: "list",
                                            layout: 'x',
                                            css:{'white-space':'normal !important;'},
                                            height: 150,
                                            template: '#kindCode# - #kindName#',
                                            url: "reg_organization_okved_add",
                                            type:{
                                                css: "chip",
                                                height:'auto'
                                            },
                                        }
                                    ]
                            },
                            {
                                view: 'textarea',
                                name: 'addressJur',
                                label: 'Юридический адрес',
                                labelPosition: 'top',
                                height: 80,
                                required: true
                            },
                            {
                                id:"emailPlace",
                                rows:[]
                            },
                            {
                                responsive: "emailPlace",
                                cols: [
                                    {
                                        view: 'text',
                                        name: 'email',
                                        minWidth: 200,
                                        label: 'Адрес электронной почты',
                                        labelPosition: 'top',
                                        validate: webix.rules.isEmail,
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true
                                    },
                                    {
                                        view: 'text',
                                        name: 'phone',
                                        minWidth: 200,
                                        label: 'Телефон',
                                        labelPosition: 'top',
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true
                                    },
                                ]
                            },

                        ]
                    }
                ],
                url: 'organization'
            }
        ],
    }
}

function adaptiveCommonInfo(){

    $$("organizationName").config.label = "Наим. орг./ФИО ИП";$$("organizationName").refresh();
    $$("shortOrganizationName").config.label = "Краткое наим. орг."; $$("shortOrganizationName").refresh();

}