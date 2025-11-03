/*
  doctorServices.js
  Handles all API interactions related to doctor management.
*/

import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = API_BASE_URL + "/doctor";

/* ==========================================================
   Function: getDoctors
   Purpose: Fetch the list of all doctors from the API
   ========================================================== */
export async function getDoctors() {
  try {
    const response = await fetch(DOCTOR_API);
    if (!response.ok) {
      console.error("Failed to fetch doctors:", response.statusText);
      return [];
    }

    const data = await response.json();
    return data?.doctors || [];
  } catch (error) {
    console.error("Error fetching doctors:", error);
    return [];
  }
}

/* ==========================================================
   Function: deleteDoctor
   Purpose: Delete a doctor by ID using admin authentication
   ========================================================== */
export async function deleteDoctor(id, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/delete/${id}/${token}`, {
      method: "DELETE",
    });

    if (!response.ok) {
      console.error("Failed to delete doctor:", response.statusText);
      return { success: false, message: "Failed to delete doctor" };
    }

    const data = await response.json();
    return {
      success: data?.success ?? true,
      message: data?.message || "Doctor deleted successfully",
    };
  } catch (error) {
    console.error("Error deleting doctor:", error);
    return { success: false, message: "An error occurred during deletion" };
  }
}

/* ==========================================================
   Function: saveDoctor
   Purpose: Add a new doctor (Admin action)
   ========================================================== */
export async function saveDoctor(doctor, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/save/${token}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(doctor),
    });

    if (!response.ok) {
      console.error("Failed to save doctor:", response.statusText);
      return { success: false, message: "Failed to save doctor" };
    }

    const data = await response.json();
    return {
      success: data?.success ?? true,
      message: data?.message || "Doctor saved successfully",
    };
  } catch (error) {
    console.error("Error saving doctor:", error);
    return { success: false, message: "An error occurred while saving doctor" };
  }
}

/* ==========================================================
   Function: filterDoctors
   Purpose: Retrieve filtered doctor list based on criteria
   ========================================================== */
export async function filterDoctors(name = "", time = "", specialty = "") {
  try {
    const url = `${DOCTOR_API}/filter/${encodeURIComponent(name || "null")}/${encodeURIComponent(
      time || "null"
    )}/${encodeURIComponent(specialty || "null")}`;

    const response = await fetch(url);
    if (!response.ok) {
      console.error("Failed to filter doctors:", response.statusText);
      return { doctors: [] };
    }

    const data = await response.json();
    return data || { doctors: [] };
  } catch (error) {
    console.error("Error filtering doctors:", error);
    alert("Error fetching filtered doctors. Please try again later.");
    return { doctors: [] };
  }
}
