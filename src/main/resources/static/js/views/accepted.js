define(['views/showform'], function(showform) {
    const DATE_FORMAT = webix.Date.dateToStr("%d.%m.%Y")
    return {
        autowidth: true,
        height: 800, //document.body.clientHeight,
        width: document.body.clientWidth - 8,
        rows: [
            {
                view: "datatable",
                height: document.body.clientHeight,
                id: 'requests_table',
                select: 'row',
                //autoConfig: true,
                pager: 'Pager',
                resizeColumn:true,
                pager: 'Pager',
                datafetch: 15,
                columns: [
                    {id: "orgName", header: "Название организации", template: "#organization.name#", adjust: true},
                    {id: "inn", header: "ИНН", template: "#organization.inn#", adjust: true},
                    {id: "ogrn", header: "ОГРН", template: "#organization.ogrn#", adjust: true},
                    {id: "orgPhone", header: "Телефон", template: "#organization.phone#", adjust: true},
                    //{id: "orgPhone", header: "Обоснование заявки", template: "#organization.description#"},
                    {id: "personSlrySaveCnt", header: "Численность с сохранением зп", adjust: true},
                    {id: "personOfficeCnt", header: "Численность работающих", adjust: true},
                    {id: "personRemoteCnt", header: "Численность на удаленный режим", adjust: true},
                    {id: "timeCreate", header: "Дата заявки", adjust: true}
                ],
                on: {
                    onBeforeLoad: function () {
                        this.showOverlay("Загружаю...");
                    },
                    onAfterLoad: function () {
                        this.hideOverlay();
                        if (!this.count()) { this.showOverlay("Отсутствуют данные") };
                    },
                    onItemDblClick: function (id) {
                        let data = $$('requests_table').getItem(id);
                        console.log(data);

                        queryWin = webix.ui({
                            view: 'window',
                            id: 'showQueryWin',
                            head: {
                                view: 'toolbar',
                                elements: [
                                    {view: 'label', label: 'Просмотр запроса'},
                                    {
                                        view: 'icon', icon: 'wxi-close',
                                        click: function () {
                                            $$('showQueryWin').close()
                                        }
                                    }
                                ]
                            },
                            width: 1200,
                            height: 800,
                            position: 'center',
                            item: data,
                            modal: true,
                            body: showform,
                            on: {
                                'onHide': function () {
                                    $$('requests_table').clearAll();
                                    $$('requests_table').load($$('requests_table').config.url)
                                }
                            }

                        });

                        $$('form').parse(data)
                        $$('addr_table').parse(data.docAddressFact)
                        $$('person_table').parse(data.docPersonSet)

                        queryWin.show()

                    }
                },
                url: "list_request/" + ID_DEPARTMENT + '/1'
            },
            {
                view: 'pager',
                id: 'Pager',
                height: 38,
                size: 15,
                group: 5,
                template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
            }
        ]
    }
})