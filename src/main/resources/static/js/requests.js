webix.i18n.setLocale("ru-RU");


webix.ready(function() {

webix.ui({
    view: "datatable",
    id: 'requests_table',
    autoConfig:true,
    //data: docRequests
     columns:[
            { id:"orgName", header:"Название организации" , template: "#organization.name#" },
            { id:"inn", header:"ИНН", template: "#organization.inn#"  },
            { id:"ogrn", header:"ОГРН", template: "#organization.ogrn#"  },
            { id:"orgPhone", header:"Телефон", template: "#organization.phone#"  },
            { id:"orgPhone", header:"Обоснование заявки", template: "#organization.description#"  },
            { id:"personOfficeCnt", header:"personOfficeCnt"},
            { id:"personRemoteCnt", header:"personRemoteCnt"},
            { id:"personSlrySaveCnt", header:"personSlrySaveCnt"},
            { id:"timeCreate", header:"Дата создания"}
      ],
    on: {
        onItemDblClick: function (id) {
            let data = $$('requests_table').getItem(id);
            console.log(data);
            //window.location="/";
        }
    },
    url: "http://localhost:8090/doc_requests"
})
})