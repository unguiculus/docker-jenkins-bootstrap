resource "google_compute_instance" "jenkins" {
  name = "jenkins"
  machine_type = "n1-standard-4"
  zone = "${var.zone}"
  tags = ["jenkins"]

  boot_disk {
    initialize_params {
      image = "ubuntu-os-cloud/ubuntu-1604-lts"
      size = "100"
      type = "pd-ssd"
    }
  }

  network_interface {
    subnetwork = "default"
    access_config {
    }
  }
}
