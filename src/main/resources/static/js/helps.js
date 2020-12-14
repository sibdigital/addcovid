webix.i18n.setLocale("ru-RU");

const dateFormat = webix.Date.dateToStr("%d.%m.%Y %H:%i:%s")
let userWindowHeight = window.innerHeight;

console.log('here');
const currentUrl = window.location.href;
console.log(currentUrl);
console.log(HELP_LIST);
console.log(HELP_DATA);

const helpsList = {
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
                        {id: "Requests", icon: "wxi-file", value: 'Заявки'},
                        {id: "Contacts", icon: "mdi mdi-book-open-blank-variant", value: 'Контакты'},
                        {id: "Settings", icon: "mdi mdi-cogs", value: 'Настройки'},
                        //{id: "Help", icon: 'mdi mdi-help', value: 'Помощь'},
                    ],
                    type: {
                        css: 'my_menubar_item',
                        height: 44
                    },
                    on: {
                        onMenuItemClick: function (id) {
                            let view;
                            let helpUrl = 'help?id=1';

                            if (id === 'CommonInfo') {
                                view = commonInfo;
                                helpUrl = 'help?key=CommonInfo';
                            } else if (id === 'Requests') {
                                view = requests;
                                //helpUrl = 'help?id=5';
                                helpUrl = 'help?key=Requests';
                            } else if (id === 'Employees') {
                                view = employees;
                                helpUrl = 'help?key=Employees';
                            } else if (id === 'Settings') {
                                view = settings;
                                helpUrl = 'help?key=Settings';
                            } else if (id === 'Documents') {
                                view = documents;
                                helpUrl = 'help?key=Documents';
                            } else if (id === 'Address') {
                                view = address;
                                helpUrl = 'help?key=Address';
                                //helpUrl = 'help?id=18';
                            } else if (id === 'Prescript') {
                                view = prescript;
                                //helpUrl = 'help?id=6';
                                helpUrl = 'help?key=Prescript';
                            } else if (id === 'News') {
                                view = news;
                                helpUrl = 'help?key=News';
                                //helpUrl = 'help?id=15';
                            } else if (id === 'Contacts') {
                                view = contacts;
                                helpUrl = 'help?key=Contacts';
                            }

                            $$('labelHelpHref').config.label = `<span style="font-size: 16px; font-family: Roboto, sans-serif;"><a target=\'_blank\' href=${helpUrl}>Помощь</a></span>`;
                            $$("labelHelpHref").refresh();

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
            rows: [{
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
                                width: 40,
                                template: "<img height='40px' width='40px' src = \"favicon.ico\">",
                            },
                            {
                                view: 'label',
                                width: 300,
                                label: `<span style="font-size: 1.0rem">${APPLICATION_NAME}</span>`,
                                //tooltip: 'Заявка на оказание парикмахерских услуг'
                            },
                            {
                                id: 'helpHeaderLabel',
                                view: 'label',
                                minWidth: 400,
                                autoheight: true,
                                label: '<span style="font-size: 1.0rem">Раздел помощи</span>',
                            }
                        ]
                    }]
            }, {
                view: 'list',
                data: HELP_LIST,
                template: '#name#',
                autowidth: true,
                autoheight: true,
                select: true,
                on: {
                    onItemClick: function (id) {
                        let item = this.getItem(id);
                        console.log(id);
                        window.location.href = 'help?id=' + id;
                    }
                }
            }]
        }
    ]
};

const help = {
    view: 'scrollview',
    scroll: 'xy',
    autowidth: true,
    // autoheight: true,
    height: '500px',
    maxHeight: '500px',
    body: {
        type: 'space',
        rows: [
            {
                view: 'text',
                label: HELP_DATA ? HELP_DATA.name : '',
                labelPosition: 'top',
                readonly: true,
                value: HELP_DATA ? HELP_DATA.description : '',
            },
            {}
        ],
    }
}

const helpTest = {
    autoheight: true,
    view: 'textarea',
    label: HELP_DATA ? HELP_DATA.name : '',
    labelPosition: 'top',
    readonly: true,
    value: HELP_DATA ? HELP_DATA.description : '',
    type: {
        height: 'auto'
    }
}

const curHelpForm =  currentUrl.includes('?') ? help : helpsList;

// if (currentUrl.includes('?')) {
//     $$('helpHeaderLabel').config.label = `<span style="font-size: 1.0rem">Помощь по разделу: '${HELP_DATA ? HELP_DATA.name : ''}'</span>`;
//     $$('helpHeaderLabel').refresh();
// }

const getHelpContent = (data) => {

    return {
        view: 'scrollview',
        scroll: 'y',
        autowidth: true,
        autoheight: true,
        body: {
            type: 'space',
            rows: [
                {
                    type: 'wide',
                    cols: [{
                        view: 'text',
                        label: data ? data.name : '',
                        labelPosition: 'top',
                        readonly: true,
                        value: data ? data.description : '',
                        type: {
                            height: 'auto',
                            width: 'auto',
                        }
                    }]
                }
            ],
        }
    };
};

webix.ready(function () {
    console.log(document.body.clientWidth);
    let layout = webix.ui({
        container: 'app',
        //autoheight: true,
        //autowidth: true,
        height: document.body.clientHeight,
        //width: document.body.clientWidth - 230,
        cols: [
            {
                width: 220,
                height: document.body.clientHeight,
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
                        layout: 'y',
                        scroll: 'y',
                        data: HELP_LIST,
                        template: '#name#',

                        type: {
                            css: 'my_menubar_item',
                            height: 44
                        },
                        on: {
                            onMenuItemClick: function (id) {
                                console.log(id);
                                webix.ajax()
                                    .headers({'Content-type': 'application/json'})
                                    .get('help?id=' + id)
                                    .then((data) => {
                                        if (data.json() !== null) {
                                            console.log(data.json());
                                            $$("labelLK").setValue("Раздел помощи > " + "<span style='color: #1ca1c1'>" + data.json().name + "</span>")
                                            webix.ui({
                                                id: 'content',
                                                rows: [
                                                    getHelpContent(data.json())
                                                ]
                                            }, $$('content'))
                                        }
                                    })
                            }
                        }
                    },
                ]
            },
            {
                rows: [{
                    view: 'toolbar',
                    //autowidth: true,
                    width: document.body.clientWidth - 222,
                    height: 45,
                    id: 't1',
                    rows: [
                        {
                            responsive: 't1',
                            cols: [
                                {
                                    view: 'label',
                                    id: 'labelLK',
                                    align: 'left',
                                    css: {"padding-left":"5px"},
                                    height: 46,
                                    label: `Раздел помощи`,
                                }
                            ]
                        }]
                }, {
                    id: 'content',
                }]
            }
        ]
    });

    webix.event(window, "resize", function (event) {
        layout.define("width", document.body.clientWidth - 220);
        layout.resize();
    });
});

// webix.ready(function() {
//     webix.ui({
//         container: 'app',
//         // autowidth: true,
//         // autoheight: true,
//         height: document.body.clientHeight,
//         width: document.body.clientWidth - 8,
//         rows: [
//             {
//                 view: 'toolbar',
//                 autoheight: true,
//                 id: 't1',
//                 rows: [
//                     {
//                         responsive: 't1',
//                         css: 'webix_dark',
//                         cols: [
//                             {
//                                 view: 'label',
//                                 width: 40,
//                                 template: "<img height='40px' width='40px' src = \"favicon.ico\">",
//                             },
//                             {
//                                 view: 'label',
//                                 width: 300,
//                                 label: `<span style="font-size: 1.0rem">${APPLICATION_NAME}</span>`,
//                                 //tooltip: 'Заявка на оказание парикмахерских услуг'
//                             },
//                             {
//                                 id: 'helpHeaderLabel',
//                                 view: 'label',
//                                 minWidth: 400,
//                                 autoheight: true,
//                                 label: '<span style="font-size: 1.0rem">Раздел помощи</span>',
//                             }
//                         ]
//                     },
//                     curHelpForm,
//                 ]
//             },
//
//
//         ]
//     })
//
// });