output "ip_address" {
  value = "${google_compute_instance.jenkins.network_interface.0.access_config.0.assigned_nat_ip}"
}
