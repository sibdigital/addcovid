requirejs.config({
    baseUrl: 'js'
})

require(
    [
        'views/requests',
        'views/showform',
        'utils/filter'
    ],
    function(requests
             , showform
             , filter
    ) {

        webix.i18n.setLocale("ru-RU");

        webix.ready(function() {
            webix.ui({
                container: 'app',
                width: document.body.clientWidth,
                height: 800, //document.body.clientHeight,
                rows: [
                    {
                        view: 'toolbar',
                        height: 40,
                        cols: [
                            {
                                view: 'label',
                                label: `<span style="font-size: 1.5rem">${APPLICATION_NAME}. Список заявок.</span>`,
                            },
                            {
                            },
                            {
                                view: 'label',
                                label: `${DEPARTMENT} ( <a href="logout" title="Выйти">' + ${USER_NAME} + </a>)`,
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
                                        let status = 0
                                        switch(id) {
                                            case 'requests':
                                                status = 0
                                                break
                                            case 'accepted':
                                                status = 1
                                                break
                                            case 'rejected':
                                                status = 2
                                                break
                                        }

                                        let search_text = $$('search').getValue()
                                        if(search_text){
                                            //$$('search').callEvent('onEnter')
                                            req_tbl_url = 'list_request/' + ID_DEPARTMENT + '/' + status + '/' + search_text
                                        }
                                        else {
                                            req_tbl_url = 'list_request/' + ID_DEPARTMENT + '/' + status
                                            //req_tbl_url = 'resource->/list_request/' + ID_DEPARTMENT + '/' + status
                                        }

                                        webix.ui({
                                            id: 'root',
                                            rows: [
                                                requests(req_tbl_url)
                                            ]
                                        }, $$('root'))
                                    }
                                }
                            },
                            filter.searchBar('requests_table')
                        ]
                    },
                    {
                        id: 'root'
                    }
                ]
            })

            // по умолчанию
            $$('tabbar').setValue('requests');
        })
    })
