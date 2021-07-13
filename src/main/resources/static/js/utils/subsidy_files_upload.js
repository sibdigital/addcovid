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
        const filepondElems = document.querySelectorAll('input.filepond');

        Array.from(filepondElems).forEach(inputElement => {
            let filepond = FilePond.create(inputElement,{
                labelIdle:`<div style="width:100%;height:100%;">
                    <p>
                    Drag &amp; Drop your files or <span class="filepond--label-action" tabindex="0">Browse</span><br>
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
                onupdatefiles: (files) => {
                    console.log(files.length)
                },
                onaddfile: (file) => {
                    console.log(1)
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