provider "google" {
  project = "${var.project}"
  region = "${var.region}"
  version = "~> 1.9"
}

variable "project" {
  description = "The name of the Google Cloud project"
}

variable "region" {
  description = "The region to create the cluster in"
  default = "europe-west1"
}

variable "zone" {
  description = "The zone that resources are created in"
  default = "europe-west1-b"
}

variable "remote_state_bucket" {
  description = "The name of the bucket for the Terraform state"
}
