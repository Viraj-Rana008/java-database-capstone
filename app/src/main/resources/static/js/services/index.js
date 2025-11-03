ž/*
  index.js — Role-Based Login Handling
  Handles admin and doctor login via modals and API calls.
*/

// ===== Imports =====
import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";
import { selectRole } from "../render.js"; // Helper for setting role and redirecting

// ===== API Endpoints =====
const ADMIN_API = API_BASE_URL + "/admin";
const DOCTOR_API = API_BASE_URL + "/doctor/login";

// ===== Event Listeners for Login Buttons =====
window.onload = function () {
  const adminBtn = document.getElementById("adminLogin");
  const doctorBtn = document.getElementById("doctorLogin");

  if (adminBtn) {
    adminBtn.addEventListener("click", () => {
      openModal("adminLogin");
    });
  }

  if (doctorBtn) {
    doctorBtn.addEventListener("click", () => {
      openModal("doctorLogin");
    });
  }
};

// ===== ADMIN LOGIN HANDLER =====
window.adminLoginHandler = async function () {
  const username = document.getElementById("adminUsername")?.value?.trim();
  const password = document.getElementById("adminPassword")?.value?.trim();

  if (!username || !password) {
    alert("Please enter both username and password.");
    return;
  }

  const admin = { username, password };

  try {
    const response = await fetch(`${ADMIN_API}/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(admin),
    });

    if (!response.ok) {
      alert("Invalid credentials!");
      return;
    }

    const data = await response.json();
    if (data?.token) {
      localStorage.setItem("token", data.token);
      selectRole("admin");
    } else {
      alert("Login failed. Token not received.");
    }
  } catch (error) {
    console.error("Error during admin login:", error);
    alert("An error occurred during login. Please try again.");
  }
};

// ===== DOCTOR LOGIN HANDLER =====
window.doctorLoginHandler = async function () {
  const email = document.getElementById("doctorEmail")?.value?.trim();
  const password = document.getElementById("doctorPassword")?.value?.trim();

  if (!email || !password) {
    alert("Please enter both email and password.");
    return;
  }

  const doctor = { email, password };

  try {
    const response = await fetch(DOCTOR_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(doctor),
    });

    if (!response.ok) {
      alert("Invalid credentials!");
      return;
    }

    const data = await response.json();
    if (data?.token) {
      localStorage.setItem("token", data.token);
      selectRole("doctor");
    } else {
      alert("Login failed. Token not received.");
    }
  } catch (error) {
    console.error("Error during doctor login:", error);
    alert("An error occurred during login. Please try again.");
  }
};
