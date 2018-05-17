resource "google_dns_record_set" "jenkins" {
  name = "jenkins.unguiculus.io."
  type = "A"
  ttl = 300
  managed_zone = "${data.terraform_remote_state.network.dns_managed_zone}"
  rrdatas = ["${google_compute_instance.jenkins.network_interface.0.access_config.0.assigned_nat_ip}"]
}
