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

webix.ready(function() {
    webix.ui({
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
                                label: `<span style="font-size: 1.0rem">${APPLICATION_NAME}</span>`,
                                tooltip: 'Заявка на оказание парикмахерских услуг'
                            },
                            {
                                view: 'label',
                                minWidth: 400,
                                autoheight: true,
                                label: '<span style="font-size: 1.0rem">Заявка на оказание парикмахерских услуг</span>',
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
                //css: 'main_label'
            },
            {
                view: 'label',
                label: `<a href="${REF_PRESCRIPTION_ROSPOTREBNADZOR_ADMINISTRATION}">&nbsp;ПРЕДПИСАНИЕ Управления Роспотребнадзора по Республике Бурятия (Обязательно распечатать и разместить)</a>`,
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
                                        label: 'Краткое наименование организации',
                                        labelPosition: 'top',
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true
                                    },
                                    {
                                        cols:[
                                            {
                                                view: 'text',
                                                name: 'organizationInn',
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
                                        label: 'e-mail',
                                        labelPosition: 'top',
                                        validate:webix.rules.isEmail,
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true
                                    },
                                    {
                                        view: 'text',
                                        name: 'organizationPhone',
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
                                        label: 'Дополнительные виды осуществляемой деятельности',
                                        height: 100,
                                        labelPosition: 'top'
                                    },
                                    {
                                        view: 'combo',
                                        id: 'departmentId',
                                        name: 'departmentId',
                                        label: 'Вашу деятельность курирует ',
                                        labelPosition: 'top',
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true,
                                        disabled: true,
                                        options: [
                                            //{ id: 4, value: 'Минпром РБ '},
                                             { id: 2, value: 'Мин.экон РБ (В сфере финансовой, страховой деятельности)'},
                                            // { id: 3, value: 'Мин.имущества РБ ("оценочная деятельность, деятельность кадастровых инженеров")'},
                                            // { id: 5, value: 'Мин.природных ресурсов РБ (Предприятия добывающей промышленности, имеющие непрерывный )'},
                                            // { id: 6, value: 'Мин.сельхоз РБ'},
                                            // { id: 7, value: 'Мин.строй РБ (Строительство: Организации (в том числе работающие с ними по договорам подряда и/или оказания услуг юридические лица и индивидуальные предприниматели):)'},
                                            // { id: 8, value: 'Мин.транс РБ (Сфера транспорта, энергетики, связи и дорожного хозяйства, а также в области энергосбережения и повышения энергетической эффективности в сфере транспорта, энергетики, связи и дорожного хозяйства)'},
                                            // { id: 9, value: 'Мин.соцзащиты РБ (Организации социального обслуживания населения)'},
                                            // { id: 10, value: 'Мин.здрав РБ (Организации по техническому обслуживанию медицинского оборудования)'},
                                            // { id: 11, value: 'Мин.культ РБ (Нет курируемых предприятий/организаций)'},
                                            // { id: 12, value: 'Мин.обр РБ (1. учреждения дошкольного образования, где функционируют дежурные группы. 2. Учреждения среднего общего образования, где очно-заочное обучение для 9,11 классов.)'},
                                            // { id: 13, value: 'Мин.спорта РБ (1. Содержание, эксплуатация и обеспечение безопасности на спортивных объектов 2. Строительство спортивных объектов 3. Волонтерская деятельность)'},
                                            // { id: 14, value: 'Мин.туризма РБ (1. Санаторно-курортная сфера 2. Гостиничный комплекс 3. Туроператоры, турагентства, экскурсоводы")'},
                                            // { id: 1, value: 'Мин.фин РБ (Нет курируемых предприятий/организаций)'},
                                            // { id: 15, value: 'РАЛХ (Выполнение мероприятий по использованию, охране, защите, воспроизводству лесов, лесозаготовка, лесопереработка)'},
                                            // { id: 16, value: 'Управление ветеринарии (Ветеринарные клиники, ветеринарные аптеки, ветеринарные кабинеты, зоомагазины, организации, занимающиеся отловом животных без владельцев)'},
                                            // { id: 17, value: 'Управление МЧС по РБ (Организации, осуществляющие деятельность в сфере противопожарной безопасности)'},
                                            // { id: 18, value: 'Росгвардия (Частные охранные предприятия)'},
                                        ]
                                    },
                                ]
                            }
                        ]
                    },
                    view_section('Адресная информация'),
                    {
                        view: 'textarea',
                        name: 'organizationAddressJur',
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
                                        fillspace: true
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
                                upload: 'uploadpart',
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
                                view: 'text', name: 'personSlrySaveCnt',
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
                                view: 'text', name: 'personRemoteCnt',
                                label: 'Суммарная численность работников, подлежащих переводу на дистанционный режим работы',
                                invalidMessage: 'Поле не может быть пустым',
                                validate: function(val){
                                    return !isNaN(val*1) && (val.trim() !== '')
                                },
                                required: true,
                                labelPosition: 'top'
                            },
                            {
                                view: 'text', name: 'personOfficeCnt',
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
                                        { id: 'index', header: '', css: 'rank', width: 50 },
                                        { id: 'lastname', header: 'Фамилия', adjust: true, sort: 'string', fillspace: true },
                                        { id: 'firstname', header: 'Имя', adjust: true, sort: 'string', fillspace: true },
                                        { id: 'patronymic', header: 'Отчество', adjust: true, sort: 'string' },
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
                        view: 'textarea',
                        height: 200,
                        readonly: true,
                        value: 'СОГЛАСИЕ на обработку персональных данных (далее – «Согласие»)\n' +
                            'Настоящим я, во исполнение требований Федерального закона от 27.07.2006 г. № 152-ФЗ «О персональных данных» (с изменениями и дополнениями) свободно, своей волей и в своем интересе даю свое согласие: Администрации Главы РБ и Правительства Республики Бурятия, юридический адрес: 670001, г. Улан-Удэ, ул. Ленина, д.54, ИНН 0323082280 (далее - Администрация), на обработку, с использованием средств автоматизации или без использования таких средств, персональных данных (фамилия, имя отчество сотрудников организации), включая сбор, запись, систематизацию, удаление и уничтожение персональных данных при подаче заявки с предоставлением сведений о численности работников организаций и индивидуальных предпринимателей.\n' +
                            'Настоящим я уведомлен Администрацией о том, что предполагаемыми пользователями персональных данных сотрудников моей организации являются работники Администрации.\n' +
                            'Я ознакомлен(а), что: настоящее согласие на обработку персональных данных моей организации являются бессрочным и может быть отозвано посредством направления в адрес Администрации письменного заявления.\n'
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
                        height: 550,
                        readonly: true,
                        scroll:true,
                        template:
                            `<p><a href="${REF_PRESCRIPTION_ROSPOTREBNADZOR_ADMINISTRATION}">&nbsp;ДАННЫЕ ПРАВИЛА ДОЛЖНЫ БЫТЬ РАСПЕЧАТАНЫ КРУПНЫМ ШРИФТОМ (РАЗМЕР ШРИФТА НЕ МЕНЕЕ, ЧЕМ 22 Times New Roman) И РАЗМЕЩЕНЫ НА ВИДНОМ ДЛЯ ПОСЕТИТЕЛЯ МЕСТЕ.</a></p>` +
                            '<p style="text-align: center;"><strong>ПРЕДПИСАНИЕ </strong></p>\n' +
                            '<p style="text-align: center;"><strong>о проведении дополнительных санитарно-противоэпидемических (профилактических) мероприятий</strong></p>\n' +
                            '<p><strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>\n' +
                            '<p>&laquo;30&raquo; апреля 2020 г. &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;№76<em>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </em>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;г. Улан-Удэ&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;(место составления)&nbsp;</p>\n' +
                            '<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>\n' +
                            '<p>В целях обеспечения санитарно-эпидемиологического благополучия населения, предупреждения распространения случаев новой коронавирусной инфекции (COVID-19), руководствуясь ч.2 ст.50 Федерального закона от 30.03.1999 № 52-ФЗ &laquo;О санитарно-эпидемиологическом благополучии населения&raquo;; постановлениями Главного государственного санитарного врача Российской Федерации о дополнительных мерах по снижению рисков завоза и распространения новой коронавирусной инфекции (COVID-2019), СП 3.4.2318-08 &laquo;Санитарная охрана территории Российской Федерации&raquo;, утв. постановлением Главного государственного санитарного врача РФ №3 22.01.2008; СП 3.1./3.2.3146-13 &laquo;Общие требования по профилактике инфекционных и паразитарных болезней&raquo;, утв. постановлением Главного государственного санитарного врача РФ 16.12.2013 №65; СП 3.5.1378-03 &laquo;Санитарно-эпидемиологические требования к организации и осуществлению дезинфекционной деятельности&raquo;, утв. постановлением Главного государственного санитарного врача РФ 09.06.2003 №131</p>\n' +
                            '<p><strong>&nbsp;</strong></p>\n' +
                            '<p style="text-align: center;"><strong>ПРЕДПИСЫВАЮ:</strong></p>\n' +
                            '<p>&nbsp;</p>\n' +
                            '<ol>\n' +
                            '<li>Предоставлять парикмахерские услуги в соответствии с требованиями СанПиН 2.1.2.2631-10 &laquo;Санитарно-эпидемиологические требования к размещению, устройству, оборудованию, содержанию и режиму работы организаций коммунально-бытового назначения, оказывающих парикмахерские и косметические услуги&raquo;.</li>\n' +
                            '<li>Не допускать оказание парикмахерских услуг в помещениях, расположенных в торговых комплексах, торгово-развлекательных центрах, торговых центрах, предоставление парикмахерских услуг возможно в помещениях при наличии изолированного входа.</li>\n' +
                            '<li>Не допускать к трудовой деятельности лиц, обязанных соблюдать режим изоляции в домашних условиях по постановлениям санитарных врачей по Республике Бурятия, а также работников с респираторными признаками.</li>\n' +
                            '<li>Обеспечить перед открытием парикмахерских проведение генеральной уборки помещений с применением дезинфицирующих средств по вирусному режиму.</li>\n' +
                            '<li>Организовать &laquo;входной фильтр&raquo; для работников с проведением бесконтактного контроля температуры тела, а также, при необходимости, в течение рабочего дня и обязательным недопущением на объект лиц с повышенной температурой тела и/или с признаками инфекционного заболевания.</li>\n' +
                            '<li>Запретить вход в парикмахерские лиц, не имеющих намерение заказать парикмахерскую услугу.</li>\n' +
                            '<li>Обслуживать клиентов только по предварительной записи, не позволяя клиенту долго оставаться в комнате ожидания или в основном помещении (более 15 минут), при непредвиденном скоплении очереди, организовать ожидание на улице с соблюдением принципа социального дистанцирования (1,5 метра).</li>\n' +
                            '<li>Не предоставлять услуги клиентам в домашних условиях или выезжать к клиенту; а также не предоставлять услуги если клиент соответствует какой-либо из групп лиц, которые обязаны соблюдать самоизоляцию, изоляцию в домашних условиях по постановлениям санитарных врачей по Республике Бурятия; при наличии у него признаков респираторной инфекции.</li>\n' +
                            '<li>Оборудовать места таким образом, чтобы в пределах 1,5 метров не было другого рабочего места, а также места ожидания для клиентов. Если это условие не может быть выполнено, то одновременно разрешается обслуживать лишь столько клиентов, сколько позволяет расположение рабочего места с соблюдением дистанции в 1,5 метра.</li>\n' +
                            '<li>Обеспечить информирование клиента перед записью о необходимости сохранения информации о нем (имя, фамилия, телефон) для эпидемиологического расследования, если такая необходимость возникнет.</li>\n' +
                            '<li>Запретить прием пищи на рабочих местах, а также исключить для посетителей предоставление чая, кофе и т.д. Организовать прием пищи в специально отведенных изолированных помещениях парикмахерских, с соблюдением противоэпидемического режима (обязательное мытье рук, снятие средств индивидуальной защиты, использование одноразовой посуды, либо обеспечение дезинфекции столовых приборов и посуды и др.).</li>\n' +
                            '<li>Обеспечить работников средствами индивидуальной защиты IV типа - пижама, медицинский халат, шапочка, маска (одноразовая или многоразовая) со сменой каждые 3 часа или респиратор фильтрующий, перчатки, носки, тапочки или туфли.</li>\n' +
                            '<li>Обеспечить проведение ежедневной (после окончания работы) стирки по договору со специализированной организацией или непосредственно в парикмахерской (при наличии соответствующих условий).</li>\n' +
                            '<li>Обеспечить, после завершения обслуживания каждого клиента, проведение обработки всех контактных поверхностей (дверных ручек, выключателей, подлокотников и т.д.), а также всех используемых инструментов с применением дезинфицирующих средств по вирусному режиму. Если посетитель воспользовался туалетом, то дезинфекция должна быть проведена всех контактных поверхностей и в туалете.</li>\n' +
                            '<li>Организовать контроль за применением работниками средств индивидуальной защиты.</li>\n' +
                            '<li>Не допускать к трудовой деятельности лиц из групп риска (старше 65 лет, имеющих хронические заболевания, сниженный иммунитет, беременных с обеспечением режима самоизоляции).</li>\n' +
                            '<li>Не допускать к работе сотрудников без актуальных результатов предварительных и периодических медицинских осмотров.</li>\n' +
                            '<li>Проводить ежедневную уборку с применением дезинфицирующих средств по вирусному режиму парикмахерских с обязательной обработкой контактных поверхностей (поручней, ручек, подлокотников и т.д.).</li>\n' +
                            '<li>Обеспечить регулярное проветривание рабочих помещений с периодичностью каждые 2 часа; использовать в рабочих помещениях бактерицидные лампы или рециркуляторы воздуха закрытого типа с целью регулярного обеззараживания воздуха (по возможности);</li>\n' +
                            '</ol>\n' +
                            '<p>&nbsp;</p>\n' +
                            '<p>Ответственность за выполнение предписания возлагается на хозяйствующий субъект, получивший на портале &laquo;Работающая Бурятия&raquo; разрешение на осуществление деятельности в период введенных ограничительных мероприятий на территории республики</p>\n' +
                            '<p>&nbsp;</p>\n' +
                            '<p>О мерах, принятых во исполнение предписания сообщить в адрес Управления Роспотребнадзора по Республике Бурятия не позднее 3-х дней с момента получения на портале &laquo;Работающая Бурятия&raquo; разрешения на осуществление деятельности по электронной почте: <a href="mailto:org@03.rospotrebnadzor.ru">org@03.rospotrebnadzor.ru</a>.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>\n' +
                            '<p>&nbsp;</p>\n' +
                            '<p>Невыполнение в установленный срок настоящего предписания влечет административную ответственность в соответствии с действующим законодательством.</p>\n' +
                            '<p>&nbsp;</p>\n' +
                            '<p>Предписание может быть обжаловано в суд общей юрисдикции, Арбитражный суд, в вышестоящий орган государственного контроля (надзора), вышестоящему должностному лицу в установленном законодательством порядке.</p>\n' +
                            '<p>&nbsp;</p>\n' +
                            '<p>&nbsp;</p>\n' +
                            '<p>&nbsp;</p>\n' +
                            '<p>Главный государственный</p>\n' +
                            '<p>санитарный врач</p>\n' +
                            '<p>по Республике Бурятия&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;______________&nbsp; С.С. Ханхареев&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;</p>\n' +
                            '<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>' +
                            '<p><a href="http://Работающаябурятия.рф/predpisanie.docx">&nbsp;ДАННЫЕ ПРАВИЛА ДОЛЖНЫ БЫТЬ РАСПЕЧАТАНЫ КРУПНЫМ ШРИФТОМ (РАЗМЕР ШРИФТА НЕ МЕНЕЕ, ЧЕМ 22 Times New Roman) И РАЗМЕЩЕНЫ НА ВИДНОМ ДЛЯ ПОСЕТИТЕЛЯ МЕСТЕ.</a></p>'
                    },
                    { //http://Работающаябурятия.рф/predpisanie.docx
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
                                            console.log(uploadedFiles)
                                            params.attachment = uploadedFiles.join(',')
                                            console.log(params)

                                            webix.ajax()
                                                .headers({'Content-type': 'application/json'})
                                                //.headers({'Content-type': 'application/x-www-form-urlencoded'})
                                                .post('barber',
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
                                                        }else{
                                                            webix.confirm({
                                                                title:"Заявка внесена",
                                                                ok: "Закрыть",
                                                                cancel: "Внести еще одну заявку",
                                                                text: `${text}
                                                                     <br/> Обязательно распечатайте предписание Управления Роспотребнадзора по Республике Бурятия и разместите на видном месте
                                                                     <br/><a href="${REF_PRESCRIPTION_ROSPOTREBNADZOR_ADMINISTRATION}">Ссылка для скачивания</a>`
                                                            })
                                                                .then(function () {
                                                                    $$('label_sogl').hideProgress();
                                                                    window.location.replace(`${REF_WORKING_PORTAL}`);
                                                                })
                                                                .fail(function(){
                                                                    $$('label_sogl').hideProgress()
                                                                    $$('form').clear();
                                                                    $$('upload').setValue();
                                                                    $$('form_person').clear();
                                                                    $$('form_addr').clear();
                                                                    $$('addr_table').clearAll();
                                                                    $$('person_table').clearAll();
                                                                    $$('organizationName').focus();
                                                                    $$("departmentId").setValue("2"); // name: '',
                                                                });
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
    $$("departmentId").setValue("2"); // name: '',
    //93.02 - Предоставление услуг парикмахерскими и салонами красоты

    webix.extend($$('label_sogl'), webix.ProgressBar);
})
