function file_upload_view(form_id, name_for_id, upload_url, list_url, form_data, accept_formats) {
    return {
        rows: [
            {
                view: "dataview",
                label: 'Прикрепленные файлы',
                labelPosition: 'top',
                id: name_for_id + '_docs_grid',
                type: 'uploader',
                css: 'contacts',
                scroll: 'y',
                minWidth: 320,
                minHeight: 200,
                select: 1,
                template: function (obj) {
                    let docImg = getFileIcon(obj.fileExtension);
                    let downloadTime = obj.timeCreate.substr(11, 8) + ', ' + obj.timeCreate.substr(0, 10)
                    let result = "<div class='overall'>" +
                        "<div>" +
                        "<img style='position: absolute' src = " + docImg + "> " +
                        "<div class='doc_title'>" + obj.originalFileName.slice(0, -4) + "</div>";
                    result += "<div id='del_button' style='position: absolute;top: 0; right: 5px;' onclick='delete_file("+obj.id+", \"" + name_for_id + "\")' class='mdi mdi-close-thick'></div>"
                    result += "<div class='doc_time_create'>" + downloadTime + "</div>" +
                        "<div class='download_docs'>" +
                        "<a style='text-decoration: none; color: #1ca1c1' href=" + LINK_PREFIX + obj.fileName + LINK_SUFFIX + obj.fileExtension + " download>Скачать файл</a>" +
                        "</div>" +
                        "</div>" +
                        "</div>"
                    return result;
                },
                url: list_url,
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
                            $$(name_for_id + '_docs_grid').config.xCount = 1;
                        }
                    },
                    onAfterLoad: () => {
                        if ($$(name_for_id + '_docs_grid').count() === 0) {
                            $$(name_for_id + '_docs_grid').hide();
                        }
                    },
                }
            },
            {
                padding: {bottom: 0},
                cols: [
                    {
                        id: name_for_id + '_upload',
                        view: 'uploader',
                        css: 'backBtnStyle',
                        type: "icon",
                        icon: "mdi mdi-download",
                        label: 'Загрузить файлы',
                        minWidth: 200,
                        maxWidth: 350,
                        upload: upload_url,
                        autosend: true,
                        align: 'left',
                        accept: accept_formats,
                        multiple: true,
                        on: {
                            onBeforeFileAdd: () => {
                                $$(name_for_id + '_upload').define("formData", form_data);
                            },

                            onFileUpload: (response) => {
                                if (response.id) {
                                    webix.message("Файл успешно загружен: " + response.originalFileName, "success")
                                    $$(name_for_id + '_docs_grid').show();
                                    $$(name_for_id + '_docs_grid').add(response);
                                } else  {
                                    webix.message("Ошибка сохранения", "error")
                                }
                            }
                        }
                    },
                    {}
                ]
            }
        ]
    }
}

function delete_file(id = null, name_for_id){
    webix.confirm({
        title:"Подтверждение",
        type:"confirm-warning",
        ok:"Да", cancel:"Нет",
        text:"Вы уверены, что хотите удалить файл?"
    }).then(function(){
        $$(name_for_id + '_docs_grid').remove($$(name_for_id + '_docs_grid').getSelectedId());
    })
}

function getFileIcon(fileExtension) {
    let docImg;
    switch (fileExtension) {
        case '.zip':
            docImg = 'zip.png';
            break;
        case '.pdf':
            docImg = 'pdf.png';
            break;
        case '.jpeg':
            docImg = 'jpg.png';
            break;
        case '.jpg':
            docImg = 'jpg.png';
            break;
        case '.doc':
            docImg = 'doc.png';
            break;
        case '.docx':
            docImg = 'doc.png';
            break;
        default:
            docImg = 'file.png';
            break;
    }
    return docImg;
}