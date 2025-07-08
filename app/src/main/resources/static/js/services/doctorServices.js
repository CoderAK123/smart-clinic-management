/*
  Import the base API URL from the config file
  Define a constant DOCTOR_API to hold the full endpoint for doctor-related actions


  Function: getDoctors
  Purpose: Fetch the list of all doctors from the API

   Use fetch() to send a GET request to the DOCTOR_API endpoint
   Convert the response to JSON
   Return the 'doctors' array from the response
   If there's an error (e.g., network issue), log it and return an empty array


  Function: deleteDoctor
  Purpose: Delete a specific doctor using their ID and an authentication token

   Use fetch() with the DELETE method
    - The URL includes the doctor ID and token as path parameters
   Convert the response to JSON
   Return an object with:
    - success: true if deletion was successful
    - message: message from the server
   If an error occurs, log it and return a default failure response


  Function: saveDoctor
  Purpose: Save (create) a new doctor using a POST request

   Use fetch() with the POST method
    - URL includes the token in the path
    - Set headers to specify JSON content type
    - Convert the doctor object to JSON in the request body

   Parse the JSON response and return:
    - success: whether the request succeeded
    - message: from the server

   Catch and log errors
    - Return a failure response if an error occurs


  Function: filterDoctors
  Purpose: Fetch doctors based on filtering criteria (name, time, and specialty)

   Use fetch() with the GET method
    - Include the name, time, and specialty as URL path parameters
   Check if the response is OK
    - If yes, parse and return the doctor data
    - If no, log the error and return an object with an empty 'doctors' array

   Catch any other errors, alert the user, and return a default empty result
*/
// doctorServices.js

import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = `${API_BASE_URL}/api/doctors`;

/**
 * Fetch all doctors from the API
 * @returns {Promise<Array>} Array of doctor objects
 */
export async function getDoctors() {
  try {
    const response = await fetch(DOCTOR_API);
    const data = await response.json();
    return data.doctors || [];
  } catch (error) {
    console.error("Error fetching doctors:", error);
    return [];
  }
}

/**
 * Delete a doctor by ID
 * @param {number|string} id - Doctor's ID
 * @param {string} token - Authorization token
 * @returns {Promise<Object>} Deletion result
 */
export async function deleteDoctor(id, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/${id}/${token}`, {
      method: "DELETE",
    });
    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || "Doctor deleted successfully.",
    };
  } catch (error) {
    console.error("Error deleting doctor:", error);
    return { success: false, message: "Failed to delete doctor." };
  }
}

/**
 * Save (add) a new doctor
 * @param {Object} doctor - Doctor data
 * @param {string} token - Authorization token
 * @returns {Promise<Object>} Save result
 */
export async function saveDoctor(doctor, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/add/${token}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(doctor),
    });
    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || "Doctor added successfully.",
    };
  } catch (error) {
    console.error("Error saving doctor:", error);
    return { success: false, message: "Failed to save doctor." };
  }
}

/**
 * Filter doctors by name, time, and specialty
 * @param {string} name
 * @param {string} time
 * @param {string} specialty
 * @returns {Promise<Array>} Filtered doctors
 */
export async function filterDoctors(name, time, specialty) {
  try {
    const response = await fetch(`${DOCTOR_API}/search/${name}/${time}/${specialty}`);
    if (response.ok) {
      const data = await response.json();
      return data.doctors || [];
    } else {
      console.error("Filtering doctors failed with status", response.status);
      return [];
    }
  } catch (error) {
    console.error("Error filtering doctors:", error);
    alert("Failed to filter doctors.");
    return [];
  }
}
