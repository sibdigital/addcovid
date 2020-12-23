const profile = {
    view: 'form',
    id: 'typeRequestForm',
    elements: [
        {
            view: "tabbar",
            id: "tabs",
            multiview: true,
            borderless: true,
            maxWidth: 350,
            minWidth: 300,
            options: [
                {value: "Общая информация", width: 170, id: 'common_info_form'},
                {value: "Настройки", id: 'form_pass', width: 150}
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

function adaptiveCommonInfo() {

    $$("organizationName").config.label = "Наим. орг./ФИО ИП";
    $$("organizationName").refresh();
    $$("shortOrganizationName").config.label = "Краткое наим. орг.";
    $$("shortOrganizationName").refresh();

}