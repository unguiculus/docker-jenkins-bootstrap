resource "google_dns_managed_zone" "jenkins" {
  name = "jenkins-zone"
  description = "Jenkins DNS Zone"
  dns_name = "unguiculus.io."
}

