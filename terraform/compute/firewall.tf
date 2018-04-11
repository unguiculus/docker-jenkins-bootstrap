resource "google_compute_firewall" "jenkins_inbound" {
  name = "ci-inbound-allow-80-443"
  network = "default"

  allow {
    protocol = "tcp"
    ports = ["80", "443"]
  }

  target_tags = ["jenkins"]
  source_ranges = ["0.0.0.0/0"]
}

//resource "google_compute_firewall" "jenkins_outbound" {
//  name = "ci-outbound-allow-all"
//  network = "default"
//
//  allow {
//    protocol = "tcp"
//  }
//
//  allow {
//    protocol = "udp"
//  }
//
//  allow {
//    protocol = "icmp"
//  }
//
//  source_tags = ["jenkins"]
//}
