const subs = () => {
    // webix.ajax().get("required_subsidy_files").then((response) => {
    //     let required_subsidy_files = response.json();
    //     // console.log(required_subsidy_files)
    //     showRequiredSubsidyFiles(required_subsidy_files);
    // })
    return {
        borderless: true,
        rows: [
            {
                height: 10,
            },
            {
                id: 'header_cols',
                margin: 10,
                padding: 10,
                borderless: true,
                cols: [
                    {
                        id: 'progress_bar',
                        borderless: true,
                        hidden: false,
                        template: "<div id='progress_bar_text'></div>"
                    },
                    {
                        view: "button",
                        value: "Проверить подписи",
                        width: 170,
                        css: "webix_primary",
                        click: async () => {
                            await webix.ajax().get('check_request_subsidy_files_signatures');
                            //signature_file_verification_window.show();
                            verify_progress();
                            webix.extend($$("progress_bar"), webix.ProgressBar);
                            let timerId = setInterval(() => {
                                verify_progress(timerId);
                            }, 3000)
                        }
                    },
                ]
            },
            {
                id: 'required_subsidy_files_templates',
                margin: 10,
                padding: 10,
                borderless: true,
                autoheight: "true",
                rows: []
            }
        ]
    }
}

function createDataView(id) {
    setTimeout(() => {
        webix.ajax().get("required_subsidy_files", {"idRequest": id}).then((response) => {
            let required_subsidy_files = response.json();
            Array.from(required_subsidy_files).forEach(required_subsidy_file => {
                $$('required_subsidy_files_templates').addView(
                    view_subsidy_files_section(required_subsidy_file)
                );
            })
        })
    }, 200)

}

function verify_progress(timerId) {
    webix.ajax()
        .get('check_signature_files_verify_progress', {"id_request": $$('requestSubsidyId').getValue()})
        .then((response) => {
            let data = response.json();
            let verified = data.verified;
            let numberOfFiles = data.numberOfFiles;
            let progress = verified / numberOfFiles;
            if (progress !== 0) {
                $$("progress_bar").show();
                $$("progress_bar").showProgress({type: "top", position: progress})
                document.getElementById("progress_bar_text").innerHTML = "<span style='position: absolute; margin-top: 16px; right: 0'>Проверено: " + verified + "/" + numberOfFiles + "</span>";

            } else {
                $$("progress_bar").hideProgress();
                document.getElementById("progress_bar_text").innerHTML = "<span style='position: absolute; margin-top: 10px; right: 200px'>Файлы добавлены в очередь";
            }

            numberOfFiles === verified ? clearInterval(timerId) : null;
        })
}

function view_subsidy_files_section(required_subsidy_file) {
    let fileTypeName = required_subsidy_file.clsFileType.name.replace(/\s+/g, '');
    let req_status = required_subsidy_file.required === true ? "Обязательный документ" : "Не обязательный документ";

    return {
        type: "clean",
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
                                formData.append('id_request', $$('requestSubsidyId').getValue())
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
                name: required_subsidy_file.required,
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
                            "doc_request_subsidy_id": $$('requestSubsidyId').getValue(),
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

let signature_file_verification_window = webix.ui({
    view: "window",
    id: "signature_file_verification_window",
    minWidth: 200,
    width: 750,
    position: "center",
    modal: true,
    close: true,
    head: {
        view: 'toolbar',
        elements: [
            {
                view: 'label',
                label: 'Проверка подписей',
                // label: 'Просмотр запроса (id: ' + data.id + ')'
            },
            {
                view: 'icon', icon: 'wxi-close',
                click: function () {
                    signature_file_verification_window.hide()
                }
            }
        ]
    },
    body: {
        rows: [
            {}
        ]
    }
});

const update_file_view_name = (el, idSubsidyFile) => {
    if (event.key === 'Enter') {
        webix.ajax().post('set_subsidy_file_view_name', {"id_subsidy_file": idSubsidyFile, "view_name": el.value});
    }
}

const del_subsidy_file = async (id, fileType, fileTypeId) => {
    await webix.ajax().post('del_request_subsidy_file', {"id_subsidy_file": id});
    updateDataview(fileType, fileTypeId)
}

const updateDataview = (fileType, fileTypeId) => {
    setTimeout(() => {
        $$(fileType + '_dataview').clearAll();
        $$(fileType + '_dataview').load(() => {
            return webix.ajax()
                .get('request_subsidy_files', {
                    "doc_request_subsidy_id": $$('requestSubsidyId').getValue(),
                    "id_file_type": fileTypeId
                })
        });
    }, 100)
}

const checkRequiredFiles = () => {
    let dataViews = $$('required_subsidy_files_templates').getChildViews()
    let oneOfDataViewsIsNull = true;
    dataViews.forEach((dataView) => {
        let dataViewId = dataView.qf[1].id;
        let dataViewRequired = dataView.qf[1].name;
        let itemId = $$(dataViewId).getFirstId();
        let item = $$(dataViewId).getItem(itemId);
        // console.log("req:" + dataViewRequired)
        // console.log("i:" + item)
        if (dataViewRequired == true && item == undefined) {
            oneOfDataViewsIsNull = false
        }
        // console.log("resStat:" + oneOfDataViewsIsNull)
    })
    return oneOfDataViewsIsNull;
}

const checkVerificationFiles = () => {
    let xhr = webix.ajax().sync().get('check_all_files_are_verified', {"id_request_subsidy": $$('requestSubsidyId').getValue()})
    if (xhr.responseText==='true') {
        return true;
    } else {
        return false;
    }
}