$(document).ready(function () {
  const userID = localStorage.getItem("userID");
  if (userID === null) {
    alert("⚠️ You need to log in first! Redirecting to login page...");
    window.location.href = "login.html";
  }
  $("#username").text(localStorage.getItem("username"));

  fetch(`http://127.0.0.1:1111/api/${userID}/getList`)
    .then(response => response.json())
    .then(lists => {
      lists.forEach(list => {
        const listItem = `
          <li class="list-group-item list-title" data-list-id="${list.listID}">
            <span>${list.title}</span>
            <div class="todo-items"></div>
          </li>
        `;
        $("#todoListContainer").append(listItem);
      });
    });

  $(document).on("click", ".list-title", function (e) {
    if ($(e.target).closest(".todo-items, .todo-add-form, .todo-update-form").length > 0) return;
    if ($(".todo-add-form").length > 0 || $(".todo-update-form").length > 0) return;

    const parent = $(this).closest("li");
    const listID = parent.data("list-id");
    const todoItems = parent.find(".todo-items");

    if (todoItems.children().length === 0) {
      fetch(`http://127.0.0.1:1111/api/${userID}/getListItems/${listID}`)
        .then(response => response.text())
        .then(text => text ? JSON.parse(text) : [])
        .then(items => {
          if (items.length === 0) {
            todoItems.append(addTaskButton(listID));
            todoItems.append(addRemoveListButton(listID));
          } else {
            items.forEach(item => {
              const deadline = new Date(item.dueDate) - new Date() > 0
                  ? `${dateDifference(new Date(), new Date(item.dueDate))} days left`
                  : "Overdue";
          
              // ✅ Check if the task is "Completed"
              const completedClass = item.status === "Completed" ? "list-group-item-success" : "";
          
              const itemHTML = `
                  <li class="list-group-item ${completedClass} ${deadline === "Overdue" ? "list-group-item-danger" : ""} d-flex justify-content-between align-items-center">
                      <div>
                          <strong>${item.title}</strong> - ${item.description || "No description"}
                          <span class="badge bg-secondary">${deadline}</span>
                      </div>
                      <div class="dropdown">
                          <button class="btn btn-light btn-sm dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">⋮</button>
                          <ul class="dropdown-menu">
                              <li><a class="dropdown-item" href="#" onclick="markDone('${item.id}', '${listID}')">✅ Done</a></li>
                              <li><a class="dropdown-item" href="#" onclick="updateTask('${item.id}', '${listID}', '${item.title}', '${item.description}', '${item.dueDate}')">✏️ Update</a></li>
                              <li><a class="dropdown-item text-danger" href="#" onclick="removeTask('${item.id}', '${listID}')">🗑️ Remove</a></li>
                          </ul>
                      </div>
                  </li>
              `;
              todoItems.append(itemHTML);
          });
          
            todoItems.append(addTaskButton(listID));
            todoItems.append(addRemoveListButton(listID));
          }
          todoItems.slideToggle();
        })
        .catch(error => console.error("Error fetching items:", error));
    } else {
      todoItems.slideToggle();
    }
  });
});

function addTaskButton(listID) {
  return `
      <li class="list-group-item" onclick="addTodoThings('${listID}')" style="background-color: #00cc66; color: white;">
        <strong>Add tasks +</strong>
      </li>
    `;
}

function addRemoveListButton(listID) {
  return `
      <button class="btn btn-danger" onclick="removeList('${listID}')">Remove List</button>
    `;
}

function removeList(listID) {
  if (!confirm("Are you sure you want to remove this list?")){
    return;
  }
  fetch(`http://127.0.0.1:1111/api/${localStorage.getItem("userID")}/deleteList/${listID}`, {
    method: "DELETE",
    headers: { "Content-Type": "application/json" }
  })
    .then(() => location.reload())
    .catch(error => console.error("Error removing list:", error));
}

function addTodo(listID) {
  const title = $(".todo-add-form input[type='text']").val();
  const description = $(".todo-add-form textarea").val();
  const dueDate = `${$(".todo-add-form input[type='date']").val()}T${$(".todo-add-form input[type='time']").val()}`;
  if (!title || !dueDate) {
    alert("Title and Due Date are required!");
    return;
  }

  const userID = localStorage.getItem("userID");
  const data = { title, description, dueDate };

  fetch(`http://127.0.0.1:1111/api/${userID}/addListItem/${listID}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data)
  })
    .then(response => {
      if (response.ok) {
        location.reload();
      } else {
        alert("Error adding to-do item!");
      }
    })
    .catch(error => console.error("Error adding to-do item:", error));

}

function cancelAddTodo(listID) {
  $(`[data-list-id="${listID}"] .todo-add-form`).remove(); // Remove the form
  $(`[data-list-id="${listID}"] .list-group-item:contains('Add tasks +')`).show(); // Show "Add tasks +"
}

function markDone(itemID, listID) {
  fetch(`http://127.0.0.1:1111/api/${localStorage.getItem("userID")}/updateListItemStatus/${listID}/${itemID}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ status: "Completed" })
  })
    .then(() => location.reload())
    .catch(error => console.error("Error marking task as done:", error));
}

function updateTask(itemID, listID) {
  const newTitle = prompt("Enter new title:");
  const newDescription = prompt("Enter new description:");
  const newDate = prompt("Enter new due date (yyyy-mm-dd):");
  const newTime = prompt("Enter new due time (HH:mm:ss):");

  const dueDate = `${newDate}T${newTime}`;
  if (!newTitle || !newDate || !newTime) return alert("All fields are required!");

  fetch(`http://127.0.0.1:1111/api/${localStorage.getItem("userID")}/updateListItem/${listID}/${itemID}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ title: newTitle, description: newDescription, dueDate })
  })
    .then(() => location.reload())
    .catch(error => console.error("Error updating task:", error));
}

function removeTask(itemID, listID) {
  if (!confirm("Are you sure you want to remove this task?")) return;
  fetch(`http://127.0.0.1:1111/api/${localStorage.getItem("userID")}/deleteListItem/${listID}/${itemID}`, {
    method: "DELETE"
  })
    .then(() => location.reload())
    .catch(error => console.error("Error removing task:", error));
}

function addTodoThings(listID) {
  if ($(".todo-add-form").length > 0) return;

  const formHTML = `
    <div class="todo-add-form mt-2 p-2 border rounded">
      <input type="text" class="form-control mb-2" placeholder="To-Do Title" />
      <textarea class="form-control mb-2" placeholder="To-Do Description"></textarea>
      <input type="date" class="form-control mb-2" />
      <input type="time" step="1" class="form-control mb-2" />
      <div class="d-flex justify-content-end">
        <button class="btn btn-success me-2" onclick="addTodo('${listID}')">+</button>
        <button class="btn btn-danger" onclick="cancelAddTodo('${listID}')">Cancel</button>
      </div>
    </div>
  `;

  $(`[data-list-id="${listID}"] .list-group-item:contains('Add tasks +')`).hide();
  const todoItems = $(`[data-list-id="${listID}"] .todo-items`);
  todoItems.append(formHTML);
  todoItems.slideDown();
}

function updateTask(itemID, listID, title, description, dueDate) {
  if ($(".todo-update-form").length > 0) return;

  const formattedDate = dueDate.split("T")[0];
  const formattedTime = dueDate.split("T")[1];

  const formHTML = `
    <div class="todo-update-form mt-2 p-2 border rounded">
      <input type="text" class="form-control mb-2" value="${title}" />
      <textarea class="form-control mb-2">${description || ""}</textarea>
      <input type="date" class="form-control mb-2" value="${formattedDate}" />
      <input type="time" step="1" class="form-control mb-2" value="${formattedTime}" />
      <div class="d-flex justify-content-end">
        <button class="btn btn-primary me-2" onclick="submitUpdate('${itemID}', '${listID}')">✔️ Save</button>
        <button class="btn btn-danger" onclick="cancelUpdate('${listID}')">Cancel</button>
      </div>
    </div>
  `;

  $(`[data-list-id="${listID}"] .todo-items`).append(formHTML);
}

function submitUpdate(itemID, listID) {
  const title = $(".todo-update-form input[type='text']").val();
  const description = $(".todo-update-form textarea").val();
  const dueDate = `${$(".todo-update-form input[type='date']").val()}T${$(".todo-update-form input[type='time']").val()}`;

  if (!title || !dueDate) {
    alert("Title and Due Date are required!");
    return;
  }

  fetch(`http://127.0.0.1:1111/api/${localStorage.getItem("userID")}/updateListItem/${listID}/${itemID}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ title, description, dueDate })
  })
    .then(() => location.reload())
    .catch(error => console.error("Error updating task:", error));
}

function cancelUpdate(listID) {
  $(".todo-update-form").remove();
}

// function cancelAddTodo(listID) {
//   $(".todo-add-form").remove();
//   $(`[data-list-id="${listID}"] .list-group-item:contains('Add tasks +')`).show();
// }

function dateDifference(date1, date2) {
  return Math.floor((date2 - date1) / (1000 * 60 * 60 * 24));
}

function logoutUser() {
  localStorage.removeItem("userID");
  localStorage.removeItem("username");
  try{
    fetch(`http://127.0.0.1:1111/api/user/logout`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: 'include',
      mode: 'cors'
    })
    .then(response => {
      if (response.ok) {
        console.log("User logged out");
      } else {
        alert("Error logging out!");
      }
    })
  }
  catch(err){
    console.log(err);
  }
  window.location.href = "/login";
}

function addNewList(){
  window.location.href = "/todo_list/create";
}

