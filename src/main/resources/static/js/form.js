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
    var layout = webix.ui({
        container: 'app',
        autowidth: true,
        height: document.body.clientHeight,
        width: document.body.clientWidth - 8,
        rows: [
            {
                view: 'toolbar',
                //borderless: true,
                height: 40,
                //align: 'center',
                cols: [
                    {
                        /*
                                                view: 'template',
                                                width: 20,
                                                borderless: true
                        */
                    },
                    {
                        view: 'label',
                        label: '<span style="font-size: 1.5rem">Работающая Бурятия. Подача заявки.</span>',
                        //css: 'main_label'
                    },
                    {}
                ]
            },
            {
                view: 'label',
                label: '<a style="font-size: 1.5rem; text-align: center;" href="http://работающаябурятия.рф/#top" target="_blank">Горячая линия. </a>'
                + '&nbsp&nbsp&nbsp <a style="font-size: 1.5rem; text-align: center;" href="http://работающаябурятия.рф/doc.pdf" target="_blank">Инструкция по заполнению формы </a>'
                + '&nbsp&nbsp&nbsp <a style="font-size: 1.5rem; text-align: center;" href="http://работающаябурятия.рф/faq.html" target="_blank">Часто задаваемые вопросы</a>'

            },
            {
                view: 'label',
                label: '<span  style="text-align: center;">Уважаемые пользователи!</span>',
                //css: 'main_label'
            },
            {
                view: 'label',
                label: '<span  style="text-align: center;">При подаче заявки на 100 и более человек обязательно используйте шаблон для заполнения! ' +
                    '<a  style="text-align: center;" href="http://работающаябурятия.рф/zayvka.xlsx" target="_blank">Скачать шаблон </a>&nbsp&nbsp&nbsp' +
                    '<a  style="text-align: center;" href="http://работающаябурятия.рф/doc_excel.pdf" target="_blank">Инструкция по заполнению шаблона Excel </a></span>'
                //css: 'main_label'
            },
            {
                view: 'label',
                label: '<span  style="text-align: center;">' +
                    '<a  style="text-align: center;" href="http://form.govrb.ru/upload" target="_blank">Форма ввода с шаблоном Excel</a>  </span>'
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
                                        label: 'Краткое наименование организации',
                                        labelPosition: 'top',
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true
                                    },
                                    {
                                        view: 'text',
                                        name: 'organizationInn',
                                        label: 'ИНН',
                                        labelPosition: 'top',
                                        validate: function(val){
                                            return !isNaN(val*1);
                                        },
                                        //attributes:{ type:"number" },
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true
                                    },
                                    {
                                        view: 'text',
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
                                        view: 'select',//'combo',
                                        name: 'departmentId',
                                        label: 'Министерство, курирующее вашу деятельность',
                                        labelPosition: 'top',
                                        invalidMessage: 'Поле не может быть пустым',
                                        required: true,
                                        options: 'cls_departments',
                                        // options: [
                                        //     { id: 4, value: 'Минпром РБ (1. Машиностроение и металообработка...)'},
                                        //     { id: 2, value: 'Мин.экон РБ (В сфере финансовой, страховой деятельности)'},
                                        //     { id: 3, value: 'Мин.имущества РБ ("оценочная деятельность, деятельность кадастровых инженеров")'},
                                        //     { id: 5, value: 'Мин.природных ресурсов РБ (Предприятия добывающей промышленности, имеющие непрерывный )'},
                                        //     { id: 6, value: 'Мин.сельхоз РБ'},
                                        //     { id: 7, value: 'Мин.строй РБ (Строительство: Организации (в том числе работающие с ними по договорам подряда и/или оказания услуг юридические лица и индивидуальные предприниматели):)'},
                                        //     { id: 8, value: 'Мин.транс РБ (Сфера транспорта, энергетики, связи и дорожного хозяйства, а также в области энергосбережения и повышения энергетической эффективности в сфере транспорта, энергетики, связи и дорожного хозяйства)'},
                                        //     { id: 9, value: 'Мин.соцзащиты РБ (Организации социального обслуживания населения)'},
                                        //     { id: 10, value: 'Мин.здрав РБ (Организации по техническому обслуживанию медицинского оборудования)'},
                                        //     { id: 11, value: 'Мин.культ РБ (Нет курируемых предприятий/организаций)'},
                                        //     { id: 12, value: 'Мин.обр РБ (1. учреждения дошкольного образования, где функционируют дежурные группы. 2. Учреждения среднего общего образования, где очно-заочное обучение для 9,11 классов.)'},
                                        //     { id: 13, value: 'Мин.спорта РБ (1. Содержание, эксплуатация и обеспечение безопасности на спортивных объектов 2. Строительство спортивных объектов 3. Волонтерская деятельность)'},
                                        //     { id: 14, value: 'Мин.туризма РБ (1. Санаторно-курортная сфера 2. Гостиничный комплекс 3. Туроператоры, турагентства, экскурсоводы")'},
                                        //     { id: 1, value: 'Мин.фин РБ (Нет курируемых предприятий/организаций)'},
                                        //     { id: 15, value: 'РАЛХ (Выполнение мероприятий по использованию, охране, защите, воспроизводству лесов, лесозаготовка, лесопереработка)'},
                                        //     { id: 16, value: 'Управление ветеринарии (Ветеринарные клиники, ветеринарные аптеки, ветеринарные кабинеты, зоомагазины, организации, занимающиеся отловом животных без владельцев)'},
                                        //     { id: 17, value: 'Управление МЧС по РБ (Организации, осуществляющие деятельность в сфере противопожарной безопасности)'},
                                        //     { id: 18, value: 'Росгвардия (Частные охранные предприятия)'},
                                        // ]
                                    },
                                    {
                                        view: 'textarea',
                                        label: '* области деятельности министерств',
                                        labelPosition: 'top',
                                        height: 150,
                                        readonly: true,
                                        value: '№\tНаименование органа власти\tОписание\n' +
                                            '1\tМинистерство финансов Республики Бурятия\tНет курируемых предприятий/организаций\n' +
                                            '2\tМинистерство экономики Республики Бурятия\tВ сфере финансовой, страховой деятельности\n' +
                                            '3\tМинистерство имущественных и земельных отношений  Республики Бурятия\t"оценочная деятельность,\n' +
                                            'деятельность кадастровых инженеров"\n' +
                                            '4\tМинистерство промышленности и торговли Республики Бурятия\t"1. Машиностроение и металообработка\n' +
                                            '2. Легкая промышленность\n' +
                                            '3. Промышленность строительных материалов\n' +
                                            '4. Целлюлюзно-бумажное производство\n' +
                                            '5. Деревообработка, лесопромышленный комплекс\n' +
                                            '6. Торговля\n' +
                                            '7. Общественное питание\n' +
                                            '8. Бытовые услуги\n' +
                                            '9. Ритуальные услуги\n' +
                                            '10. Ремонт автотранспортных средств\n' +
                                            '11. Траспортировка, хранение и логистические услуги, оказываемые органиациям торговли и общественного питания\n' +
                                            '12. Организации инфраструктуры поддержки МСП"\n' +
                                            '5\tМинистерство природных ресурсов Республики Бурятия\t"Предприятия добывающей промышленности, имеющие непрерывный производственный процесс или обеспечивающие углем объекты ЖКХ и энергетики, или иные предприятия, осуществляющие добычу полезных ископаемых в удалении от населённых пунктов при условии соблюдения режима самоизоляции на месте ведения работ;\n' +
                                            '- Предприятия и организации, осуществляющие мероприятия по предотвращению негативного воздействия вод;\n' +
                                            '- Предприятия, оказывающие услуги в сфере ЖКХ по содержанию санитарного состояния территорий (обращение с отходами);\n' +
                                            '- Организации (хозяйствующие субъекты), осуществляющие работы по охране, защите, воспроизводству лесов и тушению лесных пожаров.\n' +
                                            '"\n' +
                                            '6\tМинистерство сельского хозяйства и продовольствия Республики Бурятия\t"1) Организации осуществляющие производство, реализацию и хранение сельскохозяйственной продукции, продуктов ее переработки (включая продукты питания) удобрений, средств защиты растений, кормов и кормовых добавок, семян и посадочного материала;\n' +
                                            '2) Организации, осуществляющие формирование товарных запасов сельскохозяйственной продукции и продовольствия на будущие периоды;\n' +
                                            '3) Организации, занятые на сезонных полевых работах,\n' +
                                            '4) Рыбодобывающие, рыбоперерабатывающие предприятия, рыбоводные хозяйства, организации обслуживающие суда рыбопромыслового флота;\n' +
                                            '5) Животноводческие хозяйства, организации по искусственному осеменению сельскохозяйственных животных, производству, хранению и реализации семени сельскохозяйственных животных и перевозке криоматериала для искусственного осеменения животных;\n' +
                                            '6) Организации, осуществляющие лечение, профилактику, диагностику болезней животных, в т.ч. проводящие ветеринарные и ветринарно-санитарные экспертизы;\n' +
                                            '7) Организации, осуществляющие производство, обращение и хранение ветеринарных лекарственных средств диагностики болезней животных, зоотоваров;\n' +
                                            '8) Организации, осуществляющие реализацию сельскохозяйственной техники и ее техническое обслуживание или ремонт, в т.ч. машинотракторные станции;\n' +
                                            '9) Предприятия пищевой и перерабатывающей промышленности;\n' +
                                            '10) Организации, осуществляющие поставку ингредиентов, упаковки, сервисное обслуживание оборудования, а также компании занятые в перевозках погрузочно-разгрузочных работах, оказывающих логистические и сервисные услуги в указанных выше сферах;\n' +
                                            '11) Организации, осуществляющие иные виды деятельности, направленные на обеспечение продовольственной безопасности Российской Федерации"\n' +
                                            '7\tМинистерство строительства и модернизации жилищно-коммунального комплекса Республики Бурятия\t"Строительство:\n' +
                                            'Организации (в том числе работающие с ними по договорам подряда и/или оказания услуг юридические лица и индивидуальные предприниматели):\n' +
                                            '• С которыми заключены гос. или мун. контракты на строительство, реконструкцию объектов капитального строительства, проведение инженерных, экологических изысканий, разработку проектной документации;\n' +
                                            '• Осуществляющие строительство объектов кап. строительства в рамкам концессионных соглашений, заключенных Правительством РБ;\n' +
                                            '• Осуществляющие строительство многоквартирных домов, разрешение на строительство которых получено до 01.04.2020 года;\n' +
                                            '• Осуществляющие кап. ремонт общего имущества многоквартирных домов (за исключением домов, в которых  подтвержден факт заражения проживающего короновирусной инфекцией);\n' +
                                            'Юр. лица и/или ИП - изготовители и поставщики строительных материалов, изделий, оборудования, инструментов и расходных материалов к ним, а также авторизованные сервисные центры по обслуживанию и ремонту, для вышеуказанных организаций и/или индивидуальных предпринимателей.\n' +
                                            'Юр. лица и/или ИП, оказывающие услуги по предоставлению грузоподъемных машин и механизмов, и автотранспорта для обслуживания объектов вышеуказанных организаций и/или индивидуальных предпринимателей.\n' +
                                            'ЖКК:\n' +
                                            'Юр. лица и/или ИП, осуществляющие поставку твердого, жидкого, газового топлива, а также предприятия, осуществляющие их доставку.\n' +
                                            'Юр. лица и/или ИП, привлекаемые к аварийно-восстановительным работам, в части использования спецтехники, механизмов и оборудования, а также персонала обслуживающего указанное (исключительно в период проведения таких работ)."\n' +
                                            '8\tМинистерство по развитию транспорта, энергетики и дорожного хозяйства Республики Бурятия\tСфера транспорта, энергетики, связи и дорожного хозяйства, а также в области энергосбережения и повышения энергетической эффективности в сфере транспорта, энергетики, связи и дорожного хозяйства\n' +
                                            '9\tМинистерство социальной защиты населения Республики Бурятия\tОрганизации социального обслуживания населения\n' +
                                            '10\tМинистерство здравоохранения Республики Бурятия\tОрганизации по техническому обслуживанию медецинского оборудования\n' +
                                            '11\tМинистерство культуры Республики Бурятия\tНет курируемых предприятий/организаций\n' +
                                            '12\tМинистерство образования и науки Республики Бурятия\t"1. учреждения дошкольного образования, где функционируют дежурные группы. \n' +
                                            '2. Учреждения среднего общего образования, где очно-заочное обучение для 9,11 классов."\n' +
                                            '13\tМинистерство спорта и молодежной политики Республики Бурятия\t"1. Содержание, эксплуатация и обеспечение безопасности на спортивных объектов\n' +
                                            '2. Строительство спортивных объектов\n' +
                                            '3. Волонтерская деятельность"\n' +
                                            '14\tМинистерство туризма Республики Бурятия\t"1. Санаторно-курортная сфера\n' +
                                            '2. Гостиничный комплекс\n' +
                                            '3. Туроператоры, турагентства, экскурсоводы"\n' +
                                            '15\tРеспубликанское агентство лесного хозяйства\tВыполнение мероприятий по использованию, охране, защите, воспроизводству лесов, лесозаготовка, лесопереработка\n'
                                    }
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
                                view: 'text', name: 'personSlrySaveCnt',
                                label: 'Суммарная численность работников, в отношении которых установлен режим работы нерабочего дня с сохранением заработной платы',
                                labelPosition: 'top',
                                validate: function(val){
                                    return !isNaN(val*1) && (val.trim() !== '')
                                },
                                invalidMessage: 'Поле не может быть пустым',
                                required: true
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
                        template: '<p style="text-align: center;"><strong>ПРЕДПИСАНИЕ&nbsp;Управления Роспотребнадзора по Республике Бурятия</strong></p>\n' +
                            '<p style="text-align: center;"><strong>о проведении дополнительных санитарно-противоэпидемических (профилактических) мероприятий</strong></p>\n' +
                            '<p style="text-align: right;">&laquo;10&raquo; апреля 2020 г. №60 г. Улан-Удэ</p>\n' +
                            '<p>В целях обеспечения санитарно-эпидемиологического благополучия населения, предупреждения распространения случаев новой коронавирусной инфекции (COVID-19), руководствуясь ч.2 ст.50 Федерального закона от 30.03.1999 № 52-ФЗ &laquo;О санитарно-эпидемиологическом благополучии населения&raquo;; постановлениями Главного государственного санитарного врача Российской Федерации о дополнительных мерах по снижению рисков завоза и распространения новой коронавирусной инфекции (COVID-2019), СП 3.4.2318-08 &laquo;Санитарная охрана территории Российской Федерации&raquo;, утв. постановлением Главного государственного санитарного врача РФ №3 22.01.2008; СП 3.1./3.2.3146-13 &laquo;Общие требования по профилактике инфекционных и паразитарных болезней&raquo;, утв. постановлением Главного государственного санитарного врача РФ 16.12.2013 №65; СП 3.5.1378-03 &laquo;Санитарно-эпидемиологические требования к организации и осуществлению дезинфекционной деятельности&raquo;, утв. постановлением Главного государственного санитарного врача РФ 09.06.2003 №131</p>\n' +
                            '<p style="text-align: center;"><strong>ПРЕДПИСЫВАЮ: </strong></p>\n' +
                            '<p>1. Обеспечить при входе работников в организацию (предприятие) &ndash; возможность обработки рук кожными антисептиками, предназначенными для этих целей или дезинфицирующими салфетками с установлением контроля за соблюдением этой гигиенической процедуры;</p>\n' +
                            '<p>2. Проводить контроль температуры тела работников перед началом рабочего дня при входе в организацию (предприятие), а также при необходимости в течение рабочего дня, с обязательным отстранением от нахождения на рабочем месте лиц с повышенной температурой тела и с признаками инфекционного заболевания, в том числе ОРВИ;</p>\n' +
                            '<p>3. Проводить качественную уборку помещений с применением дезинфицирующих средств, зарегистрированных в установленном порядке, уделив особое внимание дезинфекции дверных ручек, выключателей, поручней, столов, оборудования и других контактных поверхностей, мест общего пользования с кратностью каждые 2 часа;</p>\n' +
                            '<p>4. Проводить дезинфекцию наружных поверхностей эксплуатируемых зданий и объектов (площадки у входа, наружные двери, поручни, малые архитектурные формы, урны) с кратностью каждые 2 часа;</p>\n' +
                            '<p>5. Обеспечить использование рабочих растворов дезинфицирующих средств в соответствии с инструкцией по их применению, выбирая режимы, предусмотренные для обеззараживания объектов при вирусных инфекциях;</p>\n' +
                            '<p>6. Предусмотреть в помещениях туалетов промаркированные дозаторы с дезинфицирующими средствами, активными в отношении вирусов;</p>\n' +
                            '<p>7. Обеспечить регулярное проветривание рабочих помещений с периодичностью каждые 2 часа;</p>\n' +
                            '<p>8. Обеспечить персонал средствами индивидуальной защиты, по возможности антисептическими средствами для обработки рук и осуществлять контроль за их использованием;</p>\n' +
                            '<p>9. Соблюдать социальное дистанцирование (не менее 1,5 метров между людьми), специальный режим допуска и нахождения в помещениях и на прилегающей территории организаций, довести до каждого работника информацию о необходимости соблюдения указанных режимов.</p>\n' +
                            '<p>Ответственность за выполнение предписания возлагается на хозяйствующий субъект, получивший в &laquo;Работающая Бурятия&raquo; разрешение на работу в период самоизоляции.</p>\n' +
                            '<p style="text-align: center;">должность, фамилия, имя, отчество должностного лица либо гражданина</p>\n' +
                            '<p>О мерах, принятых во исполнение предписания сообщить в адрес Управления Роспотребнадзора по Республике Бурятия не позднее 3х дней с момента получения в &laquo;Работающая Бурятия&raquo; разрешения на работу по электронной почте: <a href="mailto:org@03.rospotrebnadzor.ru. ">org@03.rospotrebnadzor.ru. </a></p>\n' +
                            '<p>Невыполнение в установленный срок настоящего предписания влечет административную ответственность в соответствии с частью 1 статьи 19.4 Кодекса Российской Федерации об административных правонарушениях.</p>\n' +
                            '<p>Предписание может быть обжаловано в суд общей юрисдикции, Арбитражный суд, в вышестоящий орган государственного контроля (надзора), вышестоящему должностному лицу в установленном законодательством порядке.</p>'
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
                                                .post('/form',
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
                                                            $$('label_sogl').hideProgress()
                                                        }else{
                                                            webix.confirm({
                                                                title:"Заявка внесена",
                                                                ok: "Закрыть",
                                                                cancel: "Внести еще одну заявку",
                                                                text: text
                                                            })
                                                                .then(function () {
                                                                    $$('label_sogl').hideProgress();
                                                                    window.location.replace('http://работающаябурятия.рф');
                                                                })
                                                                .fail(function(){
                                                                    $$('label_sogl').hideProgress()
                                                                    $$('form').clear()
                                                                    $$('upload').setValue()
                                                                    $$('form_person').clear()
                                                                    $$('form_addr').clear()
                                                                    $$('addr_table').clearAll()
                                                                    $$('person_table').clearAll()
                                                                    $$('organizationName').focus()
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
    });

    $$('form_person').bind('person_table');
    $$('form_addr').bind('addr_table');
    webix.event(window, "resize", function (event) {
        layout.define("width",document.body.clientWidth);
        layout.resize();
    });
    webix.extend($$('label_sogl'), webix.ProgressBar);
})