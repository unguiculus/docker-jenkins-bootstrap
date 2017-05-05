output "ip_address" {
  value = "${digitalocean_droplet.jenkins.ipv4_address}"
}
