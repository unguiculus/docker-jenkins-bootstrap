resource "digitalocean_droplet" "jenkins" {
  image = "ubuntu-16-04-x64"
  name = "jenkins"
  region = "${var.region}"
  size = "4GB"
  ssh_keys = ["${digitalocean_ssh_key.ssh_key.id}"]
}

resource "digitalocean_ssh_key" "ssh_key" {
  name = "SSH Key"
  public_key = "${file("~/.ssh/id_rsa.pub")}"
}
