function request_subsidy() {
   let xhr = webix.ajax().sync().get("check_right_to_apply_request_subsidy");
   let result = xhr.responseText;
   if (result === 'false') {
       return no_right_form;
   } else if (result === 'true') {
       return request_subsidy_list;
   }
}

const request_subsidy_list = {
    rows: [
        {
            autowidth: true,
            autoheight: true,
            rows: [
                {
                    view: 'datatable',
                    id: 'request_subsidy_table',
                    minHeight: 570,
                    select: 'row',
                    navigation: true,
                    resizeColumn: true,
                    fixedRowHeight:false,
                    tooltip: true,
                    rowLineHeight:28,
                    pager: 'Pager',
                    datafetch: 25,
                    columns: [
                        {
                            id: "subsidyName",
                            template: "#subsidyName#",
                            header: "Мера поддержки",
                            tooltip: false,
                            minWidth: 300,
                            adjust: true,
                            fillspace: true,
                            sort: "text",
                        },
                        {
                            id: "subsidyRequestStatusName",
                            template: "#subsidyRequestStatusName#",
                            header: "Статус",
                            tooltip: false,
                            width: 200,
                            sort: "text",
                        },
                        {
                            id: "department",
                            header: "Рассматривает",
                            template: "#departmentName#",
                            tooltip: false,
                            minWidth: 150,
                            adjust: true,
                            sort: "text",
                        },
                        {
                            id: "time_Create",
                            header: "Дата создания",
                            minWidth: 150,
                            tooltip: false,
                            adjust: true,
                            format: dateFormatWOSeconds,
                            sort: 'date',
                        },
                        {
                            id: "time_Send",
                            header: "Дата подачи",
                            minWidth: 150,
                            tooltip: false,
                            adjust: true,
                            format: dateFormatWOSeconds,
                            sort: 'date',
                        },
                        {
                            id: "time_Review",
                            header: "Дата рассмотрения",
                            minWidth: 150,
                            tooltip: false,
                            adjust: true,
                            format: dateFormatWOSeconds,
                            sort: 'date',
                        },
                        {
                            tooltip: false,
                            width: 100,
                            header: "Действия",
                            template: function (obj) {
                                var actions = '<span webix_tooltip="Просмотреть" class="webix_icon mdi mdi-eye-outline"></span>'
                                if (obj.subsidyRequestStatusCode === "NEW") {
                                    actions = actions + '<span webix_tooltip="Редактировать" class="webix_icon mdi mdi-pencil-plus-outline"></span>' ;
                                }
                                return actions;
                            }
                        },
                    ],
                    scheme: {
                        $init: function (obj) {
                            if (obj.timeCreate != null) {
                                let tmp_time_create = obj.timeCreate.substr(0, 16);
                                obj.time_Create = tmp_time_create.replace("T", " ");
                                obj.time_Create = xml_format(obj.time_Create);
                            }

                            if (obj.timeReview != null) {
                                let tmp_time_review = obj.timeReview.substr(0, 16);
                                obj.time_Review = tmp_time_review.replace("T", " ");
                                obj.time_Review = xml_format(obj.time_Review);
                            }

                            if (obj.timeSend != null) {
                                let tmp_time_send = obj.timeSend.substr(0, 16);
                                obj.time_Send = tmp_time_send.replace("T", " ");
                                obj.time_Review = xml_format(obj.time_Review);
                            }

                            $$('request_subsidy_table').refresh()
                        }
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
                        onLoadError: function () {
                            this.hideOverlay();
                        },
                        'data->onStoreUpdated': function() {
                            this.adjustRowHeight(null, true);
                        },
                    },
                    onClick: {
                        "mdi-eye-outline": function (ev, id, html) {
                            let item = this.getItem(id);
                            setTimeout(function () {
                                showRequestSubsidyViewForm(item);
                            }, 100);
                        },
                        "mdi-pencil-plus-outline": function (ev, id, html) {
                            let item = this.getItem(id);
                            showBtnBack(request_subsidy_list, 'request_subsidy_table');
                            setTimeout(function () {
                                showRequestSubsidyCreateForm(item);
                            }, 100);
                        },
                        'data->onStoreUpdated': function() {
                            this.adjustRowHeight(null, true);
                        },
                    },
                    url: 'org_request_subsidies'
                },
                {
                    align: 'center, middle',
                    body:
                        {
                            id: 'requestPagerIn',              //pager responsive-target
                            rows: []
                        },
                },
                {
                    responsive: "requestPagerIn",
                    cols: [
                        {
                            view: 'pager',
                            id: 'Pager',
                            minWidth: 220,
                            height: 38,
                            size: 25,
                            group: 5,
                            template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                        },
                        {
                            view: 'button',
                            align: 'right',
                            minWidth: 220,
                            maxWidth: 200,
                            css: 'webix_primary',
                            value: 'Подать заявку',
                            click: function () {
                                const availableSubsidies = findAvailableSubsidies();
                                showBtnBack(request_subsidy_list, 'request_subsidy_table');
                                if (availableSubsidies.length > 0) {
                                    webix.ui({
                                        id: 'content',
                                        rows: [
                                            available_subsidy_list
                                        ]
                                    }, $$('content'));
                                    $$('availableSubsidyListId').parse(availableSubsidies);
                                } else {
                                    webix.ui({
                                        id: 'content',
                                        rows: [
                                            noAvailableSubsidiesForm
                                        ]
                                    }, $$('content'))
                                }
                            }
                        }
                    ]
                }
            ]
        }
    ]
}

const no_right_form = {
    rows: [
        {
            autowidth: true,
            autoheight: true,
            rows: [
                {
                    view: 'template',
                    id: 'noRightFormId',
                    // name: 'noRightFormId',
                    autoheight: true,
                    borderless: true,
                    url: function () {
                       let xhr = webix.ajax().sync().get('no_right_to_apply_request_subsidy_message');
                       // return xhr.responseText;
                        $$('noRightFormId').setHTML(xhr.responseText);
                    }
                },
                {}
            ]
        }
    ]
}

function showRequestSubsidyViewForm(data) {
    showBtnBack(request_subsidy_list, 'request_subsidy_table');
    webix.ui({
        id: 'content',
        rows: [
                {
                    id: 'form',
                    view: 'form',
                    complexData: true,
                    data: data,
                    elements: [
                        {
                            cols: [
                                {
                                    view: 'text',
                                    name: 'subsidyName',
                                    autoheight: true,
                                    // align: 'center',
                                    label: 'Мера поддержки',
                                    labelPosition: 'top',
                                    readonly: true
                                },
                            ]
                        },
                        {
                            view: 'text',
                            name: 'departmentName',
                            autoheight: true,
                            // align: 'center',
                            label: 'Рассматривает',
                            labelPosition: 'top',
                            readonly: true
                        },
                        view_section('Обоснование'),
                        {
                            rows: [
                                {
                                    view: 'textarea',
                                    name: 'reqBasis',
                                    height: 150,
                                    label: 'Обоснование заявки',
                                    readonly: true,
                                    labelPosition: 'top'
                                },
                            ]
                        },
                        view_section('Информация о рассмотрении'),
                        {
                            view: 'text',
                            name: 'subsidyRequestStatusName',
                            labelPosition: 'top',
                            readonly: true,
                            label: 'Статус',
                        },
                        {
                            view: 'textarea',
                            readonly: true,
                            name: 'resolutionComment',
                            id: 'resolutionComment',
                            label: 'Обоснование принятия или отказа',
                            labelPosition: 'top',
                            hidden: true,
                            height: 100
                        },
                        view_section('Прикрепленные файлы'),
                        {
                            id: 'filesListViewByType'
                        }
                    ]
                }
        ]
    }, $$('content'));

    getFilesListByTypeView(data.id);

    if (data.subsidyRequestStatusCode !== 'NEW' && data.subsidyRequestStatusCode !== 'SUBMIT') {
        $$('resolutionComment').show();
    }

}
