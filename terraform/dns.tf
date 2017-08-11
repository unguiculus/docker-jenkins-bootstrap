resource "digitalocean_domain" "domain" {
    name = "unguiculus.io"
    ip_address = "${digitalocean_droplet.jenkins.ipv4_address}"
}

resource "digitalocean_record" "record" {
    domain = "${digitalocean_domain.domain.name}"
    type = "A"
    name = "jenkins"
    value = "${digitalocean_droplet.jenkins.ipv4_address}"
}
