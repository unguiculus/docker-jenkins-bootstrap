output "ip_address" {
  value = "${google_compute_address.jenkins.address}"
}

output "dns_managed_zone" {
  value = "${google_dns_managed_zone.jenkins.name}"
}
