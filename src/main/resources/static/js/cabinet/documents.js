const documents = {
    rows: [
        {
            padding: {bottom: 0},
            cols: [
                {
                    id: 'upload',
                    view: 'uploader',
                    css: 'backBtnStyle',
                    type: "icon",
                    icon: "mdi mdi-download",
                    label: 'Загрузить',
                    upload: 'upload_files',
                    required: true,
                    autosend: true,
                    accept: 'application/pdf, application/zip',
                    multiple: true,
                    link: 'docslist',
                    on: {
                        onFileUpload: (response) => {
                            if (response.cause == "Ошибка сохранения" || response.cause == "Отсутствует организация") {
                                webix.message(response.cause, "error")
                            } else if (response.cause == "Вы уже загружали этот файл") {
                                webix.message(response.cause + ": " + response.sname, "error")
                                console.log(response.cause)
                            } else {
                                webix.message(response.cause + ": " + response.sname, "success")
                                $$('docs_grid').load('org_files');
                            }
                        }
                    }
                },
                {
                    id: "uploaderWidth",
                    gravity: 4
                }
            ]
        },

        {
            view: "dataview",
            id: "docs_grid",
            css: 'contacts',
            scroll: 'y',
            minWidth: 320,
            select: 1,
            template: function (obj) {
                let docImg;
                let downloadTime = obj.timeCreate.substr(11, 8) + ', ' + obj.timeCreate.substr(0, 10)
                if (obj.fileExtension == ".zip") {
                    docImg = "zip.png"
                } else {
                    docImg = "pdf.png"
                }
                let result = "<div class='overall'>" +
                    "<div>" +
                    "<img style='position: absolute' src = " + docImg + "> " +
                    "<div class='doc_title'>" + obj.originalFileName.slice(0, -4) + "</div>";
                // let acceptionStatus = "Не связан с заявкой";
                // if (obj.docRequestByIdRequest !== null) {
                //     acceptionStatus = "Связан с заявкой"
                // } else {
                //     result += "<div id='del_button' style='position: absolute;top: 0; right: 5px;' onclick='del_file("+obj.id+")' class='mdi mdi-close-thick'></div>"
                //
                // }
                result += "<div class='doc_time_create'>" + downloadTime + "</div>" +
                    "<div class='download_docs'>" +
                    "<a style='text-decoration: none; color: #1ca1c1' href=" + LINK_PREFIX + obj.fileName + LINK_SUFFIX + " download>Скачать файл</a>" +
                    "<span style='padding-left: 10px; color: #389a0d; font-weight: 400'>" + acceptionStatus + "</span>" +
                    "</div>" +
                    "</div>" +
                    "</div>"
                return result;
            },
            url: "org_files",
            xCount: 2,
            type: {
                height: "auto",
                width: "auto",
                float: "right"
            },
            scheme: {},
            on: {
                onBeforeLoad: () => {
                    if (document.body.clientWidth < 980) {
                        $$("docs_grid").config.xCount = 1;
                    }
                    if (document.body.clientWidth <= 415) {
                        $$("uploaderWidth").config.gravity = 0;
                    }
                }
            }
        },
        /*{
            gravity: 0.4,
            view: 'form',
            id: 'formDocsLoad',
            minWidth: 200,
            position: 'center',
            elements: [
                {
                    id: 'upload',
                    view: 'uploader',
                    css: 'webix_primary',
                    value: 'Загрузить',
                    upload: 'upload_files',
                    required: true,
                    autosend: true,
                    accept: 'application/pdf, application/zip',
                    multiple: true,
                    link: 'docslist',
                    on:{
                        onFileUpload:(response) => {
                            if(response.cause == "Ошибка сохранения" || response.cause == "Отсутствует организация"){
                                webix.message(response.cause, "error")
                            }else if(response.cause == "Вы уже загружали этот файл"){
                                webix.message(response.cause + ": " + response.sname, "error")
                                console.log(response.cause)
                            }else{
                                webix.message(response.cause + ": " + response.sname, "success")
                                $$('docs_grid').load('org_files');
                            }
                        }
                    }
                },
                // {
                //     view: 'list', id: 'docslist', type: 'uploader',
                //     autoheight: true, borderless: true
                // },
                // {
                //     id: 'send_btn',
                //     view: 'button',
                //     css: 'webix_primary',
                //     value: 'Добавить',
                //     align: 'center',
                //     click: function () {
                //         $$('upload').send(function (response) {
                //             if(response.status == "server")
                //             {
                //                 if(response.cause == "Файл успешно загружен"){
                //
                //                     webix.message(response.cause + ": " + response.sname, "success")
                //                     console.log(response.cause)
                //
                //                 }
                //                 else if(response.cause == "Ошибка сохранения" || response.cause == "Отсутствует организация"){
                //
                //                     webix.message(response.cause, "error")
                //                     console.log(response.cause)
                //
                //                 }else if(response.cause == "Вы уже загружали этот файл"){
                //                     webix.message(response.cause + ": " + response.sname, "error")
                //                     console.log(response.cause)
                //                 }
                //             }
                //             $$("upload").files.data.clearAll();
                //             $$('docs_grid').load('org_files');
                //         })
                //     }
                // },
                {}
        ]
        }*/
    ]


    // }
}

function del_file(id = null){
    let param = id === null ? $$('docs_grid').getSelectedItem() : $$('docs_grid').getItem(id)
    webix.confirm({
        title:"Подтверждение",
        type:"confirm-warning",
        ok:"Да", cancel:"Нет",
        text:"Вы уверены что хотите удалить файл?"
    }).then(function(){
        webix.ajax()
            .headers({'Content-type': 'application/json'})
            .post('delete_file', JSON.stringify(param.id))
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