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

resource "google_dns_record_set" "jenkins" {
  name = "jenkins.unguiculus.io."
  type = "A"
  ttl = 300
  managed_zone = "${data.terraform_remote_state.network.dns_managed_zone}"
  rrdatas = ["${google_compute_instance.jenkins.network_interface.0.access_config.0.assigned_nat_ip}"]
}
