webix.i18n.setLocale("ru-RU");

function view_section(title){
    return {
        view: 'template',
        type: 'section',
        template: title
    }
}

let district_options = [
    { id: 24, value: 'г.Улан-Удэ'},
    { id: 20, value: 'г.Гусиноозерск'},
    { id: 18, value: 'г.Северобайкальск'},
    { id: 1, value: 'Баргузинский'},
    { id: 2, value: 'Баунтовский'},
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
    { id: 19, value: 'Селенгинский'},
    { id: 21, value: 'Тарбагатайский'},
    { id: 22, value: 'Тункинский'},
    { id: 23, value: 'Хоринский'},

]

function getOptionsValue(index){
    //district_options.(newv - 1).value
    let result = -1
    district_options.forEach(item => {
        if(item.id == index) {
            result = item.value
        }
    })
    return result
}

function equalsRow(obj){
    let l = (obj.lastname.toLowerCase().indexOf($$('addform').getValues().lastname.toLowerCase()) == 0)
    let f = (obj.firstname.toLowerCase().indexOf($$('addform').getValues().firstname.toLowerCase()) == 0)
    let p = (obj.patronymic.toLowerCase().indexOf($$('addform').getValues().patronymic.toLowerCase()) == 0)
    let a = (obj.age == $$('addform').getValues().age)
    return l && f && p && a
}

webix.ready(function() {

    webix.i18n.setLocale("ru-RU");

    webix.Date.startOnMonday = true;

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
                                label: '<span style="font-size: 1.3rem">Работающая Бурятия. </span>',
                                tooltip: 'Заявка для дачников.'
                            },
                            {
                                view: 'label',
                                minWidth: 400,
                                autoheight: true,
                                label: '<span style="font-size: 1.3rem">Заявка для дачников.</span>',
                            }
                        ]
                    }
                ]
            },
            {
                view: 'label',
                label: '<span  style="text-align: center;">Телефон: 8 (3012) 46-24-34; колл-центр: 8 (3012) 573-900; 8 (3012) 571-600 </span>',
                //css: 'main_label'
            },
            {
                id: 'form',
                view: 'form',
                complexData: true,
                elements: [
                    view_section('Откуда вы выезжаете'),
                    {
                        type: 'space',
                        //margin: 5,
                        id: 'a2',
                        rows: [
                            {
                                responsive: 'a2',
                                cols: [
                                    {
                                        id: 'raion',
                                        view: 'combo', name: 'raion',
                                        //minWidth: 350,
                                        width: 250,
                                        label: 'Район',
                                        labelPosition: 'top',
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true,
                                        options: district_options,
                                        on: {
                                            onChange: function (newv, oldv) {
                                                if (newv == 18 || newv == 20 || newv == 24) {
                                                    $$('naspunkt').setValue(getOptionsValue(newv))
                                                }
                                            }
                                        }
                                    },
                                    {
                                        id: 'naspunkt',
                                        view: 'text',
                                        name: 'naspunkt',
                                        minwidth: 250,
                                        label: 'Населенный пункт/ДНТ/СНТ',
                                        labelPosition: 'top',
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true
                                    }
                                ]
                            },
                        ]
                    },

                    view_section('Куда вы следуете'),
                    {
                        type: 'space',
                        //margin: 5,
                        id: 'a1',
                        rows: [
                            {
                                responsive: 'a1',
                                cols: [
                                    {
                                        id: 'district',
                                        view: 'combo', name: 'district',
                                        width: 250,
                                        label: 'Район',
                                        labelPosition: 'top',
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true,
                                        options: district_options,
                                        on: {
                                            onChange: function (newv, oldv) {
                                                if (newv == 18 || newv == 20 || newv == 24) {
                                                    $$('address').setValue(getOptionsValue(newv))
                                                }
                                            }
                                        }
                                    },
                                    {
                                        id: 'address',
                                        minWidth: 250,
                                        view: 'text',
                                        name: 'address',
                                        label: 'Населенный пункт/ДНТ/СНТ',
                                        labelPosition: 'top',
                                        required: true,
                                        invalidMessage: 'Поле не может быть пустым',
                                    }
                                ]
                            }
                        ]
                    },

                    view_section('День действия разрешения'),
                    {
                        view: 'datepicker',
                        id: 'validDate',
                        name: 'validDate',
                        startOnMonday:true,
                        minDate: new Date(),
                        value: new Date(),
                        format: webix.i18n.dateFormatStr,
                        required: true
                    },

                    view_section('Список дачников'),
                    {
                        view: 'button',
                        type: 'icon',
                        icon: 'wxi-plus',
                        label: 'Добавить дачника',
                        width: 250,
                        id: 'add_btn',
                        click: function () {
                            $$('addform').show()
                            $$('lastname').focus()
                            $$('add_btn').hide();
                            $$('close_btn').show();
                        }
                    },
                    {
                        view: 'button',
                        type: 'icon',
                        css: 'webix_danger',
                        icon: 'wxi-close',
                        label: 'Закрыть',
                        width: 250,
                        id: 'close_btn',
                        click: function () {
                            $$('addform').clear()
                            $$('addform').hide()
                            $$('close_btn').hide()
                            $$('add_btn').show()
                        }
                    },

                    {
                        view: 'form',
                        id: 'addform',
                        elements: [
                            {
                                type: 'space',
                                margin: 5,
                                id: 'a4',
                                rows: [
                                    {
                                        responsive: 'a4',
                                        cols: [
                                            {
                                                view: 'text',
                                                id: 'lastname',
                                                name: 'lastname',
                                                label: 'Фамилия',
                                                labelPosition: 'top',
                                                invalidMessage: 'Поле не может быть пустым',
                                                minWidth: 200,
                                                required: true
                                            },
                                            {
                                                view: 'text',
                                                name: 'firstname',
                                                id: 'firstname',
                                                label: 'Имя',
                                                labelPosition: 'top',
                                                invalidMessage: 'Поле не может быть пустым',
                                                minWidth: 200,
                                                required: true
                                            },
                                            {
                                                view: 'text',
                                                name: 'patronymic',
                                                id: 'patronymic',
                                                label: 'Отчество',
                                                minWidth: 200,
                                                labelPosition: 'top',
                                            },
                                            {
                                                view: 'text',
                                                name: 'age',
                                                id: 'age',
                                                label: 'Возраст (на момент заполнения)',
                                                validate: function(val){
                                                    return !isNaN(val*1) && (val.trim() != '') && (val >= 0) && (val < 100);
                                                },
                                                attributes: { type:"number" },
                                                labelPosition: 'top',
                                                invalidMessage: 'Поле не может быть пустым',
                                                minWidth: 50,
                                                required: true,
                                                on: {
                                                    onEnter: function () {
                                                        if($$('addform').validate()) {
                                                            if($$('age').getValue() > 65){
                                                                webix.alert("Данные не могут быть введены. Людям старше 65 лет предписана обязательная самоизоляция. Оставайтесь дома и будьте здоровы!")
                                                                return false;
                                                            }

                                                            let find = $$('person_table').find(function(obj){
                                                                return equalsRow(obj)
                                                            })
                                                            if(find.length == 0) {
                                                                $$('person_table').add($$('addform').getValues(), $$('addform').count + 1)
                                                                $$('addform').clear()
                                                                $$('lastname').focus()
                                                                //$$('addform').hide()
                                                            }
                                                            else {
                                                                webix.message('Повтор записи', 'error')
                                                                $$('lastname').focus()
                                                            }
                                                        }
                                                    }
                                                }
                                            },
                                            {
                                                rows: [
                                                    {},
                                                    {
                                                        id: 'add_chk_btn',
                                                        view: 'button', label: '', css: 'webix_primary',
                                                        type: 'icon', icon: 'wxi-check',
                                                        width: 50,
                                                        height: 50,
                                                        align: 'center',
                                                        click: function () {
                                                            $$('age').callEvent('onEnter')
                                                        }
                                                    }
                                                ]
                                            },
                                        ]
                                    }
                                ]
                            },
                        ]
                    },
                    {
                        rows: [
                            {
                                view: 'datatable',
                                id: 'person_table',
                                name: 'personList',
                                height: 200,
                                select: 'row',
                                autowidth: true,
                                editable: false,
                                columns: [
                                    // { id: 'id', header: '', css: 'rank', autowidth: true },
                                    {
                                        id: 'lastname',
                                        header: 'Фамилия',
                                        //width: 300,
                                        adjust: true,
                                        editor: 'text',
                                    },
                                    {
                                        id: 'firstname',
                                        header: 'Имя',
                                        adjust: true,
                                        //fillspace: true,
                                        editor: 'text'
                                    },
                                    {
                                        id: 'patronymic',
                                        header: 'Отчество',
                                        adjust: true,
                                        //fillspace: true,
                                        editor: 'text'
                                    },
                                    {
                                        id: 'age',
                                        header: 'Возраст',
                                        adjust: true,
                                        //fillspace: true,
                                        //attributes: { type:"number" },
                                        validate: function(val){
                                            return !isNaN(val*1) && (val.trim() != '') && (val >= 0) && (val < 100);
                                        },

                                        editor: 'text'
                                    },
                                    {
                                        id: 'action',
                                        header: '',
                                        autowidth: true,
                                        maxWidth: 50,
                                        //adjust: true,
                                        template: function(obj){
                                            return "<div class='webix_el_button'><button class='delete_btn' style='cursor: pointer'>X</button></div>";
                                        }
                                    }
                                ],
                                data: [],
/*
                                on:{
                                    'data->onStoreUpdated': function(){
                                        this.data.each(function(obj, i){
                                            obj.id = i + 1;
                                        });
                                    },
                                },
*/
                                onClick:{
                                    delete_btn: function(ev, id){
                                        this.remove(id);
                                    }
                                }
                            },
                        ]
                    },

                    view_section('Подача заявки'),
                    {
                        view: 'textarea',
                        height: 100,
                        readonly: true,
                        value: 'Ознакомлен о необходимости иметь при себе по пути к месту следования паспорт и документы, ' +
                            'подтверждающий право собственности или иное законное основание для владения загородными жилыми строениями, ' +
                            'дачными (жилыми), садовыми домами, к земельным участкам, предоставленным в целях ведения садоводства, ' +
                            'огородничества, личного подсобного хозяйства, индивидуального жилищного строительства.'
                    },
                    {
                        view: 'checkbox',
                        name: 'isAgree',
                        id: 'isAgree',
                        labelPosition: 'top',
                        invalidMessage: 'Поле не может быть пустым',
                        required: true,
                        css: 'boldLabel',
                        label: 'Подтверждаю ознакомление',
                        on: {
                            onChange (newv, oldv) {
                                if ($$('isAgree').getValue() == 1 && $$('isProtect').getValue() == 1 ){
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
                            '<p style="text-align: center;"><strong>Предписание Управления Роспотребнадзора по Республике Бурятия о соблюдении санитарно-эпидемиологических требований</strong></p>\n' +
                            '<p style="text-align: center;"><strong>Уважаемые жители Республики Бурятия!</strong></p>\n' +
                            '<p style="text-align: left;"><strong>Для профилактики новой коронавирусной инфекции необходимо соблюдать 7 основных правил: </strong></p>\n' +
                            '<p style="text-align: left;">1. Максимально сократить пребывание в общественных местах</p>\n' +
                            '<p style="text-align: left;">2. В общественных местах (магазинах, в общественном транспорте и др.) обязательно используйте одноразовую медицинскую или многоразовую тканевую маску, меняя ее каждые 2-3 часа. Помните, что многоразовые маски после использования должны пройти тщательную обработку: стирка со средством, обдача паром и глажка утюгом без пара. А одноразовую маску необходимо утилизировать, поместив в герметичный пакет.</p>\n' +
                            '<p style="text-align: left;">3. Старайтесь держать дистанцию не менее 1 м. от окружающих людей</p>\n' +
                            '<p style="text-align: left;">4. Избегайте близких контактов и пребывания в одном помещении с людьми, имеющими видимые признаки ОРВИ (кашель, чихание, выделения из носа).</p>\n' +
                            '<p style="text-align: left;">5. Мойте тщательно руки с мылом после возвращения с улицы, контактов с посторонними людьми, обрабатывайте антисептическими средствами</p>\n' +
                            '<p style="text-align: left;">6. Дезинфицируйте гаджеты и поверхности, к которым прикасаетесь: дверные ручки, выключатели, раковины и смесители, пульты, клавиатуру и мышь, ключи, поверхности столешниц, полок, панели бытовой техники и др.</p>\n' +
                            '<p style="text-align: left;">7. Пользуйтесь только индивидуальными предметами личной гигиены (полотенце, зубная щетка, посуда и т.д.)</p>\n' +
                            '<p style="text-align: left;"><strong>Симптомы заболевания новой коронавирусной инфекции (COVID-19) схожи с симптомами обычных (сезонных) ОРВИ: </strong></p>\n' +
                            '<p style="text-align: left;">&bull; высокая температура тела</p>\n' +
                            '<p style="text-align: left;">&bull; головная боль</p>\n' +
                            '<p style="text-align: left;">&bull; слабость</p>\n' +
                            '<p style="text-align: left;">&bull; кашель</p>\n' +
                            '<p style="text-align: left;">&bull; затрудненное дыхание</p>\n' +
                            '<p style="text-align: left;">&bull; боли в мышцах</p>\n' +
                            '<p style="text-align: left;">&bull; тошнота</p>\n' +
                            '<p style="text-align: left;">&bull; рвота</p>\n' +
                            '<p style="text-align: left;"><strong>Что делать при подозрении на коронавирусную инфекцию: </strong></p>\n' +
                            '<p style="text-align: left;">1. Оставайтесь дома.</p>\n' +
                            '<p style="text-align: left;">2. Вызовите врача скорой медицинской помощи, проинформируйте его о местах своего пребывания за последние 2 недели, возможных контактах.</p>\n' +
                            '<p style="text-align: left;">3. Строго следуйте рекомендациям врача. Если врач предложил Вам госпитализироваться, ни в коем случае не отказывайтесь.</p>\n' +
                            '<p style="text-align: left;">4. Если Вы остались дома:</p>\n' +
                            '<p style="text-align: left;">- минимизируйте контакты со здоровыми людьми, особенно с пожилыми и лицами с хроническими заболеваниями. Ухаживать за больным лучше одному человеку,</p>\n' +
                            '<p style="text-align: left;">- пользуйтесь при кашле или чихании одноразовой салфеткой или платком, прикрывая рот. При их отсутствии чихайте в локтевой сгиб,</p>\n' +
                            '<p style="text-align: left;">- пользуйтесь индивидуальными предметами личной гигиены и одноразовой посудой,</p>\n' +
                            '<p style="text-align: left;">- обеспечьте в помещении влажную уборку с помощью дезинфицирующих средств и частое проветривание.</p>'
                    },
                    {
                        view: 'checkbox',
                        id: 'isProtect',
                        name: 'isProtect',
                        labelPosition: 'top',
                        invalidMessage: 'Поле не может быть пустым',
                        validate: function (val) {
                            if (val) return true
                            else return false
                        },
                        required: true,
                        css: 'boldLabel',
                        label: 'Подтверждаю обязательное выполнение предписания Управления Роспотребнадзора по Республике Бурятия',
                        on: {
                            onChange(newv, oldv) {
                                if ($$('isAgree').getValue() == 1 && $$('isProtect').getValue() == 1 ){
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
                        css: 'boldLabel',
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
                                disabled: true,
                                align: 'center',
                                click: function () {
                                    if($$('form').validate()) {
                                        let params = $$('form').getValues()

                                        if($$('lastname').getValue() && $$('firstname').getValue()
                                            && $$('patronymic').getValue() && $$('age').getValue())
                                        {
                                            if($$('person_table').find(function(obj){
                                                equalsRow(obj)
                                            }).length == 0)
                                            {
                                                if($$('age').getValue() > 65){
                                                    webix.alert("Данные не могут быть введены. Людям старше 65 лет предписана обязательная самоизоляция. Оставайтесь дома и будьте здоровы!")
                                                    return false;
                                                }
                                                $$('age').callEvent('onEnter')
                                            }
                                        }
                                        else if($$('person_table').data.pull.length == 0) {
                                            webix.message('Не заполнен список дачников', 'error')
                                            $$('add_btn').focus()
                                            return false
                                        }

                                        params.personList = []
                                        for(key in $$('person_table').data.pull) {
                                            let row = $$('person_table').data.pull[key]
                                            delete(row.id)
                                            params.personList.push(row)
                                        }

                                        params.validDate = webix.i18n.dateFormatStr(params.validDate)

                                        params.raion = getOptionsValue(params.raion)
                                        params.district = getOptionsValue(params.district)

                                        $$('label_sogl').showProgress({
                                            type: 'icon',
                                            delay: 5000
                                        })

                                        webix.ajax()
                                            .headers({'Content-type': 'application/json'})
                                            .post('/dacha',
                                                JSON.stringify(params),
                                                function (data) {
                                                    webix.alert({
                                                        title:"Ваше уведомление принято.",
                                                        ok:"Закрыть",
                                                        //cancel:"Внести еще уведомление",
                                                        text:data
                                                    }).then(function(result){
                                                        window.location.replace('http://работающаябурятия.рф');
                                                    }).fail(function(){
                                                        $$('label_sogl').hideProgress()
                                                        $$('form').clear()
                                                        $$('addform').clear()
                                                        $$('addform').hide()
                                                        $$('add_btn').show()
                                                        $$('close_btn').hide()
                                                        $$('person_table').clearAll()
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

    $$('addform').hide();
    $$('close_btn').hide();
    $$('validDate').getPopup().getBody().define('minDate', new Date());
    webix.extend($$('label_sogl'), webix.ProgressBar);
})