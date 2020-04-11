webix.i18n.setLocale("ru-RU");

function view_section(title){
    return {
        view: 'template',
        type: 'section',
        template: title
    }
}

let district_options = [
    { id: 1, value: 'Баргузинский'},
    { id: 2, value: 'Баунтовский '},
    { id: 3, value: 'Бичурский'},
    { id: 4, value: 'Джидинский'},
    { id: 5, value: 'Еравнинский'},
    { id: 6, value: 'Заиграевский'},
    { id: 7, value: 'Закаменский'},
    { id: 8, value: 'Иволгинский'},
    { id: 9, value: 'Кабанский'},
    { id: 10, value: 'Кижингинский'},
    { id: 11, value: 'Курумканский'},
    { id: 12, value: 'Кяхтинский'},
    { id: 13, value: 'Муйский'},
    { id: 14, value: 'Мухоршибирский'},
    { id: 15, value: 'Окинский'},
    { id: 16, value: 'Прибайкальский'},
    { id: 17, value: 'Северо-Байкальский'},
    { id: 18, value: 'Селенгинский'},
    { id: 19, value: 'Тарбагатайский'},
    { id: 20, value: 'Тункинский'},
    { id: 21, value: 'Хоринский'},
    { id: 22, value: 'Советский'},
    { id: 23, value: 'Железнодорожный'},
    { id: 24, value: 'Октябрьский'}
]

webix.ready(function() {
    webix.ui({
        container: 'app',
        autowidth: true,
        height: document.body.clientHeight,
        width: document.body.clientWidth - 8,
        rows: [
            {
                view: 'toolbar',
                height: 40,
                cols: [
                    {
                        view: 'label',
                        label: '<span style="font-size: 1.5rem">ЕИС "Работающая Бурятия". Форма подача заявки для дачников.</span>',
                    }
                ]
            },
            {
                id: 'form',
                view: 'form',
                complexData: true,
                elements: [
                    view_section('Ваши данные'),
                    {
                        type: 'space',
                        margin: 5,
                        cols: [
                            {
                                rows: [
                                    {
                                        view: 'text',
                                        id: 'lastname',
                                        name: 'lastname',
                                        label: 'Фамилия',
                                        labelPosition: 'top',
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true
                                    },
                                    {
                                        view: 'text',
                                        name: 'firstname',
                                        id: 'firstname',
                                        label: 'Имя',
                                        labelPosition: 'top',
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true
                                    },
                                    {
                                        view: 'text',
                                        name: 'patronymic',
                                        id: 'patronymic',
                                        label: 'Отчество',
                                        labelPosition: 'top',
                                    },
                                    {
                                        view: 'text',
                                        name: 'age',
                                        id: 'age',
                                        label: 'Возраст (на момент заполнения)',
                                        validate: function(val){
                                            return !isNaN(val*1) && (val.trim() != '') && (val > 0) && (val < 100);
                                        },
                                        attributes: { type:"number" },
                                        labelPosition: 'top',
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true
                                    },
                                    {
                                        view: 'text',
                                        name: 'email',
                                        label: 'e-mail',
                                        labelPosition: 'top',
                                        validate: function(val){
                                            if(val) return webix.rules.isEmail
                                            else return true
                                        },
                                        invalidMessage: 'Введите корректный e-mail',
                                    },
                                    {
                                        view: 'text',
                                        name: 'phone',
                                        label: 'Телефон',
                                        labelPosition: 'top',
                                        // invalidMessage: 'Поле не может быть пустым',
                                        // required: true
                                    }
                                ]
                            }
                        ]
                    },
                    view_section('Адресная информация'),
                    {
                        view: 'button',
                        type: 'icon',
                        icon: 'wxi-plus',
                        label: 'Добавить адрес',
                        click: function () {
                            $$('addform').show()
                            $$('district').focus()
                        }
                    },
                    {
                        view: 'form',
                        id: 'addform',
                        elements: [
                            {
                                cols: [
                                    {
                                        id: 'district',
                                        view: 'combo', name: 'district', width: 250, label: 'Район',
                                        labelPosition: 'top',
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true,
                                        options: district_options
                                    },
                                    {view: 'text', name: 'address', label: 'ДНТ/Адрес', labelPosition: 'top', required: true }
                                ]
                            },
                            {
                                cols: [
                                    {
                                        view: 'button', label: 'Добавить', css: 'webix_primary',
                                        click: function () {
                                            if($$('addform').validate()) {
                                                $$('addr_table').add($$('addform').getValues(), $$('addform').count + 1)
                                                $$('addform').clear()
                                                $$('addform').hide()
                                            }
                                        }
                                    },
                                    { view: 'button', label: 'Cancel', css: 'webix_danger',
                                        click: function () {
                                            $$('addform').clear()
                                            $$('addform').hide()
                                        }
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        rows: [
                            {
                                view: 'datatable', name: 'addressDacha', label: '', labelPosition: 'top',
                                height: 200,
                                select: 'row',
                                editable: true,
                                id: 'addr_table',
                                columns: [
                                    { id: 'id', header: '', css: 'rank'},
                                    {
                                        id: 'district',
                                        header: 'Район',
                                        width: 300,
                                        options: district_options,
                                        editor: 'select'
                                    },
                                    {
                                        id: 'address',
                                        header: 'ДНТ/Адрес',
                                        fillspace: true,
                                        editor: 'text'
                                    },
                                    {
                                        id: 'action',
                                        header: '',
                                        width: 80,
                                        template: '<button class="delete_btn">X</button>'
                                    }
                                ],
                                data: [],
                                myadd: function() {
                                    webix.message('asdf')
                                },
                                on:{
                                    'data->onStoreUpdated': function(){
                                        this.data.each(function(obj, i){
                                            obj.id = i + 1;
                                        });
                                    },
                                },
                                onClick:{
                                    delete_btn: function(ev, id){
                                        this.remove(id);
                                    }
                                }
                            },
/*
                            {
                                view: 'form',
                                id: 'form_addr',
                                elements: [
                                    {
                                        type: 'space',
                                        cols: [
                                            {
                                                view: 'combo', name: 'district', width: 250, label: 'Район', labelPosition: 'top',
                                                invalidMessage: 'Поле не может быть пустым',
                                                required: true,
                                                options: [
                                                    { id: 1, value: 'Баргузинский'},
                                                    { id: 2, value: 'Баунтовский '},
                                                    { id: 3, value: 'Бичурский'},
                                                    { id: 4, value: 'Джидинский'},
                                                    { id: 5, value: 'Еравнинский'},
                                                    { id: 6, value: 'Заиграевский'},
                                                    { id: 7, value: 'Закаменский'},
                                                    { id: 8, value: 'Иволгинский'},
                                                    { id: 9, value: 'Кабанский'},
                                                    { id: 10, value: 'Кижингинский'},
                                                    { id: 11, value: 'Курумканский'},
                                                    { id: 12, value: 'Кяхтинский'},
                                                    { id: 13, value: 'Муйский'},
                                                    { id: 14, value: 'Мухоршибирский'},
                                                    { id: 15, value: 'Окинский'},
                                                    { id: 16, value: 'Прибайкальский'},
                                                    { id: 17, value: 'Северо-Байкальский'},
                                                    { id: 18, value: 'Селенгинский'},
                                                    { id: 19, value: 'Тарбагатайский'},
                                                    { id: 20, value: 'Тункинский'},
                                                    { id: 21, value: 'Хоринский'},
                                                    { id: 22, value: 'Советский'},
                                                    { id: 23, value: 'Железнодорожный'},
                                                    { id: 24, value: 'Октябрьский'}
                                                ]
                                            },
                                            {view: 'text', name: 'address', label: 'ДНТ/Адрес', labelPosition: 'top', required: true },
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
*/
                        ]
                    },
                    view_section('Подача заявки'),
                    {
                        view: 'template',
                        height: 550,
                        readonly: true,
                        scroll:true,
                        template: '<b>ПРЕДПИСАНИЕ</b> <br/>'+
                            '<br/>' +
                            'о проведении дополнительных санитарно-противоэпидемических (профилактических) мероприятий<br/>'+
                            '<br/>'+
                            '10 апреля 2020 г.                                      №60                                       г. Улан-Удэ<br/>'+
                            '<br/>'+
                            '        В целях обеспечения санитарно-эпидемиологического благополучия населения, предупреждения распространения случаев новой коронавирусной инфекции (COVID-19), руководствуясь ч.2 ст.50 Федерального закона от 30.03.1999 № 52-ФЗ "О санитарно-эпидемиологическом благополучии населения"; постановлениями Главного государственного санитарного врача Российской Федерации о дополнительных мерах по снижению рисков завоза и распространения новой коронавирусной инфекции (COVID-2019), СП 3.4.2318-08 "Санитарная охрана территории Российской Федерации", утв. постановлением Главного государственного санитарного врача РФ №3 22.01.2008; СП 3.1./3.2.3146-13 "Общие требования по профилактике инфекционных и паразитарных болезней", утв. постановлением Главного государственного санитарного врача  РФ 16.12.2013 №65; СП 3.5.1378-03 "Санитарно-эпидемиологические требования к организации и осуществлению дезинфекционной деятельности", утв. постановлением Главного государственного санитарного врача РФ 09.06.2003 №131<br/>'+
                            '<br/>'+
                            '<b>ПРЕДПИСЫВАЮ:</b><br/>'+
                            '1.       Обеспечить при входе работников в организацию (предприятие) – возможность обработки рук кожными антисептиками, предназначенными для этих целей или дезинфицирующими салфетками с установлением контроля за соблюдением этой гигиенической процедуры;<br/>'+
                            '2.       Проводить контроль температуры тела работников перед началом рабочего дня при входе в организацию (предприятие), а также при необходимости в течение рабочего дня, с обязательным отстранением от нахождения на рабочем месте лиц с повышенной температурой тела и с признаками инфекционного заболевания, в том числе ОРВИ;<br/>'+
                            '3.       Проводить качественную уборку помещений с применением дезинфицирующих средств, зарегистрированных в установленном порядке, уделив особое внимание дезинфекции дверных ручек, выключателей, поручней, столов, оборудования и других контактных поверхностей, мест общего пользования с кратностью каждые 2 часа;<br/>'+
                            '4.       Проводить дезинфекцию наружных поверхностей эксплуатируемых зданий и объектов (площадки у входа, наружные двери, поручни, малые архитектурные формы, урны) с кратностью каждые 2 часа;<br/>'+
                            '5.       Обеспечить использование рабочих растворов дезинфицирующих средств в соответствии с инструкцией по их применению, выбирая режимы, предусмотренные для обеззараживания объектов при вирусных инфекциях;<br/>\''+
                            '6.       Предусмотреть в помещениях туалетов промаркированные дозаторы с дезинфицирующими средствами, активными в отношении вирусов;<br/>\''+
                            '7.       Обеспечить регулярное проветривание рабочих помещений с периодичностью каждые 2 часа;<br/>\''+
                            '8.       Обеспечить персонал средствами индивидуальной защиты, по возможности антисептическими средствами для обработки рук и осуществлять контроль за их использованием;<br/>\''+
                            '9.       Соблюдать социальное дистанцирование (не менее 1,5 метров между людьми), специальный режим допуска и нахождения в помещениях и на прилегающей территории организаций, довести до каждого работника информацию о необходимости соблюдения указанных режимов.<br/>\''+
                            '<br/>'+
                            'Ответственность за выполнение предписания возлагается на хозяйствующий субъект, получивший в ЕИС "Работающая Бурятия" разрешение на работу в период самоизоляции.  <br/>\''+
                            'должность, фамилия, имя, отчество должностного лица либо гражданина<br/>\''+
                            '<br/>'+
                            'О мерах, принятых во исполнение предписания сообщить в адрес Управления Роспотребнадзора по Республике Бурятия не позднее 3х дней с момента получения в ЕИС "Работающая Бурятия" разрешения на работу по электронной почте: org@03.rospotrebnadzor.ru.<br/>\''+
                            'Невыполнение в установленный срок настоящего предписания влечет административную ответственность в соответствии с частью 1 статьи 19.4 Кодекса Российской Федерации об административных правонарушениях.<br/>\''+
                            'Предписание может быть обжаловано в суд общей юрисдикции, Арбитражный суд, в вышестоящий орган государственного контроля (надзора), вышестоящему должностному лицу в установленном законодательством порядке.<br/>\''
                        },
                        {
                        view: 'checkbox',
                        name: 'isProtect',
                        labelPosition: 'top',
                        invalidMessage: 'Поле не может быть пустым',
                        validate: function(val){
                            if(val) return true
                            else return false
                        },
                        required: true,
                        label: 'Подтверждаю обязательное выполнение требований по защите от COVID-19',
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
                                //disabled: true,
                                align: 'center',
                                click: function () {
                                    if($$('form').validate()) {
                                        let params = $$('form').getValues()

                                        let addrs = []
                                        $$('addr_table').data.each(function (obj) {
                                            let addr = {
                                                address: obj.address,
                                                district: district_options[obj.district - 1].value
                                            }
                                            addrs.push(addr)
                                        })
                                        params.addrList = addrs

                                        if(addrs.length == 0){
                                            webix.message('Не заполнена адресная часть', 'error')
                                            $$('addr_table').focus()
                                            return false
                                        }

                                        $$('label_sogl').showProgress({
                                            type: 'icon',
                                            delay: 5000
                                        })

                                        webix.ajax()
                                            .headers({'Content-type': 'application/json'})
                                            .post('/dacha',
                                                JSON.stringify(params),
                                                function (data) {
                                                    webix.alert(data)
                                                        .then(function () {
                                                            $$('label_sogl').hideProgress()
                                                            $$('form').clear()
                                                            $$('addr_table').clearAll()
                                                            $$('lastname').focus()
                                                        });
                                            })
                                    }
                                    else {
                                        webix.alert('Не заполнены обязательные поля. Для просмотра прокрутите страницу вверх', 'error')
                                            .then(function () {
                                                let values = $$('form').getValues()
                                                for(key in values){
                                                    if($$(key).config.required && !values[key]){
                                                        $$(key).focus()
                                                        break
                                                    }
                                                }
                                            })
                                    }
                                }

                            }
                        ]
                    }
                ],
            }
        ]
    })
    $$('addform').hide()
    webix.extend($$('label_sogl'), webix.ProgressBar);
})