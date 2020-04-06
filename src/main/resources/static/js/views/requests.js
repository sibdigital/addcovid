define(['views/showform'], function(showform) {
    return {
        autowidth: true,
        height: 600, //document.body.clientHeight,
        width: document.body.clientWidth - 8,
        rows: [
            {
                view: 'toolbar',
                height: 40,
                cols: [
                    {},
                    {
                        view: 'label',
                        label: '<span style="font-size: 1.5rem">ЕИС "Работающая Бурятия". Список заявок.</span>',
                    },
                    {}
                ]
            },
            {
                view: "datatable",
                height: document.body.clientHeight,
                id: 'requests_table',
                select: 'row',
                //autoConfig: true,
                columns: [
                    {id: "orgName", header: "Название организации", template: "#organization.name#"},
                    {id: "inn", header: "ИНН", template: "#organization.inn#"},
                    {id: "ogrn", header: "ОГРН", template: "#organization.ogrn#"},
                    {id: "orgPhone", header: "Телефон", template: "#organization.phone#"},
                    //{id: "orgPhone", header: "Обоснование заявки", template: "#organization.description#"},
                    {id: "personOfficeCnt", header: "personOfficeCnt"},
                    {id: "personRemoteCnt", header: "personRemoteCnt"},
                    {id: "personSlrySaveCnt", header: "personSlrySaveCnt"},
                    {id: "timeCreate", header: "Дата заявки"}
                ],
                on: {
                    onItemDblClick: function (id) {
                        let data = $$('requests_table').getItem(id);
                        console.log(data);
                        $$('head_label').config.item_data = data
                        //routie('showform')

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
                            modal: true,
                            body: showform,
                        });

                        $$('form').parse(data)
                        $$('addr_table').parse(data.docAddressFact)
                        $$('person_table').parse(data.docPersonSet)

                        queryWin.show()

                    }
                },
                url: "list_request/" + ID_DEPARTMENT
            }
        ]
    }
})