const helps = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        //type: 'space',
        rows: [
            {
                //type: 'wide',
                cols: [
                    {
                        view: "dataview",
                        id: "contact_grid",
                        scroll: false,
                        select: 1,
                        url: "helpsdata",
                        xCount: 1,
                        type: {
                            template: "#name#",
                            // height: "auto",
                            // width: "auto"
                        },
                        on: {
                            onItemDblClick: function (id) {
                                let item = this.getItem(id);
                            }
                        }
                    },
                ]
            },
        ],
    }
}

// const help = {
//     view: 'scrollview',
//     scroll: 'xy',
//     body: {
//         //type: 'space',
//         rows: [
//             {
//                 view: 'label',
//                 label: HELP_DATA.name ?? '',
//             },
//             {
//                 view: 'textarea',
//                 readonly: true,
//                 value: HELP_DATA.description ?? ''
//             }
//         ],
//     }
// }
//
// function addContact() {
//     let form = $$('contact_form')
//     let params = form.getValues()
//     if (params["type"] == 2) params["type"] = 0;
//     if (form.validate()) {
//         webix.ajax()
//             .headers({'Content-type': 'application/json'})
//             .post('/save_contact', JSON.stringify(params))
//             .then(function (data) {
//                 if (data !== null) {
//                     if (params.id) {
//                         webix.message('Контакт обновлен', 'success');
//                     } else {
//                         webix.message("Контакт добавлен", 'success');
//                     }
//                     $$('contact_grid').load('org_contacts');
//                 } else {
//                     webix.message("Не удалось добавить контакт", 'error');
//                 }
//             });
//         form.clear()
//         form.clearValidation()
//         if (params["type"] == 0) {
//             $$('type_combo').setValue('2')
//         } else {
//             $$('type_combo').setValue('1')
//         }
//     }
// }
//
// function deleteContact() {
//     let params = $$('contact_grid').getSelectedItem()
//     webix.ajax()
//         .headers({'Content-type': 'application/json'})
//         .post('/delete_org_contact', JSON.stringify(params))
//         .then(function (data) {
//             if (data !== null) {
//                 $$("contact_grid").remove($$("contact_grid").getSelectedId());
//                 webix.message("Контакт удалён", 'success');
//                 $$('contact_grid').load('org_contacts');
//             } else {
//                 webix.message("Не удалось удалить контакт", 'error');
//             }
//         });
// }


const getDropDownView = (header, desc) => {
    const buttonValue = header.title  || 'Раскрыть';
    const textAreaValue = desc.description || 'Описание';

    // const dropDownView = {
    //     view: 'scrollview',
    //     autowidth: true,
    //     autoheight: true,
    //     body: {
    //         type: 'space',
    //         rows: [
    //             {
    //                 type: 'wide',
    //                 cols: [
    //                     {
    //                         view: 'button',
    //                         id: 'dropDownButton',
    //                         css: 'webix_primary',
    //                         value: buttonValue,
    //                         click: (id, event) => {
    //                             console.log(id, event);
    //                             const textArea = $$('dropDownTextArea');
    //                             if (textArea.isVisible()) {
    //                                 textArea.hide();
    //                             } else {
    //                                 textArea.show();
    //                             }
    //                         }
    //                     },
    //                     {
    //                         view: 'textarea',
    //                         id: 'dropDownTextArea',
    //                         value: textAreaValue,
    //                         readonly: true,
    //                     }]
    //             },
    //         ]
    //     }
    // };

    const dropDownView = [
        {
            type: 'wide',
            cols: [
                {
                    view: 'button',
                    id: 'dropDownButton',
                    css: 'webix_primary',
                    value: buttonValue,
                    click: (id, event) => {
                        console.log(id, event);
                        const textArea = $$('dropDownTextArea');
                        if (textArea.isVisible()) {
                            textArea.hide();
                        } else {
                            textArea.show();
                        }
                    }
                },
                {
                    view: 'textarea',
                    id: 'dropDownTextArea',
                    value: textAreaValue,
                    readonly: true,
                }]
        },
    ];


    console.log(dropDownView);
    return dropDownView;
}

function parseData(data, globalIds) {
    const usedIndex = {};
    const getNode = (data, id) => {
        const node = [];
        data.forEach((item, index) => {
            if (!usedIndex[index]) {
                usedIndex[index] = true;
                if (item.global_parent_id === id) {
                    node.push(item);
                }
            }
        });
        return node;
    }
    const nodes = [];
    globalIds.forEach((item) => nodes.push(getNode(data, item)));
    return nodes;
}

function getFormWithData(url) {
    const typenames = {
        'dropdown-header': 'dropdown-menu',
        'dropdown-title': '',
        'dropdown-description': '',
    };

    const getForm = (data) => {
        console.log(data);

        return data.forEach((item) => {
            {
                if (!item) return;

                const headerIndex = item.find((it, index) => {
                    if (it.parent_id === null)
                        return index;
                });

                return getDropDownView(data[headerIndex], data[1]);
            }
        });
    }


    const params = '';
    return webix.ajax()
        .headers({'Content-Type': 'application/json'})
        .get(url, params)
        .then(function (data) {
            if (data.json()) {
                const nodes = data.json();
                return webix.ajax()
                    .headers({'Content-Type': 'application/json'})
                    .get('/help/statistic/globalIds', params)
                    .then(function (data) {
                        if (data.json()) {
                            const globalIds = data.json();
                            console.log('array global id', globalIds);
                            const resultNodes = parseData(nodes, globalIds);
                            console.log(resultNodes);
                            return getForm(resultNodes);
                        }
                    });
            } else {
                webix.message({ text: data.json(), type: 'error' });
            }
        });

}


function getHelp(url) {
    // const view = getFormWithData(url);
    // console.log(view);
    // return view;

    // return {
    //     view: 'scrollview',
    //     autowidth: true,
    //     autoheight: true,
    //     body: {
    //         type: 'wide',
    //         rows: getFormWithData(url)
    //         // view: 'list',
    //         // scroll: 'xy',
    //         // url: url,
    //         // datatype: 'json',
    //         // template: '#title#',
    //     }
    // };

    return {
        view: 'scrollview',
        autowidth: true,
        //autoheight: true,
        body: {
            //type: 'space',
            rows: [
                {
                    //type: 'wide',
                    rows: [
                        {
                            view: 'button',
                            id: 'dropDownButton',
                            css: 'webix_primary',
                            value: 'Страница: Статистика по заявкам',
                            click: (id, event) => {
                                console.log(id, event);
                                const textArea = $$('dropDownTextArea');
                                if (textArea.isVisible()) {
                                    textArea.hide();
                                } else {
                                    textArea.show();
                                }
                            }
                        },
                        {
                            view: 'label',
                            id: 'dropDownTextArea',
                            label: 'Здесь собрана статистика по заявкам организаций и количестве работиков в оффисах и на удаленке',
                            //autoheight: true,
                            //height: 200,
                            hidden: true,
                            //readonly: true,
                        }]
                },
            ]
        }
    };
}

const getAddHelpForm = (data = null) => {

    return {
        view: 'nic-editor',
        id: 'message',
        name: 'message',
        css: "myClass",
        cdn: false,
        config: {
            iconsPath: '../libs/nicedit/nicEditorIcons.gif'
        }
    };
}

const addHelpForm = {
    view: 'scrollview',
    autowidth: true,
    autoheight: true,
    body: {
        type: 'space',
        rows: [
            {
                view: 'form',
                id: 'newHelpForm',
                complexData: true,
                elements: [
                    {
                        view: 'text',
                        id: 'header',
                        name: 'title',
                        label: 'Название',
                        labelPosition: 'top',
                    },
                    {
                        view: 'label',
                        label: 'Описание',
                    },
                    {
                        view: 'nic-editor',
                        id: 'message',
                        name: 'description',
                        css: "myClass",
                        cdn: false,
                        config: {
                            iconsPath: '../libs/nicedit/nicEditorIcons.gif'
                        }
                    },
                    {
                        cols: [
                            {},
                            {
                                view: 'button',
                                maxWidth: 200,
                                label: 'Отмена',
                                click: function() {
                                    webix.ui({
                                        id: 'content',
                                        rows: [
                                            helpForm
                                        ]
                                    }, $$('content'))
                                }
                            },
                            {
                                view: 'button',
                                maxWidth: 200,
                                label: 'Добавить',
                                click: function() {
                                    const params = $$('newHelpForm').getValues();
                                    webix.ajax()
                                        .headers({ 'Content-Type': 'application/json' })
                                        .post('/help/add', JSON.stringify(params));

                                    webix.ui({
                                        id: 'content',
                                        rows: [
                                            helpForm
                                        ]
                                    }, $$('content'))
                                }
                            }
                        ]
                    },
                ]
            }
        ]
    },

}