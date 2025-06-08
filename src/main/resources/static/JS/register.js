function showForm(form) {
    const loginForm = document.getElementById("login-form");
    const registerForm = document.getElementById("register-form");
    const tabs = document.querySelectorAll("#authTabs .nav-link");

    if (form === "login") {
      loginForm.classList.remove("d-none");
      registerForm.classList.add("d-none");
      tabs[0].classList.add("active");
      tabs[1].classList.remove("active");
    } else {
      loginForm.classList.add("d-none");
      registerForm.classList.remove("d-none");
      tabs[0].classList.remove("active");
      tabs[1].classList.add("active");
    }
  }

async function registerUser(){
    const username = document.getElementById("username-reg").value;
    const password = document.getElementById("password-reg").value;
    const confirmPassword = document.getElementById("confirmPassword").value;
    const email = document.getElementById("email").value;
    const phone = document.getElementById("phone").value;

    console.log("Registering user:", username, password, confirmPassword, email, phone);


    if (!doubleCheck(password, confirmPassword)){
        return;
    }


    await fetch("http://127.0.0.1:1111/api/user/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password, email, phone })
    })
    .then(response => {
        if (response.ok) {
            alert("Registration successful! Please login to continue.");
            window.location.href = "/login";
        }
        else {
            alert("Registration failed. Please try again.");
        }
    })
    .catch(text => {
        console.error("Error:", text);
    });

}

function doubleCheck(password, confirmPassword){
    if(password != confirmPassword){
        console.log(password, confirmPassword);
        alert("Passwords do not match!");
        return false;
    }
    return true;
}