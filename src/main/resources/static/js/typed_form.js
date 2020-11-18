webix.i18n.setLocale("ru-RU");

let flag = 0

function view_section(title){
    return {
        view: 'template',
        type: 'section',
        template: title
    }
}

function addPerson(){
    let values = $$('form_person').getValues()
    if(values.lastname == '' || values.firstname == ''){
        webix.message('Фамилия, Имя - обязательные поля')
        return;
    }

    if(values.lastname.length > 100 || values.firstname.length > 100 || values.patronymic.length > 100 ){
        webix.message('Фамилия, имя или отчество - длиннее 100 знаков')
        return;
    }

    $$('person_table').add({
        lastname: values.lastname.trim(),
        firstname: values.firstname.trim(),
        patronymic: values.patronymic.trim(),
        //isagree: values.isagree
    }, $$('person_table').count() + 1)

    let is_no_pdf = $$('no_pdf').getValue() == upload_chack_error;
    if ($$('isAgree').getValue() == 1 && $$('isProtect').getValue() == 1 && !is_no_pdf){
        $$('send_btn').enable();
    }else{
        $$('send_btn').disable();
    }

    $$('clearPersonsBtn').enable();

    $$('form_person').clear()
}

function editPerson(){
    let values = $$('form_person').getValues()
    if(values.lastname == '' || values.firstname == '') {
        webix.message('Фамилия, Имя - обязательные поля')
        return;
    }

    if(values.lastname.length > 100 || values.firstname.length > 100 || values.patronymic.length > 100 ){
        webix.message('Фамилия, имя или отчество - длиннее 100 знаков')
        return;
    }

    $$('form_person').save()
}

function removePerson(){
    if(!$$("person_table").getSelectedId()){
        webix.message("Ничего не выбрано!");
        return;
    }
    webix.confirm('Вы действительно хотите удалить выбранную запись?')
        .then(
            function () {
                $$("person_table").remove($$("person_table").getSelectedId());
                let cnt = $$('person_table').data.count();
                let is_no_pdf = $$('no_pdf').getValue() == upload_chack_error;

                if(cnt>0){
                    $$('clearPersonsBtn').enable();
                }else {
                    $$('clearPersonsBtn').disable();
                }

                if ($$('isAgree').getValue() == 1 && $$('isProtect').getValue() == 1 && cnt > 0 && !is_no_pdf){
                    $$('send_btn').enable();
                }else{
                    $$('send_btn').disable();
                }
            }
        )
}

function clearPersons(){
    webix.confirm('Вы действительно хотите очистить данные о ваших работниках?')
        .then(
            function () {
                $$("person_table").clearAll();
                $$('send_btn').disable();
                $$('clearPersonsBtn').disable();
            }
        )
}

function addAddr(){
    let values = $$('form_addr').getValues()
    if(values.addressFact == '' || values.personOfficeFactCnt == ''){
        webix.message('не заполнены обязательные поля')
        return;
    }
    if(values.addressFact.length > 255 ){
        webix.message('Фактический адрес превышает 255 знаков!')
        return;
    }
    if(isNaN(values.personOfficeFactCnt * 1)) {
        webix.message('требуется числовое значение')
        return;
    }

    $$('addr_table').add({
        personOfficeFactCnt: values.personOfficeFactCnt,
        addressFact: values.addressFact,
    }, $$('addr_table').count() + 1)

    $$('form_addr').clear()
}

function editAddr(){
    let values = $$('form_addr').getValues()
    if(values.addressFact == '' || values.personOfficeFactCnt == ''){
        webix.message('обязательные поля')
        return;
    }
    if(values.addressFact == '' || values.personOfficeFactCnt == ''){
        webix.message('не заполнены обязательные поля')
        return;
    }
    if(isNaN(values.personOfficeFactCnt * 1)) {
        webix.message('требуется числовое значение')
        return;
    }

    $$('form_addr').save()
}

function removeAddr(){
    if(!$$("addr_table").getSelectedId()){
        webix.message("Ничего не выбрано!");
        return;
    }
    webix.confirm('Вы действительно хотите удалить выбранную запись?')
        .then(
            function () {
                $$("addr_table").remove($$("addr_table").getSelectedId());
            }
        )
}

let uploadFile = '';
let uploadFilename = '';
let pred_date = new Date();
let upload_chack_error = 'Загружать можно только PDF-файлы и ZIP-архивы!';

let departmentId;

webix.html.addStyle(
    ".typed_form_template .webix_template {" +
    "background-color: #DDDDDD;" +
    "font-size: 15px;" +
    "padding: 0px 3px;" +
    "}"
);

webix.ready(function () {
    let layout = webix.ui({
        container: 'app',
        autowidth: true,
        height: document.body.clientHeight,
        width: document.body.clientWidth - 8,
        rows: [
            {
                view: 'toolbar',
                autoheight: true,
                rows: [
                    {
                        css: 'webix_dark',
                        rows: [
                            {
                                view: 'label',
                                width: 300,
                                label: `<span style="font-size: 1.0rem">${APPLICATION_NAME}. </span>`,
                                // tooltip: ''
                            },
                            {
                                view: 'template',
                                id: 'activityKind',
                                css: {
                                    'background-color':'#3498D8',
                                    'color':'#FFFFFF'
                                },
                                borderless: true,
                                autoheight: true,
                            }
                        ]
                    }
                ]
            },
            {
                view: 'template',
                borderless: true,
                autoheight: true,
                //css: 'typed_form_template',
                template: `<a style="font-size: 1.5rem; text-align: center;" href="${HOT_LINE}" target="_blank">Горячая линия. </a>
                     &nbsp&nbsp&nbsp <a style="font-size: 1.5rem; text-align: center;" href="${FORM_FILL_INSTRUCTION}" target="_blank">Инструкция по заполнению формы </a>
                     &nbsp&nbsp&nbsp <a style="font-size: 1.5rem; text-align: center;" href="${FAQ}" target="_blank">Часто задаваемые вопросы</a>`

            },
            {
                view: 'label',
                label: '<span  style="text-align: center;">Уважаемые пользователи!</span>',
                //css: 'main_label'
            },
            {
                view: 'template',
                css: {"font-size": "15px;","padding":"0px;"},
                borderless: true,
                autoheight: true,
                template: `<span  style="text-align: center;">При подаче заявки на 100 и более человек обязательно используйте шаблон для заполнения!  
                    <a  style="text-align: center;" href="${FAQ}" target="_blank">Скачать шаблон </a>&nbsp&nbsp&nbsp 
                    <a  style="text-align: center;" href="${XLSX_FILL_INSTRUCTION}" target="_blank">Инструкция по заполнению шаблона Excel </a></span>`
                //css: 'main_label'
            },
            {
                view: 'label',
                label: `<span  style="text-align: center;">
                    <a  style="text-align: center;" href="${SUBDOMAIN_FORM}/upload" target="_blank">Форма ввода с шаблоном Excel</a>  </span>`
                //css: 'main_label'
            },
            {
                id: 'form',
                view: 'form',
                complexData: true,
                elements: [
                    view_section('Данные о вашей организации'),
                    {
                        type: "space",
                        margin: 5,
                        id:"org_data_cols",
                        cols: [
                            {
                                id: 'org_data_rows_column1',
                                rows: [
                                            {
                                                view: 'template',
                                                autoheight: true,
                                                css: 'typed_form_template',
                                                borderless: true,
                                                template: 'Полное наименование организации/фамилия, имя, отчество индивидуального предпринимателя <span style="color: red">*</span>'
                                            },
                                            {
                                                view: 'text',
                                                name: 'organizationName',
                                                id: 'organizationName',
                                                invalidMessage: 'Поле не может быть пустым',
                                                required: true
                                            },
                                            {
                                                view: 'template',
                                                autoheight: true,
                                                css: 'typed_form_template',
                                                borderless: true,
                                                template: 'Краткое наименование организации <span style="color: red">*</span>'
                                            },
                                            {
                                                view: 'text',
                                                name: 'organizationShortName',
                                                id: 'organizationShortName',
                                                invalidMessage: 'Поле не может быть пустым',
                                                required: true
                                            },
                                            {
                                                cols: [
                                                    {
                                                        view: 'text',
                                                        name: 'organizationInn',
                                                        id: 'organizationInn',
                                                        label: 'ИНН',
                                                        labelPosition: 'top',
                                                        validate: function (val) {
                                                            return !isNaN(val * 1);
                                                        },
                                                        //attributes:{ type:"number" },
                                                        invalidMessage: 'Поле не может быть пустым',
                                                        required: true
                                                    },
                                                    {
                                                        view: 'checkbox',
                                                        name: 'isSelfEmployed',
                                                        id: 'isSelfEmployed',
                                                        labelPosition: 'top',
                                                        label: 'Самозанятый',
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
                                                    }
                                                ]
                                            },
                                            {
                                                view: 'text',
                                                id: 'organizationOgrn',
                                                name: 'organizationOgrn',
                                                label: 'ОГРН',
                                                validate: function(val){
                                                    return !isNaN(val*1);
                                                },
                                                //attributes:{ type:"number" },
                                                labelPosition: 'top',
                                                //validate:webix.rules.isNumber(),
                                                invalidMessage: 'Поле не может быть пустым',
                                                required: true
                                            },
                                            {
                                                view: 'text',
                                                name: 'organizationEmail',
                                                id: 'organizationEmail',
                                                label: 'e-mail',
                                                labelPosition: 'top',
                                                validate:webix.rules.isEmail,
                                                invalidMessage: 'Поле не может быть пустым',
                                                required: true
                                            },
                                            {
                                                view: 'text',
                                                name: 'organizationPhone',
                                                id: 'organizationPhone',
                                                label: 'Телефон',
                                                labelPosition: 'top',
                                                invalidMessage: 'Поле не может быть пустым',
                                                required: true
                                            },
                                        ]
                            },
                            {
                                id: 'org_data_rows_column2',
                                rows: [
                                            {
                                                view: 'template',
                                                autoheight: true,
                                                css: 'typed_form_template',
                                                borderless: true,
                                                template: 'Основной вид осуществляемой деятельности (отрасль) <span style="color: red">*</span>'
                                            },
                                            {
                                                view: 'text',
                                                name: 'organizationOkved',
                                                id: 'organizationOkved',
                                                required: true
                                            },
                                            {

                                                view: 'template',
                                                autoheight: true,
                                                css: 'typed_form_template',
                                                borderless: true,
                                                template: 'Дополнительные виды осуществляемой деятельности <span style="color: red">*</span>'

                                            },
                                            {
                                                view: 'textarea',
                                                name: 'organizationOkvedAdd',
                                                id: 'organizationOkvedAdd',
                                                height: 100,
                                            },
                                            {
                                                view: 'template',
                                                autoheight: true,
                                                css: 'typed_form_template',
                                                borderless: true,
                                                template: 'Министерство, курирующее вашу деятельность <span style="color: red">*</span>'
                                            },
                                            {
                                                view: 'select',
                                                id: 'departmentId',
                                                name: 'departmentId',
                                                invalidMessage: 'Поле не может быть пустым',
                                                required: true,
                                                options: [],
                                            },
                                            {
                                                view: 'template',
                                                autoheight: true,
                                                css: 'typed_form_template',
                                                borderless: true,
                                                template: '* области деятельности министерств'
                                            },
                                            {
                                                view: 'textarea',
                                                id: 'desc_departments',
                                                height: 150,
                                                readonly: true,
                                            }
                                        ]
                            },

                        ]


                    },

                    view_section('Адресная информация'),
                    {
                        view: 'textarea',
                        name: 'organizationAddressJur',
                        id: 'organizationAddressJur',
                        label: 'Юридический адрес',
                        labelPosition: 'top',
                        height: 80,
                        required: true
                    },
                    {
                        rows: [
                            {
                                view: 'datatable', name: 'addressFact', label: '', labelPosition: 'top',
                                height: 200,
                                select: 'row',
                                editable: true,
                                id: 'addr_table',
                                columns: [
                                    { id: 'index', header: '', css: 'rank'},
                                    {
                                        id: 'addressFact',
                                        header: 'Фактический адрес осуществления деятельности',
                                        width: 300,
                                        //editor: 'text',
                                        //fillspace: true
                                    },
                                    {
                                        id: 'personOfficeFactCnt',
                                        header: 'Численность работников, не подлежащих переводу на дистанционный режим работы, осуществляющих деятельность по указанному в  пункте 11 настоящей формы фактическому адресу',
                                        //editor: 'text',
                                        fillspace: true,
                                        minWidth: 1300
                                        //width: 200
                                    }
                                ],
                                data: [],
                                on:{
                                    'data->onStoreUpdated': function(){
                                        this.data.each(function(obj, i){
                                            obj.index = i + 1;
                                        });
                                    }
                                },
                            },
                            {
                                view: 'form',
                                id: 'form_addr',
                                elements: [
                                    {
                                        type: 'space',
                                        id: 'input_rows',
                                        rows: [
                                            {
                                                responsive: 'input_rows',
                                                cols: [
                                                    {
                                                        view: 'text',
                                                        name: 'addressFact',
                                                        id: 'addressFactId',
                                                        minWidth: 150,
                                                        label: 'Фактический адрес',
                                                        labelPosition: 'top',
                                                        required: true
                                                    },
                                                    {
                                                        view: 'text',
                                                        name: 'personOfficeFactCnt',
                                                        id: 'personOfficeFactCntId',
                                                        minWidth: 150,
                                                        label: 'Численность работников',
                                                        labelPosition: 'top',
                                                        invalidMessage: 'Поле не может быть пустым',
                                                        required: true,
                                                    }
                                                ]
                                            },
                                        ]
                                    },
                                    {
                                        id: 'button_rows',
                                        rows: [
                                            {
                                                //type: 'space',
                                                responsive: 'button_rows',
                                                margin: 5,
                                                cols: [
                                                    {view: 'button', value: 'Добавить', minWidth: 150, click: addAddr},
                                                    {view: 'button', value: 'Изменить', minWidth: 150, click: editAddr},
                                                    {view: 'button', value: 'Удалить', minWidth: 150, click: removeAddr}
                                                ]
                                            }
                                        ],
                                    },
                                ]
                            }
                        ]
                    },
                    view_section('Обоснование заявки'),
                    {
                        rows: [
                            {
                                view: 'textarea',
                                height: 150,
                                label: 'Обоснование заявки',
                                name: 'reqBasis',
                                id: 'reqBasis',
                                invalidMessage: 'Поле не может быть пустым',
                                required: true,
                                labelPosition: 'top'
                            },
                            {
                                view: 'template',
                                borderless: true,
                                autoheight: true,
                                template: '<span  style="text-align: center; color: red">Для загрузки нескольких файлов выбирайте их с зажатой клавишей Ctrl или заранее сожмите в ZIP-архив и загрузите его</span>',
                                //css: 'main_label'
                            },
                            {
                                view: 'template',
                                autoheight: true,
                                borderless: true,
                                template: '<span  style="text-align: center; color: red">Общий размер загружаемых файлов не должен превышать 60 Мб</span>',
                                //css: 'main_label'
                            },
                            {
                                id: 'upload',
                                view: 'uploader',
                                css: 'webix_secondary',
                                autoheight: true,
                                value: 'Загрузить PDF-файл(-ы) или ZIP-архив(-ы)  с пояснением обоснования',
                                autosend: false,
                                upload: '/uploadpart',
                                required: true,
                                accept: 'application/pdf, application/zip',
                                multiple: true,
                                link: 'filelist',
                                on: {
                                    onBeforeFileAdd: function (upload) {
                                        if (upload.type.toUpperCase() !== 'PDF' && upload.type.toUpperCase() !== 'ZIP') {
                                            $$('no_pdf').setValue(upload_chack_error);
                                            $$('file').setValue('');
                                            $$('send_btn').disable();
                                            return false;
                                        }
/*
                                        let reader = new FileReader();
                                        reader.addEventListener("load", function () { // Setting up base64 URL on image
                                            uploadFile = window.btoa(reader.result);
                                            $$('no_pdf').setValue('');
                                            let cnt = $$('person_table').data.count();
                                            if ($$('isAgree').getValue() == 1 && $$('isProtect').getValue() == 1  && cnt > 0){
                                                $$('send_btn').enable();
                                            }else{
                                                $$('send_btn').disable();
                                            }
                                            $$('file').setValue(uploadFilename);
                                        }, false);
                                        reader.readAsBinaryString(upload.file);
                                        uploadFilename = upload.name
*/
                                        //return false;

                                        if($$('file').getValue()){
                                            $$('file').setValue($$('file').getValue() + ',' + upload.name)
                                        }
                                        else {
                                            $$('file').setValue(upload.name)
                                        }
                                        $$('no_pdf').setValue('');
                                        let cnt = $$('person_table').data.count();
                                        if ($$('isAgree').getValue() == 1 && $$('isProtect').getValue() == 1  && cnt > 0){
                                            $$('send_btn').enable();
                                        }else{
                                            $$('send_btn').disable();
                                        }
                                        return true
                                    }
                                }
                            },
                            {
                                view:'list',  id:'filelist', type:'uploader',
                                autoheight: true, borderless: true
                            },
                            {
                                paddingLeft: 10,
                                view: 'label',
                                visible: false,
                                label: '',
                                id: 'no_pdf'
                            }
                        ]
                    },

                    view_section('Данные о численности работников'),
                    {
                        type: 'space',
                        rows: [
                            {
                                view: 'text',
                                name: 'personSlrySaveCnt',
                                id: 'personSlrySaveCnt',
                                label: 'Суммарная численность работников, в отношении которых установлен режим работы нерабочего дня с сохранением заработной платы',
                                labelPosition: 'top',
                                validate: function(val){
                                    return !isNaN(val*1) && (val.trim() !== '')
                                },
                                invalidMessage: 'Поле не может быть пустым',
                                required: false,
                                hidden:true
                            },
                            {
                                view: 'template',
                                autoheight: true,
                                css: 'typed_form_template',
                                borderless: true,
                                template: 'Суммарная численность работников, подлежащих переводу на дистанционный режим работы <span style="color: red">*</span>'
                            },
                            {
                                view: 'text',
                                name: 'personRemoteCnt',
                                id: 'personRemoteCnt',
                                invalidMessage: 'Поле не может быть пустым',
                                validate: function(val){
                                    return !isNaN(val*1) && (val.trim() !== '')
                                },
                                required: true,
                            },
                            {
                                view: 'template',
                                autoheight: true,
                                css: 'typed_form_template',
                                borderless: true,
                                template: 'Суммарная численность работников, не подлежащих переводу на дистанционный режим работы (посещающие рабочие места) <span style="color: red">*</span>'
                            },
                            {
                                view: 'text',
                                name: 'personOfficeCnt',
                                id: 'personOfficeCnt',
                                validate: function(val){
                                    return !isNaN(val*1) && (val.trim() !== '')
                                },
                                invalidMessage: 'Поле не может быть пустым',
                                required: true
                            },
                        ]
                    },
                    view_section('Данные о ваших работниках, чья деятельность предусматривает выход на работу (Обязательный для заполнения раздел)'),
                    {
                        view: 'scrollview',
                        type: 'space',
                        height: 600,
                        scroll: 'y',
                        body: {
                            rows: [
                                {
                                    id: 'person_table',
                                    view: 'datatable',
                                    url: 'doc_persons/' + ID_REQUEST,
                                    pager: 'Pager',
                                    datafetch: 50,
                                    height: 400,
                                    name: 'persons',
                                    select: 'row',
                                    resizeColumn:true,
                                    readonly: true,
                                    columns: [
                                        { id: 'index', header: '', css: 'rank', width: 50 },
                                        { id: 'lastname', header: 'Фамилия', width: 300, sort: 'string'},
                                        { id: 'firstname', header: 'Имя',  width: 200, sort: 'string'},
                                        { id: 'patronymic', header: 'Отчество', minWidth: 300, sort: 'string', fillspace: true  },
                                        //{ id: 'isagree', header: 'Согласие', width: 100, template: '{common.checkbox()}', css: 'center' }
                                    ],
                                    on:{
                                        'data->onStoreUpdated': function(){
                                            this.data.each(function(obj, i){
                                                obj.index = i + 1;
                                            });
                                        }
                                    },
                                    data: []
                                },
                                {
                                    view: 'pager',
                                    id: 'Pager',
                                    size: 50,
                                    group: 5,
                                    template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                                },
                                {
                                    view: 'form',
                                    id: 'form_person',
                                    elements: [
                                        {
                                            type: 'space',
                                            margin: 0,
                                            id: 'form_person_rows',
                                            rows:[
                                                {
                                                    responsive: 'form_person_rows',
                                                    cols: [
                                                        {view: 'text', name: 'lastname', minWidth: 220, label: 'Фамилия', labelPosition: 'top' },
                                                        {view: 'text', name: 'firstname', minWidth: 220, label: 'Имя', labelPosition: 'top'},
                                                        {view: 'text', name: 'patronymic', minWidth: 220, label: 'Отчество', labelPosition: 'top'},
                                                        //{view: 'checkbox', label: 'Согласие', name: 'isagree', id: 'agree_checkbox'},
                                                    ]
                                                }

                                            ]

                                        },
                                        {
                                        id: 'button_rows_form_person', margin: 5,
                                        rows: [
                                            {
                                                //type: 'space',
                                                responsive: 'button_rows_form_person',
                                                cols: [
                                                    {view: 'button', value: 'Добавить', minWidth: 150, click: addPerson },
                                                    {view: 'button', value: 'Изменить', minWidth: 150, click: editPerson },
                                                    {view: 'button', value: 'Удалить', minWidth: 150, click: removePerson},
                                                    {view: 'button', value: 'Очистить', id: 'clearPersonsBtn', minWidth: 150, disabled: true, click: clearPersons}
                                                    ]
                                            }
                                            ],
                                        },
                                    ]
                                }
                            ]
                        }
                    },
                    view_section('Подача заявки'),
                    {
                        view: 'checkbox',
                        name: 'isActualization',
                        id: 'isActualization',
                        labelPosition: 'top',
                        label: 'Актуален',
                        value: 1,
                        disabled: true
                    },
                    {
                        view: 'template',
                        id: 'consent',
                        height: 200,
                        readonly: true,
                        scroll: true,
                        template: ''
                    },
                    {
                        rows:[
                            {
                                view: 'template',
                                autoheight: true,
                                css: {
                                    "font-size": "15px;",
                                },
                                borderless: true,
                                template: 'Подтверждаю согласие работников на обработку персональных данных <span style="color: red">*</span>'
                            },
                            {
                                view: 'checkbox',
                                name: 'isAgree',
                                id: 'isAgree',
                                invalidMessage: 'Поле не может быть пустым',
                                required: true,
                                on: {
                                    onChange (newv, oldv) {
                                        let cnt = $$('person_table').data.count();
                                        let is_no_pdf = $$('no_pdf').getValue() == upload_chack_error;
                                        if ($$('isAgree').getValue() == 1 && $$('isProtect').getValue() == 1  && cnt > 0 && !is_no_pdf){
                                            $$('send_btn').enable();
                                        }else{
                                            $$('send_btn').disable();
                                        }
                                        //$$('send_btn').disabled = !($$('isAgree').getValue() && $$('isProtect').getValue() )
                                    }
                                }
                            },
                        ]
                    },
                    {
                        view: 'template',
                        id: 'prescription',
                        height: 550,
                        readonly: true,
                        scroll:true,
                        template: ''
                    },
                    {
                        rows:[
                            {
                                view: 'template',
                                autoheight: true,
                                css: {
                                    "font-size": "15px;",
                                    "padding-bottom" : "0px !important;",
                                    "padding-left" : "0px !important;"
                                },
                                borderless: true,
                                template: 'Подтверждаю обязательное выполнение предписания Управления Роспотребнадзора по Республике Бурятия <span style="color: red">*</span>'
                            },
                            {
                                view: 'checkbox',
                                name: 'isProtect',
                                id: 'isProtect',
                                invalidMessage: 'Поле не может быть пустым',
                                required: true,
                                on: {
                                    onChange(newv, oldv) {
                                        let cnt = $$('person_table').data.count();
                                        let is_no_pdf = $$('no_pdf').getValue() == upload_chack_error;
                                        if ($$('isAgree').getValue() == 1 && $$('isProtect').getValue() == 1  && cnt > 0 && !is_no_pdf){
                                            $$('send_btn').enable();
                                        }else{
                                            $$('send_btn').disable();
                                        }
                                    }
                                }
                            },
                        ]
                    },

                    {
                        id: 'label_sogl',
                        view: 'template',
                        borderless: true,
                        autoheight: true,
                        css: {
                            "font-size": "15px;",
                            "text-align":"center"
                        },
                        template: 'Информация мною прочитана и я согласен с ней при подаче заявки',
                        align: 'center'
                    },
                    {
                        cols: [
                            {
                                id: 'send_btn',
                                view: 'button',
                                css: 'webix_primary',
                                value: 'Подать заявку',
                                //НЕ МЕНЯТЬ!
                                disabled: true, //НЕ МЕНЯТЬ!
                                //НЕ МЕНЯТЬ!
                                align: 'center',
                                click: function () {

                                    if($$('form').validate()) {

                                        let params = $$('form').getValues();

                                        params.actualizedRequestId = ID_REQUEST;

                                        params.organizationInn = params.organizationInn.trim();
                                        params.organizationOgrn = params.organizationOgrn.trim();

                                        if(params.organizationInn.length > 12 ){
                                            webix.message('Превышена длина ИНН', 'error')
                                            return false
                                        }

                                        if(params.organizationInn.length == 0 ){
                                            webix.message('Заполните ИНН', 'error')
                                            return false
                                        }

                                        if(params.organizationOgrn.length > 15){
                                            webix.message('Превышена длина ОГРН', 'error')
                                            return false
                                        }

                                        if(params.organizationPhone.length > 100){
                                            webix.message('Превышена длина номера телефона', 'error')
                                            return false
                                        }

                                        if(params.organizationEmail.length > 100){
                                            webix.message('Превышена длина электронной почты', 'error')
                                            return false
                                        }else{
                                            let bad_val = params.organizationEmail.indexOf("*") > -1
                                                || params.organizationEmail.indexOf("+") > -1
                                                || params.organizationEmail.indexOf('"') > -1;

                                            if(bad_val == true){
                                                webix.message('Недопустимые символы в адресе электронной почты', 'error')
                                                return false
                                            }
                                        }

                                        if(params.organizationShortName.length > 255){
                                            webix.message('Превышена длина краткого наименования', 'error')
                                            return false
                                        }

                                        if(params.organizationAddressJur.length > 255){
                                            webix.message('Превышена длина юридического адреса', 'error')
                                            return false
                                        }

                                        if (params.isAgree != 1){
                                            webix.message('Необходимо подтвердить согласие работников на обработку персональных данных', 'error')
                                            return false
                                        }
                                        if (params.isProtect != 1) {
                                            webix.message('Необходимо подтвердить обязательное выполнение предписания Управления Роспотребнадзора по Республике Бурятия', 'error')
                                            return false
                                        }

                                        let cur_date = new Date();
                                        let dif  = Math.abs((cur_date.getTime() - pred_date.getTime()) /1000);
                                        pred_date = new Date();
                                        if (dif < 5){
                                            webix.message('Слишком частое нажатие на кнопку', 'error')
                                            return false
                                        }
                                        // if(!$$('upload').files.data.count()){
                                        //     webix.message('Необходимо вложить файл', 'error')
                                        //     $$('upload').focus()
                                        //     return false
                                        // }

                                        let persons = []
                                        $$('person_table').data.each(function (obj) {
                                            let person = {
                                                lastname: obj.lastname,
                                                firstname: obj.firstname,
                                                patronymic: obj.patronymic
                                            }
                                            persons.push(person)
                                        })
                                        params.persons = persons

                                        let addrs = []
                                        $$('addr_table').data.each(function (obj) {
                                            let addr = {
                                                addressFact: obj.addressFact,
                                                personOfficeFactCnt: obj.personOfficeFactCnt
                                            }
                                            addrs.push(addr)
                                        })
                                        params.addressFact = addrs

                                        params.organizationInn = params.organizationInn.trim()
                                        params.organizationOgrn  = params.organizationOgrn.trim()

                                        //params.attachment = uploadFile
                                        //params.attachmentFilename = uploadFilename

                                        $$('label_sogl').showProgress({
                                            type: 'icon',
                                            delay: 5000
                                        })


                                        $$('upload').send(function(response) {
                                            let uploadedFiles = []
                                            $$('upload').files.data.each(function (obj) {
                                                let status = obj.status
                                                let name = obj.name
                                                if(status == 'server'){
                                                    let sname = obj.sname
                                                    uploadedFiles.push(sname)
                                                }
                                            })

                                            if(uploadedFiles.length != $$('upload').files.data.count()) {
                                                webix.message('Не удалось загрузить файлы.')
                                                $$('upload').focus()
                                                return false
                                            }
                                            // console.log(uploadedFiles)
                                            params.attachment = uploadedFiles.join(',')
                                            // console.log(params)

                                            webix.ajax()
                                                .headers({'Content-type': 'application/json'})
                                                //.headers({'Content-type': 'application/x-www-form-urlencoded'})
                                                .post('/typed_form?request_type=' + ID_TYPE_REQUEST,
                                                    JSON.stringify(params),
                                                    //params,
                                                    function (text, data, xhr) {
                                                        console.log(text);
                                                        let errorText = "ЗАЯВКА НЕ ВНЕСЕНА. ОБНАРУЖЕНЫ ОШИБКИ ЗАПОЛНЕНИЯ: ";
                                                        if(text.includes(errorText)){
                                                            webix.alert({
                                                                title: "ИСПРАВЬТЕ ОШИБКИ",
                                                                ok:"Вернуться к заполнению заявки",
                                                                text: text
                                                            });
                                                            $$('label_sogl').hideProgress();
                                                        }else if(text.includes("Данный тип заявки не существует!")){
                                                            webix.alert({
                                                                title: "ВНИМАНИЕ!",
                                                                ok: "ОК",
                                                                text: text
                                                            });
                                                            $$('label_sogl').hideProgress();
                                                        }else if(text.includes("Подача заявок с данным типом закрыта!")){
                                                            webix.alert({
                                                                title: "ВНИМАНИЕ!",
                                                                ok: "ОК",
                                                                text: text
                                                            });
                                                            $$('label_sogl').hideProgress();
                                                        }else if(text.includes("Невозможно сохранить заявку")){
                                                            webix.alert({
                                                                title: "ВНИМАНИЕ!",
                                                                ok: "ОК",
                                                                text: text
                                                            });
                                                            $$('label_sogl').hideProgress();
                                                        }else{
                                                            // Заявка принята. Ожидайте ответ на электронную почту.
                                                            if (ID_REQUEST) {
                                                                webix.alert({
                                                                    title: MESSAGES.actualization.actualizationTitle,
                                                                    text: MESSAGES.actualization.requestActualized,
                                                                }).then(function () {
                                                                    $$('label_sogl').hideProgress();
                                                                    window.location.replace('http://работающаябурятия.рф');
                                                                });
                                                            } else {
                                                                webix.confirm({
                                                                    title: "Заявка внесена",
                                                                    ok: "Закрыть",
                                                                    cancel: "Внести еще одну заявку",
                                                                    text: text
                                                                })
                                                                    .then(function () {
                                                                        $$('label_sogl').hideProgress();
                                                                        window.location.replace('http://работающаябурятия.рф');
                                                                    })
                                                                    .fail(function () {
                                                                        $$('label_sogl').hideProgress()
                                                                        $$('form').clear();
                                                                        $$('upload').setValue();
                                                                        $$('form_person').clear();
                                                                        $$('form_addr').clear();
                                                                        $$('addr_table').clearAll();
                                                                        $$('person_table').clearAll();
                                                                        $$('organizationName').focus();
                                                                        $$("departmentId").setValue(departmentId);
                                                                    });
                                                            }
                                                        }
                                                    })
                                        })
                                    }
                                    else {
                                        webix.message('Не заполнены обязательные поля. Для просмотра прокрутите страницу вверх', 'error')
                                    }
                                }
                            }
                        ]
                    },
                    {
                        paddingLeft: 10,
                        view: 'label',
                        label: '',
                        id: 'file'
                    }
                ],
                /*
                                rules: [
                                    {
                                        email: webix.rules.isEmail(),
                                        organizationInn: webix.rules.isNumber(),
                                        organizationOgrn: webix.rules.isNumber(),

                                    }
                                ]
                */
            }
        ]
    })

    $$('form_person').bind('person_table');
    $$('form_addr').bind('addr_table');

    let win = webix.ui({
        view: 'window',
        modal: true,
        container: 'app',
        width: 400,
        position: 'center',
        head: 'ВНИМАНИЕ!',
        body: {
            view: 'form',
            rows: [
                {
                    view: 'label',
                    id: 'win_id_label',
                    label: '',
                    align: 'center'
                },
            ],
            elementsConfig: {
                labelPosition: 'top',
            }
        }
    })

    webix.ajax('cls_type_request/' + ID_TYPE_REQUEST).then(function (data) {
        let typeRequest = data.json();

        if (!typeRequest) {
            $$('win_id_label').setValue('Данный тип заявки не существует!');
            win.show();
        } else if (typeRequest.statusRegistration == 0) {
            $$('win_id_label').setValue('Подача заявок с данным типом закрыта!')
            win.show();
        } else {
            webix.ajax('cls_departments').then(function (data) {
                let departments = data.json();

                $$('departmentId').define('options', departments);
                $$('departmentId').refresh();

                let descDepartments = '№\tНаименование органа власти\tОписание\n';
                departments.forEach((dep, index) => {
                    if (dep.description) {
                        descDepartments += (index + 1) + '. ' + dep.name + '\n';
                        descDepartments += dep.description + '\n';
                    }
                });
                $$('desc_departments').setValue(descDepartments);
            });

            $$('activityKind').setHTML('<span style="font-size: 1.0rem">' + typeRequest.activityKind + '</span>');
            let department = typeRequest.department;
            if (department) {
                departmentId = (department.id);
                $$("departmentId").setValue(department.id);
                $$("departmentId").disable();
            }
            $$('prescription').setHTML(typeRequest.prescription);
            $$('consent').setHTML(typeRequest.consent);

            if (typeRequest.settings) {
                console.log(typeRequest.settings);
                const settings = JSON.parse(typeRequest.settings, function (key, value) {
                    if (value === 'webix.rules.isChecked') {
                        return webix.rules.isChecked;
                    }
                    return value;
                });
                if (settings.fields) {
                    settings.fields.forEach(field => {
                        $$('form').addView(field.ui, field.pos);
                    })
                }
            }
        }
    });

    if (ID_REQUEST) {
        webix.ajax('doc_requests/' + ID_REQUEST).then(function (data) {
            data = data.json();
            $$('organizationName').setValue(data.organization.name);
            $$('organizationShortName').setValue(data.organization.shortName);
            $$('organizationInn').setValue(data.organization.inn);
            $$('organizationOgrn').setValue(data.organization.ogrn);
            $$('organizationEmail').setValue(data.organization.email);
            $$('organizationEmail').setValue(data.organization.email);
            $$('organizationPhone').setValue(data.organization.phone);
            $$('organizationOkved').setValue(data.organization.okved);
            $$('organizationOkvedAdd').setValue(data.organization.okvedAdd);
            $$('organizationAddressJur').setValue(data.organization.addressJur);
            $$('reqBasis').setValue(data.reqBasis);
            $$('personSlrySaveCnt').setValue(data.personSlrySaveCnt);
            $$('personRemoteCnt').setValue(data.personRemoteCnt);
            $$('personOfficeCnt').setValue(data.personOfficeCnt);
        });

        let addr_table_data = new webix.DataCollection({
            url: 'doc_address_fact/' + ID_REQUEST
        })
        $$('addr_table').sync(addr_table_data);

        $$('send_btn').setValue('Актуализировать')
    }

    webix.event(window, "resize", function (event) {
        layout.define("width", document.body.clientWidth);
        layout.resize();
    });
    webix.extend($$('label_sogl'), webix.ProgressBar);

    if (document.body.clientWidth < 760) {
        $$('form').addView({
            type: 'space',
            margin: 5,
            id: 'org_data_rows',
            rows: []
        }, 1);
        $$('org_data_rows').addView($$('org_data_rows_column1'));
        $$('org_data_rows').addView($$('org_data_rows_column2'));
        $$('form').removeView($$('org_data_cols'));
        $$('upload').config.height = 60; $$('upload').resize();
    }

})
