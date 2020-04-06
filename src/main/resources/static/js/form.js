webix.i18n.setLocale("ru-RU");

function view_section(title){
    return {
        view: 'template',
        type: 'section',
        template: title
    }
}

function addPerson(){
    let values = $$('form_person').getValues()
    if(values.addr == '' || values.cnt == ''){
        webix.message('Фамилия, Имя - обязательные поля')
        return;
    }

    $$('person_table').add({
        lastname: values.lastname,
        firstname: values.firstname,
        patronymic: values.patronymic,
        //isagree: values.isagree
    }, $$('person_table').count() + 1)

    $$('form_person').clear()
}

function editPerson(){
    let values = $$('form_person').getValues()
    if(values.lastname == '' || values.firstname == '') {
        webix.message('Фамилия, Имя - обязательные поля')
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
            }
        )
}

function addAddr(){
    let values = $$('form_addr').getValues()
    if(values.addressFact == '' || values.personOfficeFactCnt == ''){
        webix.message('обязательные поля')
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

webix.ready(function() {
    webix.ui({
        container: 'app',
        autowidth: true,
        height: document.body.clientHeight,
        width: document.body.clientWidth - 8,
        rows: [
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
                                        label: 'Полное наименование организации/фамилия, имя, отчество индивидуального предпринимателя',
                                        labelPosition: 'top',
                                        required: true
                                    },
                                    {
                                        view: 'text',
                                        name: 'organizationShortName',
                                        label: 'Краткое наименование организации',
                                        labelPosition: 'top',
                                        required: true
                                    },
                                    {
                                        view: 'text',
                                        name: 'organizationInn',
                                        label: 'ИНН',
                                        labelPosition: 'top',
                                        //pattern: {mask: '############', allow: /[0-9]/g},
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true
                                    },
                                    {
                                        view: 'text',
                                        name: 'organizationOgrn',
                                        label: 'ОГРН',
                                        labelPosition: 'top',
                                        required: true
                                    },
                                    {
                                        view: 'text',
                                        name: 'organizationEmail',
                                        label: 'e-mail',
                                        labelPosition: 'top',
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
                                        view: 'combo',
                                        name: 'departmentId',
                                        label: 'Министерство, курирующее вашу деятельность',
                                        labelPosition: 'top',
                                        options: [
                                            { id: 1, value: 'Министерство финансов Республики Бурятия (Нет курируемых предприятий/организаций)'},
                                            { id: 2, value: 'Министерство экономики Республики Бурятия (В сфере финансовой, страховой деятельности)'},
                                            { id: 3, value: 'Министерство имущественных и земельных отношений  Республики Бурятия ("оценочная деятельность, деятельность кадастровых инженеров")'},
                                            { id: 4, value: 'Министерство промышленности и торговли Республики Бурятия (1. Машиностроение и металообработка...)'},
                                            { id: 5, value: 'Министерство природных ресурсов Республики Бурятия (Предприятия добывающей промышленности, имеющие непрерывный )'},
                                            { id: 6, value: 'Министерство сельского хозяйства и продовольствия Республики Бурятия'},
                                            { id: 7, value: 'Министерство строительства и модернизации жилищно-коммунального комплекса Республики Бурятия (Строительство: Организации (в том числе работающие с ними по договорам подряда и/или оказания услуг юридические лица и индивидуальные предприниматели):)'},
                                            { id: 8, value: 'Министерство по развитию транспорта, энергетики и дорожного хозяйства Республики Бурятия (Сфера транспорта, энергетики, связи и дорожного хозяйства, а также в области энергосбережения и повышения энергетической эффективности в сфере транспорта, энергетики, связи и дорожного хозяйства)'},
                                            { id: 9, value: 'Министерство социальной защиты населения Республики Бурятия (Организации социального обслуживания населения)'},
                                            { id: 10, value: 'Министерство здравоохранения Республики Бурятия (Организации по техническому обслуживанию медецинского оборудования)'},
                                            { id: 11, value: 'Министерство культуры Республики Бурятия (Нет курируемых предприятий/организаций)'},
                                            { id: 12, value: 'Министерство образования и науки Республики Бурятия (1. учреждения дошкольного образования, где функционируют дежурные группы. 2. Учреждения среднего общего образования, где очно-заочное обучение для 9,11 классов.)'},
                                            { id: 13, value: 'Министерство спорта и молодежной политики Республики Бурятия (1. Содержание, эксплуатация и обеспечение безопасности на спортивных объектов 2. Строительство спортивных объектов 3. Волонтерская деятельность)'},
                                            { id: 14, value: 'Министерство туризма Республики Бурятия (1. Санаторно-курортная сфера 2. Гостиничный комплекс 3. Туроператоры, турагентства, экскурсоводы")'},
                                            { id: 15, value: 'Республиканское агентство лесного хозяйства (Выполнение мероприятий по использованию, охране, защите, воспроизводству лесов, лесозаготовка, лесопереработка)'},
                                        ]
                                    },
                                    {
                                        view: 'text',
                                        label: 'Обоснование заявки',
                                        name: 'req_basis',
                                        labelPosition: 'top'
                                    },
                                    {
                                        view: 'checkbox',
                                        name: 'isagree',
                                        //labelWidth: 400,
                                        labelPosition: 'top',
                                        label: 'Подтверждаю согласие работников на обработку персональных данных'
                                    },
                                    {
                                        view: 'checkbox',
                                        name: 'isprotect',
                                        //labelWidth: 400,
                                        labelPosition: 'top',
                                        label: 'Подтверждаю обязательное выполнение требований по защите от COVID-19'
                                    },
                                ]
                            },
                            {
                                rows: [
                                    {
                                        view: 'text',
                                        name: 'organizationOkved',
                                        label: 'Основной вид осуществляемой деятельности (отрасль)',
                                        labelPosition: 'top',
                                        required: true
                                    },
                                    {
                                        view: 'textarea',
                                        name: 'organizationOkvedAdd',
                                        label: 'Дополнительные виды осуществляемой деятельности',
                                        labelPosition: 'top'
                                    },
                                    {
                                        view: 'textarea',
                                        name: 'organizationAddressJur',
                                        label: 'Юридический адрес',
                                        labelPosition: 'top',
                                        required: true
                                    },
                                    {
                                        id: 'upload',
                                        view: 'uploader',
                                        css: 'webix_secondary',
                                        value: 'Выбрать файл для загрузки',
                                        autosend: false,
                                        multiple: false,
                                        on:{
                                            onBeforeFileAdd: function(upload){
                                                if(upload.type.toUpperCase() !== 'PDF') return false;
                                                let reader = new FileReader();
                                                // reader.onload = function (e) {
                                                //     $$("dataTable").clearAll();
                                                //     $$("dataTable").parse( e.target.result, "json" );
                                                // };
                                                reader.readAsBinaryString(upload.file);
                                                //console.log(reader);
                                                //debugger
                                                uploadFile = window.btoa(reader.result)
                                                uploadFilename = upload.name
                                                //console.log(uploadFile);
                                                return false;
                                            }
                                        }
                                        //formData: {}
                                    },
                                ]
                            }
                        ]
                    },
                    view_section('Данные о численности работников'),
                    {
                        type: 'space',
                        rows: [
                            {
                                view: 'text', name: 'personOfficeCnt',
                                label: 'Суммарная численность работников, не подлежащих переводу на дистанционный режим работы',
                                labelPosition: 'top' },
                            {
                                view: 'text', name: 'personRemoteCnt',
                                label: 'Суммарная численность работников, подлежащих переводу на дистанционный режим работы',
                                labelPosition: 'top' },
                            {
                                view: 'text', name: 'personSlrySaveCnt',
                                label: 'Суммарная численность работников, в отношении которых соответствующим решением Президента Российской Федерации установлен режим работы нерабочего дня с сохранением заработной платы',
                                labelPosition: 'top'
                            },
                            {
                                rows: [
                                    {
                                        view: 'datatable', name: 'addressFact', label: '', labelPosition: 'top',
                                        height: 200,
                                        editable: true,
                                        id: 'addr_table',
                                        columns: [
                                            { id: 'id', header: '', css: 'rank'},
                                            {
                                                id: 'addressFact',
                                                header: 'Фактический адрес осуществления деятельности',
                                                editor: 'text',
                                                fillspace: true },
                                            {
                                                id: 'personOfficeFactCnt',
                                                header: 'Численность работников, не подлежащих переводу на дистанционный режим работы, осуществляющих деятельность по указанному в  пункте 11 настоящей формы фактическому адресу',
                                                editor: 'text',
                                                width: 200}
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
                                                    {view: 'text', name: 'addressFact', label: 'Фактический адрес', labelPosition: 'top' },
                                                    {view: 'text', name: 'personOfficeFactCnt', inputWidth: '250', label: 'Численность работников', labelPosition: 'top'},
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
                            }
                        ]
                    },
                    view_section('Данные о ваших работниках, чья деятельность предусматривает выход на работу'),
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
                                        //margin: 5,
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
                                            {view: 'button', value: 'Удалить', width: 150, click: removePerson}
                                        ]
                                    }
                                ]
                            }
                        ]
                        }
                    },
                    {
                        //view: 'template',
                        //css: 'webix_dark',
                        cols: [
                            //{},
                            {
                                view: 'button',
                                css: 'webix_primary',
                                value: 'Подать',
                                align: 'center',
                                click: function () {
                                    let params = $$('form').getValues()

                                    let persons = []
                                    $$('person_table').data.each(function(obj){
                                        let person = {
                                            lastname: obj.lastname,
                                            firstname: obj.firstname,
                                            patronymic: obj.patronymic
                                        }
                                        persons.push(person)
                                    })
                                    params.persons = persons

                                    let addrs = []
                                    $$('addr_table').data.each(function(obj){
                                        let addr = {
                                            addressFact: obj.addressFact,
                                            personOfficeFactCnt: obj.personOfficeFactCnt
                                        }
                                        addrs.push(addr)
                                    })
                                    params.addressFact = addrs

                                    params.attachment = uploadFile
                                    params.attachmentFilename = uploadFilename

                                    console.log(params);

                                    webix.ajax()
                                        .headers({'Content-type': 'application/json'})
                                        .post('/',
                                        JSON.stringify(params),
                                        function(text, data, xhr){
                                            console.log(text);
                                            webix.message();
                                        })
                                }

                            }
                        ]
                    }
                ],
                rules: [
                    {
                        'email': webix.rules.isEmail()
                    }
                ]
            }
        ]
    })

    $$('form_person').bind('person_table')
})