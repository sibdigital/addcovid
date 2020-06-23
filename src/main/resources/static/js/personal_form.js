webix.i18n.setLocale("ru-RU");

let flag = 0

function view_section(title){
    return {
        view: 'template',
        type: 'section',
        template: title
    }
}

function addAddr(){
    let values = $$('form_addr').getValues()
    if(values.addressFact == ''){
        webix.message('не заполнены обязательные поля')
        return;
    }
    if(values.addressFact.length > 255 ){
        webix.message('Фактический адрес превышает 255 знаков!')
        return;
    }

    $$('addr_table').add({
        addressFact: values.addressFact,
    }, $$('addr_table').count() + 1)

    let is_no_pdf = $$('no_pdf').getValue() == upload_check_error;
    if ($$('isAgree').getValue() == 1 && $$('isProtect').getValue() == 1){
        $$('send_btn').enable();
    }else{
        $$('send_btn').disable();
    }

    $$('form_addr').clear()
}

function editAddr(){
    let values = $$('form_addr').getValues()
    if(values.addressFact == ''){
        webix.message('обязательные поля')
        return;
    }
    if(values.addressFact == ''){
        webix.message('не заполнены обязательные поля')
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
                let cnt = $$('addr_table').data.count();
                if (cnt == 0) {
                    $$('send_btn').disable();
                }
            }
        )
}


let uploadFile = '';
let uploadFilename = '';
let pred_date = new Date();
let upload_check_error = 'Загружать можно только PDF-файлы и ZIP-архивы!';

webix.ready(function() {
    let layout = webix.ui({
        container: 'app',
        // autowidth: true,
        height: document.body.clientHeight,
        width: 1200,
        css: { margin: 'auto' },
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
                                label: '<span style="font-size: 1.0rem">Работающая Бурятия. </span>',
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
                id: 'form',
                view: 'form',
                complexData: true,
                elements: [
                    view_section('Основные сведения'),
                    {
                        type: 'space',
                        margin: 5,
                        cols: [
                            {
                                rows: [
                                    {
                                        cols: [
                                            {
                                                view: 'text',
                                                name: 'person.lastname',
                                                label: 'Фамилия',
                                                labelPosition: 'top',
                                                invalidMessage: 'Поле не может быть пустым',
                                                required: true
                                            },
                                            {
                                                view: 'text',
                                                name: 'person.firstname',
                                                label: 'Имя',
                                                labelPosition: 'top',
                                                invalidMessage: 'Поле не может быть пустым',
                                                required: true
                                            },
                                            {
                                                view: 'text',
                                                name: 'person.patronymic',
                                                label: 'Отчество',
                                                labelPosition: 'top',
                                                invalidMessage: 'Поле не может быть пустым',
                                            },
                                        ]
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
                                    {
                                        view: 'text',
                                        name: 'organizationInn',
                                        label: 'ИНН',
                                        labelPosition: 'top',
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true
                                    },
                                    {
                                        view: 'select',
                                        name: 'typeTaxReporting',
                                        label: 'Способ сдачи налоговой отчетности',
                                        labelPosition: 'top',
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true,
                                        options: [
                                            { id: 1, value: 'З-НДФЛ'},
                                            { id: 2, value: 'Налог для самозанятых'},
                                        ]
                                    },
                                    {
                                        view: 'select',
                                        id: 'districtId',
                                        name: 'districtId',
                                        label: 'Район, в котором оказывается услуга',
                                        labelPosition: 'top',
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true,
                                        options: 'cls_districts',
                                    }
                                ]
                            },
                        ]
                    },
                    view_section('Адресная информация'),
                    {
                        rows: [
                            {
                                view: 'label',
                                label: 'Фактические адреса нахождения жилья, сдаваемого в аренду',
                                align: 'left',
                            },
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
                                        header: 'Адрес',
                                        width: 'auto',
                                    },
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
                                            {view: 'text', name: 'addressFact', label: 'Адрес', labelPosition: 'top', required: true },
                                        ]
                                    },
                                    {
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
                    // view_section('Обоснование'),
                    {
                        hidden: true,
                        rows: [
                            // {
                            //     view: 'textarea',
                            //     height: 150,
                            //     label: 'Обоснование',
                            //     name: 'reqBasis',
                            //     id: 'reqBasis',
                            //     invalidMessage: 'Поле не может быть пустым',
                            //     required: true,
                            //     labelPosition: 'top'
                            // },
                            {
                                view: 'label',
                                label: '<span  style="text-align: center; color: red">Для загрузки нескольких файлов выбирайте их с зажатой клавишей Ctrl или заранее сожмите в ZIP-архив и загрузите его</span>',
                            },
                            {
                                view: 'label',
                                label: '<span  style="text-align: center; color: red">Общий размер загружаемых файлов не должен превышать 60 Мб</span>',
                            },
                            {
                                id: 'upload',
                                view: 'uploader',
                                css: 'webix_secondary',
                                value: 'Загрузить PDF-файл(-ы) или ZIP-архив(-ы)  с пояснением',
                                autosend: false,
                                upload: '/uploadpart',
                                required: true,
                                accept: 'application/pdf, application/zip',
                                multiple: true,
                                link: 'filelist',
                                on: {
                                    onBeforeFileAdd: function (upload) {
                                        if (upload.type.toUpperCase() !== 'PDF' && upload.type.toUpperCase() !== 'ZIP') {
                                            $$('no_pdf').setValue(upload_check_error);
                                            $$('file').setValue('');
                                            $$('send_btn').disable();
                                            return false;
                                        }

                                        if($$('file').getValue()){
                                            $$('file').setValue($$('file').getValue() + ',' + upload.name)
                                        }
                                        else {
                                            $$('file').setValue(upload.name)
                                        }
                                        $$('no_pdf').setValue('');
                                        if ($$('isAgree').getValue() == 1 && $$('isProtect').getValue() == 1){
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
                    view_section('Подача уведомления'),
                    // {
                    //     view: 'textarea',
                    //     height: 200,
                    //     readonly: true,
                    //     value: 'СОГЛАСИЕ на обработку персональных данных (далее – «Согласие»)\n' +
                    //         'Настоящим я, во исполнение требований Федерального закона от 27.07.2006 г. № 152-ФЗ «О персональных данных» (с изменениями и дополнениями) свободно, своей волей и в своем интересе даю свое согласие: Администрации Главы РБ и Правительства Республики Бурятия, юридический адрес: 670001, г. Улан-Удэ, ул. Ленина, д.54, ИНН 0323082280 (далее - Администрация), на обработку, с использованием средств автоматизации или без использования таких средств, персональных данных (фамилия, имя отчество сотрудников организации), включая сбор, запись, систематизацию, удаление и уничтожение персональных данных при подаче заявки с предоставлением сведений о численности работников организаций и индивидуальных предпринимателей.\n' +
                    //         'Настоящим я уведомлен Администрацией о том, что предполагаемыми пользователями персональных данных сотрудников моей организации являются работники Администрации.\n' +
                    //         'Я ознакомлен(а), что: настоящее согласие на обработку персональных данных моей организации являются бессрочным и может быть отозвано посредством направления в адрес Администрации письменного заявления.\n'
                    // },
                    {
                        view: 'label',
                        label: '<span style=\"text-align: center;\"><a  style=\"text-align: center;\" href=\"http://xn--80aaacc6bzbngqek9e7crdc.xn--p1ai/soglasie_na_obrabotky_pd.docx\" target=\"_blank\">Согласие на получение и обработку, проверку персональных данных</a></span>'
                    },
                    {
                        view: 'checkbox',
                        name: 'isAgree',
                        id: 'isAgree',
                        labelPosition: 'top',
                        invalidMessage: 'Поле не может быть пустым',
                        required: true,
                        label: 'Подтверждаю согласие на обработку персональных данных',
                        on: {
                            onChange (newv, oldv) {
                                let cnt = $$('addr_table').data.count();
                                let is_no_pdf = $$('no_pdf').getValue() == upload_check_error;
                                if ($$('isAgree').getValue() == 1 && $$('isProtect').getValue() == 1 && cnt > 0 && !is_no_pdf){
                                    $$('send_btn').enable();
                                }else{
                                    $$('send_btn').disable();
                                }
                            }
                        }
                    },
                    {
                        view: 'template',
                        id: 'prescription',
                        height: 450,
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
                                let cnt = $$('addr_table').data.count();
                                let is_no_pdf = $$('no_pdf').getValue() == upload_check_error;
                                if ($$('isAgree').getValue() == 1 && $$('isProtect').getValue() == 1 && cnt > 0 && !is_no_pdf){
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
                        label: 'Информация мною прочитана и я согласен с ней при подаче уведомления',
                        align: 'center'
                    },
                    {
                        cols: [
                            {
                                id: 'send_btn',
                                view: 'button',
                                css: 'webix_primary',
                                value: 'Подать уведомление',
                                //НЕ МЕНЯТЬ!
                                disabled: true, //НЕ МЕНЯТЬ!
                                //НЕ МЕНЯТЬ!
                                align: 'center',
                                click: function () {

                                    if ($$('form').validate()) {

                                        let params = $$('form').getValues();

                                        params.organizationInn = params.organizationInn.trim();
                                        if (params.organizationInn.length > 12 ) {
                                            webix.message('Превышена длина ИНН', 'error')
                                            return false
                                        }
                                        if (params.organizationInn.length == 0 ) {
                                            webix.message('Заполните ИНН', 'error')
                                            return false
                                        }

                                        params.organizationPhone = params.organizationPhone.trim();
                                        if (params.organizationPhone.length > 100) {
                                            webix.message('Превышена длина номера телефона', 'error')
                                            return false
                                        }

                                        if (params.organizationEmail.length > 100) {
                                            webix.message('Превышена длина электронной почты', 'error')
                                            return false
                                        } else {
                                            let bad_val = params.organizationEmail.indexOf("*") > -1
                                                || params.organizationEmail.indexOf("+") > -1
                                                || params.organizationEmail.indexOf('"') > -1;

                                            if (bad_val == true) {
                                                webix.message('Недопустимые символы в адресе электронной почты', 'error')
                                                return false
                                            }
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

                                        let addrs = []
                                        $$('addr_table').data.each(function (obj) {
                                            let addr = {
                                                addressFact: obj.addressFact,
                                                personOfficeFactCnt: obj.personOfficeFactCnt
                                            }
                                            addrs.push(addr)
                                        })
                                        params.addressFact = addrs

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
                                                if (status == 'server') {
                                                    let sname = obj.sname
                                                    uploadedFiles.push(sname)
                                                }
                                            })

                                            if (uploadedFiles.length != $$('upload').files.data.count()) {
                                                webix.message('Не удалось загрузить файлы.')
                                                $$('upload').focus()
                                                return false
                                            }

                                            params.attachment = uploadedFiles.join(',')

                                            webix.ajax()
                                                .headers({'Content-type': 'application/json'})
                                                .post('/personal_form',
                                                    JSON.stringify(params),
                                                    //params,
                                                    function (text, data, xhr) {
                                                        let errorText = "УВЕДОМЛЕНИЕ НЕ ПРИНЯТО. ОБНАРУЖЕНЫ ОШИБКИ ЗАПОЛНЕНИЯ: ";
                                                        if (text.includes(errorText)) {
                                                            webix.alert({
                                                                title: "ИСПРАВЬТЕ ОШИБКИ",
                                                                ok: "Вернуться к заполнению уведомления",
                                                                text: text
                                                            });
                                                            $$('label_sogl').hideProgress()
                                                        } else {
                                                            webix.confirm({
                                                                title: "Уведомление принято",
                                                                ok: "Закрыть",
                                                                cancel: "Подать еще одно уведомление",
                                                                text: text
                                                            })
                                                                .then(function () {
                                                                    $$('label_sogl').hideProgress();
                                                                    window.location.replace('http://работающаябурятия.рф');
                                                                })
                                                                .fail(function(){
                                                                    $$('label_sogl').hideProgress()
                                                                    $$('form').clear();
                                                                    $$('upload').setValue();
                                                                    $$('form_addr').clear();
                                                                    $$('addr_table').clearAll();
                                                                    $$('form').setValues({ departmentId: 14 });
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
            }
        ]
    });

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

    webix.ajax('cls_type_request/100').then(function (data) {
        let typeRequest = data.json();

        if (!typeRequest) {
            $$('win_id_label').setValue('Данный тип заявки не существует!');
            win.show();
        } else if (typeRequest.statusRegistration == 0) {
            $$('win_id_label').setValue('Подача заявок с данным типом закрыта!')
            win.show();
        } else {
            $$('activityKind').setHTML('<span style="font-size: 1.0rem">' + typeRequest.activityKind + '</span>');
            let department = typeRequest.department;
            if (department) {
                $$('form').setValues({ departmentId: department.id });
            }
            $$('prescription').setHTML(typeRequest.prescription);

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

    webix.event(window, "resize", function (event) {
        layout.define("width",document.body.clientWidth);
        layout.resize();
    });

    webix.extend($$('label_sogl'), webix.ProgressBar);
})