resource "digitalocean_domain" "domain" {
  name = "jenkins.unguiculus.io"
  ip_address = "${digitalocean_droplet.jenkins.ipv4_address}"
}
