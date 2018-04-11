data "terraform_remote_state" "network" {
  backend = "gcs"
  config {
    project = "${var.project}"
    bucket = "${var.remote_state_bucket}"
    prefix = "network"
  }
}
