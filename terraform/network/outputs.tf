output "dns_managed_zone" {
  value = "${google_dns_managed_zone.unguiculus_io.name}"
}
