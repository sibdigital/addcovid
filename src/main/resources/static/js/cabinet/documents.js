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
                        url: "org_files",
                        xCount: 2,
                        type: {
                            template: "<div class='overall'>" +
                                "<div class='doc_title'>#originalFileName#</div>" +
                                "</div>",
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
                                            let cause = obj.status
                                            let name = obj.name
                                            if(cause == 'server') {
                                                webix.message(name)
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
                                        console.log(uploadedFiles)
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
