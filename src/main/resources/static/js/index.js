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
        'views/showForm'
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
