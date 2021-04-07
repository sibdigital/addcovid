function file_upload_view(name_for_id, upload_url, list_url, delete_url) {
    return {
        rows: [
            {
                view: "dataview",
                label: 'Прикрепленные файлы',
                labelPosition: 'top',
                id: name_for_id + '_docs_grid',
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
                    result += "<div id='del_button' style='position: absolute;top: 0; right: 5px;' onclick='delete_file("+obj.id+", \"" + name_for_id + "\", \"" + delete_url + "\")' class='mdi mdi-close-thick'></div>"
                    result += "<div class='doc_time_create'>" + downloadTime + "</div>" +
                        "<div class='download_docs'>" +
                        "<a style='text-decoration: none; color: #1ca1c1' href=uploads/" + obj.fileName + obj.fileExtension + " download>Скачать файл</a>" +
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
                        autosend: false,
                        align: 'left',
                        accept: 'application/pdf, application/zip, application/msword, application/vnd.openxmlformats-officedocument.wordprocessingml.document, image/jpeg' ,
                        multiple: true,
                        link: name_for_id + '_list',
                    },
                    {}
                ]
            },
            {
                view: 'list',
                label: 'Новые файлы',
                labelPosition: 'top',
                id: name_for_id + '_list',
                type: 'uploader',
                autoheight: true,
                borderless:true
            },
        ]
    }
}

// Указывать такое же name_for_id, что и для функции file_upload_view
function saveFiles(name_for_id, obj, formDataValues) {
    let successfullyUploaded = false;
    if (obj.id) {
        let uploader = $$(name_for_id + '_upload');
        if (uploader) {
            successfullyUploaded = true
            uploader.define('formData', formDataValues)
            uploader.send(function (response) {
                if (response) {
                    console.log(response.cause);
                    if (response.cause != 'Файл успешно загружен') {
                        successfullyUploaded = false
                    }
                }
            })
        }
    }
    return successfullyUploaded;
}

function delete_file(id = null, name_for_id, delete_url){
    let param = id === null ? $$(name_for_id + '_docs_grid').getSelectedItem() : $$(name_for_id + '_docs_grid').getItem(id)
    webix.confirm({
        title:"Подтверждение",
        type:"confirm-warning",
        ok:"Да", cancel:"Нет",
        text:"Вы уверены, что хотите удалить файл? Данное действие невозможно будет отменить."
    }).then(function(){
        webix.ajax()
            .headers({'Content-type': 'application/json'})
            .post(delete_url, JSON.stringify(param.id))
            .then(function (data) {
                if (data !== null) {
                    $$(name_for_id + '_docs_grid').remove($$(name_for_id + '_docs_grid').getSelectedId());
                    webix.message("Файл удалён", 'success');
                } else {
                    webix.message("Не удалось удалить файл", 'error');
                }
            });
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