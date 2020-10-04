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
            {
                view: 'toolbar',
                autoheight: true,
                id: 't1',
                css: 'header-dark',
                rows: [
                    {
                        responsive: 't1',
                        css: 'webix_dark',
                        cols: [
                            {
                                view: 'label',
                                width: 300,
                                label: `<span style="font-size: 1.3rem; color: #fff">${APPLICATION_NAME}. </span>`,
                                tooltip: `${APPLICATION_NAME}`,
                            }
                        ]
                    }
                ]
            }
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
        let vtxt = `<span style="font-size:calc(1.1em  1vmin);text-align: center;">Подайте заявку в соответствии с вашим видом деятельности. </span><br/><br/>
         <a style="font-size:calc(0.8em  1vmin); text-align: center;" href="${SUBDOMAIN_WORK}/form" >Общие основания</a><br/><br/>
         <a style="font-size: calc(0.8em  1vmin); text-align: center;" href="${SUBDOMAIN_WORK}/upload" >Общие основания (более 100 сотрудников)</a><br/><br/>
         <a style="font-size: calc(0.8em  1vmin); text-align: center;" href="${SUBDOMAIN_WORK}/barber" >Парикмахерские услуги</a><br/><br/>`;

        for(var  j = 0; j< typeRequests.length; j++){
            if (typeRequests[j].id == 1 || typeRequests[j].id == 2 || typeRequests[j].id == 100) {
                continue;
            }

            if (typeRequests[j].statusRegistration == 1 && typeRequests[j].statusVisible == 1 ) {
                let labl = typeRequests[j].activityKind.replace(new RegExp(' ', 'g'), '&nbsp');
                let vdid = typeRequests[j].id;
                let reqv = 'typed_form?request_type=' + vdid;
                vtxt += `<a style="font-size: calc(0.8em + 1vmin); text-align: center; word-break: break-all; overflow-wrap: break-word; line-height: 1.05;" href="${SUBDOMAIN_FORM}/${reqv}" >` +
                    '' + labl + '</a><br/><br/>'
            }
        }
        vtxt += `<a style="font-size:calc(0.8em + 1vmin); text-align: center;" href="${SUBDOMAIN_WORK}/personal_form" >Физические лица, оказывающие услуги по сдаче в аренду жилья туристам</a><br/><br/>`
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
