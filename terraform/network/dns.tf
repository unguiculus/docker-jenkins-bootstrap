resource "google_dns_managed_zone" "unguiculus_io" {
  name = "unguiculus-io"
  description = "DNS Zone"
  dns_name = "unguiculus.io."
}
