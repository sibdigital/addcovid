const documents = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                type: 'wide',
                cols:[
                    {
                        view: "dataview",
                        id: "docs_grid",
                        css: 'contacts',
                        scroll: false,
                        select: 1,
                        template:
                            "<div id='2' class='overall'>" +
                                "<div id='1' style='position: relative'>" +
                                    "<div class='doc_title'>#originalFileName#</div>" +
                                    "<div id='del_button' style='position: absolute;top: 0; right: 0;' ondblclick='del_file()' class='mdi mdi-close-thick'></div>" +
                                "</div>" +
                                "<div class='contactValue'>#timeCreate#</div>" +
                            "</div>",
                        url: "org_files",
                        xCount: 2,
                        type: {
                            height: "auto",
                            width: "auto"
                        },
                    },
                    {
                        gravity: 0.4,
                        view: 'form',
                        position: 'center',
                        elements: [
                            {gravity: 0.6},
                            {
                                id: 'upload',
                                view: 'uploader',
                                css: 'webix_secondary',
                                value: 'Загрузить',
                                autosend: false,
                                upload: '/upload_files',
                                required: true,
                                accept: 'application/pdf, application/zip',
                                multiple: true,
                                link: 'filelist',
                            },
                            {
                                view: 'list', id: 'filelist', type: 'uploader',
                                autoheight: true, borderless: true
                            },
                            {
                                id: 'send_btn',
                                view: 'button',
                                css: 'webix_primary',
                                value: 'Импорт',
                                align: 'center',
                                click: function () {
                                    $$('upload').send(function (response) {
                                        let uploadedFiles = []
                                        $$('upload').files.data.each(function (obj) {
                                            let status = obj.status
                                            let name = obj.name
                                            if(status == 'server'){
                                                let sname = obj.sname
                                                uploadedFiles.push(sname)
                                            }

                                        })
                                        if (uploadedFiles.length != $$('upload').files.data.count()) {
                                            webix.message('Не удалось загрузить файлы.', "error")
                                            $$('upload').focus()
                                        }
                                        $$("upload").files.data.clearAll();
                                        $$('docs_grid').load('org_files');
                                    })
                                }
                            },
                            {}
                    ]
                    }
                ]
            }
        ],
    }
}

function del_file(){
    let param = $$('docs_grid').getSelectedId()
    webix.ajax()
        .headers({'Content-type': 'application/json'})
        .post('/delete_file', JSON.stringify(param))
        .then(function (data) {
            if (data !== null) {
                $$("docs_grid").remove($$("docs_grid").getSelectedId());
                webix.message("Файл удалён", 'success');
            } else {
                webix.message("Не удалось удалить файл", 'error');
            }
        });
}
