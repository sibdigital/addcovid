requirejs.config({
    baseUrl: 'js'
})

const DATE_FORMAT = webix.Date.dateToStr("%d.%m.%Y")

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
        width: 150,
        align: 'center',
        click: function() {
            routie(route)
        }
    }
}

require(
    [
        'views/requests',
        'views/showForm',
        'views/accepted',
        'views/rejected',
        'utils/filter'
    ],
    function(requests, showform, accepted, rejected, filter) {

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
                            {
                                view: 'label',
                                label: '<span style="font-size: 1.5rem">ЕИС "Работающая Бурятия". Список заявок.</span>',
                            },
                            {
                            },
                            {
                                view: 'label',
                                label: DEPARTMENT + ' (' + USER_NAME + ')',
                                align: 'right'
                            }
                        ]
                    },
                    {
                        view: 'toolbar',
                        cols: [
                            {
                                value: 1, view: 'segmented', id:'tabbar', value: 'listView', multiview: true,
                                width: 450,
                                optionWidth: 150,  align: 'center', padding: 10,
                                options: [
                                    { value: 'Необработанные', id: 'requests'},
                                    { value: 'Принятые', id: 'accepted'},
                                    { value: 'Отклоненные', id: 'rejected'}
                                ],
                                on:{
                                    onChange:function(id){
                                        routie(id)

                                        //$$('requests_table').clearAll();


/*
                                        switch(id) {
                                            case 'requests':
                                                //$$('requests_table').config.url.source = 'list_request/' + ID_DEPARTMENT + '/0'
                                                //requests.rows[0].url = 'list_request/' + ID_DEPARTMENT + '/0'
                                                buildRoute(requests.requests(0))
                                                break
                                            case 'accepted':
                                                //$$('requests_table').config.url.source = 'list_request/' + ID_DEPARTMENT + '/1'
                                                buildRoute(requests.requests(1))
                                                break
                                            case 'rejected':
                                                //$$('requests_table').config.url.source = 'list_request/' + ID_DEPARTMENT + '/2'
                                                buildRoute(requests.requests(2))
                                                break
                                        }
*/

                                        //$$('requests_table').load($$('requests_table').config.url)
                                    }
                                }
                            },
                            filter.searchBar('requests_table')
                        ]
                    },
                    {
                        id: 'root'
                        //requests
                    }
                ]
            })

            // по умолчанию
            $$('tabbar').setValue('requests');
            //routie('requests')

        })

        routie({
            'requests': buildRoute(requests),
            'accepted': buildRoute(accepted),
            'rejected': buildRoute(rejected),
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