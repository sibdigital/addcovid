webix.i18n.setLocale("ru-RU");

function view_section(title){
    return {
        view: 'template',
        type: 'section',
        template: title
    }
}

webix.ready(function() {
    webix.ui({
        container: 'app',
        autowidth: true,
        height: document.body.clientHeight - 85,
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
                                        labelPosition: 'top'
                                    },
                                    {
                                        view: 'text',
                                        name: 'short_name',
                                        label: 'Краткое наименование',
                                        labelPosition: 'top'
                                    },
                                    {
                                        view: 'text',
                                        name: 'inn',
                                        label: 'ИНН',
                                        labelPosition: 'top'
                                    },
                                ]
                            },
                            {
                                rows: [
                                    {
                                        view: 'textarea',
                                        name: 'address_jur',
                                        label: 'Юридический адрес',
                                        labelPosition: 'top'
                                    },
                                ]
                            }
                        ]
                    },
                    view_section('Данные о ваших сотрудниках'),
                    {

                    }
                ]
            }
        ]
    })
})