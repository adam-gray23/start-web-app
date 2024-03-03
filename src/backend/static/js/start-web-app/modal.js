function displayModal(title, content){

    document.querySelector("header").classList.add("modal-open");
    document.querySelector("header").classList.add("disabled");
    document.getElementById("content-wrapper").classList.add("modal-open");
    document.getElementById("content-wrapper").classList.add("disabled");
    document.querySelector("footer").classList.add("modal-open");
    document.querySelector("footer").classList.add("disabled");

    document.body.style.overflow = "hidden";

    if(title == "Load Session"){
        modalContainer = `
            <div id="modal">
                <div class="row justify-content-center">
                    <div class="col-lg-7">
                        <div class="wrap">
                            <div class="card card-main shadow">
                                <div id="modalHeader" class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                                    <h6 class="m-0 font-weight-bold text-primary">${title}</h6>
                                    <button type="button" class="close" aria-label="Close" onclick="closeModal()">
                                        <span aria-hidden="true">&times;</span>
                                    </button>
                                </div>
                                <div id="modalBody" class="card-body">
                                    <div class="row d-none d-sm-inline">
                                        <div class="col-10">
                                            <div class="row">
                                                <div class="col-3 d-sm-inline">Title</div>
                                                <div class="col-3 d-sm-inline">Created</div>
                                                <div class="col-3 d-sm-inline">Modified</div>
                                                <div class="col-3 d-sm-inline">Session</div>
                                            </div>
                                        </div>
                                    </div>
                                    <hr>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }
    else if (title == "Save Session"){
        modalContainer = `
            <div id="modal">
                <div class="row justify-content-center">
                    <div class="col-lg-7">
                        <div class="wrap">
                            <div id="saveModal" class="card card-main shadow">
                                <div id="modalHeader" class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                                    <h6 class="m-0 font-weight-bold text-primary">${title}</h6>
                                    <button type="button" class="close" aria-label="Close" onclick="closeModal()">
                                        <span aria-hidden="true">&times;</span>
                                    </button>
                                </div>
                                <div id="modalBody" class="card-body">
                                    <div class="row d-none d-sm-inline">
                                        <div class="col-10">
                                            <div class="row">
                                                <div class="col-3 d-sm-inline">Title</div>
                                                <div class="col-3 d-sm-inline">Created</div>
                                                <div class="col-3 d-sm-inline">Modified</div>
                                                <div class="col-3 d-sm-inline">Session</div>
                                            </div>
                                        </div>
                                    </div>
                                    <hr>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    modal = new DOMParser().parseFromString(modalContainer, 'text/html');
    element = modal.body.firstChild;

    console.log(content)

    if(title == "Load Session" || title == "Save Session"){

        for(var i = 0; i < 10; i++){
            session = content["session"][i]

            // create row div
            var row = document.createElement("div");
            row.classList.add("row");

            if (i >= content["session"].length && title == "Load Session"){
                var main_col = document.createElement("div");
                main_col.classList.add("col-12");
                main_col.classList.add("text-center");
                main_col.innerHTML = "Empty"
                row.appendChild(main_col);
            }
            else if(i >= content["session"].length && title == "Save Session"){
                var main_col = document.createElement("div");
                main_col.classList.add("col-12");
                main_col.classList.add("col-sm-10");
                main_col.classList.add("text-center");
                main_col.innerHTML = "Empty"
                var button_col = document.createElement("div");
                button_col.classList.add("col-12");
                button_col.classList.add("col-sm-2");
                var button = document.createElement("button");
                button.classList.add("btn");
                button.classList.add("btn-primary");
                button.innerHTML = "Save";
                button.setAttribute("onclick", `confirmSave(${i}, 1)`);
                button_col.appendChild(button);
                row.appendChild(main_col);
                row.appendChild(button_col);
            }
            else{

                var main_col = document.createElement("div");
                main_col.classList.add("col-12");
                main_col.classList.add("col-sm-10");

                var sub_row = document.createElement("div");
                sub_row.classList.add("row");

                var col1 = document.createElement("div");
                col1.classList.add("col-12");
                col1.classList.add("col-sm-3");
                col1.classList.add("session-title")
                var col2 = document.createElement("div");
                col2.classList.add("col-12");
                col2.classList.add("col-sm-3");
                var col3 = document.createElement("div");
                col3.classList.add("col-12");
                col3.classList.add("col-sm-3");
                var col4 = document.createElement("div");
                col4.classList.add("col-12");
                col4.classList.add("col-sm-3");
                col4.classList.add("session")
                col4.style.overflow = "hidden";

                col1.innerHTML = "<p class='d-inline d-sm-none'>Title: </p>" + session["title"]
                col2.innerHTML = "<p class='d-inline d-sm-none'>Created: </p>" + displayDateTime(session["created"])
                col3.innerHTML = "<p class='d-inline d-sm-none'>Modified: </p>" + displayDateTime(session["modified"])
                col4.innerHTML = "<p class='d-inline d-sm-none'>Session: </p>" + session["session"]

                sub_row.appendChild(col1);
                sub_row.appendChild(col2);
                sub_row.appendChild(col3);
                sub_row.appendChild(col4);

                main_col.appendChild(sub_row);

                var button_col = document.createElement("div");
                button_col.classList.add("col-12");
                button_col.classList.add("col-sm-2");
                var button = document.createElement("button");
                button.classList.add("btn");
                button.classList.add("btn-primary");
                if(title == "Save Session"){
                    button.innerHTML = "Overwrite";
                    button.setAttribute("onclick", `confirmSave(${i}, 2)`);
                }
                else{
                    button.innerHTML = "Load";
                    button.setAttribute("onclick", `showSession(${i})`);
                }
                button_col.appendChild(button);

                row.appendChild(main_col);
                row.appendChild(button_col);
            }

            element.querySelector("#modalBody").appendChild(row);
            element.querySelector("#modalBody").appendChild(document.createElement("hr"));
        }
    }

    // if modal present, remove it
    if(document.getElementById("modal") != null){
        document.getElementById("modal").remove();
    }

    document.body.appendChild(element);

}

function confirmSave (x, y) {
    if(document.getElementById("saveTitle") != null){
        document.getElementsByClassName("confsave")[0].remove();
    }

    var row = document.createElement("div");
    row.classList.add("row");
    row.classList.add("confsave");
    row.classList.add("justify-content-center");
    row.classList.add("text-center");
    row.classList.add("ml-0");

    var main_col = document.createElement("div");
    main_col.classList.add("col-4");
    main_col.innerHTML = "Title:"
    row.appendChild(main_col);

    var text_col = document.createElement("div");
    text_col.classList.add("col-4");
    var input = document.createElement("input");
    input.setAttribute("type", "text");
    input.setAttribute("id", "saveTitle");
    input.classList.add("form-control");
    text_col.appendChild(input);
    row.appendChild(text_col);

    var col = document.createElement("div");
    col.classList.add("col-4");
    var button = document.createElement("button");
    button.classList.add("btn");
    button.classList.add("btn-primary");
    button.innerHTML = "Confirm Save";

    button.setAttribute("onclick", `saveFunc(${x}, ${y})`);
    col.appendChild(button);
    row.appendChild(col);

    document.getElementById("saveModal").appendChild(row);

}

function closeModal(){
    document.querySelector("header").classList.remove("modal-open");
    document.querySelector("header").classList.remove("disabled");
    document.getElementById("content-wrapper").classList.remove("modal-open");
    document.getElementById("content-wrapper").classList.remove("disabled");
    document.querySelector("footer").classList.remove("modal-open");
    document.querySelector("footer").classList.remove("disabled");

    document.body.style.overflow = "auto";

    document.getElementById("modal").remove();
}

function displayDateTime(dt){
    var date = new Date(dt);
    var dateStr = date.toLocaleDateString();
    var timeStr = date.toLocaleTimeString();

    return dateStr + " " + timeStr;
}