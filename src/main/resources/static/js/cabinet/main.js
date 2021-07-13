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
let btnBackHandler = null;

webix.html.addStyle(
    ".personalTemplateStyle .webix_template {" +
    "font-family: Roboto, sans-serif;" +
    "font-size: 14px;" +
    "font-weight: 500;" +
    "color: #313131;" +
    "padding: 8px 3px !important;" +
    "}"
);

webix.attachEvent("onFocusChange", function (to, from) {
    if (from && from.getTopParentView().config.view === "window" && !to) {
        from.getTopParentView().hide();
    }
})

let UsercontextMenu = webix.ui({
    view: "contextmenu",
    css: 'user_menu_items',
    data: [
        {id: "Profile",  value: 'Профиль'},
        // {id: "Contacts", value: 'Контактная информация'},
        {id: "Mailing", value: 'Рассылки', badge: setRequestsBadge()},
        {id: "Help", value: 'Помощь'},
        {id: "Settings", value: 'Настройки'},
        {id: "DepContacts", value: 'Контакты подразделений'},
        { $template:"Separator" },
        {id: 'Exit', value: 'Выход'}
        ], //"Изменить аватар", ,
    on: {
        onMenuItemClick: function (id) {
            let view;
            if (id === 'Profile') {
                view = profile;
            } else if (id === 'Contacts') {
                view = contacts;
            } else if (id === 'Mailing') {
                view = mailing;
            } else if (id == 'DepContacts') {
                view = depContacts;
            } else if (id === 'Help') {
                window.open('helps', '_blank');
            } else if (id === 'Exit') {
                webix.send("logout");
            }
            if (view != null) {
                webix.ui({
                    id: 'content',
                    rows: [
                        view
                    ]
                }, $$('content'))
            }
        }
    }
});

function showDropDownMenu(span){
    if(span.offsetWidth < 100){
        UsercontextMenu.config.width = 190; UsercontextMenu.resize();
    }else{
        UsercontextMenu.config.width = span.offsetWidth+85; UsercontextMenu.resize();
    }
    let toolBarHeight = $$('toolbar').config.height - 5;
    UsercontextMenu.show({
        x:document.body.clientWidth-span.offsetWidth-50,y:toolBarHeight
    })
}

function setRequestsBadge(){
    // webix.ajax("count_confirmed_requests").then(function(data){
    //     let requestsCount = data.json().length;
    //     let request = $$('menu').getItem("Requests");
    //     request.badge = requestsCount;
    //     $$('menu').updateItem("Requests", request);
    // });
}

function setPrescriptionBadge(){
    webix.ajax("count_non_consent_prescriptions").then(function(data){
        let prescriptCount = data.json().count;
        let prescript = $$('menu').getItem("Prescript");
        if (prescriptCount == 0) {
            prescript.badge = false;
        }
        else {
            prescript.badge = prescriptCount;
        }
        $$('menu').updateItem("Prescript", prescript);
    });
}

function getRequestStyles(){
    webix.ajax('requests_status_style')
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

function hideBtnBack() {
    $$('btnBackMainId').hide();
}

let btnBack = {
    id: 'btnBackMainId',
    view: 'button',
    label: 'Назад',
    maxWidth: 100,
    align: 'left',
    type: 'icon',
    icon: 'mdi mdi-arrow-left',
    css: 'backBtnStyle',
    hidden: true,
    click: function () {

    }
}

function showBtnBack(view, tableId) {
    $$('btnBackMainId').show();
    if (btnBackHandler != null) {
        $$('btnBackMainId').detachEvent(btnBackHandler);
    }
    btnBackHandler = $$('btnBackMainId').attachEvent("onItemClick", function(id, e) {
        if ($$(tableId) != null) {
            $$(tableId).destructor();
        }

        webix.ui({
            id: 'content',
            rows: [
                view
            ]
        }, $$('content'));

        if (view != archiveNews) {
            $$('btnBackMainId').hide();
            if (view == news) {
                $$("labelLK").setValue("Личный кабинет > " + "<span style='color: #1ca1c1'>" + "Новости" + "</span>");
            }
        } else {
            showBtnBack(news,)
            $$("labelLK").setValue("Личный кабинет > " + "<span style='color: #1ca1c1'>" + "Архив новостей" + "</span>");
        }


        return false;
    });
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
                        {id: "Profile", icon: "mdi mdi-account-circle", value: 'Профиль'},
                        {id: "Employees", icon: "mdi mdi-account-group", value: 'Сотрудники'},
                        {id: "Documents", icon: "mdi mdi-cloud-upload-outline", value: 'Документы'},
                        {id: "Address", icon: "mdi mdi-home-city-outline", value: 'Фактические адреса'},
                        {id: "Prescript", icon: "mdi mdi-text-box-check-outline", value: 'Предписания',  badge: setPrescriptionBadge()},
                        {id: "Requests", icon: "wxi-file", value: 'Заявки',
                            // badge: setRequestsBadge()
                        },
                        {id: 'Inspections', icon: 'mdi mdi-clipboard-text-multiple', value: 'Мои проверки'},
                        {id: "News", icon: "mdi mdi-message-plus-outline", value: 'Новости'},
                        {id: "Mailing", icon: "mdi mdi-email", value: 'Рассылки',},
                        {id: "Contacts", icon: "mdi mdi-book-open-blank-variant", value: 'Доп.контакты'},
                        {id: "Subsidy_Files", value: 'Файлы заявки'},
                        {id: "subs", value: 'Файлы заявки webix'}
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
                            let prescriptBadge = "";
                            let helpUrl = 'helps?key=' + id;
                            if (id == 'Profile') {
                                view = profile;
                            } else if (id == 'Requests') {
                                view = requests;
                                // let checkReqBadge = this.getMenuItem(id).badge
                                // if (checkReqBadge != null) {
                                //     requestsBadge = "(" + checkReqBadge + ")";
                                // }
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
                                let checkReqBadge = this.getMenuItem(id).badge
                                if (checkReqBadge != null && checkReqBadge != false) {
                                    prescriptBadge = "(" + checkReqBadge + ")";
                                }

                                // fix bug при двойном клике пустая таблица выходила
                                if ($$('prescriptions_table') != null) {
                                    $$('prescriptions_table').destructor();
                                }
                            } else if (id == 'Inspections'){
                                view = inspectionList;
                            } else if (id == 'News') {
                                view = news;
                            } else if (id == 'Contacts') {
                                view = contacts;
                            } else if (id == 'Mailing') {
                                view = mailing;
                                // fix bug при двойном клике пустая таблица выходила
                                if ($$('my_mailing_table') != null) {
                                    $$('my_mailing_table').destructor();
                                }
                            } else if (id == 'Subsidy_Files') {
                                view = subsidy_files_upload();
                            } else if (id == 'subs') {
                                view = subs();
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
                                $$("labelLK").setValue("Личный кабинет > " + "<span style='color: #1ca1c1'>" + itemValue + " " + requestsBadge + prescriptBadge + "</span>");

                                $$('bigHelpId').setValue(helpUrl);
                                $$('bigHelpId').refresh();
                            }
                            hideBtnBack();

                            // webix.ajax("check_session").then(function (data){
                            //     if(data.text() == "Expired"){
                            //         //webix.send("logout")
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
                        btnBack,
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
                                                profile
                                            ]
                                        }, $$('content'))

                                        $$("labelLK").setValue("Личный кабинет > " + "<span style='color: #1ca1c1'>" + "Профиль" + "</span>");
                                        $$('menu').unselectAll();
                                        hideBtnBack();
                                    },
                                },
                                {
                                    view: 'icon',
                                    icon: 'mdi mdi-clipboard-text-multiple',
                                    css: 'topMenuIcon',
                                    tooltip: 'Мои проверки',
                                    click: function () {
                                        if ($$('inspections_table') != null) {
                                            $$('inspections_table').destructor();
                                        }
                                        changeContentView(inspectionList);
                                        $$("labelLK").setValue("Личный кабинет > " + "<span style='color: #1ca1c1'>" + "Мои проверки" + "</span>");
                                        $$('menu').unselectAll();
                                        hideBtnBack();
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
                                        // fix bug при двойном клике пустая таблица выходила
                                        if ($$('dep_contacts_table') != null) {
                                            $$('dep_contacts_table').destructor();
                                        }

                                        webix.ui({
                                            id: 'content',
                                            rows: [
                                                depContacts
                                            ]
                                        }, $$('content'));

                                        $$("labelLK").setValue("Личный кабинет > " + "<span style='color: #1ca1c1'>" + "Контакты ИОГВ" + "</span>");
                                        $$('menu').unselectAll();
                                        hideBtnBack();
                                    },
                                },
                                {
                                    view: 'icon',
                                    css: 'topMenuIcon',
                                    icon: 'mdi mdi-exit-to-app',
                                    tooltip: 'Выход',
                                    click: function () {
                                        webix.send("logout");
                                    },
                                },
                                ]
                        },
                    ]
                },
                {
                    view: 'scrollview',
                    scroll: 'xy',
                    body: {
                        padding: 20,
                        rows: [
                            {id: 'content'}
                        ]
                    }
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
                                    css: 'topMenuIcon',
                                    click: function () {
                                        webix.ui({
                                            id: 'content',
                                            rows: [
                                                profile
                                            ]
                                        }, $$('content'));
                                        $$('menu').unselectAll();
                                        hideBtnBack();
                                    },
                                },
                                {
                                    view: 'icon',
                                    icon: 'mdi mdi-clipboard-text-multiple',
                                    tooltip: 'Мои проверки',
                                    css: 'topMenuIcon',
                                    click: function () {
                                        if ($$('inspections_table') != null) {
                                            $$('inspections_table').destructor();
                                        }
                                        changeContentView(inspectionList);
                                        $$('menu').unselectAll();
                                        hideBtnBack();
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
                                        // fix bug при двойном клике пустая таблица выходила
                                        if ($$('dep_contacts_table') != null) {
                                            $$('dep_contacts_table').destructor();
                                        }

                                        webix.ui({
                                            id: 'content',
                                            rows: [
                                                depContacts
                                            ]
                                        }, $$('content'));
                                        $$('menu').unselectAll();
                                        hideBtnBack();
                                    },
                                },
                                {
                                    view: 'icon',
                                    icon: 'mdi mdi-exit-to-app',
                                    css: 'topMenuIcon',
                                    tooltip: 'Выход',
                                    click: function () {
                                        webix.send("logout");
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
                    submenuConfig:{
                        width: 170,
                    },
                    data: [
                        {id: "Profile", value: 'Профиль',},
                        {id: "Employees", value: 'Сотрудники',},
                        {
                            value: "<span class='mdi mdi-dots-horizontal'></span>",
                            width: 190,
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
                                    value: 'Предписания',
                                    badge: setPrescriptionBadge()
                                },
                                {
                                    id: "Requests",
                                    icon: "wxi-file",
                                    value: 'Заявки',
                                    // badge: setRequestsBadge()
                                },
                                {id: "News", icon: "mdi mdi-message-plus-outline", value: 'Новости'},
                                {id: "Mailing", icon: "mdi mdi-email", value: 'Рассылки',},
                                {
                                    id: "Contacts",
                                    icon: "mdi mdi-book-open-blank-variant",
                                    value: 'Доп. контакты'
                                }
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

                            if (id == 'Profile') {
                                view = profile;
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
                                // fix bug
                                if ($$('prescriptions_table') != null) {
                                    $$('prescriptions_table').destructor();
                                }
                            } else if (id == 'News') {
                                view = news;
                            } else if (id == 'Mailing') {
                                view = mailing;
                                // fix bug
                                if ($$('my_mailing_table') != null) {
                                    $$('my_mailing_table').destructor();
                                }
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
                            hideBtnBack();
                        }
                    }
                },
                {
                    view: 'scrollview',
                    scroll: 'xy',
                    body: {
                        rows: [
                            {id: 'content'}
                        ]
                    }
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