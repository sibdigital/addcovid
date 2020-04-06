requirejs.config({
    baseUrl: 'js'
})


function buildRoute(view) {
    return function() {
        webix.ui({
            id: 'root',
            rows: [
                view
            ]
        }, $$('root'))
    }
}

function buildButton(label, route) {
    return {
        view: 'button',
        value: label,
        width: 100,
        align: 'center',
        click: function() {
            routie(route)
        }
    }
}

require(
    [
        'views/requests',
        'views/showform'
    ],
    function(requests, showform) {

        let itemdata = {};

        webix.i18n.setLocale("ru-RU");

        webix.ready(function() {
            webix.ui({
                container: 'app',
                width: document.body.clientWidth,
                height: document.body.clientHeight,
                rows: [
                    {
                        view: 'toolbar',
                        cols: [
                            {
                                view: 'label',
                                label: 'ЕИС Работающая Бурятия. Проверки.',
                                id: 'head_label'
                            }
                        ]
                    },
                    {
                        id: 'root'
                    }
                ]
            })
        })

        routie({
            '': buildRoute(requests),
            // buildRoute(showform)
            'showform/:item': function(item) {
                webix.ui({
                    id: 'root',
                    item: item,
                    rows: [
                        showform
                    ]
                }, $$('root'))
        }
        })
    })
