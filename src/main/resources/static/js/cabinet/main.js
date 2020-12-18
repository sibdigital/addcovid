webix.i18n.setLocale("ru-RU");

const dateFormat = webix.Date.dateToStr("%d.%m.%Y %H:%i:%s")
let userWindowHeight = window.innerHeight;

function view_section(title) {
    return {
        view: 'template',
        type: 'section',
        template: title
    }
}

let ID_TYPE_REQUEST = '';
let departmentId = '';
let uploadFile = '';
let uploadFilename = '';
let pred_date = new Date();
let upload_chack_error = 'Загружать можно только PDF-файлы и ZIP-архивы!';

webix.html.addStyle(
    ".personalTemplateStyle .webix_template {" +
    "font-family: Roboto, sans-serif;" +
    "font-size: 14px;" +
    "font-weight: 500;" +
    "color: #313131;" +
    "padding: 8px 3px !important;" +
    "}"+
    ".topMenuIcon .webix_icon:before{\n" +
    "color: #1992af;" +
    "font-size: 1.5rem;\n" +
    " }"
);

webix.attachEvent("onFocusChange", function (to, from) {
    if (from && from.getTopParentView().config.view === "window" && !to) {
        from.getTopParentView().hide();
    }
})

function setRequestsBadge(){
    webix.ajax("count_confirmed_requests").then(function(data){
        let requestsCount = data.json().length;
        let request = $$('menu').getItem("Requests");
        request.badge = requestsCount;
        $$('menu').updateItem("Requests", request);
    });
}

function getRequestStyles(){
    webix.ajax('/requests_status_style')
        .then(function (data) {
            let styleBody = Object.values(data.json())
            let styleClassName = Object.keys(data.json())
            for(let i in styleClassName){
                let style = '.' + styleClassName[i] + ' {' +
                    'background-color: ' + styleBody[i]['background-color'] +
                    '}'
                webix.html.addStyle(style)
            }
        })
}

let bigMainForm = {
    id: 'bigMainFormId',
    cols: [
        {
            width: 220,
            id: 'menuRow',
            rows: [
                {
                    view: 'label',
                    css: {
                        'background-color': '#565B67 !important',
                        'color': '#FFFFFF'
                    },
                    height: 46,
                    label: `<img height='36px' width='36px' style="padding: 0 0 2px 2px" src = \"favicon.ico\"><span style="color: white; font-size: 16px; font-family: Roboto, sans-serif;">${APPLICATION_NAME}</span>`,
                },
                {
                    view: 'menu',
                    id: 'menu',
                    css: 'my_menubar',
                    //collapsed: true,
                    layout: 'y',
                    data: [
                        {id: "CommonInfo", icon: "mdi mdi-information", value: 'Общая информация'},
                        {id: "Employees", icon: "mdi mdi-account-group", value: 'Сотрудники'},
                        {id: "Documents", icon: "mdi mdi-cloud-upload-outline", value: 'Документы'},
                        {id: "Address", icon: "mdi mdi-home-city-outline", value: 'Фактические адреса'},
                        {id: "Prescript", icon: "mdi mdi-text-box-check-outline", value: 'Предписания'},
                        {id: "Requests", icon: "wxi-file", value: 'Заявки', badge: setRequestsBadge()},
                        {id: "News", icon: "mdi mdi-message-plus-outline", value: 'Новости'},
                        {id: "Mailing", icon: "mdi mdi-email", value: 'Рассылки',},
                        {id: "Contacts", icon: "mdi mdi-book-open-blank-variant", value: 'Доп.контакты'},
                        {id: "Settings", icon: "mdi mdi-cogs", value: 'Настройки'},
                    ],
                    type: {
                        css: 'my_menubar_item',
                        height: 44
                    },
                    on: {
                        onMenuItemClick: function (id) {
                            let view;
                            let itemValue;
                            let requestsBadge = "";
                            let helpUrl = 'helps?key=' + id;
                            if (id == 'CommonInfo') {
                                view = commonInfo;
                            } else if (id == 'Requests') {
                                view = requests;
                                let checkReqBadge = this.getMenuItem(id).badge
                                if (checkReqBadge != null) {
                                    requestsBadge = "(" + checkReqBadge + ")";
                                }
                            } else if (id == 'Employees') {
                                view = employees;
                            } else if (id == 'Settings') {
                                view = settings;
                            } else if (id == 'Documents') {
                                view = documents;
                            } else if (id == 'Address') {
                                view = address;
                            } else if (id == 'Prescript') {
                                view = prescript;
                            } else if (id == 'News') {
                                view = news;
                            } else if (id == 'Contacts') {
                                view = contacts;
                            } else if (id == 'Mailing') {
                                view = mailing;
                            } else {
                                helpUrl = 'helps';
                            }
                            this.select(id)
                            if (view != null) {
                                webix.ui({
                                    id: 'content',
                                    rows: [
                                        view
                                    ]
                                }, $$('content'));
                                itemValue = this.getMenuItem(id).value
                                $$("labelLK").setValue("Личный кабинет > " + "<span style='color: #1ca1c1'>" + itemValue + " " + requestsBadge + "</span>");

                                $$('bigHelpId').setValue(helpUrl);
                                $$('bigHelpId').refresh();
                            }

                            // webix.ajax("/check_session").then(function (data){
                            //     if(data.text() == "Expired"){
                            //         //webix.send("/logout")
                            //     }
                            // })
                        }
                    }
                },
            ]
        },
        {
            rows: [

                {
                    view: 'toolbar',
                    id: 'toolbar',
                    // padding: 5,
                    height: 45,
                    elements: [

                        {
                            view: 'label',
                            id: 'labelLK',
                            align: 'left',
                            css: {"padding-left": "5px"},
                            label: 'Личный кабинет',
                        },
                        {
                            cols: [
                                {
                                    view: 'icon',
                                    icon: 'mdi mdi-account-circle',
                                    css: 'topMenuIcon',
                                    tooltip: 'Профиль',
                                    click: function () {
                                        webix.ui({
                                            id: 'content',
                                            rows: [
                                                commonInfo
                                            ]
                                        }, $$('content'))
                                    },
                                },
                                {
                                    view: 'icon',
                                    id: 'bigHelpId',
                                    css: 'topMenuIcon',
                                    icon: 'mdi mdi-help-circle',
                                    value: 'helps',
                                    tooltip: 'Помощь',
                                    click: function () {
                                        window.open(this.getValue())
                                    },
                                },
                                {
                                    view: 'icon',
                                    icon: 'mdi mdi-book-open',
                                    css: 'topMenuIcon',
                                    tooltip: 'Контакты ИОГВ',
                                    click: function () {
                                        // fix bug
                                        if ($$('dep_contacts_table') != null) {
                                            $$('dep_contacts_table').destructor();
                                        }

                                        webix.ui({
                                            id: 'content',
                                            rows: [
                                                depContacts
                                            ]
                                        }, $$('content'));
                                    },
                                },
                                {
                                    view: 'icon',
                                    css: 'topMenuIcon',
                                    icon: 'mdi mdi-exit-to-app',
                                    tooltip: 'Выход',
                                    click: function () {
                                        webix.send("/logout");
                                    },
                                },
                                ]
                        },
                    ]
                },
                {
                    id: 'content'
                }
            ],
        }
    ]
}

let smallMainForm = {
    id: 'smallMainFormId',
    cols: [
        {
            rows: [
                {
                    view: 'toolbar',
                    id: 'toolbar',
                    // padding: 5,
                    height: 45,
                    elements: [
                        {
                            align: 'left',
                            cols: [
                                {
                                    view: 'label',
                                    width: 40,
                                    template: "<img height='40px' width='40px' src = \"favicon.ico\">",
                                },
                                {
                                    view: 'label',
                                    label: `<span style="font-size: 16px; font-family: Roboto, sans-serif;">${APPLICATION_NAME}</span>`,
                                },
                                {
                                    view: 'icon',
                                    icon: 'mdi mdi-account-circle',
                                    tooltip: 'Профиль',
                                    click: function () {
                                        webix.ui({
                                            id: 'content',
                                            rows: [
                                                commonInfo
                                            ]
                                        }, $$('content'))
                                    },
                                },
                                {
                                    view: 'icon',
                                    id: 'smallHelpId',
                                    icon: 'mdi mdi-help-circle',
                                    css: 'topMenuIcon',
                                    value: 'helps',
                                    tooltip: 'Помощь',
                                    click: function () {
                                        window.open(this.getValue());
                                    },
                                },
                                {
                                    view: 'icon',
                                    icon: 'mdi mdi-book-open',
                                    css: 'topMenuIcon',
                                    tooltip: 'Контакты ИОГВ',
                                    click: function () {
                                        // fix bug
                                        if ($$('dep_contacts_table') != null) {
                                            $$('dep_contacts_table').destructor();
                                        }

                                        webix.ui({
                                            id: 'content',
                                            rows: [
                                                depContacts
                                            ]
                                        }, $$('content'))
                                    },
                                },
                                {
                                    view: 'icon',
                                    icon: 'mdi mdi-exit-to-app',
                                    css: 'topMenuIcon',
                                    tooltip: 'Выход',
                                    click: function () {
                                        webix.send("/logout");
                                    },
                                },
                            ]
                        },
                    ]
                },
                {
                    view: 'menu',
                    id: 'menu',
                    css: 'my_menubar',
                    //collapsed: true,
                    data: [
                        {id: "CommonInfo", value: 'Общая информация',},
                        {id: "Employees", value: 'Сотрудники',},
                        {
                            value: "<span class='mdi mdi-dots-horizontal'></span>",
                            submenu: [
                                {id: "Documents", icon: "mdi mdi-cloud-upload-outline", value: 'Документы'},
                                {
                                    id: "Address",
                                    icon: "mdi mdi-home-city-outline",
                                    value: 'Фактические адреса'
                                },
                                {
                                    id: "Prescript",
                                    icon: "mdi mdi-text-box-check-outline",
                                    value: 'Предписания'
                                },
                                {
                                    id: "Requests",
                                    icon: "wxi-file",
                                    value: 'Заявки',
                                    badge: setRequestsBadge()
                                },
                                {id: "News", icon: "mdi mdi-message-plus-outline", value: 'Новости'},
                                {id: "Mailing", icon: "mdi mdi-email", value: 'Рассылки',},
                                {
                                    id: "Contacts",
                                    icon: "mdi mdi-book-open-blank-variant",
                                    value: 'Доп. контакты'
                                },
                                {id: "Settings", icon: "mdi mdi-cogs", value: 'Настройки'},
                            ]
                        }

                    ],
                    type: {
                        css: 'my_menubar_item',
                        height: 44
                    },
                    on: {
                        onMenuItemClick: function (id) {
                            let view;

                            if (id == 'CommonInfo') {
                                view = commonInfo;
                            } else if (id == 'Requests') {
                                view = requests;
                            } else if (id == 'Employees') {
                                view = employees;
                            } else if (id == 'Settings') {
                                view = settings;
                            } else if (id == 'Documents') {
                                view = documents;
                            } else if (id == 'Address') {
                                view = address;
                            } else if (id == 'Prescript') {
                                view = prescript;
                            } else if (id == 'News') {
                                view = news;
                            } else if (id == 'Mailing') {
                                view = mailing;
                            } else if (id == 'Contacts') {
                                view = contacts;
                            }
                            if (view != null) {
                                webix.ui({
                                    id: 'content',
                                    rows: [
                                        view
                                    ]
                                }, $$('content'));
                                $$('smallHelpId').setValue('helps?key=' + id);
                                $$('smallHelpId').refresh();
                            }
                        }
                    }
                },
                {
                    id: 'content'
                }
            ],
        }
    ]
}

webix.ready(function () {
    let layout;
    getRequestStyles();
    var checkfrm = checkConsentPersonalDataProc();
    if (checkfrm) {
        if (document.body.clientWidth < 760) {
            layout = webix.ui(smallMainForm)
        } else {
            layout = webix.ui(bigMainForm);
        }
    }else{
        webix.ui(consentPersonalDataModal).show();
    }
    webix.event(window, "resize", function (event) {
        layout.define("width", document.body.clientWidth);
        layout.resize();
    });

    //responsive events
    webix.attachEvent("onResponsiveHide", function (id) {

        if (id === "employees_table" || id === "personOfficeCnt") {

            adaptiveEmployees()

        } else if (id === "inn") {

            adaptiveCommonInfo()

        }

        let contentChild = $$('content').getChildViews()[0]
        let contentChildName = contentChild.config.name

        if (id === "addAddressInfo" && contentChildName === 'showRequestCreateForm') {

            adaptiveRequests()

        }

    })

})