/*
  footer.js
  Dynamically renders the footer section across all pages.
*/

function renderFooter() {
  const footerDiv = document.getElementById("footer");
  if (!footerDiv) return;

  // Determine image path (adjusts for index.html vs subpages)
  const logoPath = window.location.pathname.endsWith("/") || window.location.pathname.endsWith("/index.html")
    ? "./assets/images/logo/logo.png"
    : "../assets/images/logo/logo.png";

  // Footer HTML structure
  footerDiv.innerHTML = `
    <footer class="footer">
      <div class="footer-container">

        <!-- Branding / Logo Section -->
        <div class="footer-logo">
          <img src="${logoPath}" alt="Hospital CMS Logo">
          <p>© Copyright 2025. All Rights Reserved by Hospital CMS.</p>
        </div>

        <!-- Links Section -->
        <div class="footer-links">

          <!-- Company Column -->
          <div class="footer-column">
            <h4>Company</h4>
            <a href="#">About</a>
            <a href="#">Careers</a>
            <a href="#">Press</a>
          </div>

          <!-- Support Column -->
          <div class="footer-column">
            <h4>Support</h4>
            <a href="#">Account</a>
            <a href="#">Help Center</a>
            <a href="#">Contact Us</a>
          </div>

          <!-- Legals Column -->
          <div class="footer-column">
            <h4>Legals</h4>
            <a href="#">Terms & Conditions</a>
            <a href="#">Privacy Policy</a>
            <a href="#">Licensing</a>
          </div>

        </div> <!-- End footer-links -->

      </div> <!-- End footer-container -->
    </footer>
  `;
}

// Automatically render footer when DOM is ready
document.addEventListener("DOMContentLoaded", renderFooter);
