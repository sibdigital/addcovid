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
    if(values.lastname == '' || values.firstname == ''){
        webix.message('Фамилия, Имя - обязательные поля')
        return;
    }

    $$('person_table').add({
        lastname: values.lastname,
        firstname: values.firstname,
        patronymic: values.patronymic,
        isagree: values.isagree
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
                                        name: 'name',
                                        label: 'Наименование',
                                        labelPosition: 'top',
                                        required: true
                                    },
                                    {
                                        view: 'text',
                                        name: 'short_name',
                                        label: 'Краткое наименование',
                                        labelPosition: 'top',
                                        required: true
                                    },
                                    {
                                        view: 'text',
                                        name: 'inn',
                                        label: 'ИНН',
                                        labelPosition: 'top',
                                        //pattern: {mask: '############', allow: /[0-9]/g},
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true
                                    },
                                    {
                                        view: 'text',
                                        name: 'ogrn',
                                        label: 'ОГРН',
                                        labelPosition: 'top',
                                        required: true
                                    },
                                    {
                                        view: 'text',
                                        name: 'email',
                                        label: 'e-mail',
                                        labelPosition: 'top',
                                        required: true
                                    },
                                    {
                                        view: 'text',
                                        name: 'phone',
                                        label: 'Телефон',
                                        labelPosition: 'top',
                                        required: true
                                    },
                                    {
                                        view: 'combo',
                                        name: 'department',
                                        label: 'Министерство',
                                        labelPosition: 'top',
                                        options: listDepartment
                                    }
                                ]
                            },
                            {
                                rows: [
                                    {
                                        view: 'text',
                                        name: 'okved',
                                        label: 'ОКВЭД',
                                        labelPosition: 'top',
                                        required: true
                                    },
                                    {
                                        view: 'textarea',
                                        name: 'okved_add',
                                        label: 'ОКВЭД (доп.)',
                                        labelPosition: 'top'
                                    },
                                    {
                                        view: 'textarea',
                                        name: 'address_jur',
                                        label: 'Юридический адрес',
                                        labelPosition: 'top',
                                        required: true
                                    },
                                ]
                            }
                        ]
                    },
                    view_section('Данные о ваших сотрудниках'),
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
                                    { id: 'lastname', header: 'Фамилия', adjust: true },
                                    { id: 'firstname', header: 'Имя', adjust: true },
                                    { id: 'patronymic', header: 'Отчество', adjust: true },
                                    { id: 'isagree', header: 'Согласие', width: 100, template: '{common.checkbox()}', css: 'center' }
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
                                        margin: 5,
                                        cols: [
                                            {view: 'text', name: 'lastname', inputWidth: '200', label: 'Фамилия', labelPosition: 'top' },
                                            {view: 'text', name: 'firstname', inputWidth: '200', label: 'Имя', labelPosition: 'top'},
                                            {view: 'text', name: 'patronymic', inputWidth: '200', label: 'Отчество', labelPosition: 'top'},
                                            {view: 'checkbox', label: 'Согласие', name: 'isagree', id: 'agree_checkbox'},
                                            {},
                                        ]
                                    },
                                    {
                                        type: 'space',
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