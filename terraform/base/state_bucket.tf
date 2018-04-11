resource "google_storage_bucket" "terraform_state" {
  name = "${var.remote_state_bucket}"
  location = "EU"
  force_destroy = true
}
