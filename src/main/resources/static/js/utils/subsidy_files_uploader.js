//Шаг 2 - прикрепление файлов к заявке
const subs = () => {
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
                        view: "button",
                        value: "Проверить подписи",
                        width: 170,
                        css: "webix_primary custom-btn-border",
                        click: async () => {
                            let params = {
                                "id_request": $$('requestSubsidyId').getValue()
                            }
                            await webix.ajax().get('check_request_subsidy_files_signatures', params).then((response) => {
                                let responseJson = response.json();
                            });
                            verify_progress();
                            webix.extend($$("progress_bar"), webix.ProgressBar);
                            let timerId = setInterval(() => {
                                verify_progress(timerId);
                            }, 3000)
                        }
                    },
                    {
                        id: 'progress_bar',
                        padding: 10,
                        borderless: true,
                        hidden: false,
                        template: "<div id='progress_bar_text'></div>"
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

//Создание секций с прикреплением и показом файлов по ClsFileType
function createDataView() {
    let id = $$('requestSubsidyId').getValue()
    console.log(id)
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

//ProgressBar event
function verify_progress(timerId = null) {
    webix.ajax()
        .get('check_signature_files_verify_progress', {"id_request": $$('requestSubsidyId').getValue()})
        .then((response) => {
            let data = response.json();
            if (data.notFound) {
                if (timerId == null) {
                    webix.message(data.notFound, "error", 10000);
                    $$("progress_bar").hideProgress();
                    document.getElementById("progress_bar_text").innerHTML = "";
                }
                clearInterval(timerId)
            } else {
                let verified = data.verified;
                let numberOfFiles = data.numberOfFiles;
                let progress = verified / numberOfFiles;
                if (progress !== 0) {
                    $$("progress_bar").show();
                    let dataViews = $$('required_subsidy_files_templates').getChildViews()
                    dataViews.forEach((dataView) => {
                        let dataViewId = dataView.qf[1].id;
                        console.log(dataViewId)
                        updateDataview(dataViewId.slice(0, -9), $$(dataViewId).config.formData.fileTypeId)
                    })
                    $$("progress_bar").showProgress({type: "top", position: progress})
                    document.getElementById("progress_bar_text").innerHTML = "<span style='position: absolute; margin-top: 8px; left: 10px; z-index: 100; font-weight: 500'>Проверено: " + verified + "/" + numberOfFiles + "</span>";
                } else {
                    $$("progress_bar").hideProgress();
                    document.getElementById("progress_bar_text").innerHTML = "<span style='position: absolute; margin-top: 8px;'>Файлы добавлены в очередь</span>";
                }

                if (numberOfFiles === verified) {
                    clearInterval(timerId)
                    $$('step2_continue_btn').enable();
                }
            }
        })
}

//Секция созданная по ClsFileType
function view_subsidy_files_section(required_subsidy_file) {
    let fileTypeName = required_subsidy_file.clsFileType.name;
    let dynamicElementId = required_subsidy_file.clsFileType.name.replace(/\s+/g, '');
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
                        id: dynamicElementId + '_uploader',
                        view: 'uploader',
                        css: 'backBtnStyle',
                        type: "icon",
                        icon: "mdi mdi-upload",
                        required: true,
                        autowidth: true,
                        autosend: true,
                        multiple: false,
                        tooltip: 'Загрузить документ',
                        upload: "upload_subsidy_files",
                        formData: {
                            "id_file_type": required_subsidy_file.clsFileType.id,
                            "id_request": $$('requestSubsidyId').getValue()
                        },
                        on: {
                            onFileUpload: (response) => {
                                console.log(response)
                                updateDataview(dynamicElementId, required_subsidy_file.clsFileType.id);
                            }
                        }
                    },
                    {}
                ]
            },
            {
                view: "dataview",
                id: dynamicElementId + '_dataview',
                name: required_subsidy_file.required,
                css: 'contacts',
                scroll: 'y',
                minWidth: 320,
                height: 290,
                select: false,
                formData: {
                    "fileTypeId": required_subsidy_file.clsFileType.id
                },
                template: function (obj) {
                    let viewName = obj.docFile.viewFileName ?? "";
                    let originalFileName = obj.docFile.originalFileName;
                    let overallColor = "";
                    if (originalFileName.length > 40) {
                        originalFileName = obj.docFile.originalFileName.substr(0, 40) + "...";
                    }
                    let signatureExists = obj.signatureFile != null ?
                        "<span class='mdi mdi-check-circle-outline subsidy_files_icon'></span>"
                        : "<span class='mdi mdi-clock-outline subsidy_files_icon' style='color: orange;'></span>";
                    let signatureVerifyStatus = obj?.verificationSignatureFile?.verifyStatus == undefined ? "" : obj?.verificationSignatureFile?.verifyStatus;
                    let signatureVerifyResult = "";

                    if (signatureVerifyStatus === "" || signatureVerifyStatus === 0) {
                        signatureVerifyStatus = "<span class='mdi mdi-clock-outline subsidy_files_icon' style='color: orange;'></span>";
                    } else if (signatureVerifyStatus === 1) {
                        signatureVerifyStatus = "<span class='mdi mdi-check-circle-outline subsidy_files_icon'></span>";
                        signatureVerifyResult = "<span webix_tooltip='' onclick='verifySignatureResults(" + "\"" + obj?.verificationSignatureFile?.verifyResult +"\"" + ")' class='mdi mdi mdi-information-outline subsidy_files_icon'></span>"
                        overallColor = "-webkit-gradient(linear, left top, left bottom, color-stop(0, #00ff2b5c), color-stop(1, #00ff2b5c))";
                    } else if (signatureVerifyStatus !== 1 && signatureVerifyStatus !== "" && signatureVerifyStatus !== 0) {
                        signatureVerifyStatus = "<span webix_tooltip='' class='mdi mdi mdi-alert-circle-outline subsidy_files_icon'></span>";
                        signatureVerifyResult = "<span webix_tooltip='' onclick='verifySignatureResults(" + "\"" + obj?.verificationSignatureFile?.verifyResult +"\"" + ")' class='mdi mdi mdi-information-outline subsidy_files_icon'></span>"
                        overallColor = "-webkit-gradient(linear, left top, left bottom, color-stop(0, #ff00005c), color-stop(1, #ff00005c))";
                    }
                    let result =
                        "<div id='overall_" + obj.docFile.id + "' class='overall' style='height: 48px; background: " + overallColor + "'>" +
                        "<div class='overall-title' style='margin-top: 10px'><div style='margin-top: 5px; width: 325px'>" + originalFileName +
                        "</div><input webix_tooltip='' title='Тест' class='custom-form-control' type='text' value='" + viewName + "' placeholder='Отображаемое имя файла' onkeydown='update_file_view_name(this," + obj.docFile.id + ")' style=''>" +
                        "<button type='button' class='webix_button webix_img_btn' onclick='upload_subsidy_signature(" + obj.docFile.id + "," + obj.docFile.fileType.id + ",\"" + dynamicElementId + "\"," + required_subsidy_file.clsFileType.id + ")' style='margin-left: 10px; width: auto; height: 32px; background: transparent'>" +
                        "<span class='webix_icon_btn mdi mdi-upload custom-icon-hover' style='font-size: 24px; margin-top: -2px'></span>" +
                        "</button>" +
                        signatureExists +
                        signatureVerifyStatus +
                        signatureVerifyResult +
                        "</div>" +
                        "<div id='del_button' style='position: absolute; top: 0; right: 5px;' onclick='del_subsidy_file(" + obj.docFile.id + ",\"" + dynamicElementId + "\"," + required_subsidy_file.clsFileType.id + ")' class='mdi mdi-close-thick'></div>" +
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
                on: {
                    onAfterLoad: () => {
                    }
                }
            }
        ]
    }
}

//Модальное окно с результатом проверки подписи
const verifySignatureResults = (result) =>{
    let window = webix.ui({
        view: 'window',
        id: 'verify_signature_results',
        width: 600,
        height: 300,
        position: 'center',
        // item: data,
        modal: true,
        head: {
            view: 'toolbar',
            elements: [
                {
                    view: 'label',
                    label: 'Результаты проверки подписи'
                },
                {
                    view: 'icon',
                    icon: 'wxi-close',
                    click: function () {
                        window.hide()
                    }
                }
            ]
        },
        body: {
            template: result ?? ""
        },
        // on: {
        //     'onShow': function () {
        //     }
        // }
    });
    window.show();
}

//DataViewsItem dynamic uploader
function upload_subsidy_signature(id, clsFileTypeId, uploaderId, fileTypeId) {
    $$("uploadAPI").config.formData = {
        'id_request_subsidy_file': id,
        "id_file_type": clsFileTypeId,
        "id_request": $$('requestSubsidyId').getValue(),
        "uploaderId": uploaderId,
        "fileTypeId": fileTypeId
    }
    $$("uploadAPI").fileDialog({ id: id });
}

//Api Uploader
webix.ui({
    view: "uploader",
    id: "uploadAPI",
    apiOnly: true,
    upload: "upload_subsidy_files",
    multiple: false,
    on: {
        onFileUpload: (response) => {
            console.log(response)
            updateDataview($$("uploadAPI").config.formData.uploaderId, $$("uploadAPI").config.formData.fileTypeId)
        }
    }
});

//Добавление viewFileName tpRequestSubsidyFile
const update_file_view_name = (el, idSubsidyFile) => {
    if (event.key === 'Enter') {
        webix.ajax().post('set_subsidy_file_view_name', {"id_subsidy_file": idSubsidyFile, "view_name": el.value});
    }
}

//Удаление tpRequestSubsidyFile && regVerificationSignatureFile (ifExist) && tpRequestSubsidyFile-signature (ifExist)
const del_subsidy_file = async (id, fileType, fileTypeId) => {
    await webix.ajax().post('del_request_subsidy_file', {"id_subsidy_file": id});
    updateDataview(fileType, fileTypeId)
}

//Обновление данные DataView по ID
const updateDataview = (fileType, fileTypeId) => {
    setTimeout(() => {
        console.log(fileType)
        $$(fileType + '_dataview').clearAll();
        $$(fileType + '_dataview').load(() => {
            return webix.ajax()
                .get('request_subsidy_files', {
                    "doc_request_subsidy_id": $$('requestSubsidyId').getValue(),
                    "id_file_type": fileTypeId
                })
        });
    }, 400)
}

//Проверка прикрепленности обязательных документов
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

//Удаление всех DataView
const removeChildDataviews = () => {
    setTimeout(() => {
        if ($$('required_subsidy_files_templates'))
            webix.ui({
                id: 'required_subsidy_files_templates',
                margin: 10,
                padding: 10,
                borderless: true,
                autoheight: "true",
                rows: []
            }, $$('required_subsidy_files_templates'));
    }, 2000)
}