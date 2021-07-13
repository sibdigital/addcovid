const subs = () => {
    webix.ajax().get("required_subsidy_files").then((response) => {
        let required_subsidy_files = response.json();
        // console.log(required_subsidy_files)
        showRequiredSubsidyFiles(required_subsidy_files)
    })

    return {
        id: 'required_subsidy_files_templates',
        margin: 10,
        rows: []
    }
}

function view_subsidy_files_section(required_subsidy_file) {
    let fileTypeName = required_subsidy_file.clsFileType.name;
    let req_status = required_subsidy_file.required === true ? "Обязательный документ" : "Не обязательный документ";


    return {
        type: "space",
        rows: [
            {
                cols: [
                    {
                        view: 'label',
                        label: fileTypeName + ` <span style="color: red">(` + req_status + `)</span>`,
                        autowidth: true,
                        autoheight: true,
                    },
                    {
                        id: fileTypeName + '_uploader',
                        view: 'uploader',
                        label: "Загрузить",
                        css: 'backBtnStyle',
                        type: "icon",
                        icon: "mdi mdi-upload",
                        required: true,
                        autowidth: true,
                        autosend: false,
                        multiple: true,
                        on: {
                            onAfterFileAdd: () => {
                                const formData = new FormData();
                                const request = new XMLHttpRequest();
                                const uploader = $$(fileTypeName + '_uploader');
                                let files = [];
                                for (let i = 0; i < uploader.files.data.order.length; i++) {
                                    files[i] = uploader.files.getItem(uploader.files.data.order[i]);
                                    formData.append('files', files[i].file)
                                }
                                formData.append('id_file_type', required_subsidy_file.clsFileType.id)
                                if (files.length === 2) {
                                    request.open('POST', 'upload_subsidy_files');
                                    request.onload = () => {
                                        if (request.status >= 200 && request.status < 300) {
                                            let responseJson = JSON.parse(request.response);
                                            let typeMessage = responseJson.cause != null ? "error" : "success";
                                            webix.message(responseJson.sname, typeMessage, 10000);
                                            uploader.files.data.clearAll();
                                        }
                                    }
                                    request.send(formData);
                                    updateDataview(fileTypeName, required_subsidy_file.clsFileType.id);
                                }
                            }
                        }
                    },
                    {}
                ]
            },
            {
                view: "dataview",
                id: fileTypeName + '_dataview',
                css: 'contacts',
                scroll: 'y',
                minWidth: 320,
                height: 290,
                select: false,
                template: function (obj) {
                    let viewName = obj.viewFileName ?? "";
                    let result =
                        "<div id='overall_" + obj.id + "' class='overall'>" +
                        "<div>" +
                        "<div class='doc_title'>" + obj.originalFileName.slice(0, -4) + "</div>" +
                        "<div id='del_button' style='color: red; position: absolute; top: 0; right: 5px;' onclick='del_subsidy_file(" + obj.id + ",\"" + fileTypeName + "\"," + required_subsidy_file.clsFileType.id + ")' class='mdi mdi-close-thick'></div>" +
                        "<div id='box_" + obj.id + "' style='position: absolute; top: 40px; left: 15px;'>" +
                        "<input class='custom-form-control' type='text' value='" + viewName + "' placeholder='Отображаемое имя файла' onkeydown='update_file_view_name(this," + obj.id + ")'>" +
                        "<span class='custom-span-control' style='color: green'>Подпись загружена</span>" +
                        "<span class='custom-span-control'>Статус проверки подписи</span>" +
                        "</div>" +
                        "</div>" +
                        "</div>";
                    return result;
                },
                url: () => {
                    return webix.ajax()
                        .get('request_subsidy_files', {
                            "doc_request_subsidy_id": 1,
                            "id_file_type": required_subsidy_file.clsFileType.id
                        })
                },
                xCount: 1,
                type: {
                    height: "auto",
                    width: "auto",
                    float: "right"
                },
                scheme: {},
            }
        ]
    }
}

const update_file_view_name = (el, idSubsidyFile) => {
    if (event.key === 'Enter') {
        webix.ajax().post('set_subsidy_file_view_name', {"id_subsidy_file": idSubsidyFile, "view_name": el.value});
    }
}

const del_subsidy_file = async(id, fileType, fileTypeId) => {
    await webix.ajax().post('del_request_subsidy_file', {"id_subsidy_file": id});
    updateDataview(fileType,fileTypeId)
}

const showRequiredSubsidyFiles = (required_subsidy_files) => {
    Array.from(required_subsidy_files).forEach(required_subsidy_file => {
        $$('required_subsidy_files_templates').addView(
            view_subsidy_files_section(required_subsidy_file)
        );
    })
}

const updateDataview = (fileType, fileTypeId) => {
    setTimeout(() => {
        $$(fileType + '_dataview').clearAll();
        $$(fileType + '_dataview').load(() => {
            return webix.ajax()
                .get('request_subsidy_files', {
                    "doc_request_subsidy_id": 1,
                    "id_file_type": fileTypeId
                })
        });
    }, 100)
}