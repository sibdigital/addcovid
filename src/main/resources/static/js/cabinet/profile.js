const profile = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                view: 'form',
                id: 'typeRequestForm',
                elements: [
                    {
                        view: "tabbar",
                        id: "tabs",
                        multiview: true,
                        borderless: true,
                        width: 350,
                        options: [
                            {value: "<span class='mdi mdi-information'></span> Общая информация", id: 'common_info_form'},
                            {value: "<span class='mdi mdi-cogs'></span> Настройки", id: 'form_pass', width: 150}
                        ]
                    },
                    {
                        animate: false,
                        cells: [
                            commonInfo,
                            settings
                        ]
                    }
                ]
            }
        ]
    },
}

function adaptiveCommonInfo() {

    $$("organizationName").config.label = "Наим. орг./ФИО ИП";
    $$("organizationName").refresh();
    $$("shortOrganizationName").config.label = "Краткое наим. орг.";
    $$("shortOrganizationName").refresh();

}