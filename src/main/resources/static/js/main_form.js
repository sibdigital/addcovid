webix.i18n.setLocale("ru-RU");

function view_section(title){
    return {
        view: 'template',
        type: 'section',
        template: title
    }
}

webix.ready(function() {
    webix.ui({
        container: 'app',
        view: "form",
        id: 'link_form',
        autowidth: true,
        height: document.body.clientHeight,
        width: document.body.clientWidth - 8,
        rows: [
            // {
            //     view: "form",
            //     id: 'link_form',
            //     //scroll:true,
            //     rows: [
            //     ],
            //     elementsConfig: {
            //         labelPosition: "top",
            //     }
            // }
        ]
    })

    webix.ajax('/cls_type_requests').then(function (data) {
        let typeRequests = data.json();
        let vtxt = '<span style="font-size::calc(1.5em + 1vmin); text-align: center;">Работающая Бурятия. </span><br/>'
        + '<span style="font-size::calc(1.0em + 1vmin);text-align: center;">Подайте заявку в соответствии с вашим видом деятельности. </span><br/><br/>'
        + '<a style="font-size:calc(0.8em + 1vmin); text-align: center;" href="http://rabota.govrb.ru/form" >Общие основания</a><br/><br/>'
        + '<a style="font-size: calc(0.8em + 1vmin); text-align: center;" href="http://form.govrb.ru/upload/" >Общие основания (более 100 сотрудников)</a><br/><br/>'
        + '<a style="font-size: calc(0.8em + 1vmin); text-align: center;" href="http://rabota.govrb.ru/barber" >Парикмахерские услуги</a><br/><br/>';

        for(var  j = 0; j< typeRequests.length; j++){
            if (typeRequests[j].id == 1 || typeRequests[j].id == 2) {
                continue;
            }

            if (typeRequests[j].statusRegistration == 1 && typeRequests[j].statusVisible == 1 ) {
                let labl = typeRequests[j].activityKind.replace(new RegExp(' ', 'g'), '&nbsp');
                let vdid = typeRequests[j].id;
                let reqv = 'typed_form?request_type=' + vdid;
                vtxt += '<a style="font-size: calc(0.8em + 1vmin); text-align: center; word-break: break-all; overflow-wrap: break-word; line-height: 1.05;" href="http://form.govrb.ru/' + reqv + '" >' +
                    '' + labl + '</a><br/><br/>'
            }
        }
        v = {
            view: 'template',
            template: vtxt,
            width: 0,
            autoheight:true,
            //height:300
        };
        $$('link_form').addView(v);

    });

    webix.event(window, "resize", function(event){
        $$('link_form').define("width",document.body.clientWidth);
        $$('link_form').resize();
    });

    //link_form.show();
})