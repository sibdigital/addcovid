function subsidy_files_upload() {

    webix.ajax().get("required_subsidy_files").then((response) => {
        let required_subsidy_files = response.json();
        console.log(required_subsidy_files)
        showRequiredSubsidyFiles(required_subsidy_files)
    })

    let a = {
        id: 'required_subsidy_files_templates',
        rows: []
    }

    setTimeout(() => {
        // console.log(document.querySelector('input[name=filepond]'))
        // console.log(a[0].getFiles());
        const filepondElems = document.querySelectorAll('input.filepond');

        Array.from(filepondElems).forEach(inputElement => {
            let filepond = FilePond.create(inputElement,{
                labelIdle:`<div style="width:100%;height:100%;">
                    <p>
                    Drag &amp; Drop your files or <span class="filepond--label-action" tabindex="0">Browse</span><br>
                    Some samples to give you an idea :
                  </p>
                </div>`
            });

            filepond.setOptions({
                instantUpload: false,
                allowMultiple: true,
                allowProcess: true,
                allowRevert: true,
                allowReorder: true,
                maxParallelUploads: 2,
                fileRenameFunction: (file) => {
                    return `my_new_name${file.extension}`;
                },
                onupdatefiles: (files) => {
                    console.log(files.length)
                },
                onaddfile: (file) => {
                    console.log(1)
                    // console.log($('#file_type_3 input[type=file]'))
                },
                server: {
                    process: (fieldName, file, metadata, load, error, progress, abort) => {
                        console.log(filepond.id)
                        const formData = new FormData();
                        const length = filepond.getFiles().length
                        formData.append('files', filepond.getFiles()[length-1].file)
                        formData.append('files', filepond.getFiles()[length-2].file)
                        formData.append('id_file_type', filepond.id.substr(1,1))
                        const request = new XMLHttpRequest();
                        request.open('POST', 'upload_subsidy_files');
                        request.onload = () => {
                           if (request.status >= 200 && request.status < 300) {
                                load(request.responseText);
                            }
                            else {
                                error('oh no');
                            }
                        }
                        request.send(formData);
                    },
                    revert: 'delete_subsidy_files'
                }
            });

        })

    }, 150)
    return a;
}

function view_subsidy_files_section(title, required) {
    let req_status = required === true ? "Обязательно" : "Не обязательно"
    return {
        view: 'template',
        type: 'section',
        template: title + ` (<span style="color: red">` + req_status + `</span>)`
    }
}

const showRequiredSubsidyFiles = (required_subsidy_files) => {
    Array.from(required_subsidy_files).forEach(required_subsidy_file => {
        $$('required_subsidy_files_templates').addView(
            view_subsidy_files_section(required_subsidy_file.clsFileType.name, required_subsidy_file.required)
        );

        $$('required_subsidy_files_templates').addView({
            scroll: "auto",
            height: 350,
            template: '<div class="filepond-wrapper"><input id="$' + required_subsidy_file.clsFileType.id + '" type="file" class="filepond" name="file"/></div>'
        });
    })
}