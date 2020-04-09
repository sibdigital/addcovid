define(['views/showform'], function(showform) {

    webix.i18n.setLocale("ru-RU");

    const DATE_FORMAT = webix.Date.dateToStr("%d.%m.%Y %H:%i:%s")

    return function(status) {
        return {
            autowidth: true,
            autoheight: true,
            //height: 800,
            //height: document.body.clientHeight - 100,
            //width: document.body.clientWidth - 8,
            rows: [
                {
                    view: 'datatable',
                    //height: document.body.clientHeight,
                    //height: autoheight,
                    //autoheight: true,
                    height: 650,
                    id: 'requests_table',
                    maxHeight: 750,
                    select: 'row',
                    resizeColumn: true,
                    pager: 'Pager',
                    datafetch: 16,
                    columns: [
                        {
                            id: "orgName",
                            header: "Название организации",
                            template: "#organization.name#",
                            adjust: true
                        },
                        {id: "inn", header: "ИНН", template: "#organization.inn#", adjust: true},
                        {id: "ogrn", header: "ОГРН", template: "#organization.ogrn#", adjust: true},
                        {id: "orgPhone", header: "Телефон", template: "#organization.phone#", adjust: true},
                        //{id: "orgPhone", header: "Обоснование заявки", template: "#organization.description#"},
                        {id: "personSlrySaveCnt", header: "Численность с сохранением зп", adjust: true},
                        {id: "personOfficeCnt", header: "Численность работающих", adjust: true},
                        {id: "personRemoteCnt", header: "Численность на удаленный режим", adjust: true},
                        {id: "time_Create", header: "Дата заявки", adjust: true, format: DATE_FORMAT }
                    ],
                    scheme: {
                        $init: function (obj) {
                            obj.time_Create = obj.timeCreate.replace("T", " ") //dateUtil.toDateFormat(obj.timeCreate);
                        },
                    },
                    on: {
                        onBeforeLoad: function () {
                            this.showOverlay("Загружаю...");
                        },
                        onAfterLoad: function () {
                            this.hideOverlay();
                            if (!this.count()) {
                                this.showOverlay("Отсутствуют данные")
                            }
                        },
                        onItemDblClick: function (id) {
                            let data = $$('requests_table').getItem(id);

                            queryWin = webix.ui({
                                view: 'window',
                                id: 'showQueryWin',
                                head: {
                                    view: 'toolbar',
                                    elements: [
                                        {view: 'label', label: 'Просмотр запроса (id: ' + data.id + ')'},
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
                                isSubmit: false,
                                //body: showform.showform(true),
                                body: showform(status == 0 ? false : true),
                                on: {
                                    'onHide': function () {
                                        if(this.config.isSubmit) {
                                            $$('requests_table').clearAll();
                                            $$('requests_table').load($$('requests_table').config.url)
                                        }
                                    }
                                }

                            });

                            data.attachmentFilename = data.attachmentPath.split('\\').pop().split('/').pop()
                            //data.attachmentFilename = data.attachmentPath.substring(data.attachmentPath.lastIndexOf("/"))

                            if(data.attachmentFilename != null && data.attachmentFilename != '') {
                                data.attachmentFilename = '<a href="' + LINK_PREFIX + data.attachmentFilename + LINK_SUFFIX + '" target="_blank">'
                                    + data.attachmentFilename + '</a>'
                            }
                            else {
                                data.attachmentFilename = ''
                                $$('filename_label').hide()
                            }

                            $$('form').parse(data)
                            $$('addr_table').parse(data.docAddressFact)
                            $$('person_table').parse(data.docPersonSet)

                            webix.extend($$('show_layout'), webix.ProgressBar);

                            queryWin.show()
                        }
                    },
                    //url: 'list_request/' + ID_DEPARTMENT + '/0'
                    url: 'list_request/' + ID_DEPARTMENT + '/' + status
                },
                {
                    view: 'pager',
                    id: 'Pager',
                    height: 38,
                    size: 16,
                    group: 5,
                    template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                }
            ]
        }
    }
})