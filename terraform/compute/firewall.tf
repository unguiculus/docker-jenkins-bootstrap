resource "google_compute_firewall" "allow_80_443" {
  name = "ci-inbound-allow-80-443"
  network = "default"

  allow {
    protocol = "tcp"
    ports = ["80", "443"]
  }

  target_tags = ["jenkins"]
  source_ranges = ["0.0.0.0/0"]
}
