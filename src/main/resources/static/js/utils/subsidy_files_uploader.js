const requestSubsidyStep2 = () => {
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
                            let requestSubsidyIdObj = $$('requestSubsidyId');
                            let params = {
                                "id_request": requestSubsidyIdObj !== undefined ? requestSubsidyIdObj.getValue() : null
                            }
                            if (params.id_request != null) {
                                await webix.ajax().get('check_request_subsidy_files_signatures', params).then((response) => {
                                    let responseJson = response.json();
                                    if (responseJson.status === "ok") {
                                        verify_progress(params.id_request, responseJson.cause); //show progress on start event
                                        let timerId = setInterval(() => {
                                            verify_progress(params.id_request, responseJson.cause, timerId);
                                        }, 4000)
                                    } else {
                                        webix.message(responseJson.cause, responseJson.status, 4000);
                                    }
                                });
                            }
                        }
                    },
                    {
                        id: 'check',
                        rows: [
                            {
                                id: 'progress_bar',
                                padding: 10,
                                borderless: true,
                                hidden: false,
                                template: "<div id='progress_bar_text'></div>"
                            },
                        ]
                    }
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
    setTimeout(() => {
        webix.ajax().get("required_subsidy_files", {"idRequest": id}).then((response) => {
            let required_subsidy_files = response.json();
            Array.from(required_subsidy_files).forEach(required_subsidy_file => {
                let idDataview = required_subsidy_file.clsFileType.name.replace(/\s+/g, '') + '_dataview';
                $$('required_subsidy_files_templates').addView(
                    view_subsidy_files_section(required_subsidy_file)
                );
                webix.extend($$(idDataview),webix.ProgressBar)
            })
        });
    }, 200)

}

//ProgressBar event
function verify_progress(id, queueTime, timerId = null) {
    let progressBar = $$("progress_bar");
    if (id != null && progressBar !== undefined) {
        webix.extend(progressBar, webix.ProgressBar);
        webix.ajax()
            .get('check_signature_files_verify_progress', {"id_request": id})
            .then(async(response) => {
                let data = response.json();
                if (data.notFound) {
                    if (timerId == null) {
                        progressBar.hideProgress();
                        document.getElementById("progress_bar_text").innerHTML = "";
                    }
                    clearInterval(timerId)
                } else {
                    let verified = data.verified;
                    let numberOfFiles = data.numberOfFiles;
                    let progress = verified / numberOfFiles;

                    await progressBar.showProgress({type: "top", position: progress === 0 ? 0.001 : progress})

                    if (progress !== 0) {
                        document.getElementById("progress_bar_text").innerHTML =
                            "<span style='position: absolute; margin-top: 8px; left: 10px; z-index: 100; font-weight: 500'>" +
                                "Проверено: " + verified + "/" + numberOfFiles +
                            "</span>";
                        progress === 1 ? webix.message("Началась проверка подписей", "", 2000) : null;
                        let dataViews = $$('required_subsidy_files_templates').getChildViews()
                        dataViews.forEach((dataView) => {
                            let dataViewId = dataView.qf[1].id;
                            updateDataview(dataViewId.slice(0, -9), $$(dataViewId).config.formData.fileTypeId)
                        })
                    } else {
                        document.getElementById("progress_bar_text").innerHTML =
                            "<span style='position: absolute; margin-top: 8px; left: 10px; z-index: 100; font-weight: 500'>" +
                                "Проверено: " + verified + "/" + numberOfFiles + " (" + queueTime + ")" +
                            "</span>";
                    }

                    if (numberOfFiles === verified) {
                        clearInterval(timerId);
                        webix.message("Проверка подписей завершена", "success", 10000);
                    }

                }
            })
    } else {
        clearInterval(timerId);
    }
}

//Секция созданная по ClsFileType
function view_subsidy_files_section(required_subsidy_file) {
    let fileTypeName = required_subsidy_file.clsFileType.name;
    let dynamicElementId = required_subsidy_file.clsFileType.name.replace(/\s+/g, '');
    let req_status = required_subsidy_file.required === true ? "Обязательный" : "Не обязательный";
    return {
        type: "clean",
        rows: [
            {
                cols: [
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
                                updateDataview(dynamicElementId, required_subsidy_file.clsFileType.id);
                            }
                        }
                    },
                    {
                        view: 'template',
                        borderless: true,
                        width: 800,
                        template: fileTypeName + ` <span style="color: red">(` + req_status + `)</span>`,
                        autowidth: true,
                        autoheight: true,
                    },
                    // {
                    //     view: 'label',
                    //     label: fileTypeName + ` <span style="color: red">(` + req_status + `)</span>`,
                    //     autowidth: true,
                    //     autoheight: true,
                    // },
                ]
            },
            {
                view: "dataview",
                id: dynamicElementId + '_dataview',
                name: required_subsidy_file.required,
                css: 'contacts',
                scroll: 'y',
                minWidth: 320,
                // datathrottle: 500,
                height: 215,
                select: false,
                formData: {
                    "fileTypeId": required_subsidy_file.clsFileType.id
                },
                template: function (obj) {
                    let viewName = obj?.docFile?.viewFileName ?? "";
                    let originalFileName = obj?.docFile?.originalFileName;
                    let signatureVerifyStatus = obj?.verificationSignatureFile?.verifyStatus == undefined ? 0 : obj?.verificationSignatureFile?.verifyStatus;
                    let signatureVerifyResultBtn = "";
                    let signatureVerifyResult = "";
                    let uploadSignatureBtnColor = "#94A1B3";
                    let overallColor = "";

                    if (originalFileName.length > 40) {
                        originalFileName = obj?.docFile?.originalFileName.substr(0, 40) + "...";
                    }

                    let title = `<div title='` + obj?.docFile?.originalFileName + `' style='margin-top: 5px; width: 325px' class="div-hover">` + originalFileName + `</div>`;

                    if(obj?.verificationSignatureFile != null) {
                        signatureVerifyResult = obj?.verificationSignatureFile?.verifyResult
                    }
                    let signatureExists = null;
                    if (obj.signatureFile != null) {
                        signatureExists = "<i title='Подпись загружена' class='mdi mdi-check-circle-outline subsidy_files_icon'></i>";
                        uploadSignatureBtnColor = "#1CA1C1";
                    } else {
                        signatureExists = "<i title='Ожидание загрузки подписи' class='mdi mdi-clock-outline subsidy_files_icon' style='color: orange;'></i>";
                    }

                    switch (signatureVerifyStatus) {
                        case 0:
                            signatureVerifyStatus = "<i title='Ожидание проверки подписи' class='mdi mdi-clock-outline subsidy_files_icon' style='color: orange;'></i>";
                            break;
                        case 1:
                            signatureVerifyStatus = "<i title='Подпись проверена' class='mdi mdi-check-circle-outline subsidy_files_icon'></i>";
                            overallColor = "-webkit-gradient(linear, left top, left bottom, color-stop(0, #00ff2b5c), color-stop(1, #00ff2b5c))";
                            signatureVerifyResultBtn = "<i title='Результаты проверки подписи' " +
                                "onclick='verifySignatureResults(" + "\`" +
                                    signatureVerifyResult + "\`," +
                                    obj?.verificationSignatureFile?.id + "," +
                                    obj?.signatureFile?.organization.inn  + ")' " +
                                "class='mdi mdi mdi-information-outline subsidy_files_icon'></i>"
                            break;
                        default:
                            signatureVerifyStatus = "<i title='Ошибка при проверке подписи' class='mdi mdi mdi-alert-circle-outline subsidy_files_icon'></i>";
                            overallColor = "-webkit-gradient(linear, left top, left bottom, color-stop(0, #ff00005c), color-stop(1, #ff00005c))";
                            signatureVerifyResultBtn = "<i title='Результаты проверки подписи' " +
                                "onclick='verifySignatureResults(" + "\`" +
                                    signatureVerifyResult + "\`," +
                                    obj?.verificationSignatureFile?.id + "," +
                                    obj?.signatureFile?.organization.inn + ")' " +
                                "class='mdi mdi mdi-information-outline subsidy_files_icon'></i>"
                            break;
                    }

                    let result =
                        "<div id='overall_" + obj.docFile.id + "' class='overall' style='height: 40px; background: " + overallColor + "'>" +
                        "<div class='overall-title' style='margin-top: 5px'>" +
                        title +
                        "<input title='После ввода нажмите Enter' class='custom-form-control' type='text' value='" + viewName + "' placeholder='Отображаемое имя файла' onkeydown='update_file_view_name(this," + obj.docFile.id + ")' style=''>" +
                        "<button type='button' class='webix_button webix_img_btn' onclick='upload_subsidy_signature(" + obj.docFile.id + "," + obj.docFile.fileType.id + ",\"" + dynamicElementId + "\"," + required_subsidy_file.clsFileType.id + ")' style='margin-left: 10px; width: auto; height: 32px; background: transparent'>" +
                        "<span title='Добавить подпись' class='webix_icon_btn mdi mdi-upload custom-icon-hover' style=' color:" + uploadSignatureBtnColor + "; font-size: 24px; margin-top: -2px'></span>" +
                        "</button>" +
                        "<span onclick='upload_subsidy_signature(" + obj.docFile.id + "," + obj.docFile.fileType.id + ",\"" + dynamicElementId + "\"," + required_subsidy_file.clsFileType.id + ")' style='padding-top: 5px'>Добавить подпись</span>" +
                        signatureExists +
                        signatureVerifyStatus +
                        signatureVerifyResultBtn +
                        "</div>" +
                        "<div id='del_button' title='Удалить документ' style='position: absolute; top: 0; right: 5px;' onclick='del_subsidy_file(" + obj.docFile.id + ",\"" + dynamicElementId + "\"," + required_subsidy_file.clsFileType.id + ")' class='mdi mdi-close-thick'></div>" +
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
                ready: function () {
                    if (!$$(dynamicElementId + '_dataview').count()) {
                        webix.extend($$(dynamicElementId + '_dataview'), webix.OverlayBox);
                        this.showOverlay("<div style='margin:75px; font-size:20px;'>Файлы не загружены</div>");
                    }
                },
                xCount: 1,
                type: {
                    height: "auto",
                    width: "auto",
                    float: "right"
                },
                scheme: {},
                on: {
                    onAfterLoad: function () {
                        webix.extend($$(dynamicElementId + '_dataview'), webix.OverlayBox);
                        if (!this.count())
                            this.showOverlay("<div style='margin:75px; font-size:20px;'>Файлы не загружены</div>");
                        else
                            this.hideOverlay();
                    },
                }
            }
        ]
    }
}

//Модальное окно с результатом проверки подписи
const verifySignatureResults = (result, idVerify, orgInn) => {
    let window = webix.ui({
        view: 'window',
        id: 'verify_signature_results',
        width: 700,
        height: 380,
        position: 'center',
        // item: data,
        modal: true,
        head: {
            view: 'toolbar',
            elements: [
                {
                    view: 'label',
                    label: '№ ' + idVerify + '. Результаты проверки подписи (ИНН: ' + orgInn + ")"
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
            type: "form",
            margin: 10,
            rows: [
                {
                    template: result ?? ""
                }
            ]
        },
        on: {
            'onShow': function () {
            }
        }
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
    $$("uploadAPI").fileDialog({id: id});
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
            updateDataview($$("uploadAPI").config.formData.uploaderId, $$("uploadAPI").config.formData.fileTypeId)
        }
    }
});

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
const updateDataview = async (fileType, fileTypeId) => {
    $$(fileType + '_dataview').showProgress({
        delay: 1000,
        hide: false
    })
    setTimeout(async () => {
        await webix.ajax()
        .get('request_subsidy_files', {
            "doc_request_subsidy_id": $$('requestSubsidyId').getValue(),
            "id_file_type": fileTypeId
        })
        .then((response) => {
            let data = response.json();
            $$(fileType + '_dataview').clearAll();
            $$(fileType + '_dataview').parse(data);
        })
        $$(fileType + '_dataview').hideProgress();
    },400)
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
        if (dataViewRequired == true && item == undefined) {
            oneOfDataViewsIsNull = false
        }
    })
    return oneOfDataViewsIsNull;
}

const checkVerificationFiles = () => {
    let xhr = webix.ajax().sync().get('check_all_files_are_verified', {"id_request_subsidy": $$('requestSubsidyId').getValue()})
    if (xhr.responseText === 'true') {
        return true;
    } else {
        return false;
    }
}