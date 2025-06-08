localStorage.removeItem("newCreatedListID");

async function addTasks(){
    let listID = localStorage.getItem("newCreatedListID");

    if (!listID) {
        listID = await addList(); // Wait for addList() to return the new listID
        console.log("listID after addList():", listID);
    }

    const newTodoItem = document.getElementById("newTodoItem").value;
    const todoDescription = document.getElementById("todoDescription").value;
    const todoDate = document.getElementById("todoDate").value;
    const todoTime = document.getElementById("todoTime").value;

    fetch(`http://127.0.0.1:1111/api/${localStorage.getItem("userID")}/addListItem/${listID}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ newTodoItem, todoDescription, todoDate, todoTime })
    })
    .then(response => {
        if(response.ok){
            console.log("Task added successfully");
            return response.json();
        }
        throw new Error("Request failed");
    })
    .catch(error => {
        console.log(error);
    });
}

async function addList(){
    const title = document.getElementById("listTitle").value;
    const description = document.getElementById("listDescription").value;

    return fetch(`http://127.0.0.1:1111/api/${localStorage.getItem("userID")}/addList`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ title, description })
    })
    .then(response => response.json())
    .then(data => {
        localStorage.setItem("newCreatedListID", data.listID);
        console.log("newCreatedListID:", data.listID);
        return data.listID; // Return the new listID so addTasks() can use it
    })
    .catch(error => {   
        throw new Error(error);
    });
}

function saveList(){
    addList();
    window.location.href = "/index";
}