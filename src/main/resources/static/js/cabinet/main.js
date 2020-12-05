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
    "}"
);

webix.attachEvent("onFocusChange", function (to, from) {
    if (from && from.getTopParentView().config.view == "window" && !to) {
        from.getTopParentView().hide();
    }
})

let UsercontextMenu = webix.ui({
    view: "contextmenu",
    css: 'user_menu_items',
    data: [
        {id: "CommonInfo",  value: 'Профиль'},
        {id: "Contacts", value: 'Контактная информация'},
        {id: "Mailing", value: 'Рассылки', badge: setRequestsBadge()},
        {id: "Help", value: 'Помощь'},
        {id: "Settings", value: 'Настройки'},
        { $template:"Separator" },
        {id: 'Exit', value: 'Выход'}
        ], //"Изменить аватар", ,
    on: {
        onMenuItemClick: function (id) {
            let view;
            if (id == 'CommonInfo'){
                view = commonInfo;
            }else if (id == 'Contacts'){
                view = contacts;
            }else if (id == 'Mailing'){
                view = mailing;
            }else if (id == 'Help'){
                view = settings;
            }else if (id == 'Settings'){
                view = settings;
            }else if (id == 'Exit'){
                webix.send("/logout");
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
        UsercontextMenu.config.width = span.offsetWidth+80; UsercontextMenu.resize();
    }
    let toolBarHeight = $$('toolbar').config.height - 5;
    UsercontextMenu.show({
        x:document.body.clientWidth-span.offsetWidth-50,y:toolBarHeight
    })
}

function setRequestsBadge(){
    webix.ajax("org_requests").then(function(data){
        let requestsCount = data.json().length;
        let request = $$('menu').getItem("Requests");
        request.badge = requestsCount;
        $$('menu').updateItem("Requests", request);
    });
}

webix.ready(function () {
    let layout = webix.ui({
        cols: [
            {
                width: 230,
                id: 'menuRow',
                rows: [
                    {
                        view: 'label',
                        id: 'labelLK',
                        css: {
                            'background-color': '#565B67 !important',
                            'color': '#FFFFFF'
                        },
                        align: 'center',
                        height: 46,
                        label: `<div style="color: white; font-size: 18px; font-family: Roboto, 
                                sans-serif; padding: 0 12px 0 10px;">Личный кабинет</div>`,
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
                            {id: "News", icon: "mdi mdi-message-plus-outline", value: 'Новости'},
                            {id: "Requests", icon: "wxi-file", value: 'Заявки', badge: setRequestsBadge()},
                            {id: "Contacts", icon: "mdi mdi-book-open-blank-variant", value: 'Контакты'},
                            {id: "Settings", icon: "mdi mdi-cogs", value: 'Настройки'},
                        ],
                        type: {
                            css: 'my_menubar_item',
                            height: 44
                        },
                        on: {
                            onMenuItemClick: function (id) {
                                let view;

                                if (id == 'CommonInfo'){
                                    view = commonInfo;
                                }else if (id == 'Requests'){
                                    view = requests;
                                }else if (id == 'Employees'){
                                    view = employees;
                                }else if (id == 'Settings'){
                                    view = settings;
                                }else if (id == 'Documents'){
                                    view = documents;
                                }else if (id == 'Address'){
                                    view = address;
                                }else if (id == 'Prescript'){
                                    view = prescript;
                                }else if (id == 'News'){
                                    view = news;
                                }else if (id == 'Contacts'){
                                    view = contacts;
                                }
                                if (view != null) {
                                    webix.ui({
                                        id: 'content',
                                        rows: [
                                            view
                                        ]
                                    }, $$('content'));
                                }
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
                            // {
                            //     view: 'button',
                            //     type: 'icon',
                            //     icon: 'wxi-dots',
                            //     // css: 'webix_primary',
                            //     width: 37,
                            //     click: function () {
                            //         if ($$('menu').config.hidden)
                            //             $$('menu').show();
                            //         else $$('menu').hide();
                            //     }
                            // },
                            {
                                align: 'left',
                                cols: [
                                    {
                                        view: 'label',
                                        width: 40,
                                        template: "<img height='40px' width='40px' src = \"favicon.ico\">",
                                        click: () => {
                                            webix.html.addCss($$("menuRow").$view, "animated swing")
                                            let menuSide = $$('menuRow');
                                            if (menuSide.config.hidden) {
                                                menuSide.show()
                                            } else menuSide.hide();
                                        }
                                    },
                                    {
                                        view: 'label',
                                        label: `<span style="font-size: 16px; font-family: Roboto, sans-serif;">${APPLICATION_NAME}</span>`,
                                    },
                                ]
                            },
                            {
                                view: 'template',
                                borderless: true,
                                template: "<div style='text-align: right'><img class='user_avatar' src = \"avatar.jpg\"> " +
                                    "<span id='username' onclick=\"showDropDownMenu(document.getElementById('username'));\"" +
                                    "class='user_shortName' style='margin-right: 25px'>#shortName#</span></div>",
                                url: 'organization',
                            },
                        ]
                    },
                    {
                        id: 'content'
                    }
                ],
            }
        ]
    })

    webix.event(window, "resize", function (event) {
        layout.define("width", document.body.clientWidth);
        layout.resize();
    });

    //responsive events
    webix.attachEvent("onResponsiveHide", function (id) {

        if (id === "employees_table") {

            adaptiveEmployees()

        }else if (id === "inn"){

            adaptiveCommonInfo()

        }

        let contentChild = $$('content').getChildViews()[0]
        let contentChildName = contentChild.config.name

        if(id === "addAddressInfo"  && contentChildName === 'showRequestCreateForm'){

            adaptiveRequests()

        }

    })

})