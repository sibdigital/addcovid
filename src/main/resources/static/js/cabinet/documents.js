const documents = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        id: 'documentsMainLayout',
        rows: [
            {
                type: 'wide',
                responsive: 'documentsMainLayout',
                cols:[
                    {
                        view: "dataview",
                        id: "docs_grid",
                        css: 'contacts',
                        scroll:'y',
                        minWidth:320,
                        select: 1,
                        template: function (obj){
                            let docImg;
                            let downloadTime = obj.timeCreate.substr(11,8) + ', ' +  obj.timeCreate.substr(0, 10)
                            if(obj.fileExtension == ".zip"){
                                docImg = "zip.png"
                            }else{
                                docImg = "pdf.png"
                            }
                            return "<div id='2' class='overall'>" +
                                        "<div>" +
                                            "<img style='position: absolute' src = "+docImg+"> " +
                                            "<div class='doc_title'>"+obj.originalFileName.slice(0, -4)+"</div>" +
                                            "<div id='del_button' style='position: absolute;top: 0; right: 5px;' ondblclick='del_file()' class='mdi mdi-close-thick'></div>" +
                                            "<div class='doc_time_create'>"+downloadTime+"</div>" +
                                            "<div class='download_docs'><a style='text-decoration: none; color: #1ca1c1' href=/uploads/"+obj.fileName+obj.fileExtension+" download>Скачать файл</a></div>" +
                                        "</div>" +
                                    "</div>"
                        },
                        url: "org_files",
                        xCount: 2,
                        type: {
                            height: "auto",
                            width: "auto",
                            float: "right"
                        },
                    },
                    {
                        gravity: 0.4,
                        view: 'form',
                        id: 'formDocsLoad',
                        minWidth: 200,
                        position: 'center',
                        elements: [
                            {gravity: 0.6},
                            {
                                id: 'upload',
                                view: 'uploader',
                                css: 'webix_secondary',
                                value: 'Выбрать',
                                autosend: false,
                                upload: '/upload_files',
                                required: true,
                                accept: 'application/pdf, application/zip',
                                multiple: true,
                                link: 'docslist',
                                formData:{
                                    cause: '123'
                                }
                            },
                            {
                                view: 'list', id: 'docslist', type: 'uploader',
                                autoheight: true, borderless: true
                            },
                            {
                                id: 'send_btn',
                                view: 'button',
                                css: 'webix_primary',
                                value: 'Добавить',
                                align: 'center',
                                click: function () {
                                    $$('upload').send(function (response) {
                                        if(response.status == "server")
                                        {
                                            if(response.cause == "Файл успешно загружен"){

                                                webix.message(response.cause + ": " + response.sname, "success")
                                                console.log(response.cause)

                                            }
                                            else if(response.cause == "Ошибка сохранения" || response.cause == "Отсутствует организация"){

                                                webix.message(response.cause, "error")
                                                console.log(response.cause)

                                            }else if(response.cause == "Вы уже загружали этот файл"){
                                                webix.message(response.cause + ": " + response.sname, "error")
                                                console.log(response.cause)
                                            }
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
    webix.confirm({
        title:"Подтверждение",
        type:"confirm-warning",
        ok:"Да", cancel:"Нет",
        text:"Вы уверены что хотите удалить файл?"
    }).then(function(){
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
      })
}