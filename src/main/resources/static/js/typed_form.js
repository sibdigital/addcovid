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

webix.ready(function() {
    let layout = webix.ui({
        container: 'app',
        autowidth: true,
        height: document.body.clientHeight,
        width: document.body.clientWidth - 8,
        rows: [
            {
                view: 'toolbar',
                autoheight: true,
                id: 't1',
                rows: [
                    {
                        responsive: 't1',
                        css: 'webix_dark',
                        cols: [
                            {
                                view: 'label',
                                width: 300,
                                label: `<span style="font-size: 1.0rem">${APPLICATION_NAME}. </span>`,
                                // tooltip: ''
                            },
                            {
                                view: 'label',
                                id: 'activityKind',
                                minWidth: 400,
                                autoheight: true,
                            }
                        ]
                    }
                ]
            },
            {
                view: 'label',
                label: `<a style="font-size: 1.5rem; text-align: center;" href="${HOT_LINE}" target="_blank">Горячая линия. </a>
                     &nbsp&nbsp&nbsp <a style="font-size: 1.5rem; text-align: center;" href="${FORM_FILL_INSTRUCTION}" target="_blank">Инструкция по заполнению формы </a>
                     &nbsp&nbsp&nbsp <a style="font-size: 1.5rem; text-align: center;" href="${FAQ}" target="_blank">Часто задаваемые вопросы</a>`

            },
            {
                view: 'label',
                label: '<span  style="text-align: center;">Уважаемые пользователи!</span>',
                //css: 'main_label'
            },
            {
                view: 'label',
                label: `<span  style="text-align: center;">При подаче заявки на 100 и более человек обязательно используйте шаблон для заполнения!  
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
                        type: 'space',
                        margin: 5,
                        cols: [
                            {
                                rows: [
                                    {
                                        view: 'text',
                                        name: 'organizationName',
                                        id: 'organizationName',
                                        label: 'Полное наименование организации/фамилия, имя, отчество индивидуального предпринимателя',
                                        labelPosition: 'top',
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true
                                    },
                                    {
                                        view: 'text',
                                        name: 'organizationShortName',
                                        id: 'organizationShortName',
                                        label: 'Краткое наименование организации',
                                        labelPosition: 'top',
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
                                rows: [
                                    {
                                        view: 'text',
                                        name: 'organizationOkved',
                                        id: 'organizationOkved',
                                        label: 'Основной вид осуществляемой деятельности (отрасль)',
                                        labelPosition: 'top',
                                        required: true
                                    },
                                    {
                                        view: 'textarea',
                                        name: 'organizationOkvedAdd',
                                        id: 'organizationOkvedAdd',
                                        label: 'Дополнительные виды осуществляемой деятельности',
                                        height: 100,
                                        labelPosition: 'top'
                                    },
                                    {
                                        view: 'select',
                                        id: 'departmentId',
                                        name: 'departmentId',
                                        label: 'Министерство, курирующее вашу деятельность',
                                        labelPosition: 'top',
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true,
                                        options: [],
                                    },
                                    {
                                        view: 'textarea',
                                        id: 'desc_departments',
                                        label: '* области деятельности министерств',
                                        labelPosition: 'top',
                                        height: 150,
                                        readonly: true,
                                    }
                                ]
                            }
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
                                    { id: 'id', header: '', css: 'rank'},
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
                                        fillspace: true
                                        //width: 200
                                    }
                                ],
                                data: [],
                                on:{
                                    'data->onStoreUpdated': function(){
                                        this.data.each(function(obj, i){
                                            obj.id = i + 1;
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
                                        cols: [
                                            {view: 'text', name: 'addressFact', label: 'Фактический адрес', labelPosition: 'top', required: true },
                                            {view: 'text', name: 'personOfficeFactCnt', inputWidth: '250', label: 'Численность работников', labelPosition: 'top',
                                                invalidMessage: 'Поле не может быть пустым',
                                                required: true,
                                            },
                                            {},
                                        ]
                                    },
                                    {
                                        //type: 'space',
                                        margin: 5,
                                        cols: [
                                            {view: 'button', value: 'Добавить', width: 150, click: addAddr },
                                            {view: 'button', value: 'Изменить', width: 150, click: editAddr },
                                            {view: 'button', value: 'Удалить', width: 150, click: removeAddr}
                                        ]
                                    }
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
                                view: 'label',
                                label: '<span  style="text-align: center; color: red">Для загрузки нескольких файлов выбирайте их с зажатой клавишей Ctrl или заранее сожмите в ZIP-архив и загрузите его</span>',
                                //css: 'main_label'
                            },
                            {
                                view: 'label',
                                label: '<span  style="text-align: center; color: red">Общий размер загружаемых файлов не должен превышать 60 Мб</span>',
                                //css: 'main_label'
                            },
                            {
                                id: 'upload',
                                view: 'uploader',
                                css: 'webix_secondary',
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
                                view: 'text',
                                name: 'personRemoteCnt',
                                id: 'personRemoteCnt',
                                label: 'Суммарная численность работников, подлежащих переводу на дистанционный режим работы',
                                invalidMessage: 'Поле не может быть пустым',
                                validate: function(val){
                                    return !isNaN(val*1) && (val.trim() !== '')
                                },
                                required: true,
                                labelPosition: 'top'
                            },
                            {
                                view: 'text',
                                name: 'personOfficeCnt',
                                id: 'personOfficeCnt',
                                label: 'Суммарная численность работников, не подлежащих переводу на дистанционный режим работы (посещающие рабочие места)',
                                labelPosition: 'top',
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
                                    height: 400,
                                    name: 'persons',
                                    select: 'row',
                                    resizeColumn:true,
                                    readonly: true,
                                    columns: [
                                        { id: 'id', header: '', css: 'rank', width: 50 },
                                        { id: 'lastname', header: 'Фамилия', adjust: true, sort: 'string', fillspace: true },
                                        { id: 'firstname', header: 'Имя', adjust: true, sort: 'string', fillspace: true },
                                        { id: 'patronymic', header: 'Отчество', adjust: true, sort: 'string' },
                                        //{ id: 'isagree', header: 'Согласие', width: 100, template: '{common.checkbox()}', css: 'center' }
                                    ],
                                    on:{
                                        'data->onStoreUpdated': function(){
                                            this.data.each(function(obj, i){
                                                obj.id = i + 1;
                                            });
                                        }
                                    },
                                    data: []
                                },
                                {
                                    view: 'form',
                                    id: 'form_person',
                                    elements: [
                                        {
                                            type: 'space',
                                            margin: 0,
                                            cols: [
                                                {view: 'text', name: 'lastname', inputWidth: '250', label: 'Фамилия', labelPosition: 'top' },
                                                {view: 'text', name: 'firstname', inputWidth: '250', label: 'Имя', labelPosition: 'top'},
                                                {view: 'text', name: 'patronymic', inputWidth: '250', label: 'Отчество', labelPosition: 'top'},
                                                //{view: 'checkbox', label: 'Согласие', name: 'isagree', id: 'agree_checkbox'},
                                                {},
                                            ]
                                        },
                                        {
                                            //type: 'space',
                                            margin: 5,
                                            cols: [
                                                {view: 'button', value: 'Добавить', width: 150, click: addPerson },
                                                {view: 'button', value: 'Изменить', width: 150, click: editPerson },
                                                {view: 'button', value: 'Удалить', width: 150, click: removePerson},
                                                {view: 'button', value: 'Очистить', id: 'clearPersonsBtn', width: 150, disabled: true, click: clearPersons}
                                            ]
                                        }
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
                        view: 'checkbox',
                        name: 'isAgree',
                        id: 'isAgree',
                        labelPosition: 'top',
                        invalidMessage: 'Поле не может быть пустым',
                        required: true,
                        label: 'Подтверждаю согласие работников на обработку персональных данных',
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
                    {
                        view: 'template',
                        id: 'prescription',
                        height: 550,
                        readonly: true,
                        scroll:true,
                        template: ''
                    },
                    {
                        view: 'checkbox',
                        name: 'isProtect',
                        id: 'isProtect',
                        labelPosition: 'top',
                        invalidMessage: 'Поле не может быть пустым',
                        required: true,
                        label: 'Подтверждаю обязательное выполнение предписания Управления Роспотребнадзора по Республике Бурятия',
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
                    {
                        id: 'label_sogl',
                        view: 'label',
                        label: 'Информация мною прочитана и я согласен с ней при подаче заявки',
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
                                                                    title: "Данные формы заявки актуализированы",
                                                                    text: 'Благодарим за сотрудничество!'
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

        let person_table_data = new webix.DataCollection({
            url: 'doc_persons/' + ID_REQUEST
        })
        $$('person_table').sync(person_table_data);
        let addr_table_data = new webix.DataCollection({
            url: 'doc_address_fact/' + ID_REQUEST
        })
        $$('addr_table').sync(addr_table_data);

        $$('send_btn').setValue('Актуализировать')
    }

    webix.event(window, "resize", function (event) {
        layout.define("width",document.body.clientWidth);
        layout.resize();
    });
    webix.extend($$('label_sogl'), webix.ProgressBar);
})
